package xyz.nietongxue.kanglang.runtime

import org.flowable.cmmn.api.CmmnRuntimeService
import org.flowable.cmmn.api.event.FlowableCaseEndedEvent
import org.flowable.cmmn.api.event.FlowableCaseStartedEvent
import org.flowable.common.engine.api.delegate.event.FlowableEvent
import org.flowable.common.engine.api.delegate.event.FlowableEventListener


class CmmnTask(val raw: org.flowable.task.api.Task, val caseId: String, val runtimeService: CmmnRuntimeService) : Task {
    override fun variables(): Map<String, Any> {
        return runtimeService.getLocalVariables(raw.id)
    }

    override fun caseVariables(): Map<String, Any> {
        return runtimeService.getVariables(caseId)
    }

    override val name: String
        get() = raw.name

}

class CmmnCase(val raw: org.flowable.cmmn.api.runtime.CaseInstance, val runtimeService: CmmnRuntimeService) : Case {

    override fun variables(): Map<String, Any> {
        return runtimeService.getVariables(this.id)
    }


    override val id: String
        get() = raw.id

}

val runtimeListener = object : FlowableEventListener {
    override fun onEvent(event: FlowableEvent?) {
//                    println("event: $event")
        when (event) {
            is FlowableCaseStartedEvent -> println("case started: ${event.scopeId}")
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