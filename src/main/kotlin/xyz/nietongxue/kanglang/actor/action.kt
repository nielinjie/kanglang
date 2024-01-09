package xyz.nietongxue.kanglang.actor

import xyz.nietongxue.kanglang.runtime.Task


interface Action {
    fun matchTask(task: Task): Boolean
    val name: String
    fun touch(task: Task): TouchResult
    fun passIns(task: Task): List<PassIn>
}

fun forTaskName(taskName: String, passIn: List<PassIn> = emptyList(), actionFun: (Task) -> TouchResult): Action {
    return object : Action {
        override fun matchTask(task: Task): Boolean {
            return task.name == taskName
        }

        override val name: String
            get() = "action for $taskName"

        override fun touch(task: Task): TouchResult {
            return actionFun(task)
        }

        override fun passIns(task: Task): List<PassIn> {
            return passIn
        }
    }
}

interface PassIn {
    class TaskVariable(val name: String) : PassIn
    class CaseVariable(val name: String) : PassIn
    class DomainVariable(val name: String) : PassIn
}
