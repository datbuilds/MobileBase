package vn.shb.lao.screens.home

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupWindow
import androidx.core.graphics.drawable.toDrawable
import androidx.viewbinding.ViewBinding
import com.example.imagecrouse.databinding.ItemCustomFixedSizeLayout1Binding
import com.example.imagecrouse.ui.whynotimagecarousel.listener.CarouselListener
import com.example.imagecrouse.ui.whynotimagecarousel.model.CarouselItem
import com.example.imagecrouse.ui.whynotimagecarousel.utils.setImage
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import vn.shb.core.core.delivery.ReasonDescription.ENGLISH
import vn.shb.core.core.delivery.ReasonDescription.LAO
import vn.shb.core.core.delivery.ReasonDescription.VIET
import vn.shb.core.utils.extesions.setOnSingleClickListener
import vn.shb.data.entities.AccountBase
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
import vn.shb.lao.utils.view.dialog.ScreenUtils
import vn.shb.lao.utils.view.setWidth
import vn.shb.lao.utils.widgets.LocaleHelper

class HomeFragment : BaseFragmentBinding<FragmentHomeBinding>(FragmentHomeBinding::inflate) {

    private lateinit var adapter: LoopingAdapter

    private var isShowValueBalance = false
    private var textGoneValue = "********"

    companion object {
        const val AUTO_SCROLL_BANNER_DELAY = 2000L
    }

    override fun initView(view: View) {
        bindView()
        setup()
    }

    private fun setup() {
        binding.carousel3.registerLifecycle(lifecycle)

        // Custom view
        binding.carousel3.carouselListener =
            object : CarouselListener {
                override fun onCreateViewHolder(
                    layoutInflater: LayoutInflater,
                    parent: ViewGroup,
                ): ViewBinding =
                    ItemCustomFixedSizeLayout1Binding.inflate(layoutInflater, parent, false)

                override fun onBindViewHolder(
                    binding: ViewBinding,
                    item: CarouselItem,
                    position: Int,
                ) {
                    val currentBinding = binding as ItemCustomFixedSizeLayout1Binding
                    currentBinding.root.setWidth((ScreenUtils.getScreenWidth(requireActivity()) * 0.7).toInt())
                    currentBinding.imageView.apply {
                        scaleType = ImageView.ScaleType.CENTER_CROP

                        // carousel_default_placeholder is the default placeholder comes with
                        // the library.
                        setImage(item, R.drawable.bg_place_holder)
                    }
                }
            }

        val listThree = mutableListOf<CarouselItem>()

        for (item in homeViewModel.getListBanner()) {
            listThree.add(
                CarouselItem(
                    imageDrawable = item
                ),
            )
        }

        binding.carousel3.setData(listThree)
    }

    private fun getDataUser() {
        homeViewModel.getUserInfo()
    }

    override fun onResume() {
        super.onResume()
        getDataUser()
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
                DialogSelectAccount.Build(
                    homeViewModel.getListAccount(),
                    homeViewModel.selectedAccount
                ) { ac ->
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
                    tvValueBalance.text =
                        (if (isShowValueBalance) account.getAvailableBalance() else textGoneValue).plus(
                            Const.SEPARATOR_SPACE
                        ).plus(account.currencyCode)
                }
            }
            flQrCode.setOnClickListener {
//                showLanguagePopup(flQrCode)
            }
            flBeneficiary.setOnSingleClickListener {
                safeNavigate(R.id.homeFragment, R.id.action_homeFragment_to_beneficiaryFragment)
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

/*    private fun restartApp(context: Context) {
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
    }*/
}
