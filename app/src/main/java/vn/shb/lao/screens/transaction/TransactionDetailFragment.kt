package vn.shb.lao.screens.transaction

import android.view.View
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import vn.shb.core.core.domain.usecases.transfer.UseCaseTransactionDetail
import vn.shb.core.utils.extesions.setOnSingleClickListener
import vn.shb.data.entities.getBalance
import vn.shb.data.entities.home.TransactionDetail
import vn.shb.lao.R
import vn.shb.lao.base.BaseFragmentBinding
import vn.shb.lao.databinding.ChildViewTransactionInfoBinding
import vn.shb.lao.databinding.FragmentTransactionDetailBinding
import vn.shb.lao.utils.ApiConst
import vn.shb.lao.utils.extensions.DateTimeHelper
import vn.shb.lao.utils.extensions.common.Const
import vn.shb.lao.utils.extensions.gone
import vn.shb.lao.utils.extensions.launchRepeatOnLifecycle
import vn.shb.lao.utils.extensions.visible

class TransactionDetailFragment :
    BaseFragmentBinding<FragmentTransactionDetailBinding>(FragmentTransactionDetailBinding::inflate) {

    private var prefixAmount = ""

    override fun initView(view: View) {
        binding.ivStatus.gone()
        binding.tvTransactionAmount.gone()
        val trans = homeViewModel.currentTransaction ?: return
        homeViewModel.getTransactionDetail(
            UseCaseTransactionDetail.Params(
                trans.referenceNumber,
                homeViewModel.selectedAccount?.accountNumber ?: "",
                if (trans.debitAmount > 0) ApiConst.D_TRANSFER_MONEY else ApiConst.C_RECEIVE_MONEY,
                mdCode = trans.moduleCode,
                transCode = trans.transactionCode,
                transDate = trans.transactionDate
            )
        )
        prefixAmount = if (trans.debitAmount > 0) Const.TRU else Const.CONG
    }

    private fun setupView(trans: TransactionDetail) {
        with(binding) {
            ivStatus.visible()
            tvTransactionAmount.visible()
            tvTransactionAmount.text = getString(R.string.transactionAmount)
            tvTransactionAmount.setTextColor(getColor(R.color.colorSuccess))
            tvValueBalance.text = prefixAmount.plus(trans.amount.getBalance())
            tvCurrentCode.text = trans.currency
            if (!trans.ordAccount.isNullOrEmpty()) {
                iclFromAccount.bindView(
                    getString(R.string.fromAccount),
                    trans.ordAccType.plus(Const.SEPARATOR_DASH).plus(trans.ordAccount)
                )
                iclFromAccount.root.visible()
            } else {
                iclFromAccount.root.gone()
            }
            if (!trans.benAccount.isNullOrEmpty()) {
                iclToAccount.bindView(
                    getString(R.string.toAccount),
                    trans.benAccType.plus(Const.SEPARATOR_DASH).plus(trans.benAccount)
                )
                iclToAccount.root.visible()
            } else {
                iclToAccount.root.gone()
            }

            iclRemarks.bindView(
                getString(R.string.remarks),
                trans.remarks
            )
            iclTransactionDate.bindView(
                getString(R.string.transactionDate),
                DateTimeHelper.toDisplayDate(trans.transDate)
            )
            iclReferenceNumber.bindView(
                getString(R.string.referenceNumber),
                trans.refNo
            )
            if (!trans.accountName.isBlank()) {
                iclAccountName.root.visible()
                iclAccountName.bindView(
                    getString(R.string.accountName),
                    trans.accountName
                )
            } else {
                iclAccountName.root.gone()
            }
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
                launch {
                    stateTransactionDetail.collectLatest {
                        it?.let { trans -> setupView(trans) }
                    }
                }
                launch {
                    stateDetailError.collectLatest {
                        if (it.errorCode == "FUN-020"){
                            homeViewModel.currentTransaction?.let { 
                                setupView(mapToTransactionDetail(it))
                            }
                            return@collectLatest
                        }
                        binding.ivStatus.gone()
                        binding.tvTransactionAmount.gone()
                        handleErrorHome(it) {
                            backPress()
                        }
                    }
                }
            }
        }
    }

    private fun mapToTransactionDetail(item: vn.shb.data.entities.home.TransactionItem.Transaction): TransactionDetail {
        return TransactionDetail(
            refNo = item.referenceNumber,
            transDate = item.transactionDate,
            amount = if (item.creditAmount > 0) item.creditAmount else item.debitAmount,
            remarks = item.transactionDescription,
            currency = item.currencyCode,
            ordAccount = null,
            benAccount = null,
            ordAccType = "",
            benAccType = "",
            accountName = ""
        )
    }

    private fun ChildViewTransactionInfoBinding.bindView(
        title: String?,
        des: String?
    ) {
        if (title != null) {
            tvLabel.text = title
        }
        if (des != null) {
            tvValue.text = des
        }
    }

}
