package xyz.nietongxue.app.reviewer

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import xyz.nietongxue.common.log.Log
import xyz.nietongxue.kanglang.actor.ActorLogItem
import xyz.nietongxue.kanglang.material.Domain
import xyz.nietongxue.kanglang.runtime.CaseCreateStrategy
import xyz.nietongxue.kanglang.runtime.Engine
import xyz.nietongxue.kanglang.runtime.LogService

@SpringBootTest(classes = [ConfigRepeat::class, Application::class])
class CodingTest {
    @Autowired
    var engine: Engine? = null

    @Autowired
    var logService: LogService? = null

    @Autowired
    var domain: Domain? = null

    @Test
    fun testRepeatCoding() {
        logService!!.printLogs = true
        (domain!! as CoderDomain).map["lazyToday"] = true

        engine!!.startCase(
            CaseCreateStrategy.CaseName(
                "code repeat", mapOf("codeCommitted" to false)
            )
        )
        Thread.sleep(5000)
        assertStringRepeatedInLog(logService!!, "coding kangchi kangchi")
    }

    fun withStrings(logService: LogService, string: String): List<Log<*>> {
        val logs = logService.logs
        return logs.filter {
            it.message.let {
                it is ActorLogItem.GenericItem
                        && it.string.contains(string)
            }
        }
    }

    fun assertStringRepeatedInLog(logService: LogService, string: String) {
        val reviewed = withStrings(logService, string)
        reviewed.size.also {
            Assertions.assertThat(it).isGreaterThan(1)
        }

    }

    fun assertStringInLog(logService: LogService, string: String, count: Int = -1) {
        val reviewed = withStrings(logService, string)
        reviewed.size.also {
            if (count == -1) {
                Assertions.assertThat(it).isGreaterThan(0)
            } else {
                Assertions.assertThat(it).isEqualTo(count)
            }
        }

    }
}
