package vn.shb.core.core.domain.usecases

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import vn.shb.core.core.delivery.ResultSHB

abstract class UseCase<T, in P>(
    private val coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    protected abstract suspend fun FlowCollector<ResultSHB<T>>.run(params: P)

    operator fun invoke(body: P) =
        flow {
            emit(ResultSHB.Loading)
            run(body)
        }.flowOn(coroutineDispatcher)
}

abstract class UseCaseNone<T>(
    private val coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    protected abstract suspend fun FlowCollector<ResultSHB<T>>.run()

    operator fun invoke() = flow {
        emit(ResultSHB.Loading)
        run()
    }.flowOn(coroutineDispatcher)
}