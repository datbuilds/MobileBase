package vn.shb.lao.screens.transaction

import android.os.Bundle
import android.view.View
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import vn.shb.core.utils.extesions.setOnSingleClickListener
import vn.shb.lao.R
import vn.shb.lao.base.BaseFragmentBinding
import vn.shb.lao.databinding.ChildViewTransactionInfoBinding
import vn.shb.lao.databinding.FragmentTransactionDetailBinding

import vn.shb.lao.utils.ApiConst
import vn.shb.lao.utils.extensions.common.Const
import vn.shb.lao.utils.extensions.gone
import vn.shb.lao.utils.extensions.launchRepeatOnLifecycle

class TransactionDetailFragment :
    BaseFragmentBinding<FragmentTransactionDetailBinding>(FragmentTransactionDetailBinding::inflate) {

    override fun initView(view: View) {
        setupView()
    }

    private fun setupView() {
        val trans = homeViewModel.currentTransaction ?: return
        with(binding) {
            binding.tvValueBalance.text = trans.amountFormatted
            tvCurrentCode.text = trans.currencyCode
            val fromAccount = (homeViewModel.selectedAccount?.positionDescription
                ?: "") + Const.SEPARATOR_DASH + homeViewModel.selectedAccount?.accountNumber
            iclFromAccount.bindView(
                getString(R.string.fromAccount),
                fromAccount
            )
            iclToAccount.bindView(
                getString(R.string.toAccount),
                trans.transactionDescription
            )
            iclAccountName.root.gone()
            iclRemarks.bindView(
                getString(R.string.remarks),
                trans.transactionDescription
            )
            iclTransactionDate.bindView(
                getString(R.string.transactionDate),
                trans.transactionDateFormatted
            )
            iclReferenceNumber.bindView(
                getString(R.string.referenceNumber),
                trans.referenceNumber
            )
        }
    }

    override fun initListener() {
        binding.ivClose.setOnSingleClickListener {
            backPress()
        }
    }

    override fun initObserve() {
        with(homeViewModel) {
            launchRepeatOnLifecycle {

            }
        }
    }

    private fun ChildViewTransactionInfoBinding.bindView(
        title: String,
        des: String
    ) {
        tvLabel.text = title
        tvValue.text = des
    }

}
