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
import xyz.nietongxue.kanglang.define.defineToDeploy
import xyz.nietongxue.kanglang.runtime.CaseCreateStrategy
import xyz.nietongxue.kanglang.runtime.Engine
import xyz.nietongxue.kanglang.runtime.LogService


fun main() {
    runApplication<Application>()
}


@SpringBootApplication(scanBasePackages = ["xyz.nietongxue"])
class Application() {


}


