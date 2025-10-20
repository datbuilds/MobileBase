package vn.shb.lao.screens.transfer

import android.view.View
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import vn.shb.core.utils.extesions.setOnSingleClickListener
import vn.shb.data.entities.home.AccountDetails
import vn.shb.data.entities.home.AccountInfo
import vn.shb.lao.base.BaseFragmentBinding
import vn.shb.lao.base.view.MyTextView
import vn.shb.lao.R
import vn.shb.lao.databinding.FragmentMoneyTransferBinding
import vn.shb.lao.screens.home.HomeViewModel

class MoneyTransferFragment : BaseFragmentBinding<FragmentMoneyTransferBinding>(
    FragmentMoneyTransferBinding::inflate
) {

    private val homeViewModel : HomeViewModel by sharedViewModel()

    private var currentType: String = INTRABANK

    companion object {
        const val INTRABANK = "INTRABANK"
        const val OWN_ACCOUNT = "OWN_ACCOUNT"
    }

    override fun initView(view: View) {
        bindView()
        initTypeTransfer()
        homeViewModel.selectedAccount?.let { bindViewAccount(it) }
    }

    private fun bindView() {
        with(binding){
            iclToAccount.apply {
                tvTitle.text = getString(R.string.toAccount)

            }
        }
    }

    private fun initTypeTransfer() {
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
            tvBalanceValue.text = account.availableBalance.toString()
        }
    }

    override fun initListener() {
        with(binding){
            tvMoneyTransferTitle.setOnSingleClickListener {
                backPress()
            }
        }
    }

    override fun initObserve() {
    }

}