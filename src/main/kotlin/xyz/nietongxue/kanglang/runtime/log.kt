package xyz.nietongxue.kanglang.runtime

import org.springframework.stereotype.Component
import xyz.nietongxue.common.log.Log

@Component
class LogService {
    var printLogs: Boolean = true
    val logs: MutableList<Log<*>> = mutableListOf()
    fun <M> log(log: Log<M>) {
        logs.add(log)
        if(printLogs){
            println(log.message)
        }
    }
}
