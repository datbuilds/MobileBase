package com.mobile.base.screens.home

import android.content.Context
import com.mobile.base.data.entities.AccountBase
import com.mobile.base.R
import com.mobile.base.utils.extensions.common.Const

fun getTypeAccount(context: Context, account: AccountBase): String {
    return when (account.accountType) {
        Const.CURRENT_ACCOUNT -> context.getString(R.string.currentAccount)
        Const.SAVING_ACCOUNT -> context.getString(R.string.flexibleSaving)
        Const.LOAN_ACCOUNT -> context.getString(R.string.staffLoans)
        else -> context.getString(R.string.currentAccount)
    }
}