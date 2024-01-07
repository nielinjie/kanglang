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
        pretty(toXML(define)) shouldBe pretty(
            resourceAsString("/testGenerated.cmmn.xml")
        )
    }
})