package xyz.nietongxue.app;

import org.flowable.cmmn.api.CmmnTaskService
import org.flowable.task.api.Task
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import xyz.nietongxue.kanglang.Actor
import xyz.nietongxue.kanglang.CmmnTask
import xyz.nietongxue.kanglang.GetTask


fun main() {
    runApplication<Application>()
}

@EnableScheduling
@SpringBootApplication(scanBasePackages = ["xyz.nietongxue"])
class Application() {


    @Bean
    fun actors(taskService: CmmnTaskService): List<Actor> {
        return listOf(
            object : Actor {
                override fun getTask(): GetTask {
                    return GetTask.ByUserName("johnDoe")
                }
            },
            object : Actor {
                override fun getTask(): GetTask {
                    return GetTask.ByRoleName("hr")
                }
            }
        )
    }
}


@Component
class ScheduleService(@Autowired val actors: List<Actor>, @Autowired val taskService: CmmnTaskService) {
    @Scheduled(fixedDelay = 10000)
    fun schedule() {
        println("schedule")
        actors.forEach {
            when (val task = it.getTask()) {
                is GetTask.ByUserName -> {
                    val tasks = taskService.createTaskQuery().taskAssignee(task.userName).list()
                    println("task for ${task.userName} -- ")
                    tasks!!.print()

                    val first = CmmnTask(tasks.first()) //TODO: 选择一个task。

                    it.doIt(first)
                }

                is GetTask.ByRoleName -> {
                    val tasks = taskService.createTaskQuery().taskCandidateGroup(task.roleName).list()
                    println("task for ${task.roleName} -- ")
                    tasks!!.print()
                }
            }
        }
    }
}


fun List<Task>.print() {
    this.forEach {
        println("${it.name} - ${it.assignee} - ${it.owner}")
    }
}