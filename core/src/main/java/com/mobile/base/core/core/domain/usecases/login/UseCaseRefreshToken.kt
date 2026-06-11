package com.mobile.base.core.core.domain.usecases.login

import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.flow.FlowCollector
import com.mobile.base.core.core.delivery.ResultState
import com.mobile.base.core.core.domain.usecases.BaseUseCase
import com.mobile.base.core.core.domain.usecases.UseCaseParameters
import com.mobile.base.data.entities.login.UserLog

class UseCaseRefreshToken(
    private val repository: RepositoryAuth
) : BaseUseCase<UserLog, UseCaseRefreshToken.Params>() {

    override suspend fun FlowCollector<ResultState<UserLog>>.run(params: Params) {
        emit(repository.refreshToken(params))
    }

    data class Params(@SerializedName("refresh_token") val refreshToken: String = "") :
        UseCaseParameters
}
