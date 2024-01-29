package xyz.nietongxue.app.develop

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import xyz.nietongxue.kanglang.actor.*
import xyz.nietongxue.kanglang.material.Domain
import xyz.nietongxue.kanglang.material.Material
import xyz.nietongxue.kanglang.runtime.LogService
import xyz.nietongxue.kanglang.runtime.Task

@Component
class DesignActor(@Autowired override val logService: LogService, @Autowired val domain: Domain) : Actor, WithLog,
    SingleAction("design") {
    override fun touch(task: Task): TouchResult {
        val lib = (domain as LibraryAsDomain).materialLib
        val requirement = lib.get(selectorPhaseIs("require")).first()
        return TouchResult.Completed(
            task,
            Effect.DomainVariable(task, "phase:design", "requirement is matched - \n${requirement.content}")
        )
    }

}