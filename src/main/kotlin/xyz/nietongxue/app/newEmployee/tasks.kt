package xyz.nietongxue.app.newEmployee

import xyz.nietongxue.kanglang.define.Candidate
import xyz.nietongxue.kanglang.define.caseDefine
import xyz.nietongxue.kanglang.define.completeOf

const val CREATE_EMAIL_ADDRESS = "create email address"

const val ALLOCATE_OFFICE = "allocate office"

const val AGREE_START_DATE = "agree start date"

const val PRIOR_TO_STARTING = "prior to starting"

const val SENDING_JOINING_LETTER_TO_CANDIDATE = "sending joining letter to candidate"

const val REJECT_JOB = "reject job"

const val FILL_IN_PAPERWORK = "fill in paperwork"

const val NEW_STARTER_TRAINING = "new starter training"
val hrGroup = Candidate.Group("hr")
val userNew = Candidate.User("johnDoe")


val define = caseDefine {
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
                candidate = userNew
            }
            task(NEW_STARTER_TRAINING){
                candidate = userNew
            }
        }
        task(REJECT_JOB){
            candidate = userNew
        }
        exit("rejected", completeOf(REJECT_JOB))
    }
}
