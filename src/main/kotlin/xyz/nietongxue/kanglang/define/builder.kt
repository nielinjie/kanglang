package xyz.nietongxue.kanglang.define


fun caseDefine(init: CaseDefineBuilder.() -> Unit): CaseDefine {
    val builder = CaseDefineBuilder()
    builder.init()
    return builder.build()
}

class CaseDefineBuilder {
    private var caseDefine: CaseDefine? = null
    fun case(name:String,init: CaseBuilder.() -> Unit) {
        val builder = CaseBuilder().also{
            it.name = name
            it.init()
        }
        this.caseDefine = builder.build()
    }

    fun build(): CaseDefine {
        return caseDefine!!
    }
}

class CaseBuilder {
    private val stageDefines: MutableList<StageDefine> = mutableListOf()
    var name: String? = null
    fun stage(name: String, init: StageBuilder.() -> Unit): StageDefine {
        val builder = StageBuilder().also{
            it.name = name
            it.init()
        }
        val stageDefine = builder.build()
        stageDefines.add(stageDefine)
        return stageDefine
    }

    fun build(): CaseDefine {
        return CaseDefine(this.name!!, stageDefines)
    }
}

class StageBuilder {
    private val taskDefines: MutableList<TaskDefine> = mutableListOf()
    var name: String? = null
    fun task(name: String, init: TaskBuilder.() -> Unit): TaskDefine {
        val builder = TaskBuilder().also {
            it.name = name
            it.init()
        }
        val taskDefine = builder.build()
        taskDefines.add(taskDefine)
        return taskDefine
    }

    fun build(): StageDefine {
        return StageDefine(this.name!!, taskDefines)
    }
}

class TaskBuilder {
    var name: String? = null
    fun build(): TaskDefine {
        return TaskDefine(name ?: "task1")
    }
}
