package com.mobile.base.core.core.domain.usecases.home

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.FlowCollector
import com.mobile.base.core.core.delivery.ConnectionError
import com.mobile.base.core.core.delivery.ResultState
import com.mobile.base.core.core.domain.source.response.AccountData
import com.mobile.base.core.core.domain.usecases.BaseUseCase
import com.mobile.base.core.core.domain.usecases.None
import com.mobile.base.data.entities.home.UserInfo

class UseCaseUserInfo(private val repository: RepositoryUser) :
    BaseUseCase<Pair<UserInfo, AccountData>, None>() {
    override suspend fun FlowCollector<ResultState<Pair<UserInfo, AccountData>>>.run(params: None) {
        coroutineScope {
            val userDeferred = async { repository.getUserInfo() }
            val accountDeferred = async { repository.getAccountsInfos() }

            val userResult = userDeferred.await()
            val accountResult = accountDeferred.await()

            if (userResult is ResultState.Success && accountResult is ResultState.Success) {
                emit(ResultState.Success(Pair(userResult.successData, accountResult.successData)))
            } else {
                val error = when {
                    userResult is ResultState.Failure -> userResult
                    accountResult is ResultState.Failure -> accountResult
                    else -> ResultState.Failure(ConnectionError())
                }
                emit(error)
            }
        }
    }
}