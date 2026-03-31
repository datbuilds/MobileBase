package vn.shb.cam.screens.transfer

import android.view.View
import androidx.core.content.ContextCompat
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import vn.shb.cam.R
import vn.shb.cam.base.BaseFragmentBinding
import vn.shb.cam.databinding.ChildViewTransactionInfoBinding
import vn.shb.cam.databinding.FragmentTransactionDetailBinding
import vn.shb.cam.navigation.AppDestination
import vn.shb.cam.screens.beneficiary.BeneficiaryViewModel
import vn.shb.cam.screens.transaction.DialogSetNickname
import vn.shb.cam.utils.ApiConst
import vn.shb.cam.utils.BankType
import vn.shb.cam.utils.extensions.CACHE_IMAGE_FILE_NAME
import vn.shb.cam.utils.extensions.CustomToastShowOnTop
import vn.shb.cam.utils.extensions.DateTimeHelper
import vn.shb.cam.utils.extensions.cacheBitmap
import vn.shb.cam.utils.extensions.common.Const
import vn.shb.cam.utils.extensions.gone
import vn.shb.cam.utils.extensions.invisible
import vn.shb.cam.utils.extensions.launchRepeatOnLifecycle
import vn.shb.cam.utils.extensions.setExchangeRateText
import vn.shb.cam.utils.extensions.shareImage
import vn.shb.cam.utils.extensions.toBitmap
import vn.shb.cam.utils.extensions.visible
import vn.shb.cam.utils.view.dialog.BottomSheetDialogHelper
import vn.shb.core.core.delivery.reason.AppReason
import vn.shb.core.core.domain.source.request.BeneficiaryRequest
import vn.shb.core.core.domain.usecases.transfer.UseCaseTransactionDetail
import vn.shb.core.utils.extesions.setOnSingleClickListener
import vn.shb.data.entities.beneficiary.Beneficiary
import vn.shb.data.entities.formatExchangeRate
import vn.shb.data.entities.getBalance
import vn.shb.data.entities.home.TransactionDetail

class PaymentTransferFragment :
    BaseFragmentBinding<FragmentTransactionDetailBinding>(FragmentTransactionDetailBinding::inflate) {

    private val beneficiaryViewModel: BeneficiaryViewModel by viewModel()

    private val accountNo by lazy {
        arguments?.getString(ApiConst.KEY_ACCOUNT_NO_TRANSACTION)
    }

    private val isIntrabank by lazy {
        arguments?.getBoolean(ApiConst.KEY_TYPE_TRANSFER_INTRABANK) == true
    }

    private val isConfirmError by lazy {
        arguments?.getBoolean(ApiConst.KEY_CONFIRM_ERROR) == true
    }

    private var transactionDetails: TransactionDetail? = null
    private var listBeneficiary: List<Beneficiary> = arrayListOf()

    override fun initView(view: View) {
        bindViewPayment()
        beneficiaryViewModel.getAllBeneficiary()
        val confirmSuccess = homeViewModel.confirmSuccessData
        if (confirmSuccess?.refNo != null && accountNo != null && confirmSuccess.status == ApiConst.SUCCESS && !isConfirmError) {
            homeViewModel.getTransactionDetail(
                UseCaseTransactionDetail.Params(
                    confirmSuccess.refNo, accountNo!!,
                    ApiConst.D_TRANSFER_MONEY,
                    mdCode = confirmSuccess.moduleCode,
                    transCode = confirmSuccess.transactionCode,
                    transDate = confirmSuccess.transactionDate
                )
            )

            homeViewModel.getExchangeRates(Const.USD, Const.KHR)

        } else {
            bindViewFailed()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        homeViewModel.clearSessionTransaction()
    }

    private fun bindViewFailed() {
        with(binding) {
            ivStatus.visible()
            tvTransactionAmount.visible()
            ivStatus.setImageResource(R.drawable.ic_fail)
            tvTransactionAmount.text = getString(R.string.transactionFail)
            tvTransactionAmount.setTextColor(getColor(R.color.color_error_text_login))
            homeViewModel.confirmModel?.let { cf ->
                tvValueBalance.text = cf.amount.first.getBalance()
                tvCurrentCode.text = cf.amount.second
            }
            tvAnErrorHasOccurred.visible()
            bindButtonNewTransaction(R.drawable.bg_account_info_transfer, R.color.white)
        }
    }

    private fun bindViewPayment() {
        with(binding) {
            ivClose.setImageResource(R.drawable.ic_to_home)
            tvCreateNewTransaction.visible()
        }
    }

    private fun setupView(trans: TransactionDetail) {
        transactionDetails = trans
        with(binding) {
            ivStatus.visible()
            tvTransactionAmount.visible()
            tvTransactionAmount.text = getString(R.string.transactionSuccess)
            tvTransactionAmount.setTextColor(getColor(R.color.colorSuccess))
            tvValueBalance.text = trans.amount.getBalance()
            tvCurrentCode.text = trans.currency
            tvShare.visible()

            bindButtonNewTransaction(R.drawable.bg_new_transaction, R.color.primary100)
            val fromAccount = trans.ordAccType.plus(Const.SEPARATOR_DASH).plus(trans.ordAccount)
            if (!trans.ordAccount.isNullOrEmpty()) {
                iclFromAccount.bindView(
                    getString(R.string.fromAccount),
                    trans.ordAccType.plus(Const.SEPARATOR_DASH).plus(trans.ordAccount)
                )
                iclFromAccount.root.visible()
            } else {
                iclFromAccount.root.gone()
            }
            if (!trans.benAccount.isNullOrEmpty()) {
                iclToAccount.bindView(
                    getString(R.string.toAccount),
                    trans.benAccType.plus(Const.SEPARATOR_DASH).plus(trans.benAccount)
                )
                iclToAccount.root.visible()
            } else {
                iclToAccount.root.gone()
            }
            iclRemarks.bindView(
                getString(R.string.remarks),
                trans.remarks
            )
            iclTransactionDate.bindView(
                getString(R.string.transactionDate),
                DateTimeHelper.toDisplayDate(trans.transDate)
            )
            iclReferenceNumber.bindView(
                getString(R.string.referenceNumber),
                trans.refNo
            )
            if (isIntrabank && trans.accountName != null) {
                iclAccountName.root.visible()
                iclAccountName.bindView(
                    getString(R.string.accountName),
                    trans.accountName!!
                )
            } else {
                iclAccountName.root.gone()
            }

            iclExchangeRate.apply {
                if (trans.ccyCdDst != trans.ccyCdSrc) {

                    binding.iclExchangeRate.tvLabel.text = getString(R.string.exchangeRate)

                    val isFromUsd = trans.ccyCdSrc == Const.USD
                    val exChangeRate = if (isFromUsd) trans.rate.formatExchangeRate() else (1 / trans.rate.toDouble()).formatExchangeRate()

                    tvValue.setExchangeRateText(
                        "1",
                        Const.USD,
                        exChangeRate.toString(),
                        Const.KHR
                    )
                } else {
                    root.gone()
                }
            }

            // Show save recipient logic
            if (!trans.hasBeneficiary && isIntrabank) {
                (rlSaveRecipient as View).visible()
            }
        }
    }

    override fun initListener() {

        with(binding) {
            ivClose.setOnSingleClickListener {
                popBackTo(AppDestination.Home)
            }

            ivCloseToast.setOnSingleClickListener {
                llToastStatus.animate().cancel()
                llToastStatus.visibility = View.GONE
            }

            tvCreateNewTransaction.setOnSingleClickListener {
                popBackTo(AppDestination.MoneyTransfer())
            }

            tvShare.setOnSingleClickListener {
                cutImageTransferDetails()
            }

            rlSaveRecipient.setOnSingleClickListener {
                DialogSetNickname { nickname ->
                    if (listBeneficiary.any { it.accountNick == nickname }) {
                        showDialogAlreadyNickname {
                            createBeneficiary(nickname)
                        }
                    } else {
                        createBeneficiary(nickname)
                    }
                }.show(childFragmentManager, DialogSetNickname.TAG)
            }
        }
    }

    private fun showDialogAlreadyNickname(update: () -> Unit) {
        BottomSheetDialogHelper(requireContext()).message(
            title = getString(R.string.confirmation),
            message = getString(
                R.string.nicknameAlreadyExists
            ),
            textNegative = getString(R.string.dontAllowRemoveBeneficiary),
            textPositive = getString(R.string.allowRemoveBeneficiary),
            positiveAction = {
                update.invoke()
            }
        )
    }

    private fun createBeneficiary(nickname: String) {
        if (accountNo != null) {
            val user = homeViewModel.getCurrentUserInfo()
            val toAccount = homeViewModel.confirmModel?.toAccount
            val request = BeneficiaryRequest(
                accountNumber = toAccount?.accountNumber ?: "",
                accountName = toAccount?.customerName ?: transactionDetails?.accountName ?: "",
                accountNick = nickname.ifEmpty { null },
                remark = (user?.customerName ?: "").plus(Const.SEPARATOR_SPACE)
                    .plus(getString(R.string.transferCAP)),
                bankCode = BankType.SHB.code
            )
            beneficiaryViewModel.createBeneficiary(request, false)
            binding.rlSaveRecipient.gone()
        }
    }

    private fun cutImageTransferDetails() {
        with(binding) {
            val oldVisibilityClose = ivClose.visibility
            val oldVisibilityCreate = tvCreateNewTransaction.visibility
            val oldVisibilityShare = tvShare.visibility

            ivClose.invisible()
            tvCreateNewTransaction.invisible()
            tvShare.invisible()

            val bitmap = root.toBitmap()

            ivClose.visibility = oldVisibilityClose
            tvCreateNewTransaction.visibility = oldVisibilityCreate
            tvShare.visibility = oldVisibilityShare

            requireContext().cacheBitmap(bitmap, CACHE_IMAGE_FILE_NAME) {
                if (it) {
                    requireContext().shareImage(CACHE_IMAGE_FILE_NAME)
                }
            }
        }
    }

    override fun initObserve() {
        with(homeViewModel) {
            launchRepeatOnLifecycle {

                launch {
                    stateTransactionDetail.collectLatest {
                        it?.let { trans -> setupView(trans) }
                    }
                }

                launch {
                    stateDetailError.collectLatest {
                        handleErrorHome(
                            AppReason(
                                message = getString(R.string.systemUptateTransactionStatus),
                                code = getString(R.string.errorCode)
                            )
                        )
                    }
                }
            }
        }


        with(beneficiaryViewModel) {
            launchRepeatOnLifecycle {
                launch {
                    stateAction.collect { success ->
                        if (success == true) {
                            showToastSuccess(getString(R.string.successfullySetNickname), true)
                        }
                    }
                }
                launch {
                    stateError.collect {
                        showToastSuccess(it.errMessage, false)
//                    handleErrorHome(it)
                    }
                }

                launch {
                    localBeneficiaries.collect {
                        listBeneficiary = it
                    }
                }
            }
        }
    }

    fun showToastSuccess(text: String, isSuccess: Boolean = true) {
        context?.let { context ->
            val background =
                if (isSuccess) R.drawable.bg_toast_change_avatar_ss else R.drawable.bg_toast_change_avatar_error
            val icon = if (isSuccess) R.drawable.ic_success else R.drawable.ic_error

            val toast = CustomToastShowOnTop(
                context,
                binding.root,
                icon = icon,
                message = text,
                background = background,
                duration = 3000,
                textColor = R.color.neutral1,
                iconClose = R.color.neutral1
            )
            toast.show()
        }
//
//        with(binding) {
//            tvToastMessage.text = text
//            llToastStatus.setBackgroundResource(if (isSuccess) R.drawable.bg_toast_change_avatar_ss else R.drawable.bg_toast_change_avatar_error)
//            tvToastMessage.setCompoundDrawablesWithIntrinsicBounds(
//                if (isSuccess) R.drawable.ic_success else R.drawable.ic_error,
//                0,
//                0,
//                0
//            )
//            llToastStatus.visible()
//            llToastStatus.animate()
//                .alpha(1f)
//                .setDuration(200)
//                .withEndAction {
//                    llToastStatus.postDelayed({
//                        llToastStatus.animate()
//                            .alpha(0f)
//                            .setDuration(300)
//                            .withEndAction {
//                                llToastStatus.visibility = View.GONE
//                                llToastStatus.alpha = 1f
//                            }
//                            .start()
//                    }, 3000)
//                }
//                .start()
//        }
    }

    private fun bindButtonNewTransaction(
        idBg: Int,
        color: Int
    ) {
        binding.tvCreateNewTransaction.setBackgroundDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                idBg
            )
        )
        binding.tvCreateNewTransaction.setTextColor(getColor(color))
    }

    private fun ChildViewTransactionInfoBinding.bindView(
        title: String,
        des: String
    ) {
        tvLabel.text = title
        tvValue.text = des
    }

}
