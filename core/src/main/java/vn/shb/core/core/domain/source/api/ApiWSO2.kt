package vn.shb.core.core.domain.source.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import vn.shb.core.core.domain.usecases.wso2.UseCaseGetTokenWso2
import vn.shb.core.core.domain.usecases.wso2.UseCaseRefreshTokenWso2
import vn.shb.data.entities.wso2.WsoData

interface ApiWSO2 {
    @POST(ENDPOINT.WSO_END_POINT)
    fun getTokenWso2(
        @Header("Authorization") token: String,
        @Body body: UseCaseGetTokenWso2.Params
    ): Call<WsoData>

    @POST(ENDPOINT.WSO_END_POINT)
    fun rfTokenWso2(
        @Header("Authorization") token: String,
        @Body body: UseCaseRefreshTokenWso2.Params
    ): Call<WsoData>

}