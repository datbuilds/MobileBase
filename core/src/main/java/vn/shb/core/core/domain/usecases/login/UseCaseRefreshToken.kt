package vn.shb.core.core.domain.usecases.login

import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.flow.FlowCollector
import vn.shb.core.core.delivery.ResultSHB
import vn.shb.core.core.domain.usecases.BaseUseCase
import vn.shb.core.core.domain.usecases.UseCaseParameters
import vn.shb.data.entities.login.UserLog

class UseCaseRefreshToken(
    private val repository: RepositoryAuth
) : BaseUseCase<UserLog, UseCaseRefreshToken.Params>() {

    override suspend fun FlowCollector<ResultSHB<UserLog>>.run(params: Params) {
        emit(repository.refreshToken(params))
    }

    data class Params(@SerializedName("refresh_token") val refreshToken: String = "") :
        UseCaseParameters
}
