package com.mobile.core.core.domain.source.service

import retrofit2.awaitResponse
import com.mobile.core.core.domain.source.api.ApiAuth
import com.mobile.core.core.domain.usecases.login.UseCaseLogin
import com.mobile.core.core.domain.usecases.login.UseCaseRefreshToken
import com.mobile.core.core.retrofit.SafeExecute

class ServiceAuth(private val api: ApiAuth) : SafeExecute() {

    suspend fun refreshToken(params: UseCaseRefreshToken.Params) = execute {
        api.refreshToken(body = params).awaitResponse()
    }

    suspend fun login(params: UseCaseLogin.Params) = execute {
        api.login(body = params).awaitResponse()
    }

    suspend fun logout() = execute {
        api.logout().awaitResponse()
    }

    suspend fun getSystemVars(name: String) = execute {
        api.getSystemVars(name).awaitResponse()
    }

    suspend fun registerDevice(headers: Map<String, String>, params: com.mobile.data.entities.login.RegisterDeviceRequest) = execute {
        api.registerDevice(headers, params).awaitResponse()
    }

    suspend fun verifyDevice(headers: Map<String, String>, params: com.mobile.data.entities.login.VerifyDeviceRequest) = execute {
        api.verifyDevice(headers, params).awaitResponse()
    }
}