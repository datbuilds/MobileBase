package vn.shb.lao.screens.home

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import vn.shb.core.core.delivery.onFailure
import vn.shb.core.core.delivery.onLoading
import vn.shb.core.core.delivery.onSuccess
import vn.shb.core.core.domain.usecases.None
import vn.shb.core.core.domain.usecases.home.UseCaseUser
import vn.shb.core.core.security.encrypt.AndroidSecureStorage
import vn.shb.data.entities.login.UserInfo
import vn.shb.lao.R
import vn.shb.lao.base.BaseViewModel
import vn.shb.lao.screens.account.model.TransactionItem
import vn.shb.lao.screens.home.model.AccountItem

class HomeViewModel(
    private val storage: AndroidSecureStorage,
    private val useCaseUser: UseCaseUser
) : BaseViewModel() {
    private val _stateUserInfo = MutableStateFlow(UserInfo())
    val stateUserInfo = _stateUserInfo.asStateFlow()

    private var currentUserInfo: UserInfo? = null
    var selectedAccount: AccountItem? = null

    fun getUserInfo() {
        viewModelScope.launch {
            useCaseUser.invoke(None).collect { result ->
                result.onSuccess { data ->
                    _stateUserInfo.value = data
                    currentUserInfo = data
                }
                result.onFailure { error ->
                }
                result.onLoading { }
            }
        }
    }

    fun getCurrentUserInfo(): UserInfo? {
        return currentUserInfo
    }

    fun getListBanner(): List<Int> {
        return listOf(
            R.drawable.banner_1,
            R.drawable.banner_2,
            R.drawable.banner_3,
            R.drawable.banner_1,
            R.drawable.banner_2,
            R.drawable.banner_3,
            R.drawable.banner_1,
            R.drawable.banner_2,
            R.drawable.banner_3
        )
    }

    //mock data
    fun getListAccount() = listOf(
        AccountItem(
            accountNumber = "1234567890123",
            accountType = "Savings",
            currency = "LAK",
            balance = "15000000",
            isSelected = true
        ),
        AccountItem(
            accountNumber = "9876543210987",
            accountType = "Checking",
            currency = "USD",
            balance = "2500"
        ),
        AccountItem(
            accountNumber = "4567891234567",
            accountType = "Savings",
            currency = "EUR",
            balance = "3000"
        ),
        AccountItem(
            accountNumber = "3216549873216",
            accountType = "Checking",
            currency = "JPY",
            balance = "500000"
        ),
        AccountItem(
            accountNumber = "7891234567891",
            accountType = "Savings",
            currency = "GBP",
            balance = "2000"
        ),
        AccountItem(
            accountNumber = "6549873216549",
            accountType = "Checking",
            currency = "AUD",
            balance = "3500"
        ),
        AccountItem(
            accountNumber = "1597534862587",
            accountType = "Savings",
            currency = "CAD",
            balance = "4000"
        )
    )

    fun getFirstFiveTransactions(list: List<TransactionItem> = getListTransaction()): List<TransactionItem> {
        var count = 0
        return list.takeWhile { item ->
            val oldCount = count
            if (item is TransactionItem.Transaction) count++
            oldCount < 5
        }
    }

    fun getListTransaction(): List<TransactionItem> {
        //mock data
        return listOf(
            TransactionItem.Header
                (
                title = "June 2023"
            ),
            TransactionItem.Transaction(
                name = "Salary",
                subInfo = "Company XYZ",
                amount = 5000,
                currency = "USD",
                isIncome = true
            ),
            TransactionItem.Transaction(
                name = "Grocery Shopping",
                subInfo = "Supermarket ABC",
                amount = 150,
                currency = "USD",
                isIncome = false
            ),
            TransactionItem.Header(
                title = "August 2023"
            ),
            TransactionItem.Transaction(
                name = "Freelance Project",
                subInfo = "Client DEF",
                amount = 1200,
                currency = "USD",
                isIncome = true
            ),
            TransactionItem.Transaction(
                name = "Electricity Bill",
                subInfo = "Utility Company",
                amount = 100,
                currency = "USD",
                isIncome = false
            ),
            TransactionItem.Transaction(
                name = "Dining Out",
                subInfo = "Restaurant GHI",
                amount = 75,
                currency = "USD",
                isIncome = false
            ),
            TransactionItem.Header(
                title = "July 2023"
            ),
            TransactionItem.Transaction(
                name = "Stock Dividends",
                subInfo = "Investment JKL",
                amount = 300,
                currency = "USD",
                isIncome = true
            ),
            TransactionItem.Transaction(
                name = "Car Maintenance",
                subInfo = "Auto Shop MNO",
                amount = 250,
                currency = "USD",
                isIncome = false
            ),
            TransactionItem.Transaction(
                name = "Bonus",
                subInfo = "Company XYZ",
                amount = 800,
                currency = "USD",
                isIncome = true
            )
        )
    }

}
