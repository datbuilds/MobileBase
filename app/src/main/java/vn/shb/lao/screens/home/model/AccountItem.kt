package vn.shb.lao.screens.home.model

data class AccountItem(
    val accountNumber: String,
    val accountType: String,
    val currency: String,
    val balance: String,
    var isSelected: Boolean = false
)