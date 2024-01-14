package xyz.nietongxue.app.newEmployee

import org.springframework.stereotype.Component
import xyz.nietongxue.kanglang.material.Domain

@Component
class EmployeeDomain:Domain {
    var map = mutableMapOf<String,Any>()
    override fun get(name: String): Any {
        return map[name]!!
    }
}