package com.mobile.base.screens.home

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.mobile.base.R
import com.mobile.base.activity.MainActivity
import com.mobile.base.base.BaseFragmentBinding
import com.mobile.base.databinding.FragmentHomeBinding
import com.mobile.base.navigation.AppDestination
import com.mobile.base.screens.home.helper.HomeBannerAdapter
import com.mobile.base.screens.home.widget.OnClickDetail
import com.mobile.base.utils.extensions.common.Const
import com.mobile.base.utils.extensions.getTextWelcomeUser
import com.mobile.base.utils.extensions.launchRepeatOnLifecycle
import com.mobile.base.utils.widgets.LocaleHelper
import com.mobile.base.core.utils.extesions.setOnSingleClickListener
import com.mobile.base.data.entities.AccountBase
import com.mobile.base.data.entities.login.UserLog

class HomeFragment : BaseFragmentBinding<FragmentHomeBinding>(FragmentHomeBinding::inflate) {

    private var isShowValueBalance = false
    private var textGoneValue = "********"

    private var isShowPassExpire = false
    private val bannerImages = listOf(
        R.drawable.img_banner_first,
        R.drawable.img_banner_second
    )
    private val bannerAdapter by lazy(LazyThreadSafetyMode.NONE) {
        HomeBannerAdapter(bannerImages)
    }
    private val bannerSnapHelper by lazy(LazyThreadSafetyMode.NONE) {
        PagerSnapHelper()
    }
    private val bannerScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                syncBannerIndicator()
            }
        }
    }

    override fun initView(view: View) {
        (activity as? MainActivity)?.startSessionTimer()
        checkPassExpire()
        bindView()
        initBanner()
    }

    private fun checkPassExpire() {
        val dayPassExpire = arguments?.getInt(AppDestination.ARG_DAY_PASS_EXPIRE, 0)
        if (dayPassExpire != null && dayPassExpire > 0 && !isShowPassExpire) {
            isShowPassExpire = true
            showPasswordExpire(getString(R.string.notification_password_expiring, dayPassExpire)){
                safeNavigate(AppDestination.ChangePassword())
            }
        }
    }

    override fun onResume() {
        super.onResume()
        homeViewModel.selectedAccount = null
        homeViewModel.getUserInfo()
        homeViewModel.getTransferAccount()
    }


    private fun bindView() {
        binding.apply {
            incItemTransfer.apply {
                ivIconFeature.setImageResource(R.drawable.ic_transfer)
                tvTitleFeature.text = getString(R.string.transfer)
            }
            incItemAccounts.apply {
                ivIconFeature.setImageResource(R.drawable.ic_accounts)
                tvTitleFeature.text = getString(R.string.accounts)
            }
        }
    }

    private fun initBanner() {
        binding.rvBanner.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
            adapter = bannerAdapter
            clipToPadding = false
            clipChildren = false
            addOnScrollListener(bannerScrollListener)
        }

        bannerSnapHelper.attachToRecyclerView(binding.rvBanner)
        binding.dot.setupWithRecyclerView(binding.rvBanner)
        binding.dot.selectPage(0)
        syncBannerIndicator()
    }

    private fun syncBannerIndicator() {
        val layoutManager = binding.rvBanner.layoutManager as? LinearLayoutManager ?: return
        val firstVisible = layoutManager.findFirstVisibleItemPosition()
        if (firstVisible == RecyclerView.NO_POSITION) return

        val firstView = layoutManager.findViewByPosition(firstVisible) ?: return
        val rvWidth = binding.rvBanner.width - binding.rvBanner.paddingStart - binding.rvBanner.paddingEnd
        val itemWidth = firstView.width
        val itemLeft = firstView.left - binding.rvBanner.paddingStart

        // Nếu item đầu tiên bị scroll quá nửa thì position là item kế tiếp
        val position = if (itemLeft < -(itemWidth / 2)) {
            firstVisible + 1
        } else {
            firstVisible
        }

        val safePosition = position.coerceIn(0, (bannerAdapter.itemCount - 1).coerceAtLeast(0))
        binding.dot.selectPage(safePosition)
    }

    private fun bindViewAccount(account: AccountBase) {
        val valueAccount = getTypeAccount(requireContext(), account)
        binding.tvCurrentAccount.text = valueAccount.plus(Const.SEPARATOR_DASH)
            .plus(account.accountNumber).plus(
                Const.SEPARATOR_DASH
                    .plus(account.currencyCode)
            )
        binding.tvValueBalance.text =
            (if (isShowValueBalance) account.getAvailableBalance() else textGoneValue).plus(Const.SEPARATOR_SPACE)
                .plus(account.currencyCode)
    }

    override fun initListener() {
        with(binding) {
            flAvatarUser.setListener(
                object : OnClickDetail {
                    override fun onAvatarClick() {
                        safeNavigate(AppDestination.Profile)
                    }
                })

            incItemTransfer.root.setOnSingleClickListener {
                homeViewModel.checkAccountNull {
                    safeNavigate(AppDestination.MoneyTransfer())
                }
            }

            incItemAccounts.root.setOnSingleClickListener {
                homeViewModel.checkAccountNull(homeViewModel.getListAccount()) {
                    safeNavigate(AppDestination.AccountDetail)
                }
            }

            tvCurrentAccount.setOnSingleClickListener {
                DialogSelectAccount.Build(
                    homeViewModel.getListAccount(),
                    homeViewModel.selectedAccount,
                    isCanSelect = false
                ) { ac ->
//                    homeViewModel.selectedAccount = ac
//                    bindViewAccount(ac)
                }.build().show(childFragmentManager, DialogSelectAccount.TAG)
            }

            ivEyeSeeValue.setOnSingleClickListener {
                isShowValueBalance = !isShowValueBalance
                ivEyeSeeValue.setImageResource(
                    if (isShowValueBalance) R.drawable.ic_eye_closed else R.drawable.ic_eye_show
                )
                homeViewModel.selectedAccount?.let { account ->
                    tvValueBalance.text =
                        (if (isShowValueBalance) account.getAvailableBalance() else textGoneValue).plus(
                            Const.SEPARATOR_SPACE
                        ).plus(account.currencyCode)
                }
            }
            flQrCode.setOnSingleClickListener {
//                showLanguagePopup(flQrCode)
            }
            tvBeneficiary.setOnSingleClickListener {
                safeNavigate(AppDestination.Beneficiary)
            }

            tvChatPay.setOnSingleClickListener {
                homeViewModel.checkAccountNull {
                    safeNavigate(AppDestination.Paste2Pay)
                }
            }
        }
    }

    override fun initObserve() {
        with(homeViewModel) {
            launchRepeatOnLifecycle {
                launch {
                    stateUserInfo.collectLatest {
                        val userLog = getCurrentUser()
                        userLog?.let { it ->
                            mapUserInfo(it)
                        }
                    }
                }
                launch {
                    stateSelectedAccount.collectLatest { accountInfo ->
                        bindViewAccount(accountInfo)
                    }
                }

                launch {
                    stateAccountNull.collectLatest {
                        if (it) {
                            showErrorMessageOnly(getString(R.string.yourCurrentAccountIsCurrentlyBlocked))
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        binding.rvBanner.removeOnScrollListener(bannerScrollListener)
        bannerSnapHelper.attachToRecyclerView(null)
        super.onDestroyView()
    }

    private fun mapUserInfo(userLog: UserLog) {
        userLog.let { user ->
            binding.tvNameUser.text = user.username
            binding.tvWelcomeSHB.text = requireContext().getTextWelcomeUser()
            binding.flAvatarUser.setUserName(getPathAvatarUser(), user.username)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(LocaleHelper.setLocale(context, LocaleHelper.getCurrentLanguage(context)))
    }
}
