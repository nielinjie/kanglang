package xyz.nietongxue.kanglang.define

data class DefineByResource(
    val id: String,
    val resourcePath: String
)

interface EntryCriterion {
    val entry: SentryDefine?
}

interface ExitCriterion {
    val exit: SentryDefine?
}


data class DefineByString(
    val id: String,
    val content: String
)

data class CaseDefine(
    val name: String,
    val stages: List<StageDefine>
) {
}

data class StageDefine(val name: String, val tasks: List<TaskDefine>) {
}

data class TaskDefine(
    val name: String,
    override val entry: SentryDefine? = null
) : EntryCriterion {

}

data class SentryDefine(val name: String, val onEvents:List<OnEvent>) {
}

data class OnEvent(val planItemOn:String,val event: SentryEvent)
interface SentryEvent {
    object Complete : SentryEvent
}