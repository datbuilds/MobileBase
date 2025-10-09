package vn.shb.core.core.domain.source.service

import retrofit2.awaitResponse
import vn.shb.core.core.domain.source.api.ApiAuth
import vn.shb.core.core.domain.source.api.ApiUser
import vn.shb.core.core.domain.usecases.login.UseCaseLogin
import vn.shb.core.core.domain.usecases.login.UseCaseLogout
import vn.shb.core.core.domain.usecases.login.UseCaseRefreshToken
import vn.shb.core.core.retrofit.SafeExecute

class ServiceUser(private val api: ApiUser) : SafeExecute() {

    suspend fun getUserInfo() = execute {
        api.getUserInfo().awaitResponse()
    }
}