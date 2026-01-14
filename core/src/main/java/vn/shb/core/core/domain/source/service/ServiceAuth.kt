package vn.shb.core.core.domain.source.service

import retrofit2.awaitResponse
import vn.shb.core.core.domain.source.api.ApiAuth
import vn.shb.core.core.domain.usecases.login.UseCaseLogin
import vn.shb.core.core.domain.usecases.login.UseCaseRefreshToken
import vn.shb.core.core.retrofit.SafeExecute

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
}