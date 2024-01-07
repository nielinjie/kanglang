package xyz.nietongxue.kanglang.define

data class DefineByResource(
    val id: String,
    val resourcePath: String
)
data class DefineByString(
    val id: String,
    val content: String
)
data class CaseDefine(
    val name:String,
    val stages: List<StageDefine>
) {
}

data class StageDefine(val name:String,val tasks: List<TaskDefine>) {
}

data class TaskDefine(val name: String)
