package vn.shb.lao.screens.home

import android.content.Context
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import vn.shb.core.core.delivery.Reason
import vn.shb.core.core.delivery.ResultSHB
import vn.shb.core.core.delivery.onFailure
import vn.shb.core.core.delivery.onLoading
import vn.shb.core.core.delivery.onSuccess
import vn.shb.core.core.domain.source.response.AccountUserNameModel
import vn.shb.core.core.domain.source.response.TransactionTransfer
import vn.shb.core.core.domain.source.response.TransactionTransferConfirm
import vn.shb.core.core.domain.usecases.None
import vn.shb.core.core.domain.usecases.home.UseCaseAccountDetails
import vn.shb.core.core.domain.usecases.home.UseCaseSetDefaultAccount
import vn.shb.core.core.domain.usecases.home.UseCaseTransaction
import vn.shb.core.core.domain.usecases.home.UseCaseUserInfo
import vn.shb.core.core.domain.usecases.transfer.AccountInfoRequest
import vn.shb.core.core.domain.usecases.transfer.FundTransferRequest
import vn.shb.core.core.domain.usecases.transfer.OrderDetail
import vn.shb.core.core.domain.usecases.transfer.UseCaseAccountByNumber
import vn.shb.core.core.domain.usecases.transfer.UseCaseTransactionDetail
import vn.shb.core.core.domain.usecases.transfer.UseCaseTransactionTransfer
import vn.shb.core.core.domain.usecases.transfer.UseCaseTransactionTransferConfirm
import vn.shb.core.core.domain.usecases.transfer.UseCaseTransferAccount
import vn.shb.core.core.domain.usecases.transfer.UseCaseValidateTransaction
import vn.shb.core.core.domain.usecases.wso2.UseCaseGetTokenWso2
import vn.shb.core.core.security.encrypt.AndroidSecureStorage
import vn.shb.data.entities.AccountBase
import vn.shb.data.entities.home.AccountDetails
import vn.shb.data.entities.home.AccountInfo
import vn.shb.data.entities.home.TransactionDetail
import vn.shb.data.entities.home.TransactionItem
import vn.shb.data.entities.home.UserInfo
import vn.shb.data.entities.home.beneficiary.BeneficiaryUser
import vn.shb.data.entities.login.UserConverters
import vn.shb.data.entities.transfer.ConfirmationModel
import vn.shb.data.entities.transfer.TransferAccount
import vn.shb.lao.R
import vn.shb.lao.base.BaseViewModel
import vn.shb.lao.utils.ApiConst
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
    private val useCaseTransaction: UseCaseTransaction,
    private val useCaseTransferAccount: UseCaseTransferAccount,
    private val useCaseTransactionTransfer: UseCaseTransactionTransfer,
    private val useCaseTransactionTransferConfirm: UseCaseTransactionTransferConfirm,
    private val useCaseAccountByNumber: UseCaseAccountByNumber,
    private val useCaseTransactionDetail: UseCaseTransactionDetail,
    private val useCaseValidateTransaction: UseCaseValidateTransaction,
    private val useCaseSetDefaultAccount: UseCaseSetDefaultAccount,
) : BaseViewModel() {
    private val _stateUserInfo = MutableStateFlow(UserInfo())
    val stateUserInfo = _stateUserInfo.asStateFlow()

    private val _stateFetchUser = Channel<UserInfo>(Channel.BUFFERED)
    val stateFetchUser = _stateFetchUser.receiveAsFlow()


    private val _stateAccounts = Channel<AccountBase>(Channel.BUFFERED)
    val stateSelectedAccount = _stateAccounts.receiveAsFlow()

    private val _stateAccountDetails = Channel<AccountDetails>(Channel.BUFFERED)
    val stateAccountDetails = _stateAccountDetails.receiveAsFlow()

    private val _stateTransactions5First = Channel<List<TransactionItem>>(Channel.BUFFERED)
    val stateTransactions5First = _stateTransactions5First.receiveAsFlow()

    private val _stateAllTransactions = MutableStateFlow<List<TransactionItem>>(emptyList())
    val stateAllTransactions = _stateAllTransactions.asStateFlow()

    private val _stateAllBeneficiary = MutableStateFlow<List<BeneficiaryUser>>(emptyList())
    val stateAllBeneficiary = _stateAllBeneficiary.asStateFlow()

    private val _stateError = Channel<Reason>(Channel.BUFFERED)
    val stateError = _stateError.receiveAsFlow()

    private var currentUserInfo: UserInfo? = null
    private var listAccount = listOf<AccountInfo>()
    var selectedAccount: AccountBase? = null

    var currentTransaction: TransactionItem.Transaction? = null

    //transfer
    private val _stateTransferAccount = MutableStateFlow<TransferAccount?>(null)
    val stateTransferAccount = _stateTransferAccount.asStateFlow()

    private val _stateReceiverAccount = MutableStateFlow<List<TransferAccount>>(emptyList())
    val stateReceiverAccount = _stateReceiverAccount.asStateFlow()

    private val _stateTransactionTransfer = Channel<TransactionTransfer?>(Channel.BUFFERED)
    val stateTransactionTransfer = _stateTransactionTransfer.receiveAsFlow()

    var confirmSuccessData: TransactionTransferConfirm? = null

    private var transactionTransferRealtime: TransactionTransfer? = null

    private val _stateTransactionTransferConfirm = MutableSharedFlow<TransactionTransferConfirm?>()
    val stateTransactionTransferConfirm = _stateTransactionTransferConfirm.asSharedFlow()

    private val _stateTransferConfirmError = Channel<Reason>(Channel.BUFFERED)
    val stateTransferConfirmError = _stateTransferConfirmError.receiveAsFlow()

    private val _stateTransactionDetail = Channel<TransactionDetail?>(Channel.BUFFERED)
    val stateTransactionDetail = _stateTransactionDetail.receiveAsFlow()

    private val _stateDetailError = Channel<Reason>(Channel.BUFFERED)
    val stateDetailError = _stateDetailError.receiveAsFlow()

    private val _stateUpdateDefaultAccount = Channel<Boolean>(Channel.BUFFERED)
    val stateUpdateDefaultAccount = _stateUpdateDefaultAccount.receiveAsFlow()

    private val _stateAccountByNumber = MutableSharedFlow<AccountUserNameModel?>()
    val stateAccountByNumber = _stateAccountByNumber.asSharedFlow()

    private val _stateErrorFillAccountNumber = MutableSharedFlow<Boolean>()
    val stateErrorFillAccountNumber = _stateErrorFillAccountNumber.asSharedFlow()

    private val _stateValidateTransaction = MutableSharedFlow<String>()
    val stateValidateTransaction = _stateValidateTransaction.asSharedFlow()

    private val _stateLoading = MutableStateFlow(false)
    val stateLoading = _stateLoading.asStateFlow()

    var listTransferAccount = listOf<TransferAccount>()
    var listReceiverAccount = listOf<TransferAccount>()
    var listReceiverActive = listOf<TransferAccount>()

    var confirmModel: ConfirmationModel? = null

    private var listErrorCodeConfirmContinue = listOf(ApiConst.FUN_017, ApiConst.FUN_016)

    suspend fun stateError(reason: Reason) {
        _stateError.send(reason)
    }

    suspend fun stateLoading(isLoading: Boolean) {
        _stateLoading.emit(isLoading)
    }

    fun getUserInfo(isFetchUser : Boolean = false) {
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
                    if (isFetchUser){
                        _stateFetchUser.send(userInfo)
                    }
                    listAccount = accountData.array
                    currentUserInfo = userInfo
                    getCurrentAccount(userInfo, accountData.array)
                }
                result.onFailure { error ->
                    stateError(error)
                }
                result.onLoading { }
            }
        }
    }

    private fun getCurrentAccount(
        userInfo: UserInfo, listAccount: List<AccountInfo> = this.listAccount
    ) {
        if (selectedAccount != null) {
            selectedAccount =
                listAccount.find { it.accountNumber == selectedAccount!!.accountNumber }
            viewModelScope.launch {
                _stateAccounts.send(selectedAccount!!)
            }
            return
        }
        selectedAccount = listAccount.find { it.accountNumber == userInfo.defaultAcct }
        if (selectedAccount == null && listAccount.isNotEmpty()) {
            selectedAccount = listAccount[0]
        }
        if (selectedAccount != null) {
            viewModelScope.launch {
                _stateAccounts.send(selectedAccount!!)
            }
        }
    }

    fun getCurrentUserInfo(): UserInfo? {
        return currentUserInfo
    }

    fun getListBanner(): List<Int> {
        return listOf(
            R.drawable.banner_1, R.drawable.banner_2, R.drawable.banner_3
        )
    }

    fun getListAccount() = listAccount

    fun getAccountDetails(accountNumber: String = selectedAccount?.accountNumber ?: "") {
        viewModelScope.launch {
            val params = UseCaseAccountDetails.Params(accountNumber)
            useCaseAccountDetails.invoke(params).collect { result ->
                result.onSuccess { accountDetailsData ->
                    _stateAccountDetails.send(accountDetailsData.array.firstOrNull() ?: AccountDetails())
                }
                result.onFailure { error ->
                    stateError(error)
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
                accountNumber = selectedAccount?.accountNumber ?: "", queryType = LAST5
            )
            useCaseTransaction.invoke(params).collect { result ->
                result.onSuccess { transactionData ->
                    _stateTransactions5First.send(
                        mapTransactionsToItems(
                            context, transactionData.array ?: listOf()
                        )
                    )
                }
                result.onFailure { error ->
                    stateError(error)
                }
                result.onLoading {
                    // Xử lý trạng thái tải ở đây nếu cần
                }
            }
        }
    }

    fun getAllTransactions(context: Context, pairDate: Pair<String, String>) {
        viewModelScope.launch {
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
                    stateError(error)
                }
                result.onLoading {
                    // Xử lý trạng thái tải ở đây nếu cần
                }
            }
        }
    }

    fun getInitDate(): Pair<Long, Long> {
        val today = Calendar.getInstance()
        val threeMonthsAgo = Calendar.getInstance().apply {
            add(Calendar.MONTH, -3)
        }
        return Pair(threeMonthsAgo.timeInMillis, today.timeInMillis)
    }

    fun mapTransactionsToItems(
        context: Context, transactions: List<TransactionItem.Transaction>
    ): List<TransactionItem> {
        val result = mutableListOf<TransactionItem>()

        // Sort theo ngày giảm dần (mới nhất trước)
        val sortedList = transactions.sortedByDescending {
            SimpleDateFormat(
                Const.FORMAT_TRANSACTION_DATE, Locale.getDefault()
            ).parse(it.transactionDate)
        }

//        val today = Calendar.getInstance()
//        val yesterday = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }

        var lastHeader: String? = null

        sortedList.forEach { tx ->
            val date = SimpleDateFormat(
                Const.FORMAT_TRANSACTION_DATE, Locale.getDefault()
            ).parse(tx.transactionDate)
            val cal = Calendar.getInstance().apply { time = date ?: Date() }

            // Xác định tiêu đề header
            val header = tx.transactionDate
//                when {
//                isSameDay(cal, today) -> context.getString(R.string.today)
//                isSameDay(cal, yesterday) -> context.getString(R.string.yesterday)
//                else -> tx.transactionDate
//            }

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
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(
            Calendar.DAY_OF_YEAR
        )
    }

    //transfer money
    fun getTransferAccount() {
        viewModelScope.launch {
            useCaseTransferAccount.invoke(UseCaseTransferAccount.Params(true)).collect { result ->
                result.onSuccess { accountData ->
                    listTransferAccount = accountData.array
                    val account =
                        listTransferAccount.firstOrNull { it.accountNumber == selectedAccount?.accountNumber }
                            ?: listTransferAccount.firstOrNull()
                    _stateTransferAccount.value = account
                }
                result.onFailure { error ->
                    stateError(error)
                }
                result.onLoading { }
            }
        }
    }

    fun listenChangeFromAccount(account: AccountBase) {
//        selectedAccount = account
        listReceiverActive = listReceiverAccount.filter { it.currencyCode == account.currencyCode }
        _stateReceiverAccount.value = listReceiverActive
    }

    fun getReceiverAccount() {
        viewModelScope.launch {
            useCaseTransferAccount.invoke(UseCaseTransferAccount.Params(false)).collect { result ->
                result.onSuccess { accountData ->
                    listReceiverAccount = accountData.array
                    selectedAccount?.let { listenChangeFromAccount(it) }
                }
                result.onFailure { error ->
                    stateError(error)
                }
                result.onLoading { }
            }
        }
    }

    fun postTransactionTransfer(
        type: String,
        fromAccount: String,
        toAccount: String,
        amount: Double,
        currency: String,
        remarks: String
    ) {
        viewModelScope.launch {
            val params = FundTransferRequest(
                orderDetail = OrderDetail(
                    paymentType = type,
                    amount = amount,
                    currency = currency,
                    remark = remarks,
                    saveNewAccount = true
                ),
                sender = AccountInfoRequest(accountNo = fromAccount),
                beneficiary = AccountInfoRequest(accountNo = toAccount)
            )
            useCaseTransactionTransfer.invoke(params).collect { result ->
                result.onResultHandle({ transactionTransferData ->
                    viewModelScope.launch {
                        _stateTransactionTransfer.send(transactionTransferData)
                        transactionTransferRealtime = transactionTransferData
                    }
                }, { reason ->
                    viewModelScope.launch {
                        stateError(reason)
                    }
                })
            }
        }
    }

    fun confirmTransactionTransfer(otp: String) {
        viewModelScope.launch {
            val currentTransfer = transactionTransferRealtime ?: return@launch
            val params = UseCaseTransactionTransferConfirm.Params(
                transactionId = currentTransfer.transactionId.toString(),
                confirmStatus = ApiConst.ACCEPTED,
                otp = otp
            )
            useCaseTransactionTransferConfirm.invoke(params).collect { result ->
                result.onResultHandle({ transactionTransferConfirmData ->
                    viewModelScope.launch {
                        _stateTransactionTransferConfirm.emit(transactionTransferConfirmData)
                    }
                }, { reason ->
                    viewModelScope.launch {
                        if (listErrorCodeConfirmContinue.contains(reason.errorCode)) {
                            stateError(reason)
                        } else {
                            _stateTransferConfirmError.send(reason)
                        }
                    }
                })
            }
        }
    }

    fun getAccountByNumber(accountNumber: String) {
        viewModelScope.launch {
            useCaseAccountByNumber.invoke(UseCaseAccountByNumber.Params(accountNumber))
                .collectLatest { result ->
                    result.onResultHandle(failureBlock = { error ->
                        handleErrorFillAccountNumber(error)
                    }, successBlock = { accountUserName ->
                        viewModelScope.launch {
                            _stateAccountByNumber.emit(accountUserName)
                        }
                    })
                }
        }
    }

    private fun handleErrorFillAccountNumber(error: Reason) {
        viewModelScope.launch {
            when (error.errorCode) {
                "AUTH-005", "AUTH-001", "AUTH-002", "AUTH-006" -> {
                    stateError(error)
                }

                else -> {
                    _stateErrorFillAccountNumber.emit(true)
                }
            }
        }
    }

    fun getTransactionDetail(params: UseCaseTransactionDetail.Params) {
        viewModelScope.launch {
            useCaseTransactionDetail.invoke(params).collect { result ->
                result.onSuccess { trans ->
                    _stateTransactionDetail.send(trans)
                }
                result.onFailure { error ->
                    _stateDetailError.send(error)
                }
                result.onLoading { }
            }
        }
    }

    fun validateTransaction(params: UseCaseValidateTransaction.Params, callFinish: () -> Unit) {
        viewModelScope.launch {
            useCaseValidateTransaction.invoke(params).collect { resultSHB ->
                resultSHB.onResultHandle(successBlock = {
                    viewModelScope.launch {
                        _stateValidateTransaction.emit("")
                        callFinish.invoke()
                    }
                }, failureBlock = { reason ->
                    viewModelScope.launch {
                        stateError(reason)
                        callFinish.invoke()
                    }
                })
            }
        }
    }

    fun clearSessionTransaction() {
        confirmModel = null
        viewModelScope.launch {
            _stateTransactionTransferConfirm.emit(null)
            transactionTransferRealtime = null
        }
    }

    private suspend fun <T> ResultSHB<T>.onResultHandle(
        successBlock: (T) -> Unit,
        failureBlock: (Reason) -> Unit,
        loadingBlock: (() -> Unit)? = null
    ) {
        when (this) {
            is ResultSHB.Success -> {
                successBlock(successData)
                stateLoading(false)
            }

            is ResultSHB.Failure -> {
                failureBlock(reason)
                stateLoading(false)
            }

            is ResultSHB.Loading -> {
                loadingBlock?.invoke()
                stateLoading(true)
            }
        }
    }

    fun getAllBeneficiary() {
        val listB = arrayListOf<BeneficiaryUser>()
        for (i in 0..9) {
            listB.add(
                BeneficiaryUser(
                    nameUser = "MS VATHANA SISANE",
                    accountNumber = (i * 1111111111).toString(),
                    nameAddressBank = "Saigon – Hanoi Commercial Joint Stock Bank",
                    bankCode = "3"
                )
            )
        }
        _stateAllBeneficiary.value = listB
    }

    fun setDefaultAccount(accountNo: String) {
        viewModelScope.launch {
            val params = UseCaseSetDefaultAccount.Params(accountNo)
            useCaseSetDefaultAccount.invoke(params).collect { result ->
                result.onResultHandle(
                    successBlock = {
                        // Refresh user info to update default account
                        getUserInfo(true)
                        viewModelScope.launch {
                            stateLoading(false)
                        }
                    },
                    failureBlock = { error ->
                        viewModelScope.launch {
                            stateLoading(false)
                            stateError(error)
                            _stateUpdateDefaultAccount.send(false)
                        }
                    },
                    loadingBlock = {
                        viewModelScope.launch { stateLoading(true) }
                    }
                )
            }
        }
    }
}
