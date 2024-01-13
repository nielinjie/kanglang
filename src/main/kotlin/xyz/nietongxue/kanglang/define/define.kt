package xyz.nietongxue.kanglang.define

interface HasCriterion {
    var entry: SentryDefine?
    var exit: SentryDefine?
    //TODO 目前做法，sentry 在使用的地方定义。可能会有问题。
}

interface HasTasks{
    val tasks: List<TaskDefine>
}
interface Candidate {
    class User(val name: String) : Candidate
    class Group(val name: String) : Candidate
    class Users(val names: List<String>) : Candidate
}
data class CaseDefine(
    val name: String,
    val stages: List<StageDefine>,
    override val tasks: List<TaskDefine>
) : HasCriterion ,HasTasks{
    override var entry: SentryDefine? = null
    override var exit: SentryDefine? = null
}

data class StageDefine(val name: String, override val tasks: List<TaskDefine>) : HasCriterion,HasTasks {
    override var entry: SentryDefine? = null
    override var exit: SentryDefine? = null
}

data class TaskDefine(
    val name: String,
) : HasCriterion ,CandidateBuilder{
    override var entry: SentryDefine? = null
    override var exit: SentryDefine? = null
    override var candidate: Candidate? =null
}

data class SentryDefine(val name: String, val onEvents: List<OnEvent>) {
}

data class OnEvent(val planItemOn: String, val event: SentryEvent)
interface SentryEvent {
    object Complete : SentryEvent
}
fun completeOf(planItemOn: String) = OnEvent(planItemOn, SentryEvent.Complete)