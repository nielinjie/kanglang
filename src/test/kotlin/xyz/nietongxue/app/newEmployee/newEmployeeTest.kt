package xyz.nietongxue.app.newEmployee

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import xyz.nietongxue.kanglang.actor.ActorLogItem
import xyz.nietongxue.kanglang.define.defineModelId
import xyz.nietongxue.kanglang.runtime.CaseCreateStrategy
import xyz.nietongxue.kanglang.runtime.Engine
import xyz.nietongxue.kanglang.runtime.LogService

@SpringBootTest
class NewEmployeeTest {

    @Autowired
    var engine:Engine?=null
    @Autowired
    var logService: LogService?=null
    @Test
    fun testNewEmployee() {
        logService!!.printLogs=false
        engine!!.startCase(CaseCreateStrategy.DefinitionKey(defineModelId("new employee case")))
        Thread.sleep(10000)
        val logs = logService!!.logs
        val emailGot = logs.filter {
            it.message is ActorLogItem.GenericItem &&
                    (it.message as ActorLogItem.GenericItem).string.contains("my email is")
        }
        emailGot.forEach {
            println(it)
        }
        assert(emailGot.size==1)
    }
}