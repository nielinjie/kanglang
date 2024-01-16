package xyz.nietongxue.app.reviewer

import org.springframework.context.annotation.Bean
import xyz.nietongxue.app.newEmployee.HrActor
import xyz.nietongxue.app.newEmployee.NewEmployeeActor
import xyz.nietongxue.kanglang.actor.Actor
import xyz.nietongxue.kanglang.define.DefineToDeploy
import xyz.nietongxue.kanglang.define.defineToDeploy
import xyz.nietongxue.kanglang.material.Domain
import xyz.nietongxue.kanglang.runtime.InitVariables

open class Config {
    @Bean
    fun actors(employeeActor: NewEmployeeActor, hrActor: HrActor): List<Actor> {
        return listOf(employeeActor, hrActor)
    }

    @Bean
    open fun define(): DefineToDeploy {
        return defineToDeploy(define)
    }

    @Bean
    fun initVariables(): InitVariables {
        return InitVariables(mapOf())
    }
    @Bean
    fun domain(): Domain {
        return CoderDomain()
    }
}

class ConfigRepeat(): Config() {
    @Bean
    override fun define(): DefineToDeploy {
        return defineToDeploy(repeatCodingDefine)
    }
}