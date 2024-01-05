package xyz.nietongxue.app

import xyz.nietongxue.kanglang.actor.*
import xyz.nietongxue.kanglang.runtime.Task

val actors = listOf(object : Actor {

    val reject = forTaskName("Reject job") { task ->
        TouchResult.NotCompleted(task)
    }

    val fill = forTaskName("Fill in paperwork") { task ->
        task.caseVariables()["email"]?.let {
            println("my email is $it")
            TouchResult.Completed(task, Effect.CaseVariable(task, "paper", "myEmail" to it))
        } ?: return@forTaskName TouchResult.Error(task, "can't find email in case")
    }
    val newTrain = forTaskName("New starter training") { task ->
        TouchResult.Completed(task)
    }
    override val name: String
        get() = "newEmployee"

    override fun getTask(): GetTask {
        return GetTask.ByUserName("johnDoe")
    }

    override fun touch(task: Task): TouchResult {
        println("as new employee, doIt: ${task.name}")
        return when {
            reject.matchTask(task) -> reject.touch(task)
            fill.matchTask(task) -> fill.touch(task)
            newTrain.matchTask(task) -> newTrain.touch(task)
            else -> error("unknown task: ${task.name}")
        }
    }

    override fun choose(tasks: List<Task>): ChooseResult {
        return tasks.firstOrNull {
            !reject.matchTask(it)
        }?.let { ChooseResult.Chosen(it) } ?: ChooseResult.NotChosen()
    }

}, object : Actor {
    val create = forTaskName("Create email address") { task ->
        TouchResult.Completed(task, Effect.CaseVariable(task, "email", "john@new.com"))
    }
    val agree = forTaskName("Agree start date") { task ->
        TouchResult.Completed(task)
    }
    val allocate = forTaskName("Allocate office") { task ->
        TouchResult.Completed(task)
    }
    val send = forTaskName("Send joining letter to candidate") { task ->
        TouchResult.Completed(task)
    }
    override val name: String
        get() = "hr"

    override fun getTask(): GetTask {
        return GetTask.ByRoleName("hr")
    }

    override fun touch(task: Task): TouchResult {

        println("as hr, doIt: ${task.name}")
        return when {
            create.matchTask(task) -> create.touch(task)
            agree.matchTask(task) -> agree.touch(task)
            allocate.matchTask(task) -> allocate.touch(task)
            send.matchTask(task) -> send.touch(task)
            else -> error("unknown task: ${task.name}")
        }
    }
})
