package vn.shb.core.core.domain.usecases.login

import kotlinx.coroutines.flow.FlowCollector
import vn.shb.core.core.delivery.ResultSHB
import vn.shb.core.core.domain.usecases.BaseUseCase
import vn.shb.core.core.domain.usecases.UseCaseParameters
import vn.shb.data.entities.login.UserLog
import vn.shb.data.entities.login.VerifyDeviceRequest

class VerifyDeviceUseCase(private val repository: RepositoryAuth) :
    BaseUseCase<UserLog, VerifyDeviceUseCase.Params>() {

    override suspend fun FlowCollector<ResultSHB<UserLog>>.run(params: Params) {
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
