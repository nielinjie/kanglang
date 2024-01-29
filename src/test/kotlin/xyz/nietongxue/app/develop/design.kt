package xyz.nietongxue.app.develop

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import xyz.nietongxue.kanglang.material.Domain
import xyz.nietongxue.kanglang.material.Material
import xyz.nietongxue.kanglang.runtime.CaseCreateStrategy
import xyz.nietongxue.kanglang.runtime.Engine
import xyz.nietongxue.kanglang.runtime.LogService


@SpringBootTest(classes = [Config::class, Application::class])
class DesignTest {
    @Autowired
    var engine: Engine? = null

    @Autowired
    var logService: LogService? = null

    @Autowired
    var domain: Domain? = null

    @Test
    fun design() {
        logService!!.printLogs = true
        domain!!.set("phase:require", "requirement document here")
        engine!!.startCase(
            CaseCreateStrategy.CaseName(
                "design case",
                mapOf()
            )
        )
        Thread.sleep(3000)
        val result = domain!!.get("phase:design")
        (result as List<Material>).also {
            assert(it.size == 1)
            assert(it[0].content.startsWith("requirement is matched -"))
        }

    }
    @Test
    fun designTrue(){
        logService!!.printLogs = true
    }
}