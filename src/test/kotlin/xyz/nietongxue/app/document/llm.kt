package xyz.nietongxue.app.document

import dev.langchain4j.model.openai.OpenAiChatModel
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import xyz.nietongxue.kanglang.runtime.CaseCreateStrategy
import xyz.nietongxue.kanglang.runtime.Engine
import xyz.nietongxue.kanglang.runtime.LogService
import xyz.nietongxue.withStrings


@SpringBootTest(classes = [Config::class, Application::class])
class LLMTest {
    @Autowired
    var config: LLMConfig? = null


    @Test
    fun hello() {
        val model = OpenAiChatModel.builder()
            .apiKey(config!!.key).baseUrl(config!!.base)
            .build()
        val answer = model.generate("hello world")
        assertThat(answer).isNotNull()
    }


    @Autowired
    var engine: Engine? = null

    @Autowired
    var logService: LogService? = null

    @Test
    fun testSplitDoc() {
        logService!!.printLogs = true
        engine!!.startCase(
            CaseCreateStrategy.CaseName(
                "new doc",
                mapOf("docTitle" to "this is a doc title")
            )
        )
        Thread.sleep(10000)
//        study()
        val re = withStrings(logService!!, "rewritten")
        assertThat(re.size).isEqualTo(2)
    }


}
