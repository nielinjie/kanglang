package xyz.nietongxue.app.develop

import dev.langchain4j.model.input.PromptTemplate
import dev.langchain4j.model.openai.OpenAiChatModel
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import xyz.nietongxue.kanglang.material.Domain

@SpringBootTest(classes = [Config::class, Application::class])
class PromptTest {
    @Autowired
    var config: LLMConfig? = null

    @Autowired
    var domain: Domain? = null

    @Test
    fun prompt() {
        val model = OpenAiChatModel.builder()
            .apiKey(config!!.key).baseUrl(config!!.base)
            .build()
        val domain = (domain!! as LibraryAsDomain).materialLib
        val promptTemplateString = this.javaClass.getResource("/xyz/nietongxue/app/develop/design.prompt")?.readText()
        val promptTemplate = PromptTemplate.from(promptTemplateString)
        val va = mapOf("require" to domain.get(phaseEqual("require")).first().content)
        model.generate(promptTemplate.apply(va).text()).also {
            println(it)
        }
    }
}
