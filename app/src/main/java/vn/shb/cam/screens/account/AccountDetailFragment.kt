package vn.shb.cam.screens.account

import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import vn.shb.cam.R
import vn.shb.cam.base.BaseFragmentBinding
import vn.shb.cam.databinding.FragmentAccountDetailBinding
import vn.shb.cam.screens.account.helper.TransactionAdapter
import vn.shb.cam.screens.home.DialogSelectAccount
import vn.shb.cam.utils.extensions.launchRepeatOnLifecycle
import vn.shb.cam.utils.extensions.visible
import vn.shb.core.utils.extesions.setOnSingleClickListener
import vn.shb.data.entities.home.AccountDetails
import vn.shb.data.entities.home.TransactionItem

class AccountDetailFragment :
    BaseFragmentBinding<FragmentAccountDetailBinding>(FragmentAccountDetailBinding::inflate) {
    private val adapter by lazy { TransactionAdapter() }

    override fun initView(view: View) {
        setUpRecyclerView()
    }

    override fun onResume() {
        super.onResume()
        homeViewModel.getTake5Transaction(requireContext())
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
            swDefaultCasa.setOnCheckedChangeListener { _, isChecked ->
                if (swDefaultCasa.isPressed) {
                    swDefaultCasa.alpha = if (isChecked) 0.5f else 1f
                    val currentAccount = homeViewModel.selectedAccount
                    if (isChecked) {
                        homeViewModel.setDefaultAccount(currentAccount?.accountNumber!!)
                    }
                }
            }
        }
    }

    private fun bindViewDetail(accountDetails: AccountDetails) {
        val account = homeViewModel.selectedAccount
        val defaultAccount = homeViewModel.stateUserInfo.value?.defaultAcct
        account?.let {
            binding.apply {
                tvValueBalance.text =
                    "${accountDetails.getAvailableBalance()} ${accountDetails.currencyCode}"
                tvNumberAccount.text = accountDetails.accountNumber
                tvNameBranch.text = accountDetails.positionDescription

                // Update switch state without triggering listener loop if possible, 
                // or ensure listener handles redundant calls.
                // Or temporarily nullify listener? No, just checking state match is enough.
                val isDefault = it.accountNumber == defaultAccount
                val isCurrencyValid =
                    accountDetails.currencyCode == "LAK" || accountDetails.currencyCode == "USD"
                swDefaultCasa.isEnabled = !isDefault && isCurrencyValid
                val alphaSw = if (!isCurrencyValid) 0.5f else 1f
//                tvNoteAccountDefault.isVisible = isDefault
                lifecycleScope.launch {
                    swDefaultCasa.isChecked = isDefault
                    swDefaultCasa.alpha = if (isDefault) 0.5f else alphaSw
                    delay(300)
                    swDefaultCasa.visible()
                }
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

                launch {
                    stateFetchUser.collect {
                        homeViewModel.getAccountDetails()
                    }
                }

                launch {
                    stateUpdateDefaultAccount.collect {
                        if (!it) {
                            binding.swDefaultCasa.isChecked = false
                            binding.swDefaultCasa.alpha = 1f
                        }
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