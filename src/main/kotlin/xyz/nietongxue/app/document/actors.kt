package xyz.nietongxue.app.document

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import xyz.nietongxue.kanglang.actor.*
import xyz.nietongxue.kanglang.define.Repeat
import xyz.nietongxue.kanglang.runtime.LogService
import xyz.nietongxue.kanglang.runtime.Task
import xyz.nietongxue.kanglang.runtime.caseVariable
import xyz.nietongxue.kanglang.runtime.variable


@Component
class Splitter(@Autowired override val logService: LogService) : Actor, WithLog, SingleAction("splitter") {
    override fun touch(task: Task): TouchResult {
        val docTitle: String = task.caseVariable("docTitle")!!
        return TouchResult.Completed(
            task,
            Effect.CaseVariable(task, "docParts", listOf("part1", "part2").map { "$docTitle-$it" })
        )
    }


}

@Component
class Writer(@Autowired override val logService: LogService) : Actor, WithLog, SingleAction("writer") {


    override fun touch(task: Task): TouchResult {

//        studyVariables(task as CmmnTask) // moved to test source root
        val subTitle: String = task.variable(Repeat.itemVariableName("docParts"))!!
        val subIndex = (task.variable<Int>(Repeat.indexVariableName("docParts")))!!.toString()
        log("doc - $subIndex - rewritten")
        return TouchResult.Completed(
            task, Effect.CaseVariableCollectionAdd
                (task, "docRewrittenParts", "$subIndex - rewritten")
        )
    }
}

@Component
class Merger(@Autowired override val logService: LogService) : Actor, WithLog, SingleAction("merger") {

    override fun touch(task: Task): TouchResult {
        val docRewrittenParts: List<String> = task.caseVariable("docRewrittenParts")!!
        val docTitle: String = task.caseVariable("docTitle")!!
        val mergedDoc = docRewrittenParts.joinToString("\n")
        log("doc - $docTitle - merged")
        return TouchResult.Completed(task, Effect.CaseVariable(task, "docMerged", mergedDoc))
    }
}

