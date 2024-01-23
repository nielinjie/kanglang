package xyz.nietongxue.kanglang.runtime

import org.flowable.cmmn.api.CmmnRuntimeService
import org.flowable.cmmn.api.CmmnTaskService
import org.flowable.cmmn.api.event.FlowableCaseEndedEvent
import org.flowable.cmmn.api.event.FlowableCaseStartedEvent
import org.flowable.common.engine.api.delegate.event.FlowableEvent
import org.flowable.common.engine.api.delegate.event.FlowableEventListener
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import xyz.nietongxue.common.log.Log


class CmmnTask(
    val raw: org.flowable.task.api.Task,
    val caseId: String,
    val runtimeService: CmmnRuntimeService,
    val taskService: CmmnTaskService
) : Task {
    override fun variables(): Map<String, Any> {
        return taskService.getVariables(raw.id)
    }

    override fun caseVariables(): Map<String, Any> {
        return runtimeService.getVariables(caseId)
    }

    override val name: String
        get() = raw.name

    override fun claim(userName: String) {
        taskService.claim(raw.id, userName)
    }

}

class CmmnCase(
    private val raw: org.flowable.cmmn.api.runtime.CaseInstance,
    private val runtimeService: CmmnRuntimeService
) : Case {

    override fun variables(): Map<String, Any> {
        return runtimeService.getVariables(this.id)
    }


    override val id: String
        get() = raw.id

}

@Component
class RuntimeListener(
    @Autowired
    val logService: LogService
) : FlowableEventListener {
    fun log(logItem: EngineLogItem) {
        this.logService.log(Log(logItem))
    }

    override fun onEvent(event: FlowableEvent?) {
        when (event) {
            is FlowableCaseStartedEvent -> log(EngineLogItem.CaseStarted(event.scopeId))
            is FlowableCaseEndedEvent -> println("case ended: ${event.scopeId}")
        }
    }

    override fun isFailOnException(): Boolean {
        return false
    }

    override fun isFireOnTransactionLifecycleEvent(): Boolean {
        return false
    }

    override fun getOnTransaction(): String? {
        return null
    }
}