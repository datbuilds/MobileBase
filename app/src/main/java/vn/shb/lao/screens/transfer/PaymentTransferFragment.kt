package vn.shb.lao.screens.transfer

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

class PaymentTransferFragment :
    BaseFragmentBinding<FragmentTransactionDetailBinding>(FragmentTransactionDetailBinding::inflate) {

    private val newReferenceNumber by lazy {
        arguments?.getString(ApiConst.KEY_REFERENCE_NUMBER_TRANSACTION)
    }

    private val accountNo by lazy {
        arguments?.getString(ApiConst.KEY_ACCOUNT_NO_TRANSACTION)
    }

    private val statusPayment by lazy {
        arguments?.getString(ApiConst.KEY_STATUS_CONFIRM_TRANSACTION) == ApiConst.SUCCESS
    }

    private val isIntrabank by lazy {
        arguments?.getBoolean(ApiConst.KEY_TYPE_TRANSFER_INTRABANK) == true
    }

    override fun initView(view: View) {
        bindViewPayment()
        if (newReferenceNumber != null && accountNo != null && statusPayment) {
            homeViewModel.getTransactionDetail(
                UseCaseTransactionDetail.Params(
                    newReferenceNumber!!, accountNo!!,
                    ApiConst.D_CONFIRM
                )
            )
        } else {
            bindViewFailed()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        homeViewModel.clearSessionTransaction()
    }

    private fun bindViewFailed() {
        with(binding) {
            ivStatus.setImageResource(R.drawable.ic_fail)
            tvTransactionAmount.text = getString(R.string.transactionFail)
            tvTransactionAmount.setTextColor(getColor(R.color.color_error_text_login))
            homeViewModel.confirmModel?.let { cf ->
                tvValueBalance.text = cf.amount.toString()
                tvCurrentCode.text = cf.fromAccount.currencyCode
            }
            tvAnErrorHasOccurred.visible()
        }
    }

    private fun bindViewPayment() {
        with(binding) {
            ivClose.setImageResource(R.drawable.ic_to_home)
            tvCreateNewTransaction.visible()
        }
    }

    private fun setupView(trans: TransactionDetail) {
        with(binding) {
            tvValueBalance.text = trans.amount.getBalance()
            tvCurrentCode.text = trans.currency
            val fromAccount = trans.ordAccType.plus(Const.SEPARATOR_SPACE).plus(trans.ordAccount)
            iclFromAccount.bindView(
                getString(R.string.fromAccount),
                fromAccount
            )
            iclToAccount.bindView(
                getString(R.string.toAccount),
                trans.benAccType.plus(Const.SEPARATOR_SPACE).plus(trans.benAccount)
            )
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
            if (isIntrabank) {
                iclAccountName.root.visible()
                iclAccountName.bindView(
                    getString(R.string.accountName),
                    "đợi api trả về"
                )
            } else {
                iclAccountName.root.gone()
            }
        }
    }

    override fun initListener() {
        binding.ivClose.setOnSingleClickListener {
            popBackTo(R.id.homeFragment)
        }

        binding.tvCreateNewTransaction.setOnSingleClickListener {
            popBackTo(R.id.moneyTransferFragment)
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
