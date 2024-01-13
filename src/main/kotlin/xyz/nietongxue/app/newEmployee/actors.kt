package xyz.nietongxue.app.newEmployee

import xyz.nietongxue.kanglang.actor.*
import xyz.nietongxue.kanglang.runtime.Task


val newEmployeeActor = object : Actor {


    val fill = forTaskName(FILL_IN_PAPERWORK) { task ->
        task.caseVariables()["email"]?.let {
            println("my email is $it")
            TouchResult.Completed(task, Effect.CaseVariable(task, "paper", "myEmail" to it))
        } ?: return@forTaskName TouchResult.Error(task, "can't find email in case")
    }
    val newTrain = simpleTask(NEW_STARTER_TRAINING)
    override val name: String
        get() = "newEmployee"

    override fun getTask(): GetTaskStrategy {
        return GetTaskStrategy.ByUserName("johnDoe")
    }

    override fun touch(task: Task): TouchResult {
        println("as new employee, doIt: ${task.name}")
        return when {
            fill.matchTask(task) -> fill.touch(task)
            newTrain.matchTask(task) -> newTrain.touch(task)
            else -> error("unknown task: ${task.name}")
        }
    }

    override fun choose(tasks: List<Task>): ChooseResult {
        return tasks.filterNot { it.name == REJECT_JOB }.firstOrNull()?.let { ChooseResult.Chosen(it) }
            ?: ChooseResult.NotChosen()
    }

}

val hrActor = object : Actor {
    val create = forTaskName(CREATE_EMAIL_ADDRESS) { task ->
        TouchResult.Completed(task, Effect.CaseVariable(task, "email", "john@new.com"))
    }
    val agree = simpleTask(AGREE_START_DATE)
    val allocate = simpleTask(ALLOCATE_OFFICE)
    val send = simpleTask(SENDING_JOINING_LETTER_TO_CANDIDATE)
    override val name: String = "hr"

    override fun getTask(): GetTaskStrategy {
        return GetTaskStrategy.ByRoleName("hr")
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
}
val actors = listOf(newEmployeeActor, hrActor)
