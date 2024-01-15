package xyz.nietongxue.app.newEmployee

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.FilterType
import xyz.nietongxue.kanglang.actor.Actor
import xyz.nietongxue.kanglang.actor.ActorLogItem
import xyz.nietongxue.kanglang.define.DefineToDeploy
import xyz.nietongxue.kanglang.define.defineToDeploy
import xyz.nietongxue.kanglang.runtime.CaseCreateStrategy
import xyz.nietongxue.kanglang.runtime.Engine
import xyz.nietongxue.kanglang.runtime.InitVariables
import xyz.nietongxue.kanglang.runtime.LogService


@SpringBootTest(classes = [Config::class,Application::class])
class NewEmployeeTest {

    @Autowired
    var engine: Engine? = null

    @Autowired
    var logService: LogService? = null

    @Test
    fun testNewEmployee() {
        logService!!.printLogs = true
        engine!!.startCase(
            CaseCreateStrategy.CaseName(
                "new employee case",
                mapOf("potentialEmployee" to "johnDoe")
            )
        )
        Thread.sleep(10000)
        val logs = logService!!.logs
        val emailGot = logs.filter {
            it.message.let {
                it is ActorLogItem.GenericItem
                        && it.string.contains("my email is")
                        && it.string.contains("johnDoe@new.com")
            }
        }
        emailGot.forEach {
            println(it)
        }
        assertThat(emailGot.size).isEqualTo(1)
    }
}