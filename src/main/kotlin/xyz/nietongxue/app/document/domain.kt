package xyz.nietongxue.app.document

import org.springframework.stereotype.Component
import xyz.nietongxue.kanglang.material.Domain

@Component
class DocDomain: Domain {
    var map = mutableMapOf<String,Any>()
    override fun get(name: String): Any {
        return map[name]!!
    }
}