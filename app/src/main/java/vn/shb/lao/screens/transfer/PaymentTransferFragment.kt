package vn.shb.lao.screens.transfer

import android.view.View
import androidx.core.content.ContextCompat
import vn.shb.lao.screens.transaction.DialogSetNickname
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import vn.shb.core.core.delivery.Reason
import vn.shb.core.core.delivery.reason.AppReason
import vn.shb.core.core.domain.usecases.transfer.UseCaseTransactionDetail
import vn.shb.core.utils.extesions.setOnSingleClickListener
import vn.shb.data.entities.getBalance
import vn.shb.data.entities.home.TransactionDetail
import vn.shb.lao.R
import vn.shb.lao.base.BaseFragmentBinding
import vn.shb.lao.databinding.ChildViewTransactionInfoBinding
import vn.shb.lao.databinding.FragmentTransactionDetailBinding
import vn.shb.lao.utils.ApiConst
import vn.shb.lao.utils.extensions.DateTimeHelper
import vn.shb.lao.utils.extensions.common.Const
import vn.shb.lao.utils.extensions.gone
import vn.shb.lao.utils.extensions.launchRepeatOnLifecycle
import vn.shb.lao.utils.extensions.visible
import vn.shb.lao.utils.extensions.invisible
import vn.shb.lao.utils.extensions.toBitmap
import vn.shb.lao.utils.extensions.cacheBitmap
import vn.shb.lao.utils.extensions.shareImage
import vn.shb.lao.utils.extensions.CACHE_IMAGE_FILE_NAME

import org.koin.androidx.viewmodel.ext.android.viewModel
import vn.shb.lao.screens.beneficiary.BeneficiaryViewModel

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

    override fun initView(view: View) {
        bindViewPayment()
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
                tvValueBalance.text = cf.amount.getBalance()
                tvCurrentCode.text = cf.fromAccount.currencyCode
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
            iclFromAccount.bindView(
                getString(R.string.fromAccount),
                fromAccount
            )
            iclToAccount.bindView(
                getString(R.string.toAccount),
                trans.benAccType.plus(Const.SEPARATOR_DASH).plus(trans.benAccount)
            )
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
            if (isIntrabank) {
                iclAccountName.root.visible()
                iclAccountName.bindView(
                    getString(R.string.accountName),
                    trans.accountName
                )
            } else {
                iclAccountName.root.gone()
            }
            
            // Show save recipient logic
             if (trans.benAccount?.isNotEmpty() == true) {
                 (rlSaveRecipient as View).visible()
             }
        }
    }

    override fun initListener() {

        with(binding) {
            ivClose.setOnSingleClickListener {
                popBackTo(R.id.homeFragment)
            }

            tvCreateNewTransaction.setOnSingleClickListener {
                popBackTo(R.id.moneyTransferFragment)
            }

            tvShare.setOnSingleClickListener {
                cutImageTransferDetails()
            }
            
            (rlSaveRecipient as View).setOnSingleClickListener {
                 DialogSetNickname { nickname ->
                     if (accountNo != null) {
                         val accountName = homeViewModel.confirmModel?.toAccount?.customerName ?: ""
                         val request = vn.shb.core.core.domain.source.request.BeneficiaryRequest(
                             accountNumber = accountNo!!,
                             accountName = accountName,
                             remark = nickname,
                             bankCode = null
                         )
                         beneficiaryViewModel.createBeneficiary(request)
                     }
                 }.show(childFragmentManager, DialogSetNickname.TAG)
            }
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
                     stateAction.collectLatest { success ->
                         if (success == true) {
                             vn.shb.lao.utils.view.dialog.BottomSheetDialogHelper(requireContext()).message(
                                 title = getString(R.string.notification),
                                 message = getString(R.string.newBeneficiaryAddedSuccessfully),
                                 textPositive = getString(R.string.close)
                             )
                             (binding.rlSaveRecipient as View).gone()
                             beneficiaryViewModel.resetActionState()
                         } else if (success == false) {
                              beneficiaryViewModel.resetActionState() 
                         }
                     }
                 }
             }
        }
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
