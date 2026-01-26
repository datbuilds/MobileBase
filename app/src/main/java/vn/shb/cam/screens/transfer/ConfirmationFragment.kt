package vn.shb.cam.screens.transfer

import android.view.View
import androidx.core.os.bundleOf
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import vn.shb.core.utils.extesions.setOnSingleClickListener
import vn.shb.data.entities.getBalance
import vn.shb.cam.R
import vn.shb.cam.base.BaseFragmentBinding
import vn.shb.cam.databinding.FragmentConfirmationBinding
import vn.shb.cam.screens.transfer.dialog.ConfirmCodeBottomSheetDialogFragment
import vn.shb.cam.utils.ApiConst
import vn.shb.cam.utils.extensions.common.Const
import vn.shb.cam.utils.extensions.gone
import vn.shb.cam.utils.extensions.launchRepeatOnLifecycle
import vn.shb.cam.utils.extensions.visible
import vn.shb.cam.utils.view.dialog.BottomSheetDialogHelper

class ConfirmationFragment : BaseFragmentBinding<FragmentConfirmationBinding>(
    FragmentConfirmationBinding::inflate
) {

    private val isIntrabank by lazy {
        arguments?.getBoolean(ApiConst.KEY_TYPE_TRANSFER_INTRABANK) ?: false
    }

    override fun isPaddingBottom(): Boolean {
        return true
    }

    override fun initView(view: View) {
        bindViewDefault()
        bindConfirmationView()
    }

    private fun bindViewDefault() {
        with(binding) {
            if (isIntrabank) {
                iclTo.root.visible()
                iclTo.tvLabel.text = getString(R.string.to)
                iclTo.tvValue.text = getString(R.string.SHBAccount)
                iclAccountName.tvLabel.text = getString(R.string.accountName)
            } else {
                iclTo.root.gone()
                iclAccountName.root.gone()
            }
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
                        .plus(cf.fromAccount.accountNumber)
                iclToAccount.tvValue.text =
                    cf.toAccount.productDescription.plus(Const.SEPARATOR_DASH)
                        .plus(cf.toAccount.accountNumber)
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
                if (isIntrabank) {
                    iclAccountName.tvValue.text = cf.toAccount.customerName
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
                            homeViewModel.confirmSuccessData = it
                            safeNavigate(
                                R.id.confirmationFragment, R.id.paymentTransferFragment,
                                bundle =
                                    bundleOf(
                                        ApiConst.KEY_ACCOUNT_NO_TRANSACTION to confirmModel!!.fromAccount.accountNumber,
                                        ApiConst.KEY_TYPE_TRANSFER_INTRABANK to isIntrabank
                                    )
                            )
                        }
                    }
                }

                launch {
                    stateTransferConfirmError.collectLatest {
                        safeNavigate(
                            R.id.confirmationFragment, R.id.paymentTransferFragment,
                            bundle = bundleOf(ApiConst.KEY_CONFIRM_ERROR to true)
                        )
                    }
                }

                launch {
                    stateTransactionTransfer.collectLatest {
                        if (it?.paymentType.equals(ApiConst.SELF)) {
                            homeViewModel.confirmTransactionTransfer(Const.EMPTY)
                        } else {
                            if (it?.paymentType.equals(ApiConst.INTRA)) {
                                binding.flBru.visible()
                                ConfirmCodeBottomSheetDialogFragment(
                                    authSms = it?.authSms ?: "",
                                    actionConfirmCode = { code ->
                                        homeViewModel.confirmTransactionTransfer(code)
                                    },
                                    actionDismiss = {
                                       binding.flBru.gone()
                                    }
                                ).show(parentFragmentManager, "ConfirmCodeBottomSheetDialog")
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