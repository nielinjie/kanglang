package xyz.nietongxue.kanglang.define

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.resource.resourceAsString
import io.kotest.matchers.shouldBe

class XMLTest : StringSpec({
    "xml" {
        val define = caseDefine {
            case("case1") {
                stage("stage1") {
                    task("task1") {
                    }
                    task("task2") {
                    }
                }
            }
        }
        pretty(building(define).toString(false)) shouldBe (
            resourceAsString("/testGenerated.cmmn.xml")
        )
    }
    "with sentry"{
        val define = caseDefine {
            case("case1") {
                stage("stage1") {
                    task("task1") {
                    }
                    task("task2") {
                        entry("entry1", "task1", SentryEvent.Complete)
                    }
                }
            }
        }
        pretty(building(define).toString(false)) shouldBe (
            resourceAsString("/testGeneratedWithSentry.cmmn.xml")
        )
    }
})