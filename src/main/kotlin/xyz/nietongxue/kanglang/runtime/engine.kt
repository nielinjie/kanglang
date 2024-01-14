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
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler
import xyz.nietongxue.common.log.Log
import xyz.nietongxue.kanglang.actor.Actor
import xyz.nietongxue.kanglang.actor.PassIn
import xyz.nietongxue.kanglang.define.DefineToDeploy
import xyz.nietongxue.kanglang.material.get
import java.time.Duration


interface EngineLogItem {
    data class CaseStarted(val caseId: String) : EngineLogItem
}

class InitVariables(val variables: Map<String, Any>)


@Configuration
class Engine(
    @Autowired val cmmnEngine: CmmnEngine,
    @Autowired val define: DefineToDeploy,
    @Autowired val initVariables: InitVariables,
    @Autowired val logService: LogService,
    @Autowired val actors: List<Actor>
) {


    private val log = LoggerFactory.getLogger(Engine::class.java)

    var runtimeService: CmmnRuntimeService? = null
    var taskService: CmmnTaskService? = null

    val caseInstanceIds: MutableList<String> = mutableListOf()

    var scheduler: ThreadPoolTaskScheduler? = null

    @Autowired
    var runtimeListener: RuntimeListener? = null


    private fun log(logItem: EngineLogItem) {
        this.logService.log(Log(logItem))
    }

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

    fun threadPoolTaskScheduler(): ThreadPoolTaskScheduler {
        val threadPoolTaskScheduler = ThreadPoolTaskScheduler()
        threadPoolTaskScheduler.setPoolSize(5)
        threadPoolTaskScheduler.setThreadNamePrefix(
            "FetcherScheduler"
        )
        return threadPoolTaskScheduler
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
//        val caseDefinitions = cmmnRepositoryService.createCaseDefinitionQuery().list()
        this.runtimeService = cmmnEngine.cmmnRuntimeService!!

        this.runtimeService!!.addEventListener(
            runtimeListener
        )

        taskService = cmmnEngine.cmmnTaskService

        this.scheduler = threadPoolTaskScheduler().also {
            it.initialize()
            it.scheduleAtFixedRate(
                Dispatcher(actors, taskService!!, runtimeService!!, caseInstanceIds, logService),
                Duration.ofMillis(1000)
            )
        }
    }

    fun startCase(caseCreateStrategy: CaseCreateStrategy) {
        val caseInstance = this.runtimeService!!.createCaseInstanceBuilder().also {
            it.caseDefinitionKey(caseCreateStrategy.key)
            it.variables(caseCreateStrategy.initVariables)
        }.variables(initVariables.variables).start()
        caseInstanceIds.add(caseInstance.id)
    }
}

interface CaseCreateStrategy {
    val key: String
    val initVariables: Map<String, Any>

    class DefinitionKey(override val key: String, override val initVariables: Map<String, Any>) : CaseCreateStrategy {

    }

    class DefinitionAndPassIns(
        override val key: String,
        val init: Map<String, Any>,
        private val passIns: List<PassIn.DomainVariable>
    ) :
        CaseCreateStrategy {
        override val initVariables: Map<String, Any>
            get() = init.plus(passIns.associate { it.name to get(it.name) })
    }
}




