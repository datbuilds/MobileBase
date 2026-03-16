package vn.shb.cam.screens.transfer

import android.text.InputFilter
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import vn.shb.cam.R
import vn.shb.cam.base.BaseFragmentBinding
import vn.shb.cam.databinding.FragmentMoneyTransferBinding
import vn.shb.cam.screens.beneficiary.BeneficiaryViewModel
import vn.shb.cam.screens.home.DialogSelectAccount
import vn.shb.cam.screens.home.DialogSelectBeneficiary
import vn.shb.cam.screens.home.getTypeAccount
import vn.shb.cam.utils.ApiConst
import vn.shb.cam.utils.extensions.DateTimeHelper.Companion.getDateFromCurrentDate
import vn.shb.cam.utils.extensions.common.Const
import vn.shb.cam.utils.extensions.gone
import vn.shb.cam.utils.extensions.launchRepeatOnLifecycle
import vn.shb.cam.utils.extensions.visible
import vn.shb.core.core.domain.source.response.AccountUserNameModel
import vn.shb.core.core.domain.usecases.transfer.UseCaseValidateTransaction
import vn.shb.core.utils.extesions.setOnSingleClickListener
import vn.shb.data.entities.AccountBase
import vn.shb.data.entities.beneficiary.Beneficiary
import vn.shb.data.entities.getBalance
import vn.shb.data.entities.home.AccountInfo
import vn.shb.data.entities.transfer.ConfirmationModel
import vn.shb.data.entities.transfer.TransferAccount
import java.math.BigDecimal
import java.math.RoundingMode

class MoneyTransferFragment : BaseFragmentBinding<FragmentMoneyTransferBinding>(
    FragmentMoneyTransferBinding::inflate
) {
    private val beneficiaryViewModel: BeneficiaryViewModel by viewModel()
    private var listBeneficiary: List<Beneficiary> = listOf()
    var currentTypeTransfer: String = INTRABANK

    private var fromAccount: AccountBase? = null
    private var toAccount: AccountBase? = null
    private var amountOfSender: Double = 0.0
    private var totalAmount: Double = 0.0

    private var exchangeUSDToKm: BigDecimal? = BigDecimal.ZERO
    private var exchangeKmToUSD: BigDecimal? = BigDecimal.ZERO

    var remarks = ""

    private var currentCurrencyChoose = ""

    companion object {
        const val INTRABANK = "INTRABANK"
        const val OWN_ACCOUNT = "OWN_ACCOUNT"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        toAccount = null
        amountOfSender = 0.0
        totalAmount = 0.0
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
    }

    override fun onResume() {
        super.onResume()
        onChangeTypeTransfer(INTRABANK)
        homeViewModel.getTransferAccount()
        homeViewModel.getExchangeRates(Const.USD, Const.KHR)
        homeViewModel.getExchangeRates(Const.KHR, Const.USD)
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
                        if (it != null) {
                            bindViewSelectReceiverAccount(it)
                        }
                    }
                }

                launch {
                    stateErrorFillAccountNumber.collect {
                        errorAccountNumber(getString(R.string.invalidBeneficiaryAccount))
                    }
                }

                launch {
                    stateValidateTransaction.collect {
                        homeViewModel.confirmModel = getConfirmationStatus()
                        homeViewModel.exchangeRealtime = getExchangeRealtime()
                        safeNavigate(
                            R.id.moneyTransferFragment, R.id.confirmationFragment,
                            bundleOf(ApiConst.KEY_TYPE_TRANSFER_INTRABANK to isIntrabank())
                        )
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

    private fun errorAccountNumber(value: String) {
        binding.iclToAccount.tvError.apply {
            text = value
            visible()
        }
        binding.iclAccountName.apply {
            root.gone()
            edtValue.setText("")
        }
    }

    override fun initListener() {
        with(binding) {
            //click title
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
                    bindViewFromAccount(selectedAccount)
                    homeViewModel.listenChangeFromAccount(selectedAccount)
                }.build().show(childFragmentManager, DialogSelectAccount.TAG)
            }

            iclToAccount.ivExpandDown.setOnSingleClickListener {
                handleShowDialogSelectAccount()
            }

            iclAmount.edtValue.setupDecimalInput { currentCurrencyChoose }

            iclAmount.tvCurrentCode.setOnSingleClickListener {
                popupChooseCurrency(currentCurrencyChoose, binding.iclAmount.tvCurrentCode) {
                    updateChooseCurrency(it)
                }
            }

            iclRemarks.edtValue.doAfterTextChanged {
                iclRemarks.tvError.isVisible = it.toString().isBlank()
                remarks = it.toString()
                updateStatusTransfer()
            }

            tvTransferAction.setOnSingleClickListener {
                tvTransferAction.isEnabled = false
                homeViewModel.validateTransaction(
                    UseCaseValidateTransaction.Params(
                        orderDetail = UseCaseValidateTransaction.OrderTransaction(
                            paymentType = if (isIntrabank()) ApiConst.INTRA else ApiConst.SELF,
                            amount = amountOfSender,
                            currency = fromAccount?.currencyCode!!
                        ),
                        sender = UseCaseValidateTransaction.AccountTransaction(
                            accountNo = fromAccount?.accountNumber ?: ""
                        ),
                        beneficiary = UseCaseValidateTransaction.AccountTransaction(
                            accountNo = toAccount?.accountNumber ?: ""
                        )
                    )
                ) {
                    tvTransferAction.isEnabled = true
                }
            }

            finishTyping(iclToAccount.edtValue, isIntrabank()) {
                validateToAccount()
            }

            finishTyping(iclAmount.edtValue, true) {
                checkAmountValidate()
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
                checkAmountValidate()
            }
        }
    }

    fun validateToAccount(account: String? = null) {
        with(binding) {
            val textAccountNo = account ?: iclToAccount.edtValue.text.toString()
            when {
                textAccountNo.isEmpty() -> {
                    errorAccountNumber(getString(R.string.pleaseEnterTheAccountNumber))
                }

                textAccountNo == fromAccount?.accountNumber -> {
                    errorAccountNumber(getString(R.string.invalidBeneficiaryAccount))
                }

                else -> {
                    homeViewModel.getAccountByNumber(iclToAccount.edtValue.text.toString())
                }
            }
        }
    }

    private fun checkAmountValidate() {
        checkBalanceInvalid()
        updateStatusTransfer()
    }

    private var beneficiarySelected: Beneficiary? = null

    private fun showListBeneficiary() {
        DialogSelectBeneficiary.Build(listBeneficiary) { selectedAccount ->
            with(binding.iclToAccount.edtValue) {
                if (this.text.toString() != selectedAccount.accountNumber) {
                    beneficiarySelected = selectedAccount
                    this.setText(selectedAccount.accountNumber)
                    validateToAccount(selectedAccount.accountNumber)
                }
            }
        }.build().show(childFragmentManager, DialogSelectAccount.TAG)
    }

    private fun getConfirmationStatus(): ConfirmationModel {
        return ConfirmationModel(
            fromAccount!!, toAccount!!, remarks, transactionDate = getDateFromCurrentDate(),
            amountOfSender, 0.0, totalAmount,
            paymentType = if (isIntrabank()) ApiConst.INTRA else ApiConst.SELF
        )
    }

    private fun handleShowDialogSelectAccount() {
        if (!isIntrabank()) {
            DialogSelectAccount.Build(
                homeViewModel.listReceiverActive, toAccount
            ) { selectedAccount ->
                bindViewSelectReceiverAccount(selectedAccount)
            }.build().show(childFragmentManager, DialogSelectAccount.TAG)
        } else {
            showListBeneficiary()
        }
    }

    private fun roundExchangeValue(value: Double, currency: String): Double {
        return if (currency == Const.USD) {
            BigDecimal(value.toString()).setScale(2, RoundingMode.HALF_UP).toDouble()
        } else {
            BigDecimal(value.toString()).setScale(0, RoundingMode.HALF_UP).toDouble()
        }
    }

    private fun checkBalanceInvalid() {
        val valueBalance = binding.iclAmount.edtValue.text.toString().trim()
        amountOfSender =
            if (valueBalance.isNotBlank()) valueBalance.replace(",", "").toDouble() else 0.0

        val toCurrency = toAccount?.currencyCode ?: Const.KHR
        totalAmount = if (isDiffCurrency(toCurrency)) {
            val rate = getExchangeRealtime()?.toDouble() ?: 0.0
            if (rate > 0) {
                if (currentCurrencyChoose == Const.USD && toCurrency == Const.KHR) {
                    roundExchangeValue(amountOfSender * rate, toCurrency)
                } else if (currentCurrencyChoose == Const.KHR && toCurrency == Const.USD) {
                    roundExchangeValue(amountOfSender * rate, toCurrency)
                } else {
                    amountOfSender
                }
            } else {
                amountOfSender
            }
        } else {
            amountOfSender
        }

        binding.iclTotalAmount.apply {
            edtValue.setText(totalAmount.getBalance())
            if (!valueBalance.isEmpty()) root.visible()
        }

        val isError = amountOfSender > (fromAccount?.availableBalance ?: 0.0)
        val textError = when {
            valueBalance.isEmpty() || valueBalance == "0" || (currentCurrencyChoose == Const.USD && amountOfSender == 0.0) -> getString(
                R.string.pleaseEnterTheAmount
            )

            isError -> getString(R.string.insufficientBalance)
            else -> null
        }
        binding.iclAmount.bindViewError(textError)
    }

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
                edtValue.hint = getString(R.string.enterAmount)
                ivExpandDown.gone()
                tvCurrentCode.text = currentTypeTransfer
                tvCurrentCode.visible()
                tvCurrentCode.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    0,
                    0,
                    R.drawable.ic_arrow_down_black,
                    0
                )
                tvError.text = getString(R.string.insufficientBalance)
                bindColor(R.color.neutral8)
            }

//            iclFee.apply {
//                tvTitle.text = getString(R.string.fee)
//                edtValue.setText(Const.ZERO)
//                ivExpandDown.gone()
//                tvCurrentCode.visible()
//                edtValue.enableInput(false)
//                bindColor(R.color.neutral7)
//            }

            iclTotalAmount.apply {
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
                tvCurrentCode.gone()
                tvError.text = getString(R.string.pleaseEnterTheRemarks)
                bindColor(R.color.neutral8)
                edtValue.setInputEditText(false)
                edtValue.hint = getString(R.string.enterRemarks)
                finishTyping(iclRemarks.edtValue, true) {
                    val text = iclRemarks.edtValue.text.toString()
                    edtValue.setText(text.cleanVietnameseText())
                }
            }
        }
        updateStatusTransfer()
    }

    private fun updateViewTypeTransfer() {
        with(binding) {
            viewOptionTransfer(tvIntraBankTransfer, INTRABANK)
            viewOptionTransfer(tvOwnAccountTransfer, OWN_ACCOUNT)
        }
    }

    private fun updateStatusTransfer() {
        if (
            fromAccount?.accountNumber != null
            && toAccount?.accountNumber != null
            && fromAccount?.accountNumber != toAccount?.accountNumber
            && fromAccount?.currencyCode?.isNotEmpty() == true
            && amountOfSender <= (fromAccount?.availableBalance ?: 0.0) && amountOfSender > 0.0
            && remarks.isNotEmpty()
        ) {
            setEnableDone(true)
        } else {
            setEnableDone(false)
        }
    }

    private fun setEnableDone(isEnable: Boolean) {
        binding.tvTransferAction.apply {
            isEnabled = isEnable
            alpha = if (isEnable) 1f else 0.5f
        }
    }

    private fun bindViewFromAccount(account: AccountBase) {
        fromAccount = account
        with(binding) {
            tvAccountNumber.text = account.accountNumber
            tvBalanceValue.text = account.getAvailableBalance()
            tvCurrencyValue.text = account.currencyCode
            iclAmount.tvCurrentCode.text = account.currencyCode
            iclAmount.edtValue.setInputEditText(true, isTypeSigned = account.currencyCode == "USD")
            currentCurrencyChoose = account.currencyCode
        }
        resetStateTransfer()
    }

    private fun bindViewSelectReceiverAccount(account: AccountBase) {
        with(binding) {
            iclToAccount.edtValue.setText(
                getTypeAccount(requireContext(), account).plus(Const.SEPARATOR_DASH)
                    .plus(account.accountNumber)
            )
            val isDuplicate = account.accountNumber == fromAccount?.accountNumber
            iclToAccount.bindViewError(
                if (isDuplicate) getString(
                    R.string.invalidBeneficiaryAccount
                ) else null
            )
            if (isDiffCurrency(account.currencyCode)) {
                bindExchangeCurrency(fromAccount?.currencyCode)
            } else {
                bindExchangeCurrency(null)
            }
            toAccount = account
            checkBalanceInvalid()
            iclTotalAmount.tvCurrentCode.text = account.currencyCode
        }

        updateStatusTransfer()
    }

    private fun bindViewSelectReceiverAccount(userInfo: AccountUserNameModel) {
        binding.iclAccountName.apply {
            root.isVisible = true
            edtValue.setText(userInfo.customerName)
        }
        if (beneficiarySelected?.accountNumber == userInfo.accountNumber) {
            binding.iclRemarks.edtValue.setText(beneficiarySelected?.remark)
        }
        if (isDiffCurrency(userInfo.currency)) {
            bindExchangeCurrency(userInfo.currency)
        } else {
            toAccount = AccountInfo().apply {
                setValueAccountNumber(binding.iclToAccount.edtValue.text.toString())
                currencyCode = userInfo.currency
                productCode = userInfo.productCode
                productDescription = userInfo.productDescription
                customerName = userInfo.customerName
            }
            binding.iclTotalAmount.tvCurrentCode.text = userInfo.currency
            bindExchangeCurrency(null)
            with(binding) {
                iclToAccount.tvError.gone()
//                iclFee.edtValue.setText(Const.ZERO)
//                iclFee.root.gone()
                iclTotalAmount.edtValue.setText("")
                iclTotalAmount.root.gone()
            }
        }

        updateStatusTransfer()
    }

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
        return if (currency == Const.USD) {
            getString(
                R.string.formatTextExchangeCurrency,
                Const.USD,
                exchangeUSDToKm?.toPlainString(),
                Const.KHR
            )
        } else {
            getString(
                R.string.formatTextExchangeCurrency,
                Const.KHR,
                exchangeKmToUSD?.toPlainString(),
                Const.USD
            )
        }
    }

    private fun getExchangeRealtime(): BigDecimal? {
        return if (currentCurrencyChoose == Const.USD) exchangeUSDToKm else exchangeKmToUSD
    }

    private fun isDiffCurrency(toCurrency: String): Boolean =
        run { currentCurrencyChoose != toCurrency }

    private fun resetStateTransfer() {
        with(binding) {
            iclToAccount.apply {
                ivExpandDown.setImageResource(if (!isIntrabank()) R.drawable.ic_arrow_down_black else R.drawable.ic_account_intrabank)
                edtValue.apply {
                    setText(Const.EMPTY)
                    hint =
                        if (isIntrabank()) getString(R.string.enterAccountNumber) else getString(R.string.selectAccount)
                    isFocusable = isIntrabank()
                    isFocusableInTouchMode = isIntrabank()
                    isClickable = !isIntrabank()
                    isLongClickable = isIntrabank()
                }

                edtValue.filters =
                    arrayOf(InputFilter.LengthFilter(if (isIntrabank()) 10 else 1000000))
            }
            iclAmount.edtValue.setText(Const.EMPTY)
//            iclFee.root.gone()
            iclExchangeRate.root.gone()
            iclTotalAmount.root.gone()
            iclAccountName.root.gone()
            iclToAccount.tvError.gone()
            iclAmount.bindViewError(null)
            iclRemarks.bindViewError(null)
            resetRemarks()
        }

        updateStatusTransfer()
    }

    private fun isIntrabank() = run { currentTypeTransfer == INTRABANK }

    private fun onChangeTypeTransfer(type: String) {
        if (currentTypeTransfer != type) {
            currentTypeTransfer = type
            updateViewTypeTransfer()
            resetStateTransfer()
        }
    }

    private fun updateStatusByListReceiverAccount(list: List<AccountBase>) {
        if (!isIntrabank()) {
            with(binding.iclToAccount.edtValue) {
                if (list.isEmpty()) {
                    setText(getString(R.string.noAccountAvailable))
                } else if (!isIntrabank()) {
                    setText(Const.EMPTY)
                }
            }
        }
    }

}