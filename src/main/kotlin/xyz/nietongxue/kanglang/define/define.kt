package xyz.nietongxue.kanglang.define

interface HasCriterion {
    var entry: SentryDefine?
    var exit: SentryDefine?

}

data class CaseDefine(
    val name: String,
    val stages: List<StageDefine>,
    val tasks: List<TaskDefine>
) : HasCriterion {
    override var entry: SentryDefine? = null
    override var exit: SentryDefine? = null
}

data class StageDefine(val name: String, val tasks: List<TaskDefine>) : HasCriterion {
    override var entry: SentryDefine? = null
    override var exit: SentryDefine? = null
}

data class TaskDefine(
    val name: String,
) : HasCriterion {
    override var entry: SentryDefine? = null
    override var exit: SentryDefine? = null
}

data class SentryDefine(val name: String, val onEvents: List<OnEvent>) {
}

data class OnEvent(val planItemOn: String, val event: SentryEvent)
interface SentryEvent {
    object Complete : SentryEvent
}
fun completeOf(planItemOn: String) = OnEvent(planItemOn, SentryEvent.Complete)