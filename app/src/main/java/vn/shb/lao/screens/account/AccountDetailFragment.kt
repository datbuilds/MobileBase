package vn.shb.lao.screens.account

import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.getSharedViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import vn.shb.core.utils.extesions.setOnSingleClickListener
import vn.shb.data.entities.home.AccountDetails
import vn.shb.data.entities.home.TransactionItem
import vn.shb.lao.R
import vn.shb.lao.base.BaseFragmentBinding
import vn.shb.lao.databinding.FragmentAccountDetailBinding
import vn.shb.lao.screens.account.helper.TransactionAdapter
import vn.shb.lao.screens.home.DialogSelectAccount

import vn.shb.lao.utils.extensions.launchRepeatOnLifecycle

class AccountDetailFragment :
    BaseFragmentBinding<FragmentAccountDetailBinding>(FragmentAccountDetailBinding::inflate) {
    private val adapter by lazy { TransactionAdapter() }

    override fun initView(view: View) {
        setUpRecyclerView()
    }

    override fun onResume() {
        super.onResume()
        context?.let { homeViewModel.getTake5Transaction(it) }
        homeViewModel.getAccountDetails()
    }

    private fun setUpRecyclerView() {
        adapter.setOnClickDetailListener {
            homeViewModel.currentTransaction = it
            safeNavigate(R.id.accountDetailFragment, R.id.transactionDetailFragment)
        }
        with(binding.rcvTransaction) {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@AccountDetailFragment.adapter
        }
    }

    private fun bindViewDetail(accountDetails: AccountDetails) {
        val account = homeViewModel.selectedAccount
        account?.let {
            binding.apply {
                tvValueBalance.text =
                    "${accountDetails.getAvailableBalance()} ${accountDetails.currencyCode}"
                tvNumberAccount.text = accountDetails.accountNumber
                tvNameBranch.text = accountDetails.positionDescription
            }
        }

    }

    override fun initListener() {
        with(binding) {
            tvAccountDetails.setOnSingleClickListener {
                safeNavigate(R.id.accountDetailFragment, R.id.backToHomeFragment)
            }

            flTransfer.setOnSingleClickListener {
                safeNavigate(R.id.accountDetailFragment, R.id.moneyTransferFragment)
            }

            tvViewAll.setOnSingleClickListener {
                safeNavigate(R.id.accountDetailFragment, R.id.action_to_transaction_history)
            }

            ivExpandDown.setOnSingleClickListener {
                DialogSelectAccount.Build(
                    homeViewModel.getListAccount(), homeViewModel.selectedAccount
                ) { ac ->
                    homeViewModel.selectedAccount = ac
                    homeViewModel.getAccountDetails(ac.accountNumber)
                    homeViewModel.getTake5Transaction(requireContext())
                }.build().show(childFragmentManager, DialogSelectAccount.TAG)
            }
        }
    }

    override fun initObserve() {
        with(homeViewModel) {
            launchRepeatOnLifecycle {
                launch {
                    stateTransactions5First.collectLatest {
                        updateList(it)
                    }
                }

                launch {
                    stateAccountDetails.collectLatest {
                        bindViewDetail(it)
                    }
                }

            }
        }
    }

    private fun updateList(list: List<TransactionItem>) {
        adapter.submitList(list)
        binding.apply {
            tvViewAll.isVisible = list.isNotEmpty()
            rcvTransaction.isVisible = list.isNotEmpty()
            tvNoTransaction.isVisible = list.isEmpty()
        }
    }

}