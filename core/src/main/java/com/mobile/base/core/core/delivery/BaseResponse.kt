package com.mobile.base.core.core.delivery

import com.google.gson.annotations.SerializedName
import java.io.Serializable

abstract class BaseResponse<T>(
    @SerializedName("errorCode") val errorCode: String = "",
    @SerializedName("errorMessage") val errorMessage: String = "",
    @SerializedName("data") val data: T? = null,
//    @SerializedName("tranDate") val tranDate: String = "",
//    @SerializedName("totalElements") val totalElements: Int = 0,
) : Serializable {
    fun isSuccess() = errorCode.contentEquals("00")

    fun isOtherDevice() = errorCode.contentEquals("999")
}

fun <T> ResultState<BaseResponse<T>>.toResultData(preReturn: ((data: T) -> Unit)? = null): ResultState<T> {
    return when (this) {
        is ResultState.Success ->
            this.successData.toResult(preReturn)

        is ResultState.Failure -> this
        else -> ResultState.Loading
    }
}

fun <T> BaseResponse<T>.toResult(preReturn: ((data: T) -> Unit)? = null): ResultState<T> {
    return if (data != null) {
        preReturn?.invoke(data)
        ResultState.Success(data)
    } else
        ResultState.Failure(EmptyResultError())
}

class EmptyResponse : BaseResponse<ActionDone>()

fun ResultState<EmptyResponse>.toResultDataAction(preReturn: ((data: ActionDone) -> Unit)? = null) =
    when (this) {
        is ResultState.Success -> {
            preReturn?.invoke(ActionDone)
            ResultState.Success(ActionDone)
        }

        is ResultState.Failure -> this
        else -> ResultState.Loading
    }

data class ErrorResponse(
    @SerializedName("message") val message: String? = null,
    @SerializedName("code", alternate = ["errorCode"]) val code: String? = null
)