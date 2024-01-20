package xyz.nietongxue.app.document

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer
import org.springframework.stereotype.Component
import org.springframework.test.context.TestPropertySource
import xyz.nietongxue.app.newEmployee.AllNewEmployeeActor
import xyz.nietongxue.app.newEmployee.EmployeeDomain
import xyz.nietongxue.app.newEmployee.HrActor
import xyz.nietongxue.kanglang.actor.Actor
import xyz.nietongxue.kanglang.define.DefineToDeploy
import xyz.nietongxue.kanglang.define.defineToDeploy
import xyz.nietongxue.kanglang.material.Domain
import xyz.nietongxue.kanglang.runtime.InitVariables


@Configuration
class Config{
    @Bean
    fun actors(): List<Actor> {
        return listOf()
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