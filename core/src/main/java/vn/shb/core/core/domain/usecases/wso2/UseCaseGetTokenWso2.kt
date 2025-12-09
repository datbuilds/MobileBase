package vn.shb.core.core.domain.usecases.wso2

import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.flow.FlowCollector
import vn.shb.core.core.delivery.ResultSHB
import vn.shb.core.core.domain.usecases.BaseUseCase
import vn.shb.core.core.domain.usecases.UseCaseParameters
import vn.shb.core.core.domain.usecases.login.RepositoryAuth
import vn.shb.data.entities.login.UserLog

class UseCaseGetTokenWso2(
    private val repository: RepositoryWSO
) : BaseUseCase<UserLog, UseCaseGetTokenWso2.Params>() {

    override suspend fun FlowCollector<ResultSHB<UserLog>>.run(params: Params) {
        emit(repository.getToken(params))
    }

    data class  Params(
        val grant_type: String = GRANT_TYPE,
        val username: String = USERNAME,
        val password: String = PASSWORD,
    ) : UseCaseParameters

    companion object {
        const val GRANT_TYPE = "password"
        const val USERNAME = "mobilelao"
        const val PASSWORD = "mobilelao@2025"
    }
}
