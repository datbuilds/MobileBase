package vn.shb.cam.screens.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewbinding.ViewBinding
import com.example.imagecrouse.databinding.ItemCustomFixedSizeLayout1Binding
import com.example.imagecrouse.ui.whynotimagecarousel.listener.CarouselListener
import com.example.imagecrouse.ui.whynotimagecarousel.model.CarouselItem
import com.example.imagecrouse.ui.whynotimagecarousel.utils.setImage
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import vn.shb.cam.R
import vn.shb.cam.base.BaseFragmentBinding
import vn.shb.cam.databinding.FragmentHomeBinding
import vn.shb.cam.screens.home.helper.LoopingAdapter
import vn.shb.cam.screens.home.widget.OnClickDetail
import vn.shb.cam.utils.extensions.common.Const
import vn.shb.cam.utils.extensions.getTextWelcomeUser
import vn.shb.cam.utils.extensions.launchRepeatOnLifecycle
import vn.shb.cam.utils.view.dialog.ScreenUtils
import vn.shb.cam.utils.view.setWidth
import vn.shb.cam.utils.widgets.LocaleHelper
import vn.shb.core.utils.extesions.setOnSingleClickListener
import vn.shb.data.entities.AccountBase
import vn.shb.data.entities.login.UserLog

class HomeFragment : BaseFragmentBinding<FragmentHomeBinding>(FragmentHomeBinding::inflate) {

    private lateinit var adapter: LoopingAdapter

    private var isShowValueBalance = false
    private var textGoneValue = "********"

    override fun initView(view: View) {
        bindView()
//        setup()
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

    override fun onResume() {
        super.onResume()
        homeViewModel.selectedAccount = null
        homeViewModel.getUserInfo()
//        bindViewAccount(AccountInfo().apply {
//            setValueAccountNumber("123456789")
//        })
//        mapUserInfo(UserLog(username = "PHASOUK BOUNMIXAY"))
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
            ivBanner.setImageResource(R.drawable.iv_banner)
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
            }
        }
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
