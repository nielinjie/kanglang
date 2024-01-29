package xyz.nietongxue.kanglang.runtime

import jakarta.annotation.PostConstruct
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
import xyz.nietongxue.kanglang.define.defineModelId
import xyz.nietongxue.kanglang.material.Domain
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
    var domain: Domain? = null

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
            "DispatcherScheduler"
        )
        return threadPoolTaskScheduler
    }


    @PostConstruct
    fun onInit(): Unit {
        log.info("engine: {}", cmmnEngine)
        val cmmnRepositoryService: CmmnRepositoryService = cmmnEngine.cmmnRepositoryService
        cmmnRepositoryService.createDeployment().also {
            when (define) {
                is DefineToDeploy.DefineByResource -> it.addClasspathResource((define as DefineToDeploy.DefineByResource).resourcePath)
                is DefineToDeploy.DefineByString -> {
                    (define as DefineToDeploy.DefineByString).also { d ->
                        it.addString(d.id, d.content)
                    }
                }

            }
        }.deploy()
        this.runtimeService = cmmnEngine.cmmnRuntimeService!!

        this.runtimeService!!.addEventListener(
            runtimeListener
        )

        taskService = cmmnEngine.cmmnTaskService

        this.scheduler = threadPoolTaskScheduler().also {
            it.initialize()
            it.scheduleAtFixedRate(
                Dispatcher(actors, taskService!!, runtimeService!!, caseInstanceIds, logService, domain!!),
                Duration.ofMillis(1000)
            )
        }
    }

    fun startCase(caseCreateStrategy: CaseCreateStrategy): List<String> {
        return caseCreateStrategy.create().map {
            startCase(it)
        }
    }

    fun startCase(caseCreating: CaseCreating): String {
        val caseInstance = this.runtimeService!!.createCaseInstanceBuilder().also {
            it.caseDefinitionKey(caseCreating.key)
            it.variables(caseCreating.initVariables)
        }.variables(initVariables.variables).start()
        caseInstanceIds.add(caseInstance.id)
        return caseInstance.id
    }
}

interface CaseCreateStrategy {
    fun create(): List<CaseCreating>
    class CaseName(val caseName: String, val initVariables: Map<String, Any>) : CaseCreateStrategy {
        override fun create(): List<CaseCreating> {
            return listOf(CaseCreating(defineModelId(caseName), initVariables))
        }

    }

    class FromDomain(
        val caseName: String,
        val initVariables: Map<String, Any>,
        val domainVariable: PassIn.DomainVariable,
        val domain: Domain
    ) : CaseCreateStrategy {
        override fun create(): List<CaseCreating> {
            return (domain.get(domainVariable.name) as? List<*>)?.filterNotNull()?.let {
                return it.map {
                    CaseCreating(defineModelId(caseName), initVariables + Pair(domainVariable.name, it))
                }
            } ?: emptyList()
        }

    }
}

class CaseCreating(
    val key: String,
    val initVariables: Map<String, Any>
)



