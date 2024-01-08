package xyz.nietongxue.kanglang.define

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import xyz.nietongxue.kanglang.define.SentryEvent.*


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
    "with sentry" {
        val define = caseDefine {
            case("case1") {
                stage("stage1") {
                    task("task1") {
                    }
                    task("task2") {
                        entry("entry1", "task1", Complete)
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
                    TaskDefine(
                        "task2",
                        SentryDefine("entry1", listOf(OnEvent("task1", Complete)))
                    )
                )
            ),
            StageDefine("stage2", listOf())
        )
    }
})