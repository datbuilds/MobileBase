package com.mobile.base.core.core.domain.usecases

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import com.mobile.base.core.core.delivery.ResultState

abstract class BaseUseCase<T, in P : UseCaseParameters> {

    protected abstract suspend fun FlowCollector<ResultState<T>>.run(params: P)

    operator fun invoke(params: P) =
        flow<ResultState<T>> {
            emit(ResultState.Loading)
            run(params)
        }.flowOn(Dispatchers.IO)
}

interface UseCaseParameters

object None : UseCaseParameters
