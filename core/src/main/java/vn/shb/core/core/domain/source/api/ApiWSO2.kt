package vn.shb.core.core.domain.source.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import vn.shb.core.core.domain.usecases.wso2.UseCaseGetTokenWso2
import vn.shb.data.entities.login.UserLog

interface ApiWSO2 {
    @POST(ENDPOINT.WSO_END_POINT)
    fun getTokenWso2(@Body body: UseCaseGetTokenWso2.Params): Call<UserLog>

}