package vn.shb.core.core.domain.usecases

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import vn.shb.core.core.delivery.ResultSHB

abstract class BaseUseCase<T, in P : UseCaseParameters> {

    protected abstract suspend fun FlowCollector<ResultSHB<T>>.run(params: P)

    operator fun invoke(params: P) =
        flow<ResultSHB<T>> {
            emit(ResultSHB.Loading)
            run(params)
        }.flowOn(Dispatchers.IO)
}

interface UseCaseParameters

object None : UseCaseParameters
