package vn.shb.lao.screens.transfer

import android.text.InputFilter
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import kotlinx.coroutines.launch
import vn.shb.core.core.domain.source.response.AccountUserNameModel
import vn.shb.core.core.domain.usecases.transfer.UseCaseValidateTransaction
import vn.shb.core.utils.extesions.setOnSingleClickListener
import vn.shb.data.entities.AccountBase
import vn.shb.data.entities.home.AccountInfo
import vn.shb.data.entities.transfer.ConfirmationModel
import vn.shb.data.entities.transfer.TransferAccount
import vn.shb.lao.R
import vn.shb.lao.base.BaseFragmentBinding
import vn.shb.lao.databinding.FragmentMoneyTransferBinding
import vn.shb.lao.screens.home.DialogSelectAccount
import vn.shb.lao.screens.home.DialogSelectBeneficiary
import vn.shb.lao.screens.home.getTypeAccount
import vn.shb.lao.utils.ApiConst
import vn.shb.lao.utils.extensions.DateTimeHelper.Companion.getDateFromCurrentDate
import vn.shb.lao.utils.extensions.common.Const
import vn.shb.lao.utils.extensions.gone
import vn.shb.lao.utils.extensions.launchRepeatOnLifecycle
import vn.shb.lao.utils.extensions.visible

class MoneyTransferFragment : BaseFragmentBinding<FragmentMoneyTransferBinding>(
    FragmentMoneyTransferBinding::inflate
) {
    var currentTypeTransfer: String = INTRABANK

    private var fromAccount: AccountBase? = null
    private var toAccount: AccountBase? = null

    private var totalAmount: Double = 0.0
    var remarks = ""

    companion object {
        const val INTRABANK = "INTRABANK"
        const val OWN_ACCOUNT = "OWN_ACCOUNT"
    }

    override fun onStart() {
        super.onStart()
//        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
    }

    override fun isPaddingBottom(): Boolean {
        return true
    }

    override fun initView(view: View) {
        bindView()
        updateViewTypeTransfer()
        resetStateTransfer()
    }

    override fun onResume() {
        super.onResume()
        homeViewModel.getTransferAccount()
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
                            bindViewReceiverAccount(it)
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
                        safeNavigate(
                            R.id.moneyTransferFragment, R.id.confirmationFragment,
                            bundleOf(ApiConst.KEY_TYPE_TRANSFER_INTRABANK to isIntrabank())
                        )
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
            tvMoneyTransferTitle.setOnSingleClickListener {
                backPress()
            }

            tvIntraBankTransfer.setOnSingleClickListener {
                onChangeTypeTransfer(INTRABANK)
            }

            tvOwnAccountTransfer.setOnSingleClickListener {
                onChangeTypeTransfer(OWN_ACCOUNT)
            }

            tvAccountNumber.setOnSingleClickListener {
                DialogSelectAccount.Build(
                    homeViewModel.listTransferAccount, fromAccount
                ) { selectedAccount ->
                    bindViewFromAccount(selectedAccount)
                    homeViewModel.listenChangeFromAccount(selectedAccount)
                }.build().show(childFragmentManager, DialogSelectAccount.TAG)
            }

            finishTyping(iclToAccount.edtValue, isIntrabank()) {
                val textAccountNo = iclToAccount.edtValue.text.toString()
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

            iclToAccount.ivExpandDown.setOnSingleClickListener {
                handleShowDialogSelectAccount()
            }

            finishTyping(iclAmount.edtValue, true) {
                checkAmountValidate()
            }
            iclAmount.edtValue.setupDecimalInput()

            iclRemarks.edtValue.doAfterTextChanged {
                iclRemarks.tvError.isVisible = it.toString().isBlank()
                remarks = it.toString()
                updateStatusTransfer()
            }

            tvTransferAction.setOnSingleClickListener {
                tvTransferAction.isEnabled = false
                homeViewModel.validateTransaction(
                    UseCaseValidateTransaction.Params(
                        UseCaseValidateTransaction.OrderTransaction(
                            paymentType = if (isIntrabank()) ApiConst.INTRA else ApiConst.SELF,
                            amount = totalAmount, fromAccount?.currencyCode!!
                        )
                    )
                ) {
                    tvTransferAction.isEnabled = true
                }
            }
        }
    }

    private fun checkAmountValidate() {
        val text = binding.iclAmount.edtValue.text.toString().trim()
        checkBalanceInvalid(text)
        updateStatusTransfer()
    }

    private fun showListBeneficiary() {
        DialogSelectBeneficiary.Build(listOf()) { selectedAccount ->
            bindViewReceiverAccount(selectedAccount)
        }.build().show(childFragmentManager, DialogSelectAccount.TAG)
    }

    private fun getConfirmationStatus(): ConfirmationModel {
        return ConfirmationModel(
            fromAccount!!, toAccount!!, remarks, transactionDate = getDateFromCurrentDate(),
            totalAmount, 0.0, totalAmount,
            paymentType = if (isIntrabank()) ApiConst.INTRA else ApiConst.SELF
        )
    }

    private fun handleShowDialogSelectAccount() {
        if (!isIntrabank()) {
            DialogSelectAccount.Build(
                homeViewModel.listReceiverActive, toAccount
            ) { selectedAccount ->
                bindViewReceiverAccount(selectedAccount)
            }.build().show(childFragmentManager, DialogSelectAccount.TAG)
        } else {
            showListBeneficiary()
        }
    }

    private fun checkBalanceInvalid(valueBalance: String) {
        totalAmount =
            if (valueBalance.isNotBlank()) valueBalance.replace(",", "").toDouble() else 0.0
        binding.iclFee.apply {
            edtValue.setText(Const.ZERO)
            if (!valueBalance.isEmpty()) root.visible()
        }

        binding.iclTotalAmount.apply {
            edtValue.setText(valueBalance)
            if (!valueBalance.isEmpty()) root.visible()
        }

        val isError = totalAmount > (fromAccount?.availableBalance ?: 0.0)
        val textError = when {
            valueBalance.isEmpty() || valueBalance == "0" -> getString(R.string.pleaseEnterTheAmount)
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
                viewLine.gone()
                tvCurrentCode.gone()
                edtValue.setInputEditText(true)
                tvError.text = getString(R.string.invalidBeneficiaryAccount)
            }

            iclAccountName.apply {
                tvTitle.text = getString(R.string.accountName)
                edtValue.setTextColor(getColor(R.color.neutral7))
                viewLine.gone()
                edtValue.isEnabled = false
            }

            iclAmount.apply {
                tvTitle.text = getString(R.string.amount)
                edtValue.hint = getString(R.string.enterAmount)
                ivExpandDown.gone()
                viewLine.visible()
                tvCurrentCode.visible()
                tvError.text = getString(R.string.insufficientBalance)
                bindColor(R.color.neutral8)
            }

            iclFee.apply {
                tvTitle.text = getString(R.string.fee)
                edtValue.setText(Const.ZERO)
                ivExpandDown.gone()
                viewLine.visible()
                tvCurrentCode.visible()
                edtValue.enableInput(false)
                bindColor(R.color.neutral7)
            }

            iclTotalAmount.apply {
                tvTitle.text = getString(R.string.totalAmount)
                edtValue.hint = getString(R.string.zero)
                ivExpandDown.gone()
                viewLine.visible()
                tvCurrentCode.visible()
                edtValue.enableInput(false)
                bindColor(R.color.neutral7)
            }

            iclRemarks.apply {
                tvTitle.text = getString(R.string.remarks)
                resetRemarks()
                ivExpandDown.gone()
                viewLine.gone()
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
            && fromAccount?.currencyCode == toAccount?.currencyCode
            && totalAmount <= (fromAccount?.availableBalance ?: 0.0) && totalAmount > 0.0
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
            tvBalanceValue.text =
                account.getAvailableBalance().plus(Const.SEPARATOR_SPACE).plus(account.currencyCode)
            iclAmount.tvCurrentCode.text = account.currencyCode
            iclAmount.edtValue.setInputEditText(true, isTypeSigned = account.currencyCode == "USD")

            iclFee.tvCurrentCode.text = account.currencyCode
            iclTotalAmount.tvCurrentCode.text = account.currencyCode
        }
        resetStateTransfer()
    }

    private fun bindViewReceiverAccount(account: AccountBase) {
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
            toAccount = account
        }

        updateStatusTransfer()
    }

    private fun bindViewReceiverAccount(userInfo: AccountUserNameModel) {
        binding.iclAccountName.apply {
            root.isVisible = true
            edtValue.setText(userInfo.customerName)
        }
        if (userInfo.currency != fromAccount?.currencyCode) {
            with(binding) {
                iclToAccount.tvError.text =
                    getString(R.string.transferAccountAndReceivingAccountNotSame)
                iclToAccount.tvError.visible()
            }
        } else {
            toAccount = AccountInfo().apply {
                setValueAccountNumber(binding.iclToAccount.edtValue.text.toString())
                currencyCode = userInfo.currency
                productCode = userInfo.productCode
                productDescription = userInfo.productDescription
                customerName = userInfo.customerName
            }
            with(binding) {
                iclToAccount.tvError.gone()
                iclFee.edtValue.setText(Const.ZERO)
                iclFee.root.gone()
                iclTotalAmount.edtValue.setText("")
                iclTotalAmount.root.gone()
            }
        }

        updateStatusTransfer()
    }

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

                edtValue.filters = arrayOf(InputFilter.LengthFilter(if (isIntrabank()) 10 else 1000000))
            }
            iclAmount.edtValue.setText(Const.EMPTY)
            iclFee.root.gone()
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