package vn.shb.core.core.domain.usecases.transfer

import kotlinx.coroutines.flow.FlowCollector
import vn.shb.core.core.delivery.ResultSHB
import vn.shb.core.core.domain.source.response.AccountUserNameModel
import vn.shb.core.core.domain.usecases.BaseUseCase
import vn.shb.core.core.domain.usecases.UseCaseParameters

class UseCaseAccountByNumber(private val repoTransfer: RepositoryTransfer) :
    BaseUseCase<AccountUserNameModel, UseCaseAccountByNumber.Params>() {
    override suspend fun FlowCollector<ResultSHB<AccountUserNameModel>>.run(
        params: Params
    ) {
        repoTransfer.getAccountByNumber(accountNumber = params.accountNumber)
    }

    data class Params(
        val accountNumber: String
    ) : UseCaseParameters
}