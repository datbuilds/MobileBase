package vn.shb.core.core.domain.source.service

import retrofit2.awaitResponse
import vn.shb.core.core.domain.source.api.ApiWSO2
import vn.shb.core.core.domain.usecases.wso2.UseCaseGetTokenWso2
import vn.shb.core.core.domain.usecases.wso2.UseCaseRefreshTokenWso2
import vn.shb.core.core.retrofit.SafeExecute

class ServiceWso2(private val api: ApiWSO2) : SafeExecute() {

    suspend fun getTokenWso2(token: String, params: UseCaseGetTokenWso2.Params) = execute {

        api.getTokenWso2(token = token, body = params).awaitResponse()
    }

    suspend fun rfTokenWso2(token: String, params: UseCaseRefreshTokenWso2.Params) = execute {
        api.rfTokenWso2(token, body = params).awaitResponse()
    }
}