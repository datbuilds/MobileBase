package vn.shb.lao.screens.transfer

import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
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
import vn.shb.lao.screens.home.HomeViewModel
import vn.shb.lao.utils.ApiConst
import vn.shb.lao.utils.extensions.DateTimeHelper.Companion.getDateFromCurrentDate
import vn.shb.lao.utils.extensions.common.Const
import vn.shb.lao.utils.extensions.gone
import vn.shb.lao.utils.extensions.hideSoftKeyboard
import vn.shb.lao.utils.extensions.launchRepeatOnLifecycle
import vn.shb.lao.utils.extensions.visible

class MoneyTransferFragment : BaseFragmentBinding<FragmentMoneyTransferBinding>(
    FragmentMoneyTransferBinding::inflate
) {

    private val homeViewModel: HomeViewModel by sharedViewModel()

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
                            bindViewReceiverAccount(it.customerName)
                        }
                    }
                }

                launch {
                    stateErrorFillAccountNumber.collect {
                        binding.iclToAccount.tvError.apply {
                            text = getString(R.string.incorrectAccountInfomation)
                            visible()
                        }
                    }
                }

            }
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

//            iclToAccount.edtValue.setOnSingleClickListener {
//                handleShowDialogSelectAccount()
//            }

            iclToAccount.edtValue.setOnEditorActionListener { v, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    v.clearFocus()
                    hideSoftKeyboard()
                    homeViewModel.getAccountByNumber(iclToAccount.edtValue.text.toString())
                    true
                } else {
                    false
                }
            }

            iclToAccount.edtValue.setOnFocusChangeListener { v, hasFocus ->
                if (!hasFocus && isIntrabank()) {
                    homeViewModel.getAccountByNumber(iclToAccount.edtValue.text.toString())
                }
            }

            iclToAccount.ivExpandDown.setOnSingleClickListener {
                handleShowDialogSelectAccount()
            }
            iclAmount.edtValue.onTypingAmount {
                iclTotalAmount.edtValue.setText(it)
                checkBalanceInvalid(it)
                updateStatusTransfer()
            }

            iclRemarks.edtValue.doAfterTextChanged {
                iclRemarks.tvError.isVisible = it.toString().isBlank()
                remarks = it.toString()
                updateStatusTransfer()
            }

            tvTransferAction.setOnSingleClickListener {
                homeViewModel.confirmModel = getConfirmationStatus()
                safeNavigate(R.id.moneyTransferFragment, R.id.confirmationFragment)
            }
        }
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
        binding.iclAmount.tvError.isVisible =
            (totalAmount > (fromAccount?.availableBalance ?: 0.0))
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
                llEdit.alpha = 0.8f
            }

            iclAmount.apply {
                tvTitle.text = getString(R.string.amount)
                edtValue.hint = getString(R.string.enterAmount)
                ivExpandDown.gone()
                viewLine.visible()
                tvCurrentCode.visible()
                edtValue.setInputEditText(true)
                tvError.text = getString(R.string.insufficientBalance)
            }

            iclFee.apply {
                tvTitle.text = getString(R.string.fee)
                edtValue.hint = getString(R.string.zero)
                ivExpandDown.gone()
                viewLine.visible()
                tvCurrentCode.visible()
                edtValue.enableInput(false)
                llEdit.alpha = 0.8f
            }

            iclTotalAmount.apply {
                tvTitle.text = getString(R.string.totalAmount)
                edtValue.hint = getString(R.string.zero)
                ivExpandDown.gone()
                viewLine.visible()
                tvCurrentCode.visible()
                edtValue.enableInput(false)
                llEdit.alpha = 0.8f
            }
            iclRemarks.apply {
                tvTitle.text = getString(R.string.remarks)
                resetRemarks()
                ivExpandDown.gone()
                viewLine.gone()
                tvCurrentCode.gone()
                edtValue.setInputEditText(false)
                tvError.text = getString(R.string.invalidRemarks)
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
            && fromAccount?.currencyCode?.isNotEmpty() == true
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
            iclFee.tvCurrentCode.text = account.currencyCode
            iclTotalAmount.tvCurrentCode.text = account.currencyCode
        }
        resetStateTransfer()
    }

    private fun bindViewReceiverAccount(account: AccountBase) {
        toAccount = account
        with(binding) {
            iclToAccount.edtValue.setText(
                account.productDescription.plus(Const.SEPARATOR_DASH).plus(account.accountNumber)
            )
            iclFee.root.visible()
            iclTotalAmount.root.visible()
        }

        updateStatusTransfer()
    }

    private fun bindViewReceiverAccount(userName: String) {
        binding.iclAccountName.apply {
            root.isVisible = true
            edtValue.setText(userName)
        }
        toAccount = AccountInfo().apply {
            setValueAccountNumber(binding.iclToAccount.edtValue.text.toString())
        }
        with(binding) {
            iclToAccount.tvError.gone()
            iclFee.root.visible()
            iclTotalAmount.root.visible()
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
            }
            iclAmount.edtValue.setText(Const.EMPTY)
            iclFee.root.gone()
            iclTotalAmount.root.gone()
            iclAccountName.root.gone()
            iclToAccount.tvError.gone()
            iclAmount.tvError.gone()
            iclRemarks.tvError.gone()
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