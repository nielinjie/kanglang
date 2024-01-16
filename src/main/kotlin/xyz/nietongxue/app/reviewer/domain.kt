package xyz.nietongxue.app.reviewer

import org.springframework.stereotype.Component
import xyz.nietongxue.kanglang.material.Domain

class CoderDomain : Domain  {
    val map : MutableMap<String,Any> = mutableMapOf()
    override fun get(name: String): Any {
        return map[name]!!
    }
}