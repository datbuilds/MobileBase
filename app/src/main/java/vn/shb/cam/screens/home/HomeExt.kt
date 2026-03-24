package vn.shb.cam.screens.home

import android.content.Context
import vn.shb.data.entities.AccountBase
import vn.shb.cam.R
import vn.shb.cam.utils.extensions.common.Const

fun getTypeAccount(context: Context, account: AccountBase): String {
    return when (account.accountType) {
        Const.CURRENT_ACCOUNT -> context.getString(R.string.currentAccount)
        Const.SAVING_ACCOUNT -> context.getString(R.string.flexibleSaving)
        Const.LOAN_ACCOUNT -> context.getString(R.string.staffLoans)
        else -> context.getString(R.string.currentAccount)
    }
}