package com.mobile.base.core.core.delivery.reason

import com.mobile.base.core.core.delivery.Reason

class AppReason(override val message: String, val code: String = "") : Reason() {
    override val errMessage: String
        get() = message

    override val errorCode: String
        get() = code
}