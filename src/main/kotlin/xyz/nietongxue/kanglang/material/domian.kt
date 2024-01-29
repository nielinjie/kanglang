package xyz.nietongxue.kanglang.material

import org.springframework.stereotype.Component

interface Domain {
    fun get(name: String): Any
    fun set(name: String, value: Any)
}

