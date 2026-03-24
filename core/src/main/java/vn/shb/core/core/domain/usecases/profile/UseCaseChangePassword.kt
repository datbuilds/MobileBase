package vn.shb.core.core.domain.usecases.profile

import kotlinx.coroutines.flow.FlowCollector
import vn.shb.core.core.delivery.BaseResponse
import vn.shb.core.core.delivery.ResultSHB
import vn.shb.core.core.domain.source.request.ChangePasswordRequest
import vn.shb.core.core.domain.usecases.BaseUseCase
import vn.shb.core.core.domain.usecases.UseCaseParameters
import vn.shb.core.core.domain.usecases.home.RepositoryUser

import vn.shb.core.core.domain.source.response.ChangePasswordResponse

class UseCaseChangePassword(private val repository: RepositoryUser) :
    BaseUseCase<ChangePasswordResponse, UseCaseChangePassword.Params>() {

    override suspend fun FlowCollector<ResultSHB<ChangePasswordResponse>>.run(params: Params) {
        emit(repository.changePassword(ChangePasswordRequest(params.oldPassword, params.newPassword)))
    }

    data class Params(val oldPassword: String, val newPassword: String) : UseCaseParameters
}
