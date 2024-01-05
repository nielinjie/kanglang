package xyz.nietongxue.kanglang.define

data class Define(
    val id: String,
    val resource: String
)

data class CaseDefine(
    val id: String,
    val stages: List<StageDefine>
) {
}

data class StageDefine(val id: String,val tasks: List<TaskDefine>) {
}

data class TaskDefine(val id: String)
