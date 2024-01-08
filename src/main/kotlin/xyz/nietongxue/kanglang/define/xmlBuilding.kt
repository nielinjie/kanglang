package xyz.nietongxue.kanglang.define

import org.redundent.kotlin.xml.Namespace
import org.redundent.kotlin.xml.Node
import org.redundent.kotlin.xml.xml

fun building(caseDefine: CaseDefine): Node {
    val flowableNs = Namespace("flowable", "http://flowable.org/cmmn")

    return xml("definitions") {
        xmlns = "http://www.omg.org/spec/CMMN/20151109/MODEL"
        namespace(flowableNs)
        attribute("targetNamespace", "http://www.flowable.org/casedef")
        "case" {
            attribute("id", defineModelId(caseDefine.name))
            attribute("name", caseDefine.name)
            "casePlanModel" {
                attribute("id", planModelId(caseDefine.name))
                caseDefine.stages.forEach {
                    "planItem" {
                        attribute("id", planItemId(it.name))
                        attribute("definitionRef", defineModelId(it.name))
                    }
                }

                caseDefine.stages.forEach {
                    "stage" {
                        attribute("id", defineModelId(it.name))
                        attribute("name", it.name)
                        it.tasks.forEach {
                            "planItem" {
                                attribute("id", planItemId(it.name))
                                attribute("definitionRef", defineModelId(it.name))
                                criterion(it)
                            }
                        }
                        it.tasks.filter { it.entry != null }.forEach {
                            sentry(it.entry!!)
                        }
                        it.tasks.filter { it.exit != null }.forEach {
                            sentry(it.exit!!)
                        }
                        it.tasks.forEach {
                            "humanTask" {
                                attribute("id", defineModelId(it.name))
                                attribute("name", it.name)
                            }
                        }
                    }
                }
            }

        }
    }

}
fun Node.criterion(it:HasCriterion){
    if (it.entry != null) {
        "entryCriterion" {
            attribute("id", entryCriterionId(it.entry!!))
            attribute("sentryRef", sentryId(it.entry!!.name))
        }
    }
    if (it.exit != null) {
        "exitCriterion" {
            attribute("id", exitCriterionId(it.exit!!))
            attribute("sentryRef", sentryId(it.exit!!.name))
        }
    }
}
fun Node.sentry(sentryDefine: SentryDefine){
    val sentryId = sentryId(sentryDefine.name)
    "sentry" {
        attribute("id", sentryId)
        sentryDefine.onEvents.forEach {
            "planItemOnPart" {
                attribute("id", "sentry_on_${sentryId}_${it.planItemOn}")
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