package xyz.nietongxue.kanglang.runtime

import jakarta.annotation.PostConstruct
import jakarta.annotation.Resource
import org.flowable.cmmn.api.CmmnRepositoryService
import org.flowable.cmmn.api.CmmnRuntimeService
import org.flowable.cmmn.api.CmmnTaskService
import org.flowable.cmmn.api.event.FlowableCaseEndedEvent
import org.flowable.cmmn.api.event.FlowableCaseStartedEvent
import org.flowable.cmmn.engine.CmmnEngine
import org.flowable.common.engine.api.delegate.event.FlowableEvent
import org.flowable.common.engine.api.delegate.event.FlowableEventListener
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import xyz.nietongxue.kanglang.actor.*
import xyz.nietongxue.kanglang.define.DefineToDeploy

@EnableScheduling
@Configuration
class Engine(
    @Autowired
    val cmmnEngine: CmmnEngine,
    @Resource(name = "define")
    val define: DefineToDeploy.DefineByResource,
    @Resource(name = "initVariables")
    val initVariables: Map<String, Any>
) {


    private val log = LoggerFactory.getLogger(Engine::class.java)


    var runtimeService: CmmnRuntimeService? = null
    var taskService: CmmnTaskService? = null

    val caseInstanceIds: MutableList<String> = mutableListOf()

    @Bean
    fun taskService(): CmmnTaskService {
        return taskService!!
    }

    @Bean

    fun runtimeService(): CmmnRuntimeService {
        return runtimeService!!
    }

    @Bean
    fun caseInstanceIds(): List<String> {
        return caseInstanceIds
    }

    @PostConstruct
    fun onInit(): Unit {
        log.info("engine: {}", cmmnEngine)
        val cmmnRepositoryService: CmmnRepositoryService = cmmnEngine.cmmnRepositoryService
        val cmmnDeployment = cmmnRepositoryService.createDeployment()
            .addClasspathResource(define.resourcePath)
            .deploy()
        val caseDefinitions = cmmnRepositoryService.createCaseDefinitionQuery().list()
        this.runtimeService = cmmnEngine.cmmnRuntimeService!!
        this.runtimeService!!.addEventListener(
            object : FlowableEventListener {
                override fun onEvent(event: FlowableEvent?) {
//                    println("event: $event")
                    when(event){
                        is FlowableCaseStartedEvent -> println("case started: ${event.scopeId}")
                        is FlowableCaseEndedEvent -> println("case ended: ${event.scopeId}")
                    }
                }

                override fun isFailOnException(): Boolean {
                    return false
                }

                override fun isFireOnTransactionLifecycleEvent(): Boolean {
                    return false
                }

                override fun getOnTransaction(): String? {
                    return null
                }
            }
        )
        val caseInstance = this.runtimeService!!.createCaseInstanceBuilder()
            .caseDefinitionKey(define.id).variables(initVariables)
            .start()
        caseInstanceIds.add(caseInstance.id)
        taskService = cmmnEngine.cmmnTaskService

    }
}

@Component
class ScheduleService(
    @Autowired val actors: List<Actor>,
    @Autowired val taskService: CmmnTaskService,
    @Autowired val runtimeService: CmmnRuntimeService,
    @Autowired val caseInstanceIds: List<String>
) {
    @Scheduled(fixedDelay = 1000)
    fun schedule() {
        println("schedule")
        caseInstanceIds.forEach { caseId ->
            println(caseId)
            actors.forEach act@{ actor ->
                val tasks = when (val task = actor.getTask()) {
                    is GetTask.ByUserName -> {
                        taskService.createTaskQuery().taskAssignee(task.userName).caseInstanceId(caseId).list()
                    }

                    is GetTask.ByRoleName -> {
                        taskService.createTaskQuery().taskCandidateGroup(task.roleName).caseInstanceId(caseId).list()
                    }

                    else -> error("unknown task: $task")
                }
                if (tasks.isEmpty()) {
                    return@act
                }
                actor.choose(tasks.map { CmmnTask(it, caseId, runtimeService) }).also {
                    when (it) {
                        is ChooseResult.Chosen -> {
                            val result = actor.touch(it.task)
                            val effects = result.effects
                            effects.forEach { effect ->

                                when (effect) {

                                    is Effect.TaskVariable -> {
                                        effect.task as CmmnTask
                                        taskService.setVariableLocal(effect.task.raw.id, effect.name, effect.value)
                                    }

                                    is Effect.CaseVariable -> {
                                        (effect.task as CmmnTask).also {
                                            runtimeService.setVariable(
                                                it.caseId, effect.name, effect.value
                                            )
                                            it.raw.caseVariables
                                        }
                                    }
                                }
                            }
                            when (result) {
                                is TouchResult.Completed -> taskService.complete((it.task as CmmnTask).raw.id)
                                is TouchResult.NotCompleted -> return@act
                                is TouchResult.Error -> {
                                    println("error: ${result.log}")
                                    return@act
                                }
                            }
                        }

                        is ChooseResult.NotChosen -> return@act
                    }
                }
            }
        }
    }
}


class CmmnTask(val raw: org.flowable.task.api.Task, val caseId: String, val runtimeService: CmmnRuntimeService) : Task {
    override fun variables(): Map<String, Any> {
        return runtimeService.getLocalVariables(raw.id)
    }

    override fun caseVariables(): Map<String, Any> {
        return runtimeService.getVariables(caseId)
    }

    override val name: String
        get() = raw.name

}

class CmmnCase(val raw: org.flowable.cmmn.api.runtime.CaseInstance, val runtimeService: CmmnRuntimeService) : Case {

    override fun variables(): Map<String, Any> {
        return runtimeService.getVariables(this.id)
    }


    override val id: String
        get() = raw.id

}