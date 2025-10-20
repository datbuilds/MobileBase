package vn.shb.lao.screens.home

import android.content.Context
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import vn.shb.core.core.delivery.ConnectionError
import vn.shb.core.core.delivery.Reason
import vn.shb.core.core.delivery.onFailure
import vn.shb.core.core.delivery.onLoading
import vn.shb.core.core.delivery.onSuccess
import vn.shb.core.core.domain.usecases.None
import vn.shb.core.core.domain.usecases.home.UseCaseAccountDetails
import vn.shb.core.core.domain.usecases.home.UseCaseTransaction
import vn.shb.core.core.domain.usecases.home.UseCaseUserInfo
import vn.shb.core.core.security.encrypt.AndroidSecureStorage
import vn.shb.data.entities.home.AccountDetails
import vn.shb.data.entities.home.AccountInfo
import vn.shb.data.entities.home.TransactionItem
import vn.shb.data.entities.home.UserInfo
import vn.shb.data.entities.login.UserConverters
import vn.shb.lao.R
import vn.shb.lao.base.BaseViewModel
import vn.shb.lao.utils.ApiConst.FR_2_TO_DATE
import vn.shb.lao.utils.ApiConst.LAST5
import vn.shb.lao.utils.extensions.common.Const
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class HomeViewModel(
    private val storage: AndroidSecureStorage,
    private val useCaseUserInfo: UseCaseUserInfo,
    private val useCaseAccountDetails: UseCaseAccountDetails,
    private val useCaseTransaction: UseCaseTransaction
) : BaseViewModel() {
    private val _stateUserInfo = MutableStateFlow(UserInfo())
    val stateUserInfo = _stateUserInfo.asStateFlow()

    private val _stateAccounts = MutableStateFlow(AccountInfo())
    val stateSelectedAccount = _stateAccounts.asStateFlow()

    private val _stateAccountDetails = MutableStateFlow(AccountDetails())
    val stateAccountDetails = _stateAccountDetails.asStateFlow()

    private val _stateTransactions5First = MutableStateFlow<List<TransactionItem>>(emptyList())
    val stateTransactions5First = _stateTransactions5First.asStateFlow()

    private val _stateAllTransactions = MutableStateFlow<List<TransactionItem>>(emptyList())
    val stateAllTransactions = _stateAllTransactions.asStateFlow()

    private val _stateError = MutableSharedFlow<Reason?>()
    val stateError = _stateError.asSharedFlow()

    private var currentUserInfo: UserInfo? = null
    private var listAccount = listOf<AccountInfo>()
    var selectedAccount: AccountInfo? = null

    var currentTransaction: TransactionItem.Transaction? = null

    suspend fun showError(reason: Reason) {
        _stateError.emit(reason)
    }

    fun getUserInfo() {
        viewModelScope.launch {
            useCaseUserInfo.invoke(None).collect { result ->
                result.onSuccess { (userInfo, accountData) ->
                    val useLog = UserConverters.stringToUserInfo(storage.getUserLog())
                    useLog?.apply {
                        username = userInfo.customerName
                        customerId = userInfo.customerId
                        storage.setUserLog(UserConverters.userInfoToString(this))
                    }
                    _stateUserInfo.value = userInfo
                    listAccount = accountData.array
                    currentUserInfo = userInfo
                    getCurrentAccount(userInfo, accountData.array)
                }
                result.onFailure { error ->
                    showError(error)
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

    fun getAccountDetails(accountNumber: String = selectedAccount?.accountNumber ?: "") {
        viewModelScope.launch {
            val params = UseCaseAccountDetails.Params(accountNumber)
            useCaseAccountDetails.invoke(params).collect { result ->
                result.onSuccess { accountDetailsData ->
                    _stateAccountDetails.value =
                        accountDetailsData.array.firstOrNull() ?: AccountDetails()
                }
                result.onFailure { error ->
                    showError(error)
                }
                result.onLoading {
                    // Xử lý trạng thái tải ở đây nếu cần
                }
            }
        }
    }

    fun getTake5Transaction(context: Context) {
        viewModelScope.launch {
            val params = UseCaseTransaction.Params(
                accountNumber = selectedAccount?.accountNumber ?: "",
                queryType = LAST5
            )
            useCaseTransaction.invoke(params).collect { result ->
                result.onSuccess { transactionData ->
                    _stateTransactions5First.value =
                        mapTransactionsToItems(context, transactionData.array ?: listOf())
                }
                result.onFailure { error ->
                    showError(error)
                }
                result.onLoading {
                    // Xử lý trạng thái tải ở đây nếu cần
                }
            }
        }
    }

    fun getAllTransactions(context: Context, pairDate: Pair<String, String>? = null) {
        viewModelScope.launch {
            val pairDate = pairDate ?: getInitDate()
            val params = UseCaseTransaction.Params(
                accountNumber = selectedAccount?.accountNumber ?: "",
                queryType = FR_2_TO_DATE,
                fromDate = pairDate.first,
                toDate = pairDate.second
            )
            useCaseTransaction.invoke(params).collect { result ->
                result.onSuccess { transactionData ->
                    _stateAllTransactions.value =
                        mapTransactionsToItems(context, transactionData.array ?: listOf())
                }
                result.onFailure { error ->
                    showError(error)
                }
                result.onLoading {
                    // Xử lý trạng thái tải ở đây nếu cần
                }
            }
        }
    }

    fun getInitDate(): Pair<String, String> {
        val dateFormat = SimpleDateFormat(Const.FORMAT_TRANSACTION_DATE, Locale.getDefault())
        val today = Calendar.getInstance()
        val threeMonthsAgo = Calendar.getInstance().apply {
            add(Calendar.MONTH, -3)
        }
        val toDate = dateFormat.format(today.time)
        val fromDate = dateFormat.format(threeMonthsAgo.time)
        return Pair(fromDate, toDate)
    }

    fun mapTransactionsToItems(
        context: Context,
        transactions: List<TransactionItem.Transaction>
    ): List<TransactionItem> {
        val result = mutableListOf<TransactionItem>()

        // Sort theo ngày giảm dần (mới nhất trước)
        val sortedList = transactions.sortedByDescending {
            SimpleDateFormat(
                Const.FORMAT_TRANSACTION_DATE,
                Locale.getDefault()
            ).parse(it.transactionDate)
        }

        val today = Calendar.getInstance()
        val yesterday = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }

        var lastHeader: String? = null

        sortedList.forEach { tx ->
            val date = SimpleDateFormat(
                Const.FORMAT_TRANSACTION_DATE,
                Locale.getDefault()
            ).parse(tx.transactionDate)
            val cal = Calendar.getInstance().apply { time = date ?: Date() }

            // Xác định tiêu đề header
            val header = when {
                isSameDay(cal, today) -> context.getString(R.string.today)
                isSameDay(cal, yesterday) -> context.getString(R.string.yesterday)
                else -> tx.transactionDate
            }

            // Nếu header khác so với item trước → thêm header vào list
            if (header != lastHeader) {
                result.add(TransactionItem.Header(header))
                lastHeader = header
            }

            // Thêm transaction vào list
            result.add(tx)
        }

        return result
    }

    private fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }


}
