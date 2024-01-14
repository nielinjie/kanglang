package xyz.nietongxue.app.newEmployee

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import xyz.nietongxue.kanglang.define.DefineToDeploy
import xyz.nietongxue.kanglang.define.defineToDeploy
import xyz.nietongxue.kanglang.runtime.InitVariables

@Configuration
class Config {
    @Bean
    fun define(): DefineToDeploy {
        return defineToDeploy(define)
    }

    @Bean
    fun initVariables(): InitVariables{
        return InitVariables(mapOf())
    }
}