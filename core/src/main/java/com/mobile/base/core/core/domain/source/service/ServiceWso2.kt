package com.mobile.base.core.core.domain.source.service

import retrofit2.awaitResponse
import com.mobile.base.core.core.domain.source.api.ApiWSO2
import com.mobile.base.core.core.domain.usecases.wso2.UseCaseGetTokenWso2
import com.mobile.base.core.core.domain.usecases.wso2.UseCaseRefreshTokenWso2
import com.mobile.base.core.core.retrofit.SafeExecute

class ServiceWso2(private val api: ApiWSO2) : SafeExecute() {

    suspend fun getTokenWso2(token: String, params: UseCaseGetTokenWso2.Params) = execute {

        api.getTokenWso2(token = token, body = params).awaitResponse()
    }

    suspend fun rfTokenWso2(token: String, params: UseCaseRefreshTokenWso2.Params) = execute {
        api.rfTokenWso2(token, body = params).awaitResponse()
    }
}