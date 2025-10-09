package vn.shb.core.core.domain.usecases.home

import kotlinx.coroutines.flow.FlowCollector
import vn.shb.core.core.delivery.ResultSHB
import vn.shb.core.core.domain.source.response.AccountData
import vn.shb.core.core.domain.usecases.BaseUseCase
import vn.shb.core.core.domain.usecases.None

class UseCaseAccounts(private val repository: RepositoryUser) : BaseUseCase<AccountData, None>() {
    override suspend fun FlowCollector<ResultSHB<AccountData>>.run(
        params: None
    ) {
        emit(repository.getAccountsInfos())
    }
}