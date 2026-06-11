package com.mobile.base.core.core.domain.usecases.login

import kotlinx.coroutines.flow.FlowCollector
import com.mobile.base.core.core.delivery.ResultState
import com.mobile.base.core.core.domain.usecases.BaseUseCase
import com.mobile.base.core.core.domain.usecases.UseCaseParameters
import com.mobile.base.data.entities.login.UserLog
import com.mobile.base.data.entities.login.VerifyDeviceRequest

class VerifyDeviceUseCase(private val repository: RepositoryAuth) :
    BaseUseCase<UserLog, VerifyDeviceUseCase.Params>() {

    override suspend fun FlowCollector<ResultState<UserLog>>.run(params: Params) {
        val request = VerifyDeviceRequest(
            username = params.username,
            password = params.password,
            transactionId = params.transactionId,
            otpCode = params.otpCode
        )
        emit(repository.verifyDevice(params.headers, request))
    }

    data class Params(
        val headers: Map<String, String>,
        val username: String,
        val password: String,
        val transactionId: String,
        val otpCode: String
    ) : UseCaseParameters
}
