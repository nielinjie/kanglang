package xyz.nietongxue.app.newEmployee

import io.kotest.core.spec.style.StringSpec
import xyz.nietongxue.kanglang.define.caseDefine
import xyz.nietongxue.kanglang.define.completeOf

class NewEmployeeTest : StringSpec({
    "dsl" {
        val define = caseDefine {
            case("new employee case") {
                stage("prior to starting") {
                    task("create email address")
                    task("allocate office")
                    task("agree start date")
                    task("sending joining letter to candidate") {
                        entry(
                            "sending",
                            completeOf("agree start date"),
                            completeOf("allocate office"),
                            completeOf("create email address")
                        )
                    }
                }
                stage("after starting") {
                    entry("start", completeOf("prior to starting"))
                    task("fill in paperwork")
                    task("new starter training")
                }
                task("reject job")
                exit("rejected", completeOf("reject job"))
            }
        }

    }
})