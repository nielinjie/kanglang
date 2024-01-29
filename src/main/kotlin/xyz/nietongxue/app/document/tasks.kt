package xyz.nietongxue.app.document

import xyz.nietongxue.kanglang.define.*

val define = caseDefine {
    case("new doc") {
        stage("doc") {
            splitAndProcessAndMerge("write")
        }
    }
}


fun StageBuilder.splitAndProcessAndMerge(
    processName: String,
    splitName: String = "split",
    mergeName: String = "merge"
) {
    val collectionName = "${processName}Parts"
    val resultCollectionName = "${processName}ResultParts"
    task(splitName) {
        candidate = Candidate.Group(splitName)
    }
    task(processName) {
        candidate = Candidate.Group(processName)
        repeat = Repeat.ByCollection("${processName}Parts")
        entry("from split") {
            on(completeOf(splitName))
        }
    }
    task(mergeName) {
        candidate = Candidate.Group(mergeName)
        entry("from process") {
            on(completeOf(processName))
            guard("var:get($collectionName).size() == var:get($resultCollectionName).size()")
        }
    }
}