package xyz.nietongxue.app.document

import xyz.nietongxue.kanglang.define.*

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
            task("merge"){
                candidate = Candidate.Group("merger")
                entry("from write") {
                    on(completeOf("write"))
                    guard("var:get(docParts).size() == var:get(docRewrittenParts).size()")
                }
            }
        }
    }
}


fun StageBuilder.splitAndProcessAndMerge(processName:String){
    task("split") {
        candidate = Candidate.Group("splitter")
    }
    task(processName) {
        candidate = Candidate.Group(processName)
        repeat = Repeat.ByCollection("${processName}Parts")
        entry("from split") {
            on(completeOf("split"))
        }
    }
    task("merge"){
        candidate = Candidate.Group("merger")
        entry("from process") {
            on(completeOf(processName))
            guard("var:get(${processName}Parts).size() == var:get(${processName}ResultParts).size()")
        }
    }
}