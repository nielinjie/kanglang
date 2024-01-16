package xyz.nietongxue.app.newEmployee

import org.springframework.context.annotation.Bean
import xyz.nietongxue.kanglang.actor.Actor
import xyz.nietongxue.kanglang.define.DefineToDeploy
import xyz.nietongxue.kanglang.define.defineToDeploy
import xyz.nietongxue.kanglang.material.Domain
import xyz.nietongxue.kanglang.runtime.InitVariables

//NOTE，显式包含的Configuration，不需要annotation。
//@Configuration
open class Config {
    @Bean
    fun actors(employeeActor: NewEmployeeActor, hrActor: HrActor): List<Actor> {
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
    @Bean
    fun domain(): Domain {
        return EmployeeDomain()
    }


}