package com.mobile.base.utils.extensions

import com.mobile.base.utils.extensions.common.Const
import com.mobile.base.data.entities.AccountBase

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
