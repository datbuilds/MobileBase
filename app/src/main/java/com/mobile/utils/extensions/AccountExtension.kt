package com.mobile.utils.extensions

import com.mobile.utils.extensions.common.Const
import com.mobile.data.entities.AccountBase

fun <T : AccountBase> Iterable<T>.sortAccount(): List<T> {
    return this.sortedWith(
        compareBy<T> {
            when (it.currencyCode) {
                Const.KHR -> 0
                Const.USD -> 1
                else -> 2
            }
        }.thenByDescending { it.availableBalance }
    )
}
