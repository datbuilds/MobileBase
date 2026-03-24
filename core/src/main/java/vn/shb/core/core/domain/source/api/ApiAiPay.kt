package vn.shb.core.core.domain.source.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import vn.shb.core.core.domain.source.response.AiPayResponse
import vn.shb.core.core.domain.usecases.chatpay.UseCaseAiPay

interface ApiAiPay {

    @POST(ENDPOINT.AI_PAY)
    suspend fun submitAiPay(@Body params: UseCaseAiPay.Params): Response<AiPayResponse>
}
