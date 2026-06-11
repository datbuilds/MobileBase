package com.mobile.base.core.core.delivery

sealed class ResultState<out T> {
    object Loading : ResultState<Nothing>()
    data class Failure(val reason: Reason) : ResultState<Nothing>()
    data class Success<out T>(val successData: T) : ResultState<T>()
}

//region Extensions
inline fun <T> ResultState<T>.onResultHandle(
    loadingBlock: () -> Unit,
    failureBlock: (Reason) -> Unit,
    successBlock: (T) -> Unit
) {
    when (this) {
        is ResultState.Success -> successBlock(successData)
        is ResultState.Failure -> failureBlock(reason)
        is ResultState.Loading -> loadingBlock()
    }
}

inline fun <T> ResultState<T>.onResultHandle(
    resultBlock: (ResultState<T>) -> Unit
) {
    resultBlock(this)
}

inline fun <T> ResultState<T>.onSuccess(successBlock: (T) -> Unit): ResultState<T> {
    if (this is ResultState.Success)
        successBlock(successData)

    return this
}

inline fun <T> ResultState<T>.onFailure(errorBlock: (Reason) -> Unit): ResultState<T> {
    if (this is ResultState.Failure)
        errorBlock(reason)

    return this
}

inline fun <T> ResultState<T>.onLoading(loadingBlock: () -> Unit): ResultState<T> {
    if (this is ResultState.Loading)
        loadingBlock()

    return this
}
//endregion