package xyz.nietongxue.kanglang.actor

import xyz.nietongxue.kanglang.runtime.Task


interface Action {
    fun matchTask(task: Task): Boolean
    val name: String
    fun touch(task: Task): TouchResult
    val passIns: List<PassIn>
}

fun actionForTask(taskName: String, passIns: List<PassIn> = emptyList(), actionFun: (Task) -> TouchResult): Action {
    return object : Action {
        override fun matchTask(task: Task): Boolean {
            return task.name == taskName
        }

        override val name: String = "action for $taskName"

        override fun touch(task: Task): TouchResult {
            return actionFun(task)
        }

        override val passIns: List<PassIn> = passIns


    }
}
fun simpleAction(taskName:String):Action{
    return actionForTask(taskName){
        TouchResult.Completed(it)
    }
}

interface PassIn {
    class TaskVariable(val name: String) : PassIn
    class CaseVariable(val name: String) : PassIn
    class DomainVariable(val name: String) : PassIn
}
