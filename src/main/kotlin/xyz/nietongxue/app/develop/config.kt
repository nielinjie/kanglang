package xyz.nietongxue.app.develop

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource

@Configuration
@PropertySource("classpath:secret.properties")
class LLMConfig(
    @Value("\${openAi.base}")
    val base: String,
    @Value("\${openAi.key}")
    val key: String
)