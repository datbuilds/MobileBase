package vn.shb.core.core.domain.usecases.login

import kotlinx.coroutines.flow.FlowCollector
import vn.shb.core.core.delivery.ResultSHB
import vn.shb.core.core.domain.usecases.BaseUseCase
import vn.shb.core.core.domain.usecases.UseCaseParameters
import vn.shb.data.entities.login.RegisterDeviceData
import vn.shb.data.entities.login.RegisterDeviceRequest

class RegisterDeviceUseCase(private val repository: RepositoryAuth) :
    BaseUseCase<RegisterDeviceData, RegisterDeviceUseCase.Params>() {

    override suspend fun FlowCollector<ResultSHB<RegisterDeviceData>>.run(params: Params) {
        val request = RegisterDeviceRequest(username = params.username, password = params.password)
        emit(repository.registerDevice(params.headers, request))
    }

    data class Params(
        val headers: Map<String, String>,
        val username: String,
        val password: String
    ) : UseCaseParameters
}
