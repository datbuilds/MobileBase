package vn.shb.core.core.domain.usecases.home

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.FlowCollector
import vn.shb.core.core.delivery.ConnectionError
import vn.shb.core.core.delivery.ResultSHB
import vn.shb.core.core.domain.source.response.AccountData
import vn.shb.core.core.domain.usecases.BaseUseCase
import vn.shb.core.core.domain.usecases.None
import vn.shb.data.entities.home.UserInfo

class UseCaseUserInfo(private val repository: RepositoryUser) :
    BaseUseCase<Pair<UserInfo, AccountData>, None>() {
    override suspend fun FlowCollector<ResultSHB<Pair<UserInfo, AccountData>>>.run(params: None) {
        coroutineScope {
            val userDeferred = async { repository.getUserInfo() }
            val accountDeferred = async { repository.getAccountsInfos() }

            val userResult = userDeferred.await()
            val accountResult = accountDeferred.await()

            if (userResult is ResultSHB.Success && accountResult is ResultSHB.Success) {
                emit(ResultSHB.Success(Pair(userResult.successData, accountResult.successData)))
            } else {
                val error = when {
                    userResult is ResultSHB.Failure -> userResult
                    accountResult is ResultSHB.Failure -> accountResult
                    else -> ResultSHB.Failure(ConnectionError())
                }
                emit(error)
            }
        }
    }
}