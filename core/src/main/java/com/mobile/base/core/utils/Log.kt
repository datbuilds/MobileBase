package com.mobile.base.core.utils

import timber.log.Timber
import com.mobile.base.core.core.delivery.ReasonDescription.EMPTY


inline fun <reified T> T.logD(message: String = EMPTY) {
    Timber.tag(T::class.java.simpleName).d(message)
}

inline fun <reified T> T.logD(tag: String, message: String = EMPTY) {
    Timber.tag(tag).d(message)
}

inline fun <reified T> T.logE(message: String? = null, throwable: Throwable? = null) {
    Timber.tag(T::class.java.simpleName).e(throwable, message)
}

inline fun <reified T> T.logE(
    tag: String? = null,
    message: String? = null,
    throwable: Throwable? = null
) {
    if (tag.isNullOrEmpty()) {
        Timber.tag(T::class.java.simpleName).e(throwable, message)
    } else {
        Timber.tag(tag).e(throwable, message)
    }
}

inline fun <reified T> T.logDm(message: String? = EMPTY) {
    when {
        message.isNullOrEmpty() -> Timber.tag("lovingo").d(EMPTY)
        else -> Timber.tag("lovingo").d(message)
    }
}