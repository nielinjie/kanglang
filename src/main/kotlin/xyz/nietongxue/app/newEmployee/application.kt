package xyz.nietongxue.app.newEmployee;

import org.flowable.cmmn.api.CmmnTaskService
import org.flowable.task.api.Task
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import xyz.nietongxue.kanglang.actor.Actor
import xyz.nietongxue.kanglang.define.DefineToDeploy
import xyz.nietongxue.kanglang.define.building
import xyz.nietongxue.kanglang.define.defineModelId
import xyz.nietongxue.kanglang.runtime.CaseCreateStrategy
import xyz.nietongxue.kanglang.runtime.Engine


fun main() {
    runApplication<Application>()
}


@SpringBootApplication(scanBasePackages = ["xyz.nietongxue"])
class Application() {

    @Bean(name = ["define"])
    fun define(): DefineToDeploy {
        return DefineToDeploy.DefineByString("new_employee_case.cmmn", building(define).toString(false).also {
            println(it)
        })
//        return DefineToDeploy.DefineByResource("new_employee_case.cmmn","newEmployee.cmmn.xml")

    }

    @Bean(name = ["initVariables"])
    fun initVariables(): Map<String, Any> {
        return mapOf("potentialEmployee" to "johnDoe")
    }

    @Bean
    fun actors(taskService: CmmnTaskService): List<Actor> {
        return actors
    }

    @Bean
    fun init(engine: Engine): CommandLineRunner {
        return CommandLineRunner {
            println("starting case")
            engine.cmmnEngine.cmmnRepositoryService.createCaseDefinitionQuery().list().also {
                it.forEach {
                    println(it.key)
                }
            }
            engine.startCase(CaseCreateStrategy.DefinitionKey(defineModelId("new employee case")))
        }
    }
}


fun List<Task>.print() {
    this.forEach {
        println("${it.name} - ${it.assignee} - ${it.owner}")
    }
}