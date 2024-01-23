package xyz.nietongxue.kanglang.define

import xyz.nietongxue.common.base.singular


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
    var repeat: Repeat = Repeat.No
}

data class TaskDefine(
    val name: String,
) : HasCriterion, CandidateBuilder {
    override var criterion: List<SentryDefine> = emptyList()
    override var candidate: Candidate? = null
    var repeat: Repeat = Repeat.No

}

sealed interface SentryDefine {
    val name: String
    val onEvents: List<OnEvent>
    val ifPart: ExpressionDefine?

    data class EntrySentry(
        override val name: String, override val onEvents: List<OnEvent>,
        override val ifPart: ExpressionDefine?
    ) : SentryDefine

    data class ExitSentry(
        override val name: String, override val onEvents: List<OnEvent>,
        override val ifPart: ExpressionDefine?
    ) : SentryDefine
}

data class OnEvent(val planItemOn: String, val event: SentryEvent)

data class ExpressionDefine(val expression: String) {
    fun dollar(): String {
        return "\${$expression}"
    }
}

interface SentryEvent {
    object Complete : SentryEvent
    object Start : SentryEvent
}


interface Repeat {
    val condition: ExpressionDefine

    companion object {
        fun itemVariableName(collectionName: String): String {
            return collectionName.singular() + "Item"
        }

        fun indexVariableName(collectionName: String): String {
            return collectionName.singular() + "ItemIndex"
        }
    }

    object No : Repeat {
        override val condition: ExpressionDefine = ExpressionDefine("false")
    }

    class Yes() : Repeat {
        override val condition: ExpressionDefine = ExpressionDefine("true")
        var maxInstance: Int = 1
    }

    class ByCollection(val collectionName: String, val itemVariableName: String, val indexVariableName: String) :
        Repeat {
        constructor(collectionName: String) : this(
            collectionName,
            itemVariableName(collectionName),
            indexVariableName(collectionName)
        )

        override val condition: ExpressionDefine = ExpressionDefine("true")
    }
}

fun completeOf(planItemOn: String) = OnEvent(planItemOn, SentryEvent.Complete)
fun startOf(planItemOn: String) = OnEvent(planItemOn, SentryEvent.Start)