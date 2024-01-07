package xyz.nietongxue.kanglang.define

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe


class DefineBuilderTest : StringSpec({
    "builder" {
        val define = caseDefine {
            case("case1") {
                stage("stage1") {
                    task("task1") {
                    }
                    task("task2") {
                    }
                }
                stage("stage2") {

                }
            }
        }
        define.stages shouldBe listOf(
            StageDefine(
                "stage1",
                listOf(
                    TaskDefine("task1"),
                    TaskDefine("task2")
                )
            ),
            StageDefine("stage2", listOf())
        )
    }
})