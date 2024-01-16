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
        //TODO 检查各种标识符是否引用正确。taskName、stageName……
        //TODO 变量层面是否需要检查？
        return caseDefine!!
    }
}

class CaseBuilder : CBuilder {
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
            it.criterion = this.sentryDefines
        }
    }

    override var sentryDefines: MutableList<SentryDefine> = mutableListOf()

}

class StageBuilder : CBuilder {
    private val taskDefines: MutableList<TaskDefine> = mutableListOf()
    var name: String? = null
    var repeat :Repeat = Repeat.No

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
            it.criterion = this.sentryDefines
            it.repeat = this.repeat
        }
    }

    override var sentryDefines: MutableList<SentryDefine> = mutableListOf()

}

interface CBuilder {
    var sentryDefines: MutableList<SentryDefine>

    fun entry(name: String, vararg onEvent: OnEvent) {
        this.entry(name, null, *onEvent)
    }

    fun entry(name: String, guard: String? = null, vararg onEvent: OnEvent) {
        this.sentryDefines.add(SentryDefine.EntrySentry(name, onEvent.toList(), guard?.let { ExpressionDefine(it) }))
    }

    fun exit(name: String, vararg onEvent: OnEvent) {
        this.sentryDefines.add(SentryDefine.ExitSentry(name, onEvent.toList(), null))
    }


    fun exit(name: String, planItemOn: String, event: SentryEvent) {
        this.exit(name, OnEvent(planItemOn, event))
    }

    fun entry(name: String, planItemOn: String, event: SentryEvent) {
        this.entry(name, OnEvent(planItemOn, event))
    }

    fun entry(name: String, init: SentryDefineBuilder.() -> Unit) {
        val builder = SentryDefineBuilder().also {
            it.name = name
            it.init()
        }
        this.sentryDefines.add(builder.build())
    }
    fun exit(name: String, init: SentryDefineBuilder.() -> Unit) {
        val builder = SentryDefineBuilder().also {
            it.type = "exit"
            it.name = name
            it.init()
        }
        this.sentryDefines.add(builder.build())
    }

}


interface CandidateBuilder {
    var candidate: Candidate?
}

class TaskBuilder : CBuilder, CandidateBuilder {
    var name: String? = null
    var repeat: Repeat = Repeat.No
    fun build(): TaskDefine {
        return TaskDefine(name!!).also {
            it.criterion = this.sentryDefines
            it.candidate = this.candidate
            it.repeat = this.repeat

        }
    }

    override var sentryDefines: MutableList<SentryDefine> = mutableListOf()

    override var candidate: Candidate? = null
}


class SentryDefineBuilder {
    var type: String = "entry"
    var name: String? = null
    var onEvents: MutableList<OnEvent> = mutableListOf()
    var ifPart: ExpressionDefine? = null
    fun build(): SentryDefine {
        return when (type) {
            "entry" -> SentryDefine.EntrySentry(name!!, onEvents, ifPart)
            "exit" -> SentryDefine.ExitSentry(name!!, onEvents, ifPart)
            else -> throw IllegalArgumentException("type $type not supported")
        }
    }
    fun on(event:OnEvent){
        this.onEvents.add(event)
    }
    fun guard(guard:String){
        this.ifPart = ExpressionDefine(guard)
    }
}