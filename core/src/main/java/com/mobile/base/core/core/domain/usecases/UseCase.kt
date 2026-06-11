package com.mobile.base.core.core.domain.usecases

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import com.mobile.base.core.core.delivery.ResultState

abstract class UseCase<T, in P>(
    private val coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    protected abstract suspend fun FlowCollector<ResultState<T>>.run(params: P)

    operator fun invoke(body: P) =
        flow {
            emit(ResultState.Loading)
            run(body)
        }.flowOn(coroutineDispatcher)
}

abstract class UseCaseNone<T>(
    private val coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    protected abstract suspend fun FlowCollector<ResultState<T>>.run()

    operator fun invoke() = flow {
        emit(ResultState.Loading)
        run()
    }.flowOn(coroutineDispatcher)
}