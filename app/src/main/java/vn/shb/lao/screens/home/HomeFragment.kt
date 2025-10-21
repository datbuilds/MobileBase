package vn.shb.lao.screens.home

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.core.graphics.drawable.toDrawable
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import vn.shb.core.core.delivery.ReasonDescription.ENGLISH
import vn.shb.core.core.delivery.ReasonDescription.LAO
import vn.shb.core.core.delivery.ReasonDescription.VIET
import vn.shb.core.utils.extesions.setOnSingleClickListener
import vn.shb.data.entities.home.AccountInfo
import vn.shb.data.entities.login.UserLog
import vn.shb.lao.R
import vn.shb.lao.base.BaseFragmentBinding
import vn.shb.lao.databinding.FragmentHomeBinding
import vn.shb.lao.databinding.LayoutLanguagePopupBinding
import vn.shb.lao.screens.home.helper.LoopingAdapter
import vn.shb.lao.screens.home.widget.OnClickDetail
import vn.shb.lao.screens.login.ui.widget.setDisableAlpha
import vn.shb.lao.utils.extensions.common.Const
import vn.shb.lao.utils.extensions.launchRepeatOnLifecycle
import vn.shb.lao.utils.widgets.LocaleHelper

class HomeFragment : BaseFragmentBinding<FragmentHomeBinding>(FragmentHomeBinding::inflate) {

    private val homeViewModel: HomeViewModel by sharedViewModel()

    private lateinit var adapter: LoopingAdapter
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var autoRunnable: Runnable
    private lateinit var pageCallback: ViewPager2.OnPageChangeCallback

    private var isShowValueBalance = false
    private var textGoneValue = "********"

    companion object {
        const val AUTO_SCROLL_BANNER_DELAY = 2000L
    }

    override fun initView(view: View) {
        bindView()
        bindBannerView()
    }

    private fun getDataUser() {
        homeViewModel.getUserInfo()
    }

    private fun bindBannerView() {
        val images = homeViewModel.getListBanner()
        adapter = LoopingAdapter(images)
        binding.viewPager.adapter = adapter

        // --- cho phép xem 1 phần ảnh sau ---
        val pageMargin = resources.getDimensionPixelOffset(R.dimen.pageMargin)
        val pageOffset = resources.getDimensionPixelOffset(R.dimen.pageOffset)

        binding.viewPager.offscreenPageLimit = 3

        // allow children to draw outside
        val recyclerView = binding.viewPager.getChildAt(0) as RecyclerView
        recyclerView.clipToPadding = false
        recyclerView.clipChildren = false
        binding.viewPager.clipToPadding = false
        binding.viewPager.clipChildren = false

        // padding để lộ phần ảnh kế
        binding.viewPager.setPadding(pageOffset, 0, pageOffset, 0)

        // transformer để tạo khoảng cách / hiệu ứng
        binding.viewPager.setPageTransformer { page, position ->
            val offset = position * -(2 * pageOffset + pageMargin)
            page.translationX = offset
        }

        // Bắt đầu ở khối giữa (đảm bảo có trống trước/sau)
        val start = adapter.getMiddlePosition()
        binding.viewPager.setCurrentItem(start, false)

        // --- Đăng ký callback: khi vào khối đầu/cuối thì nhảy vào khối giữa (no animation) ---
        pageCallback = object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                val real = adapter.getRealCount()
                // nếu đi tới khối sau cùng -> nhảy về tương ứng trong khối giữa
                if (position >= real * 2) {
                    val newPos = position - real
                    binding.viewPager.setCurrentItem(newPos, false)
                } else if (position < real) {
                    // nếu đi tới khối trước cùng -> nhảy về tương ứng trong khối giữa
                    val newPos = position + real
                    binding.viewPager.setCurrentItem(newPos, false)
                }
            }
        }
        binding.viewPager.registerOnPageChangeCallback(pageCallback)

        // --- Auto-scroll ---
        autoRunnable = object : Runnable {
            override fun run() {
                // next item, callback trên sẽ tự điều chỉnh nếu cần
                val next = binding.viewPager.currentItem + 1
                binding.viewPager.setCurrentItem(next, true)
                handler.postDelayed(this, AUTO_SCROLL_BANNER_DELAY)
            }
        }
        handler.postDelayed(autoRunnable, AUTO_SCROLL_BANNER_DELAY)
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(autoRunnable)
        binding.viewPager.unregisterOnPageChangeCallback(pageCallback)
    }

    override fun onResume() {
        super.onResume()
        getDataUser()
        handler.postDelayed(autoRunnable, AUTO_SCROLL_BANNER_DELAY)
        binding.viewPager.registerOnPageChangeCallback(pageCallback)
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

    private fun bindViewAccount(account: AccountInfo) {
        val valueAccount = getTypeAccount(requireContext(), account)
        binding.tvCurrentAccount.text = valueAccount.first.plus(Const.SEPARATOR_DASH)
            .plus(account.accountNumber).plus(
                Const.SEPARATOR_DASH
                    .plus(account.currencyCode)
            )
        binding.tvValueBalance.text =
            (if (isShowValueBalance) valueAccount.second else textGoneValue).plus(Const.SEPARATOR_SPACE)
                .plus(account.currencyCode)
    }

    override fun initListener() {
        with(binding) {
            flAvatarUser.setListener(
                object : OnClickDetail {
                    override fun onAvatarClick() {
                        safeNavigate(R.id.homeFragment, R.id.action_homeFragment_to_profileFragment)
                    }
                })

            incItemTransfer.root.setOnSingleClickListener {
                safeNavigate(R.id.homeFragment, R.id.moneyTransferFragment)
            }
            incItemAccounts.root.setOnSingleClickListener {
                safeNavigate(R.id.homeFragment, R.id.action_homeFragment_to_accountDetailFragment)
            }

            tvCurrentAccount.setOnSingleClickListener {
                DialogSelectAccount.Build(homeViewModel.getListAccount(), homeViewModel.selectedAccount) { ac ->
                    homeViewModel.selectedAccount = ac
                    bindViewAccount(ac)
                }.build().show(childFragmentManager, DialogSelectAccount.TAG)
            }

            ivEyeSeeValue.setOnSingleClickListener {
                isShowValueBalance = !isShowValueBalance
                ivEyeSeeValue.setImageResource(
                    if (isShowValueBalance) R.drawable.ic_eye_closed else R.drawable.ic_eye_show
                )
                homeViewModel.selectedAccount?.let { account ->
                    val valueAccount = getTypeAccount(requireContext(), account)
                    tvValueBalance.text =
                        (if (isShowValueBalance) valueAccount.second else textGoneValue).plus(
                            Const.SEPARATOR_SPACE
                        ).plus(account.currencyCode)
                }
            }
            flQrCode.setOnClickListener {
//                showLanguagePopup(flQrCode)
            }
        }
    }

    override fun initObserve() {
        with(homeViewModel) {
            launchRepeatOnLifecycle {
                launch {
                    stateUserInfo.collectLatest { userInfo ->
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
                    stateError.collect { error ->
                        handleErrorHome(error)
                    }
                }
            }
        }
    }

    private fun mapUserInfo(userLog: UserLog) {
        userLog.let { user ->
            binding.tvNameUser.text = user.username
            binding.flAvatarUser.setUserName(getPathAvatarUser(), user.username)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(LocaleHelper.setLocale(context, LocaleHelper.getCurrentLanguage(context)))
    }


    fun updateLanguage(type: String) {
        context?.let { ct ->
            LocaleHelper.saveLanguage(ct, type)
            LocaleHelper.setLocale(ct, type)
//            restartApp(activity!!)
            requireActivity().recreate()
        }
    }

    private fun restartApp(context: Context) {
        val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)
        intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
        if (context is Activity) {
            context.finish()
        }
    }

    private fun showLanguagePopup(anchor: View) {
        val binding = LayoutLanguagePopupBinding.inflate(LayoutInflater.from(anchor.context))

        val popupWindow = PopupWindow(
            binding.root,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true // focusable, click outside sẽ tự đóng
        )

        val currentLanguage = LocaleHelper.getCurrentLanguage(requireContext())

        // style
        popupWindow.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
        popupWindow.isOutsideTouchable = true
        popupWindow.elevation = 8f

        binding.apply {
            iclLanguage1.apply {
                ivLogo.setImageResource(R.drawable.ic_logo_uk)
                tvNameLanguage.text = getString(R.string.english)
                root.setDisableAlpha(currentLanguage == ENGLISH)
                root.setOnSingleClickListener {
                    updateLanguage(ENGLISH)
                    popupWindow.dismiss()
                }
            }

            iclLanguage2.apply {
                ivLogo.setImageResource(R.drawable.ic_logo_vn)
                tvNameLanguage.text = getString(R.string.vietnamese)
                root.setDisableAlpha(currentLanguage == VIET)
                root.setOnSingleClickListener {
                    updateLanguage(VIET)
                    popupWindow.dismiss()
                }
            }

            iclLanguage3.apply {
                ivLogo.setImageResource(R.drawable.ic_logo_lao)
                tvNameLanguage.text = getString(R.string.lao)
                root.setDisableAlpha(currentLanguage == LAO)
                root.setOnSingleClickListener {
                    updateLanguage(LAO)
                    popupWindow.dismiss()
                }
            }
        }

        val marginRight = (130 * anchor.context.resources.displayMetrics.density).toInt()
        popupWindow.showAsDropDown(anchor, -marginRight, 0, Gravity.END)
    }
}
