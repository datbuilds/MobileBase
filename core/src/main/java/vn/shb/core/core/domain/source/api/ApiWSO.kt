package vn.shb.core.core.domain.source.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import vn.shb.core.core.domain.source.response.LoginResponse
import vn.shb.core.core.domain.usecases.login.UseCaseLogin

interface ApiWSO {
    @POST(ENDPOINT.AUTH_LOGIN)
    fun getTokenWso2(@Body body: UseCaseLogin.Params): Call<LoginResponse>

}