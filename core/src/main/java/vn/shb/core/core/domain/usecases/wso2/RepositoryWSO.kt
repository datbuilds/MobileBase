package vn.shb.core.core.domain.usecases.wso2

import vn.shb.core.core.delivery.ResultSHB
import vn.shb.data.entities.login.UserLog

interface RepositoryWSO {

    suspend fun getToken(params: UseCaseGetTokenWso2.Params): ResultSHB<UserLog>

}