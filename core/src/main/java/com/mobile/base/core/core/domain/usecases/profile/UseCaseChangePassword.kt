package com.mobile.base.core.core.domain.usecases.profile

import kotlinx.coroutines.flow.FlowCollector
import com.mobile.base.core.core.delivery.BaseResponse
import com.mobile.base.core.core.delivery.ResultState
import com.mobile.base.core.core.domain.source.request.ChangePasswordRequest
import com.mobile.base.core.core.domain.usecases.BaseUseCase
import com.mobile.base.core.core.domain.usecases.UseCaseParameters
import com.mobile.base.core.core.domain.usecases.home.RepositoryUser

import com.mobile.base.core.core.domain.source.response.ChangePasswordResponse

class UseCaseChangePassword(private val repository: RepositoryUser) :
    BaseUseCase<ChangePasswordResponse, UseCaseChangePassword.Params>() {

    override suspend fun FlowCollector<ResultState<ChangePasswordResponse>>.run(params: Params) {
        emit(repository.changePassword(ChangePasswordRequest(params.oldPassword, params.newPassword)))
    }

    data class Params(val oldPassword: String, val newPassword: String) : UseCaseParameters
}
