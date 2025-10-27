package vn.shb.lao.screens.transfer

import android.os.Bundle
import android.view.View
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import vn.shb.core.utils.extesions.setOnSingleClickListener
import vn.shb.data.entities.getBalance
import vn.shb.lao.R
import vn.shb.lao.base.BaseFragmentBinding
import vn.shb.lao.databinding.FragmentConfirmationBinding
import vn.shb.lao.screens.home.HomeViewModel
import vn.shb.lao.utils.ApiConst
import vn.shb.lao.utils.extensions.common.Const
import vn.shb.lao.utils.extensions.launchRepeatOnLifecycle

class ConfirmationFragment : BaseFragmentBinding<FragmentConfirmationBinding>(
    FragmentConfirmationBinding::inflate
) {

    private val homeViewModel: HomeViewModel by sharedViewModel()

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

    override fun onResume() {
        super.onResume()
        homeViewModel.getTransferAccount()
    }

    override fun initObserve() {
        launchRepeatOnLifecycle {
            with(homeViewModel) {
                stateTransactionTransferConfirm.collectLatest {
                    safeNavigate(
                        R.id.confirmationFragment, R.id.transactionDetailFragment,
                        bundle = Bundle().apply { putString(ApiConst.KEY_REFERENCE_NUMBER_TRANSACTION, it?.refNo) })
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
                homeViewModel.confirmTransactionTransfer(Const.EMPTY)
            }
        }
    }

}