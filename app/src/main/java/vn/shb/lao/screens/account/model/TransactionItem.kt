package vn.shb.lao.screens.account.model

sealed class TransactionItem {
    data class Header(val title: String) : TransactionItem()
    data class Transaction(
        val name: String,
        val subInfo: String,
        val amount: Long,
        val currency: String,
        val isIncome: Boolean
    ) : TransactionItem()
}