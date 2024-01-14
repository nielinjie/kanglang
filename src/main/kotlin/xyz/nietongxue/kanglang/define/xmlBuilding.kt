package xyz.nietongxue.kanglang.define

import org.redundent.kotlin.xml.Namespace
import org.redundent.kotlin.xml.Node
import org.redundent.kotlin.xml.xml
import xyz.nietongxue.common.base.lowerUnderscore

val flowableNs = Namespace("flowable", "http://flowable.org/cmmn")
interface Item{
    class TaskAsItem(val taskDefine: TaskDefine):Item
    class StageAsItem(val stageDefine: StageDefine):Item
}
fun CaseDefine.items():List<Item>{
    return stages.map { Item.StageAsItem(it) } + tasks.map { Item.TaskAsItem(it) }
}
fun StageDefine.items():List<Item>{
    return tasks.map { Item.TaskAsItem(it) }
}
typealias Items = List<Item>

context (Node)
fun Items.planItems() {
    forEach {
        when (it) {
            is Item.TaskAsItem -> {
                "planItem" {
                    attribute("id", planItemId(it.taskDefine.name))
                    attribute("definitionRef", defineModelId(it.taskDefine.name))
                    criterion(it.taskDefine)
                }
            }
            is Item.StageAsItem -> {
                "planItem" {
                    attribute("id", planItemId(it.stageDefine.name))
                    attribute("definitionRef", defineModelId(it.stageDefine.name))
                    criterion(it.stageDefine)
                }
            }
        }
    }
}
context (Node)
fun Items.sentries(){
    forEach {
        when (it) {
            is Item.TaskAsItem -> {
                sentries(it.taskDefine)
            }
            is Item.StageAsItem -> {
                sentries(it.stageDefine)
            }
        }
    }
}
context (Node)
fun Items.defines(){
    forEach {
        when (it) {
            is Item.TaskAsItem -> {
                "humanTask" {
                    attribute("id", defineModelId(it.taskDefine.name))
                    attribute("name", it.taskDefine.name)
                    it.taskDefine.candidate?.also {
                        when (it) {
                            is Candidate.User ->
                                attribute("candidateUsers", it.name, flowableNs)

                            is Candidate.Users ->
                                attribute("candidateUsers", it.names.joinToString(","), flowableNs)

                            is Candidate.Group ->
                                attribute("candidateGroups", it.name, flowableNs)
                        }
                    }
                }
            }
            is Item.StageAsItem -> {
                "stage" {
                    attribute("id", defineModelId(it.stageDefine.name))
                    attribute("name", it.stageDefine.name)
                    it.stageDefine.items().also {
                        it.planItems()
                        it.sentries()
                        it.defines()
                    }
                }
            }
        }
    }
}

fun building(caseDefine: CaseDefine): Node {
    return xml("definitions") {
        xmlns = "http://www.omg.org/spec/CMMN/20151109/MODEL"
        namespace(flowableNs)
        attribute("targetNamespace", "http://www.flowable.org/casedef")
        "case" {
            attribute("id", defineModelId(caseDefine.name))
            attribute("name", caseDefine.name)
            "casePlanModel" {
                attribute("id", planModelId(caseDefine.name))
                caseDefine.items().also {
                    it.planItems()
                    it.sentries()
                    it.defines()
                }
            }

        }
    }

}



fun Node.criterion(hasCriterion: HasCriterion) {

//    if (hasCriterion.entry != null) {
//        "entryCriterion" {
//            attribute("id", entryCriterionId(hasCriterion.entry!!))
//            attribute("sentryRef", sentryId(hasCriterion.entry!!.name))
//        }
//    }
//    if (hasCriterion.exit != null) {
//        "exitCriterion" {
//            attribute("id", exitCriterionId(hasCriterion.exit!!))
//            attribute("sentryRef", sentryId(hasCriterion.exit!!.name))
//        }
//    }
    hasCriterion.criterion.forEach {
        when (it) {
            is SentryDefine.EntrySentry -> {
                "entryCriterion" {
                    attribute("id", entryCriterionId(it))
                    attribute("sentryRef", sentryId(it.name))
                }
            }
            is SentryDefine.ExitSentry -> {
                "exitCriterion" {
                    attribute("id", exitCriterionId(it))
                    attribute("sentryRef", sentryId(it.name))
                }
            }
        }
    }
}
fun Node.sentries(hasCriterion: HasCriterion) {
//    if (hasCriterion.entry != null) {
//        sentry(hasCriterion.entry!!)
//    }
//    if (hasCriterion.exit != null) {
//        sentry(hasCriterion.exit!!)
//    }
    hasCriterion.criterion.forEach {
        sentry(it)
    }
}

fun Node.sentry(sentryDefine: SentryDefine) {
    val sentryId = sentryId(sentryDefine.name)
    "sentry" {
        attribute("id", sentryId)
        sentryDefine.onEvents.forEach {
            "planItemOnPart" {
                attribute("id", "sentry_on_${sentryId}_${it.planItemOn}".lowerUnderscore())
                attribute("sourceRef", planItemId(it.planItemOn))
                when (it.event) {
                    SentryEvent.Complete -> {
                        "standardEvent" {
                            text("complete")
                        }
                    }
                }
            }
        }
    }
}