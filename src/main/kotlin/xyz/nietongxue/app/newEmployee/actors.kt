package xyz.nietongxue.app.newEmployee

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import xyz.nietongxue.kanglang.actor.*
import xyz.nietongxue.kanglang.runtime.LogService
import xyz.nietongxue.kanglang.runtime.Task

@Component
class NewEmployeeActor(
    @Autowired
    override val logService: LogService
) : Actor {


    val fill = forTaskName(FILL_IN_PAPERWORK) { task ->
        task.caseVariables()["email"]?.let {
            log("my email is $it")
            TouchResult.Completed(task, Effect.CaseVariable(task, "paper", "myEmail" to it))
        } ?: return@forTaskName TouchResult.Error(task, "can't find email in case")
    }
    val newTrain = simpleTask(NEW_STARTER_TRAINING)
    override val name: String
        get() = "newEmployee"

    override fun fetch(): FetchStrategy {
        return FetchStrategy.ByUserName("johnDoe")
    }

    override fun touch(task: Task): TouchResult {
        log(ActorLogItem.GoingToDoItem(task.name, name))
        return matchOneByOne(task, listOf(fill, newTrain))
    }

    override fun choose(tasks: List<Task>): ChooseResult {
        if (tasks.isEmpty()) return ChooseResult.NothingToChose
        return tasks.filterNot { it.name == REJECT_JOB }.firstOrNull()?.let { ChooseResult.ChosenOne(it) }
            ?: ChooseResult.NotChosen
    }

}

@Component
class HrActor(
    @Autowired
    override val logService: LogService
) : Actor {


    val create = forTaskName(CREATE_EMAIL_ADDRESS) { task ->
        TouchResult.Completed(task, Effect.CaseVariable(task, "email", "john@new.com"))
    }
    val agree = simpleTask(AGREE_START_DATE)
    val allocate = simpleTask(ALLOCATE_OFFICE)
    val send = simpleTask(SENDING_JOINING_LETTER_TO_CANDIDATE)
    override val name: String = "hr"

    override fun fetch(): FetchStrategy {
        return FetchStrategy.ByRoleName("hr")
    }

    override fun touch(task: Task): TouchResult {
        log(ActorLogItem.GoingToDoItem(task.name, name))
        return matchOneByOne(task, listOf(create, agree, allocate, send))
    }


}
