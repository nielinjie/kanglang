package xyz.nietongxue.kanglang.define

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import org.flowable.cmmn.api.repository.CmmnDeployment
import org.flowable.cmmn.engine.impl.cfg.StandaloneInMemCmmnEngineConfiguration
import org.flowable.task.service.impl.persistence.entity.TaskEntityImpl
import xyz.nietongxue.kanglang.define.caseDefine
import xyz.nietongxue.kanglang.define.pretty
import xyz.nietongxue.kanglang.define.toXML


class CmmnTest : StringSpec({
    "simple" {
        val cmmnEngine = StandaloneInMemCmmnEngineConfiguration().buildCmmnEngine()
        val cmmnRepositoryService = cmmnEngine.cmmnRepositoryService
        val cmmnDeployment: CmmnDeployment = cmmnRepositoryService.createDeployment()
            .addClasspathResource("testR.cmmn.xml")
            .deploy()
        val cmmnRuntimeService = cmmnEngine.cmmnRuntimeService
        val caseInstance = cmmnRuntimeService.createCaseInstanceBuilder()
            .caseDefinitionKey("testR")
            .start()
        val planItemInstances = cmmnRuntimeService.createPlanItemInstanceQuery()
            .caseInstanceId(caseInstance.id)
            .planItemInstanceStateActive()
            .orderByName().asc()
            .list()
        planItemInstances.shouldHaveSize(1)
        val cmmnTaskService = cmmnEngine.cmmnTaskService
        val tasks = cmmnTaskService.createTaskQuery().taskAssignee("first").list()
        tasks.shouldHaveSize(1)
        val task = tasks.first() as TaskEntityImpl
        cmmnRuntimeService.setVariable(caseInstance.id, "subQuestions", listOf("a", "b"))
        cmmnTaskService.complete(task.id)
        val tasks2 = cmmnTaskService.createTaskQuery().taskAssignee("second").list()
        tasks2.shouldHaveSize(2)
        tasks2.map {
            cmmnTaskService.getVariable(it.id, "subQuestion") as String
        }.shouldBe(listOf("a", "b"))
        val cmmnHistoryService = cmmnEngine.cmmnHistoryService
        cmmnHistoryService.also { h ->
            h.createHistoricTaskInstanceQuery().list().shouldHaveSize(3)
            h.createHistoricTaskInstanceQuery().list().first().also {
                it.assignee.shouldBe("first")
                it.name.shouldBe("spite question")
            }
            h.createHistoricVariableInstanceQuery().list().forEach {
                println(it.variableName)
                println(it.value)
            }
        }
    }
    "parse" {
        val cmmnEngine = StandaloneInMemCmmnEngineConfiguration().buildCmmnEngine()
        val cmmnRepositoryService = cmmnEngine.cmmnRepositoryService
        val cmmnDeployment: CmmnDeployment = cmmnRepositoryService.createDeployment()
            .addClasspathResource("testParse.cmmn.xml")
            .deploy()
        val cmmnRuntimeService = cmmnEngine.cmmnRuntimeService
        val caseInstance = cmmnRuntimeService.createCaseInstanceBuilder()
            .caseDefinitionKey("testParse")
            .start()
        val tasks = cmmnEngine.cmmnTaskService.createTaskQuery().active().list()
        tasks shouldHaveSize 2
    }
    "parse from generated" {
        val cmmnEngine = StandaloneInMemCmmnEngineConfiguration().buildCmmnEngine()
        val cmmnRepositoryService = cmmnEngine.cmmnRepositoryService
        val cmmnDeployment: CmmnDeployment = cmmnRepositoryService.createDeployment()
            .addClasspathResource("testGenerated.cmmn.xml")
            .deploy()
        val cmmnRuntimeService = cmmnEngine.cmmnRuntimeService
        val caseInstance = cmmnRuntimeService.createCaseInstanceBuilder()
            .caseDefinitionKey("model_id_case_1")
            .start()
        val tasks = cmmnEngine.cmmnTaskService.createTaskQuery().active().list()
        tasks shouldHaveSize 2
    }
    "simple dsl" {
        val define = caseDefine {
            case("case1") {
                stage("stage1") {
                    task("task1") {
                    }
                    task("task2") {
                    }
                }
            }
        }
        val cmmnEngine = StandaloneInMemCmmnEngineConfiguration().buildCmmnEngine()
        val cmmnRepositoryService = cmmnEngine.cmmnRepositoryService
        val cmmnDeployment: CmmnDeployment = cmmnRepositoryService.createDeployment()
            .addString("testGenerated.cmmn", pretty(toXML(define)))
            .deploy()
        val cmmnRuntimeService = cmmnEngine.cmmnRuntimeService

        val caseInstance = cmmnRuntimeService.createCaseInstanceBuilder()
            .caseDefinitionKey("model_id_case_1")
            .start()
        val tasks = cmmnEngine.cmmnTaskService.createTaskQuery().active().list()
        tasks shouldHaveSize 2
    }
})