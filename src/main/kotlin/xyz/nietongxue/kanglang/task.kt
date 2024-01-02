package xyz.nietongxue.kanglang

interface Task{
    fun variables():Map<String,Any>
    fun caseVariables():Map<String,Any>
    val name:String
}