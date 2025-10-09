package vn.shb.core.core.domain.usecases.home

import kotlinx.coroutines.flow.FlowCollector
import vn.shb.core.core.delivery.ResultSHB
import vn.shb.core.core.domain.usecases.BaseUseCase
import vn.shb.core.core.domain.usecases.None
import vn.shb.data.entities.login.UserInfo

class UseCaseUser(private val repository: RepositoryUser) : BaseUseCase<UserInfo, None>() {
    override suspend fun FlowCollector<ResultSHB<UserInfo>>.run(
        params: None
    ) {
        emit(repository.getUserInfo())
    }

}