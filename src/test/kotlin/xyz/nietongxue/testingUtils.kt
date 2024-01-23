package xyz.nietongxue

import xyz.nietongxue.common.log.Log
import xyz.nietongxue.kanglang.actor.ActorLogItem
import xyz.nietongxue.kanglang.runtime.LogService

fun withStrings(logService: LogService, string: String): List<Log<*>> {
    val logs = logService.logs
    return logs.filter {
        it.message.let {
            it is ActorLogItem.GenericItem
                    && it.string.contains(string)
        }
    }
}