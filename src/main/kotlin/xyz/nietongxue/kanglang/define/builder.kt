package xyz.nietongxue.kanglang.define


fun caseDefine(init: CaseDefineBuilder.() -> Unit): CaseDefine {
    val builder = CaseDefineBuilder()
    builder.init()
    return builder.build()
}

class CaseDefineBuilder {
    private var caseDefine: CaseDefine? = null
    fun case(name: String, init: CaseBuilder.() -> Unit) {
        val builder = CaseBuilder().also {
            it.name = name
            it.init()
        }
        this.caseDefine = builder.build()
    }

    fun build(): CaseDefine {
        return caseDefine!!
    }
}

class CaseBuilder : CBuilder() {
    private val stageDefines: MutableList<StageDefine> = mutableListOf()
    private val taskDefines: MutableList<TaskDefine> = mutableListOf()
    var name: String? = null
    fun stage(name: String, init: StageBuilder.() -> Unit): StageDefine {
        val builder = StageBuilder().also {
            it.name = name
            it.init()
        }
        val stageDefine = builder.build()
        stageDefines.add(stageDefine)
        return stageDefine
    }

    fun task(name: String, init: TaskBuilder.() -> Unit = {}): TaskDefine {
        val builder = TaskBuilder().also {
            it.name = name
            it.init()
        }
        val taskDefine = builder.build()
        taskDefines.add(taskDefine)
        return taskDefine
    }

    fun build(): CaseDefine {
        return CaseDefine(this.name!!, stageDefines, taskDefines).also {
            it.entry = this.entry
            it.exit = this.exit
        }
    }
}

class StageBuilder : CBuilder() {
    private val taskDefines: MutableList<TaskDefine> = mutableListOf()
    var name: String? = null


    fun task(name: String, init: TaskBuilder.() -> Unit = {}): TaskDefine {
        val builder = TaskBuilder().also {
            it.name = name
            it.init()
        }
        val taskDefine = builder.build()
        taskDefines.add(taskDefine)
        return taskDefine
    }

    fun build(): StageDefine {
        return StageDefine(this.name!!, taskDefines).also {
            it.entry = this.entry
            it.exit = this.exit
        }
    }
}

open class CBuilder {
    var entry: SentryDefine? = null
    var exit: SentryDefine? = null

    fun entry(name: String, vararg onEvent: OnEvent) {
        this.entry = SentryDefine(name, onEvent.toList())
    }

    fun exit(name: String, vararg onEvent: OnEvent) {
        this.exit = SentryDefine(name, onEvent.toList())
    }



    fun exit(name: String, planItemOn: String, event: SentryEvent) {
        this.exit(name, OnEvent(planItemOn, event))
    }
    fun entry(name: String, planItemOn: String, event: SentryEvent) {
        this.entry(name, OnEvent(planItemOn, event))
    }

}

class TaskBuilder : CBuilder() {
    var name: String? = null
    fun build(): TaskDefine {
        return TaskDefine(name!!).also {
            it.entry = this.entry
            it.exit = this.exit
        }
    }
}
