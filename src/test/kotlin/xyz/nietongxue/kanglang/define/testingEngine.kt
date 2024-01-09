package xyz.nietongxue.kanglang.define

import org.flowable.cmmn.api.repository.CmmnDeployment
import org.flowable.cmmn.engine.CmmnEngine
import org.flowable.cmmn.engine.impl.cfg.StandaloneInMemCmmnEngineConfiguration

fun engine(define: CaseDefine, name: String): CmmnEngine {
    val cmmnEngine = StandaloneInMemCmmnEngineConfiguration().buildCmmnEngine()
    val cmmnRepositoryService = cmmnEngine.cmmnRepositoryService
    val cmmnDeployment: CmmnDeployment = cmmnRepositoryService.createDeployment()
        .addString("${name}.cmmn", building(define).toString(false))
        .deploy()
    return cmmnEngine
}