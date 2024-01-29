package xyz.nietongxue.app.newEmployee

import org.springframework.stereotype.Component
import xyz.nietongxue.kanglang.material.Domain

class EmployeeDomain:Domain {
    var map = mutableMapOf<String,Any>()
    override fun get(name: String): Any {
        return map[name]!!
    }

    override fun set(name: String, value: Any) {
        TODO("Not yet implemented")
    }
}