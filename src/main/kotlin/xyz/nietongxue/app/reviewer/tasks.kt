package xyz.nietongxue.app.reviewer

import xyz.nietongxue.kanglang.define.*

val define = caseDefine {
    case("code with review") {
        stage("code stage") {
            task("code") {
//                entry("first", OnEvent("code stage",SentryEvent.Start ))
//                entry("from review", OnEvent("review",SentryEvent.Complete),)//if not pass
                this.candidate = Candidate.Group("coder")
//                this.repeatable = true
            }
            task("review") {
                entry("from coding", "codeCommitted==true", OnEvent("code", SentryEvent.Complete))
                this.candidate = Candidate.Group("reviewer")
//                this.repeatable = true
            }
        }
    }
}
val repeatCodingDefine = caseDefine{
    case ("code repeat"){
        stage("code stage"){
            task("code"){
                candidate = Candidate.Group("coder")
                repeat = Repeat.Yes()
            }
        }
    }
}
val circleDefine = caseDefine {
    case("code till pass") {
        stage("code stage") {
            task("code") {
                entry("first coding") {
                    on(startOf("code stage"))
                }
                entry("from review") {
                    on(completeOf("review"))
                    guard("codePass==false")
                }
                candidate = Candidate.Group("coder")
                repeat = Repeat.Yes()
            }
            task("review") {
                entry("from coding") {
                    on(completeOf("code"))
                    guard("codeCommitted==true")
                }
                candidate = Candidate.Group("reviewer")
                repeat = Repeat.Yes()
            }
            exit("review passed") {
                on(completeOf("review"))
                guard("codePass==true")
            }
        }
    }
}

