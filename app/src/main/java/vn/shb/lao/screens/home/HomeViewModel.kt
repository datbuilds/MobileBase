package vn.shb.lao.screens.home

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import vn.shb.core.core.delivery.onFailure
import vn.shb.core.core.delivery.onLoading
import vn.shb.core.core.delivery.onSuccess
import vn.shb.core.core.domain.usecases.None
import vn.shb.core.core.domain.usecases.home.UseCaseUserInfo
import vn.shb.core.core.security.encrypt.AndroidSecureStorage
import vn.shb.data.entities.home.AccountInfo
import vn.shb.data.entities.home.UserInfo
import vn.shb.lao.R
import vn.shb.lao.base.BaseViewModel
import vn.shb.lao.screens.account.model.TransactionItem

class HomeViewModel(
    private val storage: AndroidSecureStorage,
    private val useCaseUserInfo: UseCaseUserInfo
) : BaseViewModel() {
    private val _stateUserInfo = MutableStateFlow(UserInfo())
    val stateUserInfo = _stateUserInfo.asStateFlow()

    private val _stateAccounts = MutableStateFlow(AccountInfo())
    val stateSelectedAccount = _stateAccounts.asStateFlow()

    private var currentUserInfo: UserInfo? = null
    private var listAccount = listOf<AccountInfo>()
    var selectedAccount: AccountInfo? = null

    fun getUserInfo() {
        viewModelScope.launch {
            useCaseUserInfo.invoke(None).collect { result ->
                result.onSuccess { (userInfo, accountData) ->
                    _stateUserInfo.value = userInfo
                    listAccount = accountData.array
                    currentUserInfo = userInfo
                    getCurrentAccount(userInfo, accountData.array)
                }
                result.onFailure { error ->
                }
                result.onLoading { }
            }
        }
    }

    private fun getCurrentAccount(
        userInfo: UserInfo,
        listAccount: List<AccountInfo> = this.listAccount
    ) {
        selectedAccount = listAccount.find { it.accountNumber == userInfo.defaultAcct }
        if (selectedAccount == null && listAccount.isNotEmpty()) {
            selectedAccount = listAccount[0]
        }
        if (selectedAccount != null) {
            _stateAccounts.value = selectedAccount!!
        }

    }

    fun getCurrentUserInfo(): UserInfo? {
        return currentUserInfo
    }

    fun getAccounts() {
//        viewModelScope.launch {
//            useCaseAccounts.invoke(None).collect { result ->
//                result.onSuccess { data ->
//                    listAccount = data.array
//                    if (listAccount.isNotEmpty()) {
//                        selectedAccount = listAccount[0]
//                    }
//                    _stateAccounts.value = listAccount
//                }
//                result.onFailure { error ->
//                }
//                result.onLoading { }
//            }
//        }
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

    fun getListAccount() = listAccount

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
