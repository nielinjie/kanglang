package xyz.nietongxue.app.develop

import xyz.nietongxue.kanglang.define.Candidate
import xyz.nietongxue.kanglang.define.caseDefine

val define = caseDefine {
    case("design case") {
       task("design") {
           candidate = Candidate.Group("design")
       }
    }
}