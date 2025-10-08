package vn.shb.core.core.domain.source.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import vn.shb.core.core.domain.source.response.LoginResponse
import vn.shb.core.core.domain.source.response.LogoutResponse
import vn.shb.core.core.domain.usecases.login.UseCaseLogin
import vn.shb.core.core.domain.usecases.login.UseCaseLogout
import vn.shb.core.core.domain.usecases.login.UseCaseRefreshToken

interface ApiAuth {
    @POST(ENDPOINT.AUTH_LOGIN)
    fun login(@Body body: UseCaseLogin.Params): Call<LoginResponse>

    @POST(ENDPOINT.AUTH_LOGOUT)
    fun logout(): Call<LogoutResponse>

    @POST(ENDPOINT.AUTH_REFRESH_TOKEN)
    fun refreshToken(@Body body: UseCaseRefreshToken.Params): Call<LoginResponse>
}