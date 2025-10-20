package vn.shb.lao.screens.home

import android.content.Context
import vn.shb.data.entities.home.AccountInfo
import vn.shb.lao.R
import vn.shb.lao.utils.extensions.common.Const

fun getTypeAccount(
    context: Context,
    account: AccountInfo
): Pair<String, String> {
    return when (account.accountType) {
        Const.CURRENT_ACCOUNT -> Pair(context.getString(R.string.currentAccount), account.availableBalance.toString())
        Const.SAVING_ACCOUNT -> Pair(account.productDescription, account.availableBalance.toString())
        Const.LOAN_ACCOUNT -> Pair(account.productDescription, account.availableBalance.toString())

        else -> Pair(
            context.getString(R.string.defaultCasaAccount),
            account.availableBalance.toString()
        )
    }
}