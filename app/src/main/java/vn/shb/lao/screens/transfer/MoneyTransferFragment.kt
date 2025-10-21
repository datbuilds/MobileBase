package vn.shb.lao.screens.transfer

import android.view.View
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import vn.shb.core.utils.extesions.setOnSingleClickListener
import vn.shb.data.entities.home.AccountInfo
import vn.shb.lao.R
import vn.shb.lao.base.BaseFragmentBinding
import vn.shb.lao.base.view.MyTextView
import vn.shb.lao.databinding.FragmentMoneyTransferBinding
import vn.shb.lao.screens.home.HomeViewModel
import vn.shb.lao.utils.extensions.common.Const
import vn.shb.lao.utils.extensions.gone
import vn.shb.lao.utils.extensions.visible

class MoneyTransferFragment : BaseFragmentBinding<FragmentMoneyTransferBinding>(
    FragmentMoneyTransferBinding::inflate
) {

    private val homeViewModel: HomeViewModel by sharedViewModel()

    private var currentType: String = INTRABANK

    companion object {
        const val INTRABANK = "INTRABANK"
        const val OWN_ACCOUNT = "OWN_ACCOUNT"
    }

    override fun initView(view: View) {
        bindView()
        updateViewTypeTransfer()
        homeViewModel.selectedAccount?.let { bindViewAccount(it) }
        resetStateTransfer()
    }

    private fun bindView() {
        with(binding) {
            iclToAccount.apply {
                tvTitle.text = getString(R.string.toAccount)
                edtValue.hint = getString(R.string.selectAccount)
                ivExpandDown.visible()
                viewLine.gone()
                tvCurrentCode.gone()
            }

            iclAmount.apply {
                tvTitle.text = getString(R.string.amount)
                edtValue.hint = getString(R.string.selectAccount)
                ivExpandDown.gone()
                viewLine.visible()
                tvCurrentCode.visible()
            }

            iclFee.apply {
                tvTitle.text = getString(R.string.fee)
                edtValue.hint = getString(R.string.zero)
                ivExpandDown.gone()
                viewLine.visible()
                tvCurrentCode.visible()
            }

            iclTotalAmount.apply {
                tvTitle.text = getString(R.string.totalAmount)
                edtValue.hint = getString(R.string.zero)
                ivExpandDown.gone()
                viewLine.visible()
                tvCurrentCode.visible()
            }
            iclRemarks.apply {
                tvTitle.text = getString(R.string.remarks)
                edtValue.setText(
                    getCurrentUser()?.username.plus(Const.SEPARATOR_SPACE)
                        .plus(getString(R.string.transfer))
                )
                ivExpandDown.gone()
                viewLine.gone()
                tvCurrentCode.gone()
            }
        }
    }

    private fun updateViewTypeTransfer() {
        with(binding) {
            viewOptionTransfer(tvIntraBankTransfer, INTRABANK)
            viewOptionTransfer(tvOwnAccountTransfer, OWN_ACCOUNT)
        }
    }

    private fun viewOptionTransfer(tv: MyTextView, typeView: String) {
        val isChoose = currentType == typeView
        tv.setTextColor(getColor(if (isChoose) R.color.neutral1 else R.color.primary100))
        tv.setBackgroundResource(if (isChoose) R.drawable.bg_transfer_choose else R.drawable.bg_transfer_normal)
    }

    private fun bindViewAccount(account: AccountInfo) {
        with(binding) {
            tvAccountNumber.text = account.accountNumber
            tvBalanceValue.text = account.getAvailableBalance()
            iclAmount.tvCurrentCode.text = account.currencyCode
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
        }
    }

    private fun resetStateTransfer() {
        with(binding) {
            iclToAccount.apply {
                edtValue.setText(Const.EMPTY)
                edtValue.hint =
                    if (isIntrabank()) getString(R.string.enterAccountNumber) else getString(R.string.selectAccount)
                ivExpandDown.setImageResource(if (!isIntrabank()) R.drawable.ic_arrow_down_black else R.drawable.ic_account_intrabank)
            }
            iclAmount.edtValue.setText(Const.EMPTY)
            iclFee.root.gone()
            iclTotalAmount.root.gone()
            iclRemarks.edtValue.setText(
                getCurrentUser()?.username.plus(Const.SEPARATOR_SPACE)
                    .plus(getString(R.string.transfer))
            )
        }
    }

    private fun isIntrabank() = run { currentType == INTRABANK }

    private fun onChangeTypeTransfer(type: String) {
        if (currentType != type) {
            currentType = type
            updateViewTypeTransfer()
            resetStateTransfer()
        }
    }

    override fun initObserve() {
    }

}