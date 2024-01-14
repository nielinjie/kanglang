package xyz.nietongxue.app.newEmployee

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import xyz.nietongxue.kanglang.actor.ActorLogItem
import xyz.nietongxue.kanglang.define.defineModelId
import xyz.nietongxue.kanglang.runtime.CaseCreateStrategy
import xyz.nietongxue.kanglang.runtime.Engine
import xyz.nietongxue.kanglang.runtime.LogService


@SpringBootTest(classes = [Config::class, Application::class])
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
        assert(emailGot.size == 1)
    }
}