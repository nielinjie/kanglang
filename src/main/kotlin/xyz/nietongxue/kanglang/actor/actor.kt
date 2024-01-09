package xyz.nietongxue.kanglang.actor

import xyz.nietongxue.kanglang.runtime.Task


interface GetTaskStrategy {
    class ByUserName(val userName: String) : GetTaskStrategy
    class ByRoleName(val roleName: String) : GetTaskStrategy
}


open class TouchResult(val task: Task, val effects: List<Effect>) {
    class Completed(task: Task, vararg effects: Effect) : TouchResult(task, effects.toList())
    class NotCompleted(task: Task, vararg effect: Effect) : TouchResult(task, effect.toList())
    class Error(task: Task, val log: String) : TouchResult(task, emptyList())
}

interface Effect {
    class TaskVariable(val task: Task, val name: String, val value: Any) : Effect
    class CaseVariable(val task: Task, val name: String, val value: Any) : Effect
    class DomainVariable(val task: Task, val name: String, val value: Any) : Effect
}

interface ChooseResult {
    class Chosen(val task: Task) : ChooseResult
    class NotChosen() : ChooseResult
}

interface Actor {
    val name: String
    fun getTask(): GetTaskStrategy
    fun touch(task: Task): TouchResult
    fun choose(tasks: List<Task>): ChooseResult {
        return tasks.firstOrNull()?.let { ChooseResult.Chosen(it) } ?: ChooseResult.NotChosen()
    }
}








