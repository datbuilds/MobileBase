package vn.shb.lao.screens.transfer

import android.os.Bundle
import android.view.View
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import vn.shb.core.core.domain.usecases.transfer.UseCaseTransactionDetail
import vn.shb.core.utils.extesions.setOnSingleClickListener
import vn.shb.data.entities.home.TransactionDetail
import vn.shb.lao.R
import vn.shb.lao.base.BaseFragmentBinding
import vn.shb.lao.databinding.ChildViewTransactionInfoBinding
import vn.shb.lao.databinding.FragmentTransactionDetailBinding

import vn.shb.lao.utils.ApiConst
import vn.shb.lao.utils.extensions.common.Const
import vn.shb.lao.utils.extensions.launchRepeatOnLifecycle
import vn.shb.lao.utils.extensions.visible

class PaymentTransferFragment :
    BaseFragmentBinding<FragmentTransactionDetailBinding>(FragmentTransactionDetailBinding::inflate) {


    private var newReferenceNumber: String? = null
    private var accountNo: String? = null
    private var statusPayment: Boolean? = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        newReferenceNumber = arguments?.getString(ApiConst.KEY_REFERENCE_NUMBER_TRANSACTION)
        accountNo = arguments?.getString(ApiConst.KEY_ACCOUNT_NO_TRANSACTION)
        statusPayment =
            arguments?.getString(ApiConst.KEY_STATUS_CONFIRM_TRANSACTION) == ApiConst.SUCCESS
    }

    override fun initView(view: View) {
        bindViewPayment()
        if (newReferenceNumber != null && accountNo != null && statusPayment == true) {
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
            tvValueBalance.text = trans.amount.toString()
            tvCurrentCode.text = trans.currency
            val fromAccount = trans.ordAccType.plus(Const.SEPARATOR_SPACE).plus(trans.ordAccount)
            iclTransactionInfo1.bindView(
                getString(R.string.fromAccount),
                fromAccount
            )
            iclTransactionInfo2.bindView(
                getString(R.string.toAccount),
                trans.benAccType.plus(Const.SEPARATOR_SPACE).plus(trans.benAccount)
            )
            iclTransactionInfo3.bindView(
                getString(R.string.remarks),
                trans.remarks
            )
            iclTransactionInfo4.bindView(
                getString(R.string.transactionDate),
                trans.transDate
            )
            iclTransactionInfo5.bindView(
                getString(R.string.referenceNumber),
                trans.refNo
            )
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
