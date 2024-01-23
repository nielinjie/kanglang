package xyz.nietongxue.kanglang.runtime

interface Task {
    fun variables(): Map<String, Any>
    fun caseVariables(): Map<String, Any>
    val name: String

}

inline fun <reified T> Task.variable(name: String): T? {
    return variables()[name] as T?
}
inline fun <reified T> Task.caseVariable(name: String): T? {
    return caseVariables()[name] as T?
}