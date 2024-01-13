package xyz.nietongxue.kanglang.actor

import xyz.nietongxue.common.log.Log
import xyz.nietongxue.kanglang.runtime.LogService
import xyz.nietongxue.kanglang.runtime.Task


interface ActorLogItem {
    data class GenericItem(val string: String) : ActorLogItem
    data class GoingToDoItem(val taskName: String, val actorName: String,val string:String="GoingToDo") : ActorLogItem
    data class DoneItem(val taskName: String, val actorName: String,val string:String="Done") : ActorLogItem
}

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
    data class ChosenOne(val task: Task) : ChooseResult
    data class ChosenAsync(val task: Task) : ChooseResult
    data class ChosenConcurrent(val tasks: List<Task>) : ChooseResult
    data class ChosenSequential(val tasks: List<Task>) : ChooseResult
    data object NotChosen : ChooseResult
    data object NothingToChose : ChooseResult
}

interface Actor {
    val logService: LogService
    fun log(logString: String) {
        logService.log(Log(ActorLogItem.GenericItem(logString)))
    }
    fun log(logItem:ActorLogItem){
        logService.log(Log(logItem))
    }
    val name: String
    fun getTask(): GetTaskStrategy
    fun touch(task: Task): TouchResult
    fun choose(tasks: List<Task>): ChooseResult {
        return tasks.firstOrNull()?.let { ChooseResult.ChosenOne(it) } ?: ChooseResult.NotChosen
    }
}








