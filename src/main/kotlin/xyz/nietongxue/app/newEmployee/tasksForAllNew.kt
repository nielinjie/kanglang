package xyz.nietongxue.app.newEmployee

import xyz.nietongxue.kanglang.define.Candidate
import xyz.nietongxue.kanglang.define.caseDefine
import xyz.nietongxue.kanglang.define.completeOf

val allUserNew = Candidate.Group("newEmployee")


val defineForAllNew = caseDefine {
    case("new employee case") {
        stage(PRIOR_TO_STARTING) {
            task(CREATE_EMAIL_ADDRESS) {
                candidate = hrGroup
            }
            task(ALLOCATE_OFFICE) {
                candidate = hrGroup
            }
            task(AGREE_START_DATE) {
                candidate = hrGroup
            }
            task(SENDING_JOINING_LETTER_TO_CANDIDATE) {
                candidate = hrGroup
                entry(
                    "sending",
                    completeOf(AGREE_START_DATE),
                    completeOf(ALLOCATE_OFFICE),
                    completeOf(CREATE_EMAIL_ADDRESS)
                )
            }
        }
        stage("after starting") {
            entry("start", completeOf(PRIOR_TO_STARTING))
            task(FILL_IN_PAPERWORK){
                candidate = allUserNew
            }
            task(NEW_STARTER_TRAINING){
                candidate = allUserNew
            }
        }
        task(REJECT_JOB){
            candidate = allUserNew
        }
        exit("rejected", completeOf(REJECT_JOB))
    }
}