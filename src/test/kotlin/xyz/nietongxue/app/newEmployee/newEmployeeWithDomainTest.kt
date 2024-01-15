package xyz.nietongxue.app.newEmployee

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.stereotype.Component
import xyz.nietongxue.kanglang.actor.*
import xyz.nietongxue.kanglang.material.Domain
import xyz.nietongxue.kanglang.runtime.CaseCreateStrategy
import xyz.nietongxue.kanglang.runtime.Engine
import xyz.nietongxue.kanglang.runtime.LogService
import xyz.nietongxue.kanglang.runtime.Task

@SpringBootTest(classes = [AllNewConfig::class, Application::class])
class NewEmployeeDomainTest {


    @Autowired
    var engine: Engine? = null

    @Autowired
    var logService: LogService? = null

    @Autowired
    var domain: Domain? = null

    @Test
    fun testNewEmployee() {
        logService!!.printLogs = true
        (domain!! as EmployeeDomain).map.set("potentialEmployee", listOf("johnDoe", "Alice"))
        engine!!.startCase(
            CaseCreateStrategy.FromDomain(
                "new employee case", emptyMap(), PassIn.DomainVariable("potentialEmployee"), domain!!
            )
        )
        Thread.sleep(10000)
        val logs = logService!!.logs
        val emailGot = logs.filter {
            it.message.let {
                it is ActorLogItem.GenericItem
                        && it.string.contains("my email is")
                        && (it.string.contains("johnDoe@new.com")
                        || it.string.contains("Alice@new.com"))
            }
        }

        assert(emailGot.size == 2)
    }
}

@Component
class AllNewEmployeeActor(
    @Autowired
    override val logService: LogService
) : Actor {

    val fill = actionForTask(FILL_IN_PAPERWORK) { task ->
        task.caseVariables()["email"]?.let {
            log("my email is $it")
            TouchResult.Completed(
                task,
                Effect.CaseVariable(task, "paper", "myEmail" to it)
            )
        } ?: return@actionForTask TouchResult.Error(task, "can't find email in case")
    }
    val newTrain = simpleAction(NEW_STARTER_TRAINING)


    override fun touch(task: Task): TouchResult {
        log(ActorLogItem.GoingToDoItem(task.name, name))
        return matchOneByOne(task, listOf(fill, newTrain))
    }

    override fun choose(tasks: List<Task>): ChooseResult {
        if (tasks.isEmpty()) return ChooseResult.NothingToChose
        return tasks.filterNot { it.name == REJECT_JOB }.firstOrNull()?.let { ChooseResult.ChosenOne(it) }
            ?: ChooseResult.NotChosen
    }


    override val name: String
        get() = "allNewEmployee"

    override fun fetch(): FetchStrategy {
        return FetchStrategy.ByRoleName("newEmployee")
    }


}