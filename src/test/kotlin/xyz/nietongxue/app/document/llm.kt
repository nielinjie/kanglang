package xyz.nietongxue.app.document

import dev.langchain4j.model.openai.OpenAiChatModel
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest


@SpringBootTest(classes = [Config::class, Application::class])
class LLMTest {
    @Autowired
    var config: LLMConfig? = null

    @Test
    fun test() {
        println(config)
        println(config!!.base)
        println(config!!.key)
    }

    @Test
    fun hello() {
        val model = OpenAiChatModel.builder()
            .apiKey(config!!.key).baseUrl(config!!.base)
            .build()
        val answer = model.generate("hello world")
        println(answer)
    }
}