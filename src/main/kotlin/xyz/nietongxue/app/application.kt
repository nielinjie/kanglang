package xyz.nietongxue.app;

import org.flowable.cmmn.api.CmmnTaskService
import org.flowable.task.api.Task
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import xyz.nietongxue.kanglang.actor.Actor
import xyz.nietongxue.kanglang.actor.GetTask
import xyz.nietongxue.kanglang.define.Define
import xyz.nietongxue.kanglang.runtime.CmmnTask


fun main() {
    runApplication<Application>()
}


@SpringBootApplication(scanBasePackages = ["xyz.nietongxue"])
class Application() {

    @Bean(name = ["define"])
    fun define(): Define {
        return Define("employeeOnboarding", "my-case.cmmn.xml")
    }

    @Bean(name = ["initVariables"])
    fun initVariables(): Map<String, Any> {
        return mapOf("potentialEmployee" to "johnDoe")
    }

    @Bean
    fun actors(taskService: CmmnTaskService): List<Actor> {
        return actors
    }
}


fun List<Task>.print() {
    this.forEach {
        println("${it.name} - ${it.assignee} - ${it.owner}")
    }
}