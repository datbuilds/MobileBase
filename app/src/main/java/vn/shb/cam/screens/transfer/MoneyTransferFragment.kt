package vn.shb.cam.screens.transfer

import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.text.method.DigitsKeyListener
import android.util.Log
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import vn.shb.cam.R
import vn.shb.cam.base.BaseFragmentBinding
import vn.shb.cam.databinding.FragmentMoneyTransferBinding
import vn.shb.cam.navigation.AppDestination
import vn.shb.cam.screens.beneficiary.BeneficiaryViewModel
import vn.shb.cam.screens.home.DialogSelectAccount
import vn.shb.cam.screens.home.DialogSelectBeneficiary
import vn.shb.cam.utils.ApiConst
import vn.shb.cam.utils.extensions.DateTimeHelper.Companion.getDateFromCurrentDate
import vn.shb.cam.utils.extensions.common.Const
import vn.shb.cam.utils.extensions.gone
import vn.shb.cam.utils.extensions.hideSoftKeyboard
import vn.shb.cam.utils.extensions.launchRepeatOnLifecycle
import vn.shb.cam.utils.extensions.serializable
import vn.shb.cam.utils.extensions.setLatinAlphanumericFilter
import vn.shb.cam.utils.extensions.visible
import vn.shb.core.core.domain.source.response.AccountUserNameModel
import vn.shb.core.core.domain.source.response.AiPayResult
import vn.shb.core.core.domain.usecases.transfer.UseCaseValidateTransaction
import vn.shb.core.utils.extesions.setOnSingleClickListener
import vn.shb.data.entities.AccountBase
import vn.shb.data.entities.beneficiary.Beneficiary
import vn.shb.data.entities.formatExchangeRate
import vn.shb.data.entities.getBalance
import vn.shb.data.entities.getBalanceFormatted
import vn.shb.data.entities.hasDecimal
import vn.shb.data.entities.home.AccountInfo
import vn.shb.data.entities.transfer.ConfirmationModel
import vn.shb.data.entities.transfer.TransferAccount
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.roundToInt

class MoneyTransferFragment : BaseFragmentBinding<FragmentMoneyTransferBinding>(
    FragmentMoneyTransferBinding::inflate
) {
    private val MAX_LENGTH_INPUT_AMOUNT = 15
    private val beneficiaryViewModel: BeneficiaryViewModel by viewModel()
    private var listBeneficiary: List<Beneficiary> = listOf()
    var currentTypeTransfer: String = INTRABANK

    private var exChangeScreen: String? = null

    private var fromAccount: AccountBase? = null
        set(value) {
            field = value
            try {
                if (isAdded) {
                    updateCurrencySelectorState()
                }
            } catch (_: Exception) {
            }
        }
    private var toAccount: AccountBase? = null
        set(value) {
            field = value
            stateAmountInput(value != null)
            try {
                if (isAdded && value != null) updateCurrencySelectorState()
            } catch (_: Exception) {
            }
        }

    private var aiPayResult: AiPayResult? = null


    var remarks = ""

    private var currentCurrencyChoose = ""
        set(value) {
            field = value
            try {
                if (isAdded) {
                    binding.iclAmount.edtValue.setValidateDataPasteToAmount(
                        true,
                        value,
                        MAX_LENGTH_INPUT_AMOUNT
                    )
                }
            } catch (_: Exception) {
            }
        }

    companion object {
        const val INTRABANK = "INTRABANK"
        const val OWN_ACCOUNT = "OWN_ACCOUNT"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        toAccount = null
    }

    override fun isPaddingBottom(): Boolean {
        return true
    }

    override fun initView(view: View) {
        bindView()
        updateViewTypeTransfer()
        resetStateTransfer()
        beneficiaryViewModel.getAllBeneficiary()
        beneficiaryViewModel.getBanks()

        if (arguments?.containsKey(ApiConst.KEY_TYPE_TRANSFER_DATA) == true) {
            aiPayResult = arguments?.serializable(ApiConst.KEY_TYPE_TRANSFER_DATA)
        }

//        lifecycleScope.launch {
//            delay(600)
//            if (aiPayResult != null) {
//                bindViewPay(aiPayResult)
//            }
//        }
    }

    private fun bindViewPay(aiPayResult: AiPayResult?) {
        with(binding) {
            iclToAccount.edtValue.setText(aiPayResult?.accountNum)

            if (fromAccount?.currencyCode != (aiPayResult?.currency ?: Const.USD)) {
                bindExchangeCurrency(fromAccount?.currencyCode)
            } else {
                bindExchangeCurrency(null)
            }

            updateChooseCurrency(aiPayResult?.currency ?: Const.USD)
            iclAmount.edtValue.setText(formatAiAmount(aiPayResult?.amount))

            if (aiPayResult?.remark.isNullOrEmpty()) {
                resetRemarks()
            } else {
                binding.iclRemarks.edtValue.setText(aiPayResult!!.remark)
            }

        }
        homeViewModel.getAccountByNumber(aiPayResult!!.accountNum ?: "")
    }

    private fun formatAiAmount(amount: BigDecimal?): String {
        if (amount == null) return ""
        return amount.stripTrailingZeros().toPlainString()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onChangeTypeTransfer(INTRABANK)
        homeViewModel.getTransferAccount()
        homeViewModel.getExchangeRates(Const.USD, Const.KHR)
        homeViewModel.getExchangeRates(Const.KHR, Const.USD)
    }

    override fun onResume() {
        super.onResume()
    }

    override fun initObserve() {
        launchRepeatOnLifecycle {
            with(homeViewModel) {
                launch {
                    stateTransferAccount.collect {
                        bindViewFromAccount(it ?: TransferAccount())
                        homeViewModel.getReceiverAccount()
                    }
                }

                launch {
                    stateReceiverAccount.collect {
                        updateStatusByListReceiverAccount(it)
                    }
                }

                launch {
                    stateAccountByNumber.collect {
                        if (it != null && isIntrabank()) {
                            bindViewSelectReceiverAccount(it)
                        }
                    }
                }

                launch {
                    stateErrorFillAccountNumber.collect {
                        if (isIntrabank()) {
                            errorAccountNumber(getString(R.string.invalidBeneficiaryAccount))
                        }
                    }
                }

                launch {
                    stateExchangeUSDToKm.collect { rateModel ->
                        exchangeUSDToKm = rateModel?.exchangeRate
                    }
                }

                launch {
                    stateExchangeKmToUSD.collect { rateModel ->
                        exchangeKmToUSD = rateModel?.exchangeRate
                    }
                }
            }
            with(beneficiaryViewModel) {
                launch {
                    stateAllBeneficiary.collect { list ->
                        listBeneficiary = list.filter { it.bankCode == "SHB" }
                    }
                }
            }
        }
    }

    /*
    xử lý khi nhập số tài khoản nhận không hợp lệ
    * */
    private fun errorAccountNumber(value: String) {
        binding.iclToAccount.tvError.apply {
            text = value
            visible()
        }
        binding.iclAccountName.apply {
            root.gone()
            edtValue.setText("")
        }
        toAccount = null
        beneficiarySelected = null
        bindExchangeCurrency(null)

        binding.iclAmount.edtValue.setText("")
        binding.iclAmount.bindViewError(null)
        binding.iclAmount.tvCurrentCode.gone()
        binding.iclTotalAmount.edtValue.setText("")
        updateStatusTransfer()
    }

    private fun stateAmountInput(value: Boolean) {
        try {
            binding.iclAmount.edtValue.enableInput(value)
            if (!value) binding.iclAmount.edtValue.setText(Const.EMPTY)
        } catch (e: Exception) {
        }
    }

    private fun updateCurrencySelectorState() {
        try {
            val fromCur = fromAccount?.currencyCode
            val toCur = toAccount?.currencyCode

            if (fromAccount == null || toAccount == null) {
                binding.iclAmount.tvCurrentCode.gone()
                return
            }

            val isSame = fromCur == toCur

            binding.iclAmount.tvCurrentCode.apply {
                visible()
                if (isSame) {
                    setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)
                    isClickable = false
                } else {
                    setCompoundDrawablesRelativeWithIntrinsicBounds(
                        0,
                        0,
                        R.drawable.ic_arrow_down_black,
                        0
                    )
                    isClickable = true
                }
            }
        } catch (e: Exception) {
        }
    }

    override fun initListener() {
        with(binding) {
            //click title
            iclExchangeRate.edtValue.setStateShowButtonClear(false)
            iclTotalAmount.edtValue.setStateShowButtonClear(false)

            iclToAccount.edtValue.doAfterTextChanged {
                iclToAccount.bindViewError(null)
            }

            tvMoneyTransferTitle.setOnSingleClickListener {
                backPress()
            }

            tvIntraBankTransfer.setOnSingleClickListener {
                onChangeTypeTransfer(INTRABANK)
            }

            tvOwnAccountTransfer.setOnSingleClickListener {
                onChangeTypeTransfer(OWN_ACCOUNT)
            }

            // click option value
            tvAccountNumber.setOnSingleClickListener {
                DialogSelectAccount.Build(
                    homeViewModel.listTransferAccount, fromAccount
                ) { selectedAccount ->
//                    bindViewFromAccount(selectedAccount)
                    fromAccount = selectedAccount
                    tvAccountNumber.text = selectedAccount.accountNumber
                    tvBalanceValue.text = selectedAccount.getAvailableBalance()
                    tvCurrencyValue.text = selectedAccount.currencyCode
                    iclAmount.tvCurrentCode.text = selectedAccount.currencyCode
                    iclAmount.edtValue.setInputEditText(
                        true,
                        isTypeSigned = selectedAccount.currencyCode == "USD"
                    )
                    currentCurrencyChoose = selectedAccount.currencyCode

                    toAccount?.currencyCode?.let { toAccountCurrencyCode ->
                        if (isDiffCurrency(toAccountCurrencyCode)) {
                            bindExchangeCurrency(fromAccount?.currencyCode)
                        } else {
                            bindExchangeCurrency(null)
                        }

                        val isDuplicate = toAccount?.accountNumber == fromAccount?.accountNumber
                        iclToAccount.bindViewError(
                            if (isDuplicate) getString(
                                R.string.sourceAndRecipient
                            ) else null
                        )
                    }

                    val rawInput =
                        binding.iclAmount.edtValue.text.toString().trim().replace(",", "")
                    val enteredAmount = rawInput.toDoubleOrNull() ?: 0.0

                    if (enteredAmount.hasDecimal()) {
                        iclAmount.edtValue.setText("")
                    }

                    iclAmount.bindViewError(null)
                    checkAmountValidate()
                    iclAmount.edtValue.enableInput(true)
                    homeViewModel.listenChangeFromAccount(selectedAccount)

                }.build().show(childFragmentManager, DialogSelectAccount.TAG)
            }

            iclToAccount.ivExpandDown.setOnSingleClickListener {
                handleShowDialogSelectAccount()
            }

//            iclAmount.edtValue.setOnFocusChangeListener { v, hasFocus ->
//                if (!hasFocus) {
//                    if (binding.iclAmount.edtValue.text?.isEmpty() == true) {
//                        setEnableDone(false)
//                        binding.iclAmount.bindViewError(getString(R.string.pleaseEnterTheAmount))
//                    }
//                }
//            }

            iclAmount.edtValue.apply {
                addTextChangedListener(object : TextWatcher {

                    private var current = ""
                    private var editing = false

                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {

                    }

                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                    }

                    override fun afterTextChanged(s: Editable?) {
                        if (editing) return
                        val text = s?.toString() ?: return
                        if (text == current) return

                        editing = true
                        val currency = currentCurrencyChoose
                        val isUSD = currency == Const.USD

                        // 1. Lấy chuỗi thuần túy (Bỏ hết dấu phẩy)
                        var cleanText = text.replace(",", "")

                        // 2. Xử lý các trường hợp đặc biệt ngay từ đầu
                        if (cleanText.isNotEmpty()) {
                            if (!isUSD) {
                                // Nếu là KHR (không có thập phân) mà người dùng cố tình gõ dấu chấm -> Xóa dấu chấm
                                if (cleanText.contains(".")) {
                                    cleanText = cleanText.replace(".", "")
                                }
                                if (cleanText.startsWith("0")) {
                                    updateTextAndCurrent("")
                                    return
                                }
                            } else { // Nếu là USD
                                if (cleanText.startsWith(".")) {
                                    updateTextAndCurrent("0.")
                                    return
                                }
                                if (cleanText.startsWith("0") && cleanText.length > 1 && !cleanText.startsWith(
                                        "0."
                                    )
                                ) {
                                    val clean0 = cleanText.replaceFirst("^0+(?!$)".toRegex(), "")
                                    if (clean0 != cleanText) {
                                        updateTextAndCurrent(clean0)
                                        return
                                    }
                                }
                            }
                        }

                        try {
                            val cursorStart = selectionStart
                            var formattedResult = ""

                            // 3. LOGIC FORMAT TRÁNH LỖI DẤU CHẤM
                            if (isUSD && cleanText.contains(".")) {
                                // Cắt đôi chuỗi tại vị trí dấu chấm
                                val parts = cleanText.split(".")
                                val intPart = parts[0]
                                val decPart = parts[1]

                                // Chỉ gọi hàm format cho phần số nguyên
                                val formattedInt =
                                    if (intPart.isEmpty()) "0" else intPart.getBalanceFormatted()

                                // Ghép lại thành chuỗi hoàn chỉnh
                                formattedResult = "$formattedInt.$decPart"
                            } else {
                                // Không có dấu chấm thì format bình thường
                                formattedResult = cleanText.getBalanceFormatted()
                            }

                            // 4. KIỂM TRA GIỚI HẠN (MAX_LENGTH và THẬP PHÂN)
                            var isExceedLimit = false

                            if (isUSD && formattedResult.contains(".")) {
                                val parts = formattedResult.split(".")
                                val intPartLength = parts[0].length
                                val decPartLength = if (parts.size > 1) parts[1].length else 0

                                // Giới hạn: Phần nguyên không quá MAX_LENGTH, thập phân không quá 2
                                if (intPartLength > MAX_LENGTH_INPUT_AMOUNT || decPartLength > 2) {
                                    isExceedLimit = true
                                }
                            } else {
                                if (formattedResult.length > MAX_LENGTH_INPUT_AMOUNT) {
                                    isExceedLimit = true
                                }
                            }

                            if (isExceedLimit) {
                                // HỦY thao tác, trả về text cũ
                                setText(current)
                                val oldCursorPos =
                                    (cursorStart - (text.length - current.length)).coerceIn(
                                        0,
                                        current.length
                                    )
                                setSelection(oldCursorPos)
                                editing = false
                                return
                            }

                            // 5. CẬP NHẬT UI VÀ CON TRỎ NẾU HỢP LỆ
                            val diff = formattedResult.length - text.length
                            val newPos = (cursorStart + diff).coerceIn(0, formattedResult.length)

                            current = formattedResult
                            setText(formattedResult)
                            setSelection(newPos)

                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                        checkAmountValidate()

                        editing = false
                    }

                    // Hàm phụ
                    private fun updateTextAndCurrent(newVal: String) {
                        editing = true
                        setText(newVal)
                        if (newVal.isNotEmpty()) setSelection(newVal.length)
                        current = newVal
                        editing = false
                    }
                })
            }


            iclAmount.tvCurrentCode.setOnSingleClickListener {
                popupChooseCurrency(currentCurrencyChoose, binding.iclAmount.tvCurrentCode) {
                    updateChooseCurrency(it)
                }
            }


            iclRemarks.edtValue.setLatinAlphanumericFilter(200)

            iclRemarks.edtValue.doAfterTextChanged {
                iclRemarks.tvError.isVisible = it.toString().isBlank()
                remarks = it.toString()
                updateStatusTransfer()
            }

            tvTransferAction.setOnSingleClickListener {

                val amountInput = binding.iclAmount.edtValue.text.toString().trim()

                val amountSend =
                    if (amountInput.isNotBlank()) amountInput.replace(",", "").toDouble() else 0.0

//                val isValid =
//                    amountSend <= (fromAccount?.availableBalance ?: 0.0) && amountSend > 0.0

//                if (!isValid) return@setOnSingleClickListener

                tvTransferAction.isEnabled = false
                val param = UseCaseValidateTransaction.Params(
                    orderDetail = UseCaseValidateTransaction.OrderTransaction(
                        paymentType = if (isIntrabank()) ApiConst.INTRA else ApiConst.SELF,
                        amount = amountSend,
                        currency = currentCurrencyChoose
                    ),
                    sender = UseCaseValidateTransaction.AccountTransaction(
                        accountNo = fromAccount?.accountNumber ?: ""
                    ),
                    beneficiary = UseCaseValidateTransaction.AccountTransaction(
                        accountNo = toAccount?.accountNumber ?: ""
                    )
                )

                homeViewModel.validateTransaction(
                    param,
                    onSuccess = {
                        homeViewModel.confirmModel = getConfirmationStatus()
                        homeViewModel.exchangeRealtime = getExchangeRealtime()

//                        homeViewModel.exchangeUSDToKm = exchangeUSDToKm
//                        homeViewModel.exchangeKmToUSD = exchangeKmToUSD

                        safeNavigate(
                            AppDestination.Confirmation(
                                bundleOf(ApiConst.KEY_TYPE_TRANSFER_INTRABANK to isIntrabank())
                            )
                        )
                    }, callFinish = {
                        tvTransferAction.isEnabled = true
                    })
            }

            finishTyping(iclToAccount.edtValue, isIntrabank(), callBack = {
                beneficiarySelected = null
                resetRemarks()
                validateToAccount()
            })

            finishTyping(iclAmount.edtValue, true, callBack = {
                checkAmountValidate()
            }) { hasFocus ->
                if (!hasFocus) {
                    val textInput = binding.iclAmount.edtValue.text.toString()

                    if (textInput.isEmpty()) {
                        setEnableDone(false)
                        binding.iclAmount.bindViewError(getString(R.string.pleaseEnterTheAmount))
                    }

                    if (textInput.isNotEmpty() && currentCurrencyChoose == Const.USD && textInput.last() == '.') {
                        Log.e("TAG", "checkAmountValidate: ")
                        binding.iclAmount.edtValue.setText(textInput.dropLast(1))
                    }
                }
            }

        }
    }


    private fun updateChooseCurrency(currency: String) {
        val oldCurrency = currentCurrencyChoose
        currentCurrencyChoose = currency
        with(binding) {
            iclAmount.tvCurrentCode.text = currency
            if (oldCurrency != currency) {
                iclAmount.edtValue.setInputEditText(true, isTypeSigned = currency == Const.USD)
                iclAmount.edtValue.setText("")
                iclAmount.bindViewError(null)
                checkAmountValidate()
            }
        }
    }

    /*
    *  kiểm tra và call api kiểm tra tài khoản theo số được nhập
    * */
    fun validateToAccount(account: String? = null) {
        with(binding) {
            val textAccountNo = account ?: iclToAccount.edtValue.text.toString()
            when {
                textAccountNo.isEmpty() -> {
                    errorAccountNumber(getString(R.string.pleaseEnterToAccountNumber))
                }

                textAccountNo == fromAccount?.accountNumber -> {
                    errorAccountNumber(getString(R.string.sourceAndRecipient))
                }

                else -> {
                    homeViewModel.getAccountByNumber(iclToAccount.edtValue.text.toString())
                }
            }
        }
    }

    /*
    * kiểm tra và fill ra các trường khác sau khi nhập Amount
    */
    private fun checkAmountValidate() {
        updateUiTotalAmount()
        updateStatusTransfer()
    }

    private var beneficiarySelected: Beneficiary? = null

    /*
    * hiển thị list người huởng thụ
    * */
    private fun showListBeneficiary() {
        DialogSelectBeneficiary.Build(listBeneficiary, beneficiarySelected) { selectedAccount ->
            beneficiarySelected = selectedAccount
            if (binding.iclToAccount.edtValue.text.toString() != selectedAccount.accountNumber) {
                binding.iclToAccount.edtValue.setText(selectedAccount.accountNumber)
                validateToAccount(selectedAccount.accountNumber)
            } else {
                val remark = if (beneficiarySelected?.remark.isNullOrEmpty()) {
                    getCurrentUser()?.username.plus(Const.SEPARATOR_SPACE)
                        .plus(getString(R.string.transferCAP))
                } else {
                    beneficiarySelected?.remark
                }

                binding.iclRemarks.edtValue.setText(remark)
            }
        }.build().show(childFragmentManager, DialogSelectAccount.TAG)
    }

    /*
    *  lấy dữ liệu cho màn confirm phía sau
    * */


    /*
    *  xử lý khi click vào icon ở mục toAccount
    * */
    private fun handleShowDialogSelectAccount() {
        if (!isIntrabank()) {
            //show list tài khoản của bản thân
            DialogSelectAccount.Build(
                homeViewModel.listReceiverActive, toAccount
            ) { selectedAccount ->
                bindViewSelectReceiverAccount(selectedAccount)
            }.build().show(childFragmentManager, DialogSelectAccount.TAG)
        } else {
            // show list người  hưởng thụ
            showListBeneficiary()
        }
    }

    private fun roundExchangeValue(value: Double, currency: String): BigDecimal {
        return if (currency == Const.USD) {
            BigDecimal(value.toString()).setScale(2, RoundingMode.HALF_UP)
        } else {
            BigDecimal(value.toString()).setScale(3, RoundingMode.HALF_UP).stripTrailingZeros()
        }
    }

    private fun updateUiTotalAmount() {
        val rawInput = binding.iclAmount.edtValue.text.toString().trim().replace(",", "")
        val enteredAmount = rawInput.toDoubleOrNull() ?: 0.0

        val toCurrency = toAccount?.currencyCode ?: Const.KHR
        val totalAmount = calculateTotalAmount(enteredAmount, toCurrency)

        binding.iclTotalAmount.root.isVisible = rawInput.isNotBlank()
        if (rawInput.isNotBlank()) {
            val totalAmountFormat = if (toCurrency == Const.KHR) {
                totalAmount.roundToInt().toDouble().getBalance()
            } else {
                totalAmount.getBalance()
            }

            binding.iclTotalAmount.edtValue.setText(totalAmountFormat)
        }
    }

    /** * Logic tính toán tỷ giá thực tế
     */
    private fun calculateTotalAmount(amount: Double, toCurrency: String): Double {
        if (!isDiffCurrency(toCurrency)) return amount

        val rate = getExchangeRealtime()?.toDouble() ?: 0.0
        if (rate <= 0) return amount

        // Chỉ quy đổi nếu là cặp USD - KHR
        val isUsdToKhr = currentCurrencyChoose == Const.USD && toCurrency == Const.KHR
        val isKhrToUsd = currentCurrencyChoose == Const.KHR && toCurrency == Const.USD

        return if (isUsdToKhr || isKhrToUsd) {
            roundExchangeValue(amount * rate, toCurrency).toDouble()
        } else {
            amount
        }
    }

    /**
     * Đồng nhất số tiền về loại tiền của tài khoản nguồn (From Account)
     */
    private fun normalizeAmountForBalanceCheck(amount: Double): Double {
        return if (fromAccount?.currencyCode == Const.KHR && currentCurrencyChoose == Const.USD) {
            val rate = homeViewModel.exchangeUSDToKm?.toDouble() ?: 0.0
            amount * rate
        } else {
            amount
        }
    }

    /*
    *  hiển thị view khi vào màn
    * */
    private fun bindView() {
        with(binding) {
            iclToAccount.apply {
                tvTitle.text = getString(R.string.toAccount)
                edtValue.hint = getString(R.string.selectAccount)
                ivExpandDown.visible()
                tvCurrentCode.gone()
                edtValue.setInputEditText(true)
                tvError.text = getString(R.string.invalidBeneficiaryAccount)
            }

            iclExchangeRate.apply {
                tvTitle.text = getString(R.string.exchangeRate)
                ivExpandDown.gone()
                tvCurrentCode.gone()
                edtValue.setTextColor(getColor(R.color.neutral7))
                edtValue.enableInput(false)
                tvError.text = getString(R.string.rateAreIndicative)
                tvError.setTextColor(getColor(R.color.yellow))
                tvError.visible()
            }

            iclAccountName.apply {
                tvTitle.text = getString(R.string.accountName)
                edtValue.setTextColor(getColor(R.color.neutral7))
                edtValue.isEnabled = false
            }

            iclAmount.apply {
                tvTitle.text = getString(R.string.amount)
                edtValue.hint = getString(R.string.pleaseEnterTheAmount)
                ivExpandDown.gone()
                tvCurrentCode.gone()
                tvCurrentCode.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    0,
                    0,
                    R.drawable.ic_arrow_down_black,
                    0
                )
                tvError.text = getString(R.string.insufficientBalance)
                bindColor(R.color.neutral8)
            }

            iclTotalAmount.apply {
                edtValue.setValidateDataPasteToAmount(
                    true,
                    currentCurrencyChoose,
                    MAX_LENGTH_INPUT_AMOUNT
                )
                tvTitle.text = getString(R.string.totalAmount)
                edtValue.hint = getString(R.string.zero)
                ivExpandDown.gone()
                tvCurrentCode.visible()
                edtValue.enableInput(false)
                bindColor(R.color.neutral7)
            }

            iclRemarks.apply {
                tvTitle.text = getString(R.string.remarks)
                resetRemarks()
                ivExpandDown.gone()
//                ivExpandDown.setImageResource(R.drawable.ic_clear_text)
//                ivExpandDown.imageTintList =
//                    ColorStateList.valueOf(requireContext().getColorCompat(R.color.neutral8))
                tvCurrentCode.gone()
                tvError.text = getString(R.string.pleaseEnterTheRemarks)
                bindColor(R.color.neutral8)
                edtValue.setInputEditText(false)
                edtValue.hint = getString(R.string.enterRemarks)
                finishTyping(edtValue, true, callBack = {
                    val text = edtValue.text.toString()
                    edtValue.setText(text.cleanVietnameseText())
                })
            }
        }
        updateStatusTransfer()
    }

    fun resetRemarks() {
        remarks = getCurrentUser()?.username.plus(Const.SEPARATOR_SPACE)
            .plus(getString(R.string.transferCAP))
        binding.iclRemarks.edtValue.setText(remarks)
    }

    /*
    * xử lý khi click đổi type chuyển khoản
    * */
    private fun updateViewTypeTransfer() {
        with(binding) {
            viewOptionTransfer(tvIntraBankTransfer, INTRABANK)
            viewOptionTransfer(tvOwnAccountTransfer, OWN_ACCOUNT)
        }
    }


    /*
    * update trạng thái nút chuyển khoản
    * */
    private fun setEnableDone(isEnable: Boolean) {
        binding.tvTransferAction.apply {
            isEnabled = isEnable
            alpha = if (isEnable) 1f else 0.5f
        }
    }

    /*
    * nhận giá trị tài khoản gửi và update UI
    * */
    private fun bindViewFromAccount(account: AccountBase) {
        fromAccount = account
        toAccount = null



        with(binding) {
            tvAccountNumber.text = account.accountNumber
            tvBalanceValue.text = account.getAvailableBalance()
            tvCurrencyValue.text = account.currencyCode
            iclAmount.tvCurrentCode.text = account.currencyCode
            iclAmount.edtValue.setInputEditText(true, isTypeSigned = account.currencyCode == "USD")
            currentCurrencyChoose = account.currencyCode
        }

        if (aiPayResult != null) {
            bindViewPay(aiPayResult)
        }

        // cập nhật trạng thái của exchange rate
        toAccount?.currencyCode?.let { updateExchangeStatus(it) }
        updateUiTotalAmount()
        updateStatusTransfer()
    }

    /*
* nhận giá trị tài khoản nhận từ list tài khoản cá nhân rồi xử lý UI
* */
    private fun bindViewSelectReceiverAccount(account: AccountBase) {
        with(binding) {
            iclToAccount.edtValue.setText(account.accountNumber)
            val isDuplicate = account.accountNumber == fromAccount?.accountNumber
            iclToAccount.bindViewError(
                if (isDuplicate) getString(
                    R.string.sourceAndRecipient
                ) else null
            )
            // cập nhật trạng thái của exchange rate
            updateExchangeStatus(account.currencyCode)

            toAccount = account
            updateUiTotalAmount()
            iclTotalAmount.tvCurrentCode.text = account.currencyCode
        }

        updateStatusTransfer()
    }


    /*
    * nhận giá trị tài khoản nhận từ API ( case tự nhập stk hoặc chọn từ list người hưởng thụ)
    * xử lý UI tài khoản nhận
    * */
    private fun bindViewSelectReceiverAccount(userInfo: AccountUserNameModel) {
        binding.iclAccountName.apply {
            root.isVisible = true
            edtValue.setText(userInfo.customerName)
        }
        if (beneficiarySelected?.accountNumber == userInfo.accountNumber) {
            val remark = if (beneficiarySelected?.remark.isNullOrEmpty()) {
                getCurrentUser()?.username.plus(Const.SEPARATOR_SPACE)
                    .plus(getString(R.string.transferCAP))
            } else {
                beneficiarySelected?.remark
            }

            binding.iclRemarks.edtValue.setText(remark)
        }

        if (aiPayResult == null)
            updateExchangeStatus(userInfo.currency)
        else if (fromAccount?.currencyCode == userInfo.currency && fromAccount?.currencyCode != aiPayResult?.currency) {
            currentCurrencyChoose = fromAccount?.currencyCode ?: Const.KHR
            binding.iclAmount.tvCurrentCode.text = currentCurrencyChoose
            updateExchangeStatus(userInfo.currency)
        }

        toAccount = AccountInfo().apply {
            setValueAccountNumber(binding.iclToAccount.edtValue.text.toString())
            currencyCode = userInfo.currency
            productCode = userInfo.productCode
            productDescription = userInfo.productDescription
            customerName = userInfo.customerName
        }
        binding.iclTotalAmount.tvCurrentCode.text = userInfo.currency
        with(binding) {
            iclToAccount.tvError.gone()
            if (iclAmount.edtValue.text.toString().isNotEmpty()) {
                updateUiTotalAmount()
            } else {
                iclTotalAmount.edtValue.setText("")
                iclTotalAmount.root.gone()
            }
        }

        binding.iclAmount.bindViewError(null)

        updateStatusTransfer()
    }

    /*
 * reset lại trạng thái chuyển khoản của UI
 * */
    private fun bindExchangeCurrency(currency: String? = null) {
        with(binding.iclExchangeRate) {
            if (currency != null) {
                root.visible()
                edtValue.setText(getTextExchangeCurrency(currency))
            } else {
                root.gone()
            }
        }
    }

    private fun getTextExchangeCurrency(currency: String): String {
        try {
            exChangeScreen =
                if (currency == Const.USD) {
                    homeViewModel.exchangeUSDToKm?.formatExchangeRate()
                } else {
                    var exchangeRate = (homeViewModel.exchangeKmToUSD?.toDouble() ?: 1.0)

                    if (exchangeRate == 0.0) {
                        exchangeRate = 1.0
                    }

                    1.div(exchangeRate).formatExchangeRate()
                }

        } catch (e: Exception) {
            Log.e("TAG", "getTextExchangeCurrency: ")
        }


        return getString(
            R.string.formatTextExchangeCurrency,
            Const.USD,
            exChangeScreen,
            Const.KHR
        )
    }

    private fun getExchangeRealtime(): BigDecimal? {
        return if (currentCurrencyChoose == Const.USD) homeViewModel.exchangeUSDToKm else homeViewModel.exchangeKmToUSD
    }

    private fun isDiffCurrency(toCurrency: String): Boolean =
        run { currentCurrencyChoose != toCurrency }

    private fun resetStateTransfer() {
        toAccount = null
        with(binding) {
            iclToAccount.apply {
                ivExpandDown.setImageResource(if (!isIntrabank()) R.drawable.ic_arrow_down_black else R.drawable.ic_account_intrabank)
                edtValue.apply {
                    setText(Const.EMPTY)
                    edtValue.clearFocus()
                    hint =
                        if (isIntrabank()) getString(R.string.enterAccountNumber) else getString(R.string.selectAccount)

                    if (isIntrabank()) {
                        // Cho nhập bình thường
                        isEnabled = true
                        isFocusable = true
                        isFocusableInTouchMode = true
                        isCursorVisible = true
//                        keyListener = TextKeyListener.getInstance()
                        inputType = InputType.TYPE_CLASS_NUMBER
                        keyListener = DigitsKeyListener.getInstance("0123456789")
                        setOnClickListener(null)
                    } else {
                        // Không cho nhập nhưng vẫn click
                        isEnabled = true
                        isFocusable = false
                        isFocusableInTouchMode = false
                        isCursorVisible = false

                        // ❗ disable edit actions (copy/paste/select)
                        keyListener = null
                        setTextIsSelectable(false)
                        isLongClickable = false

                        setOnClickListener {
                            handleShowDialogSelectAccount()
                        }
                    }
                }

            }
            iclAmount.edtValue.setText(Const.EMPTY)
            iclToAccount.edtValue.setText(Const.EMPTY)
            iclExchangeRate.root.gone()
            iclTotalAmount.root.gone()
            iclAccountName.root.gone()
            iclToAccount.tvError.gone()
            iclAmount.bindViewError(null)
            iclRemarks.bindViewError(null)
            beneficiarySelected = null
            resetRemarks()
        }
        hideSoftKeyboard()
        updateStatusTransfer()
    }

    private fun isIntrabank() = run { currentTypeTransfer == INTRABANK }

    /*
    * update khi người dùng đổi type chuyển khoản
    * */
    private fun onChangeTypeTransfer(type: String) {
        if (currentTypeTransfer != type) {
            currentTypeTransfer = type
            updateViewTypeTransfer()
            resetStateTransfer()
        }
    }

    /*
    * update khi nhận được danh sách tài khoản cá nhân của bản thân
    * */
    private fun updateStatusByListReceiverAccount(list: List<AccountBase>) {
        if (!isIntrabank()) {
            with(binding.iclToAccount.edtValue) {
                if (list.isEmpty()) {
                    setText(getString(R.string.noAccountAvailable))
                }
//                else if (!isIntrabank()) {
//                    setText(Const.EMPTY)
//                }
            }
        }
    }

    private fun updateExchangeStatus(currencyCode: String) {
        if (isDiffCurrency(currencyCode)) {
            bindExchangeCurrency(fromAccount?.currencyCode)
        } else {
            bindExchangeCurrency(null)
        }
    }

    /*
*check state enable button tranfer
* */
    private fun updateStatusTransfer() {

        if (fromAccount == null || toAccount == null || fromAccount?.accountNumber == null || toAccount?.accountNumber == null) {
            setEnableDone(false)
            return
        }

        val rawInput = binding.iclAmount.edtValue.text.toString().trim().replace(",", "")
        val amountInput = rawInput.toDoubleOrNull() ?: 0.0

        if (amountInput == 0.0 && binding.iclAmount.edtValue.text?.isEmpty() == true) {
            setEnableDone(false)
            return
        }

        val currencyFrom = fromAccount!!.currencyCode
        val currencyTo = toAccount!!.currencyCode

        var isAmountEnoughToTransfer: Boolean
        var isAmountGreaterThanMin: Boolean
        var messageErrorAmoun: String? = null

        val minAmountUSDTransfer = 0.01
        val minAmountKHRTransfer = 0.0

        when {
            currencyFrom == Const.USD && currentCurrencyChoose == Const.USD && currencyTo == Const.KHR -> {
                isAmountEnoughToTransfer = amountInput <= (fromAccount?.availableBalance ?: 0.0)
                isAmountGreaterThanMin = amountInput >= minAmountUSDTransfer

                messageErrorAmoun = getString(R.string.minumumTransferAmount)
            }

            currencyFrom == Const.USD && currentCurrencyChoose == Const.KHR && currencyTo == Const.KHR -> {
                val rate = (homeViewModel.exchangeUSDToKm?.toDouble() ?: 0.0).toBigDecimal()
                val amountAfterCalculate =
                    amountInput.toBigDecimal().divide(rate, 2, RoundingMode.HALF_UP)
                val accountAvailableBalance = (fromAccount?.availableBalance ?: 1.0).toBigDecimal()

                isAmountEnoughToTransfer = amountAfterCalculate <= accountAvailableBalance
                isAmountGreaterThanMin =
                    amountAfterCalculate >= BigDecimal.valueOf(minAmountUSDTransfer)
                messageErrorAmoun = getString(R.string.minumumTransferAmount)

            }

            currencyFrom == Const.KHR && currentCurrencyChoose == Const.KHR && currencyTo == Const.USD -> {
                val rate = (homeViewModel.exchangeKmToUSD?.toDouble() ?: 0.0).toBigDecimal()
                val amountAfterCalculate =
                    amountInput.toBigDecimal().multiply(rate).setScale(2, RoundingMode.HALF_UP)
                val accountAvailableBalance = (fromAccount?.availableBalance ?: 1.0).toBigDecimal()

                isAmountEnoughToTransfer = amountInput.toBigDecimal() <= accountAvailableBalance
                isAmountGreaterThanMin =
                    amountAfterCalculate >= BigDecimal.valueOf(minAmountUSDTransfer)

                messageErrorAmoun = getString(R.string.minumumCreditAmount)
            }

            currencyFrom == Const.KHR && currentCurrencyChoose == Const.USD && currencyTo == Const.USD -> {
                val rate = (1 / (homeViewModel.exchangeKmToUSD?.toDouble() ?: 0.0)).toBigDecimal()
                val amountKHR =
                    amountInput.toBigDecimal().multiply(rate).setScale(7, RoundingMode.HALF_UP)
                val accountAvailableBalance = (fromAccount?.availableBalance ?: 1.0).toBigDecimal()

                isAmountEnoughToTransfer = amountKHR <= accountAvailableBalance
                isAmountGreaterThanMin = amountInput.toBigDecimal()
                    .setScale(2, RoundingMode.HALF_UP) >= BigDecimal.valueOf(
                    minAmountUSDTransfer
                )
                messageErrorAmoun = getString(R.string.minumumCreditAmount)

            }

            currencyFrom == currencyTo && currentCurrencyChoose == Const.KHR -> {
                isAmountEnoughToTransfer = amountInput <= (fromAccount?.availableBalance ?: 0.0)
                isAmountGreaterThanMin = amountInput > minAmountKHRTransfer

                messageErrorAmoun = getString(R.string.minumumCreditAmountKHR)

            }

            currencyFrom == currencyTo && currentCurrencyChoose == Const.USD -> {
                isAmountEnoughToTransfer = amountInput <= (fromAccount?.availableBalance ?: 0.0)
                isAmountGreaterThanMin = amountInput >= minAmountUSDTransfer

                messageErrorAmoun = getString(R.string.minumumTransferAmount)
            }

            else -> {
                isAmountEnoughToTransfer = false
                isAmountGreaterThanMin = false
            }
        }

        if (!isAmountGreaterThanMin) {
            setEnableDone(false)
            binding.iclAmount.bindViewError(messageErrorAmoun)
            return
        }

        if (!isAmountEnoughToTransfer) {
            setEnableDone(false)
            binding.iclAmount.bindViewError(getString(R.string.insufficientBalance))
            return
        }

        binding.iclAmount.bindViewError(null)

        if (fromAccount?.accountNumber == toAccount?.accountNumber || remarks.isEmpty()) {
            setEnableDone(false)
            return
        }

        setEnableDone(true)
    }

    private fun getConfirmationStatus(): ConfirmationModel? {

        if (fromAccount == null || toAccount == null) {
            setEnableDone(false)
            return null
        }

        val rawInput = binding.iclAmount.edtValue.text.toString().trim().replace(",", "")
        val amountInput = rawInput.toDoubleOrNull() ?: 0.0

        val rawInputAmountReceive =
            binding.iclTotalAmount.edtValue.text.toString().trim().replace(",", "")
        val amountAmountReceive = rawInputAmountReceive.toDoubleOrNull() ?: 0.0

        return ConfirmationModel(
            fromAccount = fromAccount!!,
            toAccount = toAccount!!,
            remarks = remarks,
            transactionDate = getDateFromCurrentDate(),
            amount = Pair(amountInput, currentCurrencyChoose),
            totalAmount = Pair(amountAmountReceive, toAccount!!.currencyCode),
            paymentType = if (isIntrabank()) ApiConst.INTRA else ApiConst.SELF,
            exchangeRateScreen = exChangeScreen
        )
    }

}
