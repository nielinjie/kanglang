package xyz.nietongxue.kanglang.define


interface DefineToDeploy {
    data class DefineByResource(
        val id: String,
        val resourcePath: String
    ) : DefineToDeploy

    data class DefineByString(
        val id: String,
        val content: String
    ) : DefineToDeploy
}

fun defineToDeploy(caseDefine: CaseDefine): DefineToDeploy {

    return DefineToDeploy.DefineByString(
        caseDefine.name.lowercase()+".cmmn",
        building(caseDefine).toString(false)
    )
}
