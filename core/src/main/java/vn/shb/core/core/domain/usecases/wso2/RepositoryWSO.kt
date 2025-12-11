package vn.shb.core.core.domain.usecases.wso2

import vn.shb.core.core.delivery.ResultSHB
import vn.shb.data.entities.login.UserLog
import vn.shb.data.entities.wso2.WsoData

interface RepositoryWSO {

    suspend fun getToken(token: String, params: UseCaseGetTokenWso2.Params): ResultSHB<WsoData>
    suspend fun refreshToken(token: String, params: UseCaseRefreshTokenWso2.Params): ResultSHB<WsoData>

}