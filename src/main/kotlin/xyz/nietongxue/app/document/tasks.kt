package xyz.nietongxue.app.document

import xyz.nietongxue.kanglang.define.Candidate
import xyz.nietongxue.kanglang.define.Repeat
import xyz.nietongxue.kanglang.define.caseDefine
import xyz.nietongxue.kanglang.define.completeOf

val define = caseDefine {
    case("new doc") {
        stage("doc") {
            task("split") {
                candidate = Candidate.Group("splitter")
            }
            task("write") {
                candidate = Candidate.Group("writer")
                repeat = Repeat.ByCollection("docParts")
                entry("from split") {
                    on(completeOf("split"))
                }
            }
        }
    }
}