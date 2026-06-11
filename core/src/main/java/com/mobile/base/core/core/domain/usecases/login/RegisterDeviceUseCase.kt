package com.mobile.base.core.core.domain.usecases.login

import kotlinx.coroutines.flow.FlowCollector
import com.mobile.base.core.core.delivery.ResultState
import com.mobile.base.core.core.domain.usecases.BaseUseCase
import com.mobile.base.core.core.domain.usecases.UseCaseParameters
import com.mobile.base.data.entities.login.RegisterDeviceData
import com.mobile.base.data.entities.login.RegisterDeviceRequest

class RegisterDeviceUseCase(private val repository: RepositoryAuth) :
    BaseUseCase<RegisterDeviceData, RegisterDeviceUseCase.Params>() {

    override suspend fun FlowCollector<ResultState<RegisterDeviceData>>.run(params: Params) {
        val request = RegisterDeviceRequest(username = params.username, password = params.password)
        emit(repository.registerDevice(params.headers, request))
    }

    data class Params(
        val headers: Map<String, String>,
        val username: String,
        val password: String
    ) : UseCaseParameters
}
