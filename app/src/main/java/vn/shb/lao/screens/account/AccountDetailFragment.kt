package vn.shb.lao.screens.account

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import vn.shb.core.utils.extesions.setOnSingleClickListener
import vn.shb.lao.R
import vn.shb.lao.base.BaseFragmentBinding
import vn.shb.lao.databinding.FragmentAccountDetailBinding
import vn.shb.lao.screens.account.helper.TransactionAdapter
import vn.shb.lao.screens.home.HomeViewModel

class AccountDetailFragment :
    BaseFragmentBinding<FragmentAccountDetailBinding>(FragmentAccountDetailBinding::inflate) {
    private lateinit var adapter: TransactionAdapter

    private val homeViewModel : HomeViewModel by sharedViewModel()

    override fun initView(view: View) {
        bindViewDetail()
        setUpRecyclerView()
    }

    private fun setUpRecyclerView() {
        adapter = TransactionAdapter(homeViewModel.getListTransaction())
        adapter.setOnClickDetailListener {
            // Handle click event here
        }
        with(binding.rcvTransaction) {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@AccountDetailFragment.adapter
        }
    }

    private fun bindViewDetail() {
        val account = homeViewModel.selectedAccount
        account?.let {
            with(binding){
                tvValueBalance.text = "${it.balance} ${it.currency}"
                tvNumberAccount.text = it.accountNumber
                tvNameBranch.text = "SHB LAO - HO"
            }
        }

    }

    override fun initListener() {
        with(binding) {
            tvAccountDetails.setOnSingleClickListener {
                safeNavigate(R.id.accountDetailFragment, R.id.backToHomeFragment)
            }
        }
    }

    override fun initObserve() {
    }

}