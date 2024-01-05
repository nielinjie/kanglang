package xyz.nietongxue.kanglang.runtime

interface Task{
    fun variables():Map<String,Any>
    fun caseVariables():Map<String,Any>
    val name:String
}