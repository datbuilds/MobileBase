package vn.shb.lao.screens.transfer

import android.view.View
import androidx.core.os.bundleOf
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import vn.shb.core.utils.extesions.setOnSingleClickListener
import vn.shb.data.entities.getBalance
import vn.shb.lao.R
import vn.shb.lao.base.BaseFragmentBinding
import vn.shb.lao.databinding.FragmentConfirmationBinding
import vn.shb.lao.utils.ApiConst
import vn.shb.lao.utils.extensions.common.Const
import vn.shb.lao.utils.extensions.launchRepeatOnLifecycle
import vn.shb.lao.utils.view.dialog.BottomSheetDialogHelper

class ConfirmationFragment : BaseFragmentBinding<FragmentConfirmationBinding>(
    FragmentConfirmationBinding::inflate
) {

    override fun isPaddingBottom(): Boolean {
        return true
    }

    override fun initView(view: View) {
        bindViewDefault()
        bindConfirmationView()
    }

    private fun bindViewDefault() {
        with(binding) {
            iclFromAccount.tvLabel.text = getString(R.string.fromAccount)
            iclToAccount.tvLabel.text = getString(R.string.toAccount)
            iclRemarks.tvLabel.text = getString(R.string.remarks)
            iclTransactionDate.tvLabel.text = getString(R.string.transactionDate)
            iclAmount.tvLabel.text = getString(R.string.amount)
            iclFee.tvLabel.text = getString(R.string.fee)
            iclTotalAmount.tvLabel.text = getString(R.string.totalAmount)
        }
    }

    private fun bindConfirmationView() {
        homeViewModel.confirmModel?.let { cf ->
            with(binding) {
                iclFromAccount.tvValue.text =
                    cf.fromAccount.productDescription.plus(Const.SEPARATOR_DASH)
                        .plus(cf.fromAccount.getAvailableBalance())
                iclToAccount.tvValue.text =
                    cf.toAccount.productDescription.plus(Const.SEPARATOR_DASH)
                        .plus(cf.toAccount.getAvailableBalance())
                iclRemarks.tvValue.text = cf.remarks
                iclTransactionDate.tvValue.text = cf.transactionDate
                val currencyCode = cf.fromAccount.currencyCode
                iclAmount.apply {
                    tvValue.text = cf.amount.getBalance()
                    tvCurrencyCode.text = currencyCode
                }
                iclFee.apply {
                    tvValue.text = cf.fee.getBalance()
                    tvCurrencyCode.text = currencyCode
                }
                iclTotalAmount.apply {
                    tvValue.text = cf.totalAmount.getBalance()
                    tvCurrencyCode.text = currencyCode
                }

            }
        }
    }

    override fun initObserve() {
        launchRepeatOnLifecycle {
            with(homeViewModel) {
                launch {
                    stateTransactionTransferConfirm.collectLatest {
                        if (it != null) {
                            safeNavigate(
                                R.id.confirmationFragment, R.id.paymentTransferFragment,
                                bundle =
                                    bundleOf(
                                        ApiConst.KEY_REFERENCE_NUMBER_TRANSACTION to it.refNo,
                                        ApiConst.KEY_ACCOUNT_NO_TRANSACTION to confirmModel!!.fromAccount.accountNumber,
                                        ApiConst.KEY_STATUS_CONFIRM_TRANSACTION to it.status
                                    )
                            )
                            homeViewModel.clearSessionTransaction()
                        }
                    }
                }

                launch {
                    stateTransactionTransfer.collectLatest {
                        if (it?.paymentType.equals(ApiConst.SELF)) {
                            homeViewModel.confirmTransactionTransfer(Const.EMPTY)
                        } else {
                            if (it?.paymentType.equals(ApiConst.INTRA)) {
                                BottomSheetDialogHelper(requireContext()).showDialogConfirmCode(
                                    it?.authSms ?: "",
                                    actionConfirmCode = { otp ->
                                        homeViewModel.confirmTransactionTransfer(otp)
                                    },
                                    actionDismiss = {
                                        BottomSheetDialogHelper(requireContext()).message(
                                            title = getString(R.string.notification),
                                            message = getString(R.string.authenticationFailed),
                                            textPositive = getString(R.string.close),
                                            positiveAction = {
                                            }
                                        )
                                    }
                                )
                            }
                        }
                    }
                }


            }
        }
    }

    override fun initListener() {
        with(binding) {
            tvConfirmation.setOnSingleClickListener {
                backPress()
            }
            tvConfirm.setOnSingleClickListener {
                homeViewModel.confirmModel?.let { cf ->
                    homeViewModel.postTransactionTransfer(
                        cf.paymentType,
                        cf.fromAccount.accountNumber, cf.toAccount.accountNumber,
                        cf.totalAmount, cf.fromAccount.currencyCode, cf.remarks
                    )
                }
            }
        }
    }

}