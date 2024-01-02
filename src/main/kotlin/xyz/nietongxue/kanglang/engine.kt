package xyz.nietongxue.kanglang

import org.flowable.cmmn.api.CmmnRepositoryService
import org.flowable.cmmn.api.CmmnRuntimeService
import org.flowable.cmmn.api.CmmnTaskService
import org.flowable.cmmn.engine.CmmnEngine
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class Engine(
    @Autowired
    val cmmnEngine: CmmnEngine
) {


    private val log = LoggerFactory.getLogger(Engine::class.java)

    private final val runtimeService: CmmnRuntimeService
    private final val taskService: CmmnTaskService


    @Bean
    fun taskService(): CmmnTaskService {
        return taskService
    }

    init {
        log.info("engine: {}", cmmnEngine)
        val cmmnRepositoryService: CmmnRepositoryService = cmmnEngine.cmmnRepositoryService
        val cmmnDeployment = cmmnRepositoryService.createDeployment()
            .addClasspathResource("testR.cmmn.xml")
            .deploy()
        val caseDefinitions = cmmnRepositoryService.createCaseDefinitionQuery().list()
        runtimeService = cmmnEngine.cmmnRuntimeService
        val caseInstance = runtimeService.createCaseInstanceBuilder()
            .caseDefinitionKey("testR")
//            .variable("potentialEmployee", "johnDoe")
            .start()

        taskService = cmmnEngine.cmmnTaskService
    }
}


class CmmnTask(private val task: org.flowable.task.api.Task) : Task {
    override fun variables(): Map<String, Any> {
        return task.taskLocalVariables
    }

    override fun caseVariables(): Map<String, Any> {
        return task.caseVariables
    }

    override val name: String
        get() = task.name

}
