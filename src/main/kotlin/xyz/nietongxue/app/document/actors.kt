package xyz.nietongxue.app.document

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import xyz.nietongxue.kanglang.actor.Actor
import xyz.nietongxue.kanglang.actor.Effect
import xyz.nietongxue.kanglang.actor.FetchStrategy
import xyz.nietongxue.kanglang.actor.TouchResult
import xyz.nietongxue.kanglang.runtime.LogService
import xyz.nietongxue.kanglang.runtime.Task
import xyz.nietongxue.kanglang.runtime.caseVariable
import xyz.nietongxue.kanglang.runtime.variable

@Component
class Splitter(@Autowired override val logService: LogService) : Actor {
    override val name: String = "splitter"

    override fun fetch(): FetchStrategy {
        return FetchStrategy.ByRoleName("splitter")
    }

    override fun touch(task: Task): TouchResult {
        val docTitle: String = task.caseVariable("docTitle")!!
        return TouchResult.Completed(
            task,
            Effect.CaseVariable(task, "docParts", listOf("part1", "part2").map { "$docTitle-$it" })
        )
    }


}

@Component
class Writer(@Autowired override val logService: LogService) : Actor {
    override val name: String = "writer"

    override fun fetch(): FetchStrategy {
        return FetchStrategy.ByRoleName("writer")
    }

    override fun touch(task: Task): TouchResult {

//        studyVariables(task as CmmnTask) // moved to test source root
        val subTitle :String= task.variable("part")!!
        val subIndex = (task.variable<Int>("partIndex"))!!.toString()
        log("doc - $subIndex - rewritten")
        return TouchResult.Completed(task, Effect.CaseVariable(task, "doc - $subIndex", "$subIndex - rewritten"))
    }


}

