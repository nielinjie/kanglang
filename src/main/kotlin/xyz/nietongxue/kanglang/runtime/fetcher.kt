package xyz.nietongxue.kanglang.runtime

import org.flowable.cmmn.api.CmmnRuntimeService
import org.flowable.cmmn.api.CmmnTaskService
import org.springframework.beans.factory.annotation.Autowired
import xyz.nietongxue.common.log.Log
import xyz.nietongxue.kanglang.actor.*


interface FetcherLogItem {
    data class FetchingForCase(val caseId: String) : FetcherLogItem
    data class TouchDone(val touchResult: TouchResult) : FetcherLogItem
    data class ChosenDone(val chooseResult: ChooseResult) : FetcherLogItem
}

class Fetcher(
    @Autowired val actors: List<Actor>,
    @Autowired val taskService: CmmnTaskService,
    @Autowired val runtimeService: CmmnRuntimeService,
    @Autowired val caseInstanceIds: List<String>,
    @Autowired val logService: LogService
) : Runnable {
    private fun log(logItem: FetcherLogItem) {
        this.logService.log(Log(logItem))
    }

    override fun run() {
        caseInstanceIds.forEach { caseId ->
            log(FetcherLogItem.FetchingForCase(caseId))
            actors.forEach act@{ actor ->
                val tasks = when (val task = actor.getTask()) {
                    is GetTaskStrategy.ByUserName -> {
                        taskService.createTaskQuery().taskCandidateUser(task.userName).caseInstanceId(caseId).list()
                    }

                    is GetTaskStrategy.ByRoleName -> {
                        taskService.createTaskQuery().taskCandidateGroup(task.roleName).caseInstanceId(caseId).list()
                    }

                    else -> error("unknown task: $task")
                }
                if (tasks.isEmpty()) {
                    return@act
                }
                actor.choose(tasks.map { CmmnTask(it, caseId, runtimeService) }).also {
                    log(FetcherLogItem.ChosenDone(it))
                    when (it) {
                        is ChooseResult.ChosenOne -> {
                            val result = actor.touch(it.task)
                            log(FetcherLogItem.TouchDone(result))
                            val effects = result.effects
                            effects.forEach { effect ->
                                doWithEffect(effect)
                            }

                            when (result) {
                                is TouchResult.Completed -> taskService.complete((it.task as CmmnTask).raw.id)
                                is TouchResult.NotCompleted -> return@act
                                is TouchResult.Error -> {
                                    return@act
                                }
                            }
                        }

                        is ChooseResult.ChosenAsync -> TODO()
                        is ChooseResult.ChosenConcurrent -> TODO()
                        is ChooseResult.ChosenSequential -> TODO()
                        is ChooseResult.NotChosen -> return@act
                    }
                }
            }
        }
    }

    private fun doWithEffect(effect: Effect) {
        when (effect) {
            is Effect.TaskVariable -> {
                effect.task as CmmnTask
                taskService.setVariableLocal(effect.task.raw.id, effect.name, effect.value)
            }

            is Effect.CaseVariable -> {
                (effect.task as CmmnTask).also {
                    runtimeService.setVariable(
                        it.caseId, effect.name, effect.value
                    )
                    it.raw.caseVariables
                }
            }
        }
    }
}