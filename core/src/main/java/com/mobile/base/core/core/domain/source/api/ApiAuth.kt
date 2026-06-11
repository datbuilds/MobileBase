package com.mobile.base.core.core.domain.source.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import com.mobile.base.core.core.domain.source.api.ENDPOINT
import com.mobile.base.core.core.domain.source.response.LoginResponse
import com.mobile.base.core.core.domain.source.response.LogoutResponse
import com.mobile.base.core.core.domain.source.response.RegisterDeviceResponse
import com.mobile.base.core.core.domain.source.response.SystemVarResponse
import com.mobile.base.core.core.domain.source.response.VerifyDeviceResponse
import com.mobile.base.core.core.domain.usecases.login.UseCaseLogin
import com.mobile.base.core.core.domain.usecases.login.UseCaseRefreshToken
import com.mobile.base.data.entities.login.RegisterDeviceRequest
import com.mobile.base.data.entities.login.VerifyDeviceRequest

interface ApiAuth {
    @POST(ENDPOINT.AUTH_LOGIN)
    fun login(@Body body: UseCaseLogin.Params): Call<LoginResponse>

    @POST(ENDPOINT.AUTH_LOGOUT)
    fun logout(): Call<LogoutResponse>

    @POST(ENDPOINT.AUTH_REFRESH_TOKEN)
    fun refreshToken(@Body body: UseCaseRefreshToken.Params): Call<LoginResponse>

    @GET(ENDPOINT.SYSTEM_VARS)
    fun getSystemVars(@Query("name") name: String): Call<SystemVarResponse>

    @POST(ENDPOINT.REGISTER_DEVICE)
    fun registerDevice(
        @retrofit2.http.HeaderMap headers: Map<String, String>,
        @Body body: RegisterDeviceRequest
    ): Call<RegisterDeviceResponse>

    @POST(ENDPOINT.VERIFY_DEVICE)
    fun verifyDevice(
        @retrofit2.http.HeaderMap headers: Map<String, String>,
        @Body body: VerifyDeviceRequest
    ): Call<VerifyDeviceResponse>
}