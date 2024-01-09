package xyz.nietongxue.kanglang.runtime

import jakarta.annotation.PostConstruct
import jakarta.annotation.Resource
import org.flowable.cmmn.api.CmmnRepositoryService
import org.flowable.cmmn.api.CmmnRuntimeService
import org.flowable.cmmn.api.CmmnTaskService
import org.flowable.cmmn.engine.CmmnEngine
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import xyz.nietongxue.kanglang.actor.*
import xyz.nietongxue.kanglang.define.DefineToDeploy
import xyz.nietongxue.kanglang.material.get

@EnableScheduling
@Configuration
class Engine(
    @Autowired val cmmnEngine: CmmnEngine,
    @Resource(name = "define") val define: DefineToDeploy,
    @Resource(name = "initVariables") val initVariables: Map<String, Any>
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
        val cmmnDeployment = cmmnRepositoryService.createDeployment().also {
                when (define) {
                    is DefineToDeploy.DefineByResource -> it.addClasspathResource((define as DefineToDeploy.DefineByResource).resourcePath)
                    is DefineToDeploy.DefineByString -> {
                        (define as DefineToDeploy.DefineByString).also { d ->
                            it.addString(d.id, d.content)
                        }
                    }

                }
            }.deploy()
        val caseDefinitions = cmmnRepositoryService.createCaseDefinitionQuery().list()
        this.runtimeService = cmmnEngine.cmmnRuntimeService!!

        this.runtimeService!!.addEventListener(
            runtimeListener
        )

        taskService = cmmnEngine.cmmnTaskService

    }

    fun startCase(caseCreateStrategy: CaseCreateStrategy) {
        val caseInstance = this.runtimeService!!.createCaseInstanceBuilder().also {
                when (caseCreateStrategy) {
                    is CaseCreateStrategy.DefinitionKey -> it.caseDefinitionKey(caseCreateStrategy.key)
                    is CaseCreateStrategy.DefinitionAndPassIns -> {
                        it.caseDefinitionKey(caseCreateStrategy.key)
                        it.variables(caseCreateStrategy.initVariables)
                    }
                }
            }.variables(initVariables).start()
        caseInstanceIds.add(caseInstance.id)
    }
}

interface CaseCreateStrategy {
    val key: String
    val initVariables: Map<String, Any>

    class DefinitionKey(override val key: String) : CaseCreateStrategy {
        override val initVariables: Map<String, Any>
            get() = emptyMap()

    }

    class DefinitionAndPassIns(override val key: String, private val passIns: List<PassIn.DomainVariable>) :
        CaseCreateStrategy {
        override val initVariables: Map<String, Any>
            get() = passIns.associate { it.name to get(it.name) }
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
                    is GetTaskStrategy.ByUserName -> {
                        taskService.createTaskQuery().taskAssignee(task.userName).caseInstanceId(caseId).list()
                    }

                    is GetTaskStrategy.ByRoleName -> {
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
                                doWithEffect(effect)
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

    private fun doWithEffect(effect: Effect) {
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
}

