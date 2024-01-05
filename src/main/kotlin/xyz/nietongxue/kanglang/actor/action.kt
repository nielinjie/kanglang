package xyz.nietongxue.kanglang.actor

import xyz.nietongxue.kanglang.runtime.Task


abstract class Action(val name: String, val taskName: String) {
    fun matchTask(task: Task): Boolean {
        return task.name == taskName
    }

    abstract fun touch(task: Task): TouchResult
}

fun forTaskName(taskName: String, actionFun: (Task) -> TouchResult): Action {
    return object : Action(taskName, taskName) {
        override fun touch(task: Task): TouchResult {
            return actionFun(task)
        }

    }
}