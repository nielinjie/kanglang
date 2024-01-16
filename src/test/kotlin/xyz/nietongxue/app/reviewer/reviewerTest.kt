package xyz.nietongxue.app.reviewer

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import xyz.nietongxue.kanglang.actor.ActorLogItem
import xyz.nietongxue.kanglang.material.Domain
import xyz.nietongxue.kanglang.runtime.CaseCreateStrategy
import xyz.nietongxue.kanglang.runtime.Engine
import xyz.nietongxue.kanglang.runtime.LogService


@SpringBootTest(classes = [Config::class, Application::class])
class ReviewerTest {
    @Autowired
    var engine: Engine? = null

    @Autowired
    var logService: LogService? = null

    @Autowired
    var domain: Domain? = null

    @Test
    fun testReviewer() {
        logService!!.printLogs = true
        (domain!! as CoderDomain).map["lazyToday"] = false
        engine!!.startCase(
            CaseCreateStrategy.CaseName(
                "code with review", mapOf("codeCommitted" to false)
            )
        )
        Thread.sleep(5000)
        val logs = logService!!.logs
        val reviewed = logs.filter {
            it.message.let {
                it is ActorLogItem.GenericItem
                        && it.string.contains("code is ok")
            }
        }

        Assertions.assertThat(reviewed.size).isEqualTo(1)
    }

    @Test
    fun testReviewerLazyCoder() {
        logService!!.printLogs = true
        (domain!! as CoderDomain).map["lazyToday"] = true
        engine!!.startCase(
            CaseCreateStrategy.CaseName(
                "code with review", mapOf("codeCommitted" to false)
            )
        )
        Thread.sleep(5000)
        val logs = logService!!.logs
        val reviewed = logs.filter {
            it.message.let {
                it is ActorLogItem.GenericItem
                        && it.string.contains("code is ok")
            }
        }

        Assertions.assertThat(reviewed.size).isEqualTo(0)
    }

}
