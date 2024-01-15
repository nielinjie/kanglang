package xyz.nietongxue.app.newEmployee

import org.springframework.context.annotation.Bean
import xyz.nietongxue.kanglang.actor.Actor
import xyz.nietongxue.kanglang.define.DefineToDeploy
import xyz.nietongxue.kanglang.define.defineToDeploy
import xyz.nietongxue.kanglang.runtime.InitVariables

//@Configuration
class AllNewConfig{
    @Bean
    fun actors(employeeActor: AllNewEmployeeActor, hrActor: HrActor): List<Actor> {
        return listOf(employeeActor, hrActor)
    }

    @Bean
    fun define(): DefineToDeploy {
        return defineToDeploy(define)
    }

    @Bean
    fun initVariables(): InitVariables {
        return InitVariables(mapOf())
    }
}