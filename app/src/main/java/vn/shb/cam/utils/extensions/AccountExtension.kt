package vn.shb.cam.utils.extensions

import vn.shb.cam.utils.extensions.common.Const
import vn.shb.data.entities.AccountBase

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
