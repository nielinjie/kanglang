package xyz.nietongxue.app.reviewer

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import xyz.nietongxue.common.log.Log
import xyz.nietongxue.kanglang.actor.*
import xyz.nietongxue.kanglang.material.Domain
import xyz.nietongxue.kanglang.runtime.LogService
import xyz.nietongxue.kanglang.runtime.Task

@Component
class CodeActor(
    @Autowired override val logService: LogService,
    @Autowired val coderDomain: Domain
) : Actor, WithLog {
    override val name: String = "coder"
    override fun fetch(): FetchStrategy {
        return FetchStrategy.ByRoleName("coder")
    }

    override fun touch(task: Task): TouchResult {

        val lazyToday = coderDomain.get("lazyToday") as Boolean
        logService.log(Log(ActorLogItem.GenericItem("coding kangchi kangchi")))
        return TouchResult.Completed(
            task,
            Effect.CaseVariable(task, "codeCommitted", !lazyToday)
        )
    }
}


@Component
class ReviewActor(@Autowired override val logService: LogService) : Actor, WithLog {
    override val name: String = "reviewer"
    override fun fetch(): FetchStrategy {
        return FetchStrategy.ByRoleName("reviewer")
    }

    override fun touch(task: Task): TouchResult {
        logService.log(Log(ActorLogItem.GenericItem("code is ok")))
        return TouchResult.Completed(task)
    }

}