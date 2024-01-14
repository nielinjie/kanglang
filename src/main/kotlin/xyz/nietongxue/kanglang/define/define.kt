package xyz.nietongxue.kanglang.define


interface HasCriterion {
    var criterion: List<SentryDefine>
    //TODO 目前做法，sentry 在使用的地方定义。可能会有问题。
}

interface HasTasks {
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
) : HasCriterion, HasTasks {
    override var criterion: List<SentryDefine> = emptyList()
}

data class StageDefine(val name: String, override val tasks: List<TaskDefine>) : HasCriterion, HasTasks {
    override var criterion: List<SentryDefine> = emptyList()

}

data class TaskDefine(
    val name: String,
) : HasCriterion, CandidateBuilder {
    override var criterion: List<SentryDefine> = emptyList()
    override var candidate: Candidate? = null
}

sealed interface SentryDefine {
    val name: String
    val onEvents: List<OnEvent>

    data class EntrySentry(override val name: String, override val onEvents: List<OnEvent>) : SentryDefine
    data class ExitSentry(override val name: String, override val onEvents: List<OnEvent>) : SentryDefine
}

data class OnEvent(val planItemOn: String, val event: SentryEvent)
interface SentryEvent {
    object Complete : SentryEvent
    object Start : SentryEvent
}

fun completeOf(planItemOn: String) = OnEvent(planItemOn, SentryEvent.Complete)