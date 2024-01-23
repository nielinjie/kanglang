package xyz.nietongxue.app.document

import org.flowable.cmmn.api.CmmnRuntimeService
import org.flowable.cmmn.api.CmmnTaskService
import org.flowable.variable.api.persistence.entity.VariableInstance
import xyz.nietongxue.kanglang.runtime.CmmnTask

fun study() {
    println("##########################")
    val (p1, p2) = variables

    val taskId1  = p1.first.raw.id
    val taskId2  = p2.first.raw.id
    println("taskId1 vs taskId2 - ${taskId1 == taskId2} $taskId1 - $taskId2")


    val subScope1 = p1.second.subScopeId
    val subScope2 = p2.second.subScopeId
    println("VariableSS1 vs VariableSS2 - ${subScope1 == subScope2} $subScope1 - $subScope2")


    val taskSS1 = p1.first.raw.subScopeId
    val taskSS2 = p2.first.raw.subScopeId
    println("taskSS1 vs taskSS2 - ${taskSS1 == taskSS2} $taskSS1 - $taskSS2")

    println("VariableSS1 vs taskSS1 - ${subScope1 == taskSS1} $subScope1 - $taskSS1")
    println("VariableSS2 vs taskSS2 - ${subScope2 == taskSS2} $subScope2 - $taskSS2")

    val value1 = p1.second.value
    val value2 = p2.second.value
    println("value1 vs value2 - ${value1 == value2} $value1 - $value2")



    println("##########################")
    // 结论
    // variable's subScopeId is the same as task's subScopeId
    // 不同的task，有不同的subScopeId。但subScopeId跟taskId不同。
    // repeat造成的variable，不是task local variable，可以用getVariable得到，不能通过getLocalVariable得到
    // case层的variable的subScope是null。
    // subScopeId 不是 planItemInstanceId。
    // TODO，研究下subScopeId，是某个planItem的id？ use runtimeService 的 planItem query。

}


fun studyVariables(task: CmmnTask){
    val runtimeService: CmmnRuntimeService = task.runtimeService
    val taskService: CmmnTaskService =task.taskService
    println("===NO-LOCAL==== variables from taskService.getVariableInstances:")
    with(taskService.getVariableInstances(task.raw.id)){
        println("size - ${this.size}")
        this.filter{it.key=="part"}.forEach{
            studyV(it,task)
            variables.add(task to it.value)
        }
    }
    println("========")
    println("===LOCAL==== variables from taskService.getVariableInstancesLocal:")
    with(taskService.getVariableInstancesLocal(task.raw.id)){
        println("size - ${this.size}")
        this.forEach{
            studyV(it,task)
        }
    }
    println("========")
}

private fun studyV(it: Map.Entry<String, VariableInstance>, task: CmmnTask) {
    val (k, v) = it
    println("variableName: $k")
    println("subScope: ${v.subScopeId}")
    println("taskId: ${task.raw.id}")
    println("subScopeId==taskId: ${v.subScopeId==task.raw.id}")
    println("taskSubScopeId: ${task.raw.subScopeId}")
    println("subScopeId==taskSubScopeId: ${v.subScopeId==task.raw.subScopeId}")
    println("value: ${v.value}")

}

val variables:MutableList<Pair<CmmnTask, VariableInstance>> = mutableListOf()