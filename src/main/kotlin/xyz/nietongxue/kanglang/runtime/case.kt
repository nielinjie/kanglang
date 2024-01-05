package xyz.nietongxue.kanglang.runtime

interface Case{
    val id:String
    fun variables():Map<String,Any>
}

interface CaseListener{
    fun onCaseStart(case:Case)
    fun onCaseEnd(case:Case)
}