package vn.shb.core.core.delivery

sealed class ResultSHB<out T> {
    object Loading : ResultSHB<Nothing>()
    data class Failure(val reason: Reason) : ResultSHB<Nothing>()
    data class Success<out T>(val successData: T) : ResultSHB<T>()
}

//region Extensions
inline fun <T> ResultSHB<T>.onResultHandle(
    loadingBlock: () -> Unit,
    failureBlock: (Reason) -> Unit,
    successBlock: (T) -> Unit
) {
    when (this) {
        is ResultSHB.Success -> successBlock(successData)
        is ResultSHB.Failure -> failureBlock(reason)
        is ResultSHB.Loading -> loadingBlock()
    }
}

inline fun <T> ResultSHB<T>.onSuccess(successBlock: (T) -> Unit): ResultSHB<T> {
    if (this is ResultSHB.Success)
        successBlock(successData)

    return this
}

inline fun <T> ResultSHB<T>.onFailure(errorBlock: (Reason) -> Unit): ResultSHB<T> {
    if (this is ResultSHB.Failure)
        errorBlock(reason)

    return this
}

inline fun <T> ResultSHB<T>.onLoading(loadingBlock: () -> Unit): ResultSHB<T> {
    if (this is ResultSHB.Loading)
        loadingBlock()

    return this
}
//endregion