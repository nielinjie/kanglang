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
            attribute("name", caseDefine.name)
            attribute("id", defineModelId(caseDefine.name))
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
                        attribute("name", it.name)
                        attribute("id", defineModelId(it.name))
                        it.tasks.forEach {
                            "planItem" {
                                attribute("id", planItemId(it.name))
                                attribute("definitionRef", defineModelId(it.name))
                                if (it.entry != null) {
                                    "entryCriterion" {
                                        attribute("id", entryCriterionId(it.entry!!))
                                        attribute("sentryRef", sentryId(it.entry!!.name))
                                    }
                                }
                                if(it.exit!=null){
                                    "exitCriterion" {
                                        attribute("id", exitCriterionId(it.exit!!))
                                        attribute("sentryRef", sentryId(it.exit!!.name))
                                    }
                                }
                            }
                        }
                        it.tasks.filter { it.entry != null }.forEach {
                            val sentryId = sentryId(it.entry!!.name)
                            "sentry" {
                                attribute("id", sentryId)
                                it.entry!!.onEvents.forEach {
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
                        it.tasks.filter { it.exit != null }.forEach {
                            val sentryId = sentryId(it.exit!!.name)
                            "sentry" {
                                attribute("id", sentryId)
                                it.exit!!.onEvents.forEach {
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
                        it.tasks.forEach {
                            "humanTask" {
                                attribute("name", it.name)
                                attribute("id", defineModelId(it.name))
                            }
                        }
                    }
                }
            }

        }
    }

}