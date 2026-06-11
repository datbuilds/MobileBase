package com.mobile.base.screens.account

import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.mobile.base.R
import com.mobile.base.base.BaseFragmentBinding
import com.mobile.base.databinding.FragmentAccountDetailBinding
import com.mobile.base.navigation.AppDestination
import com.mobile.base.screens.account.helper.TransactionAdapter
import com.mobile.base.screens.home.DialogSelectAccount
import com.mobile.base.utils.extensions.common.Const
import com.mobile.base.utils.extensions.launchRepeatOnLifecycle
import com.mobile.base.utils.extensions.visible
import com.mobile.base.core.utils.extesions.setOnSingleClickListener
import com.mobile.base.data.entities.home.AccountDetails
import com.mobile.base.data.entities.home.TransactionItem

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
            safeNavigate(AppDestination.TransactionDetail)
        }
        with(binding.rcvTransaction) {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@AccountDetailFragment.adapter
        }
    }

    override fun initListener() {
        with(binding) {
            tvAccountDetails.setOnSingleClickListener {
                popBackTo(AppDestination.HomeArg())
            }

            flTransfer.setOnSingleClickListener {
                homeViewModel.checkAccountNull {
                    safeNavigate(AppDestination.MoneyTransfer())
                }
            }

            tvViewAll.setOnSingleClickListener {
                safeNavigate(AppDestination.TransactionHistory)
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
                tvValueBalance.text = accountDetails.getAvailableBalance()
                tvCurrencyBalance.text = accountDetails.currencyCode
                tvNumberAccount.text = accountDetails.accountNumber
                tvNameBranch.text = accountDetails.positionDescription

                // Update switch state without triggering listener loop if possible, 
                // or ensure listener handles redundant calls.
                // Or temporarily nullify listener? No, just checking state match is enough.
                val isDefault = it.accountNumber == defaultAccount
                val isCurrencyValid =
                    accountDetails.currencyCode == Const.KHR || accountDetails.currencyCode == Const.USD
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
                stateAccountNull.collectLatest {
                    if (it) {
                        showErrorMessageOnly(getString(R.string.yourCurrentAccountIsCurrentlyBlocked))
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
