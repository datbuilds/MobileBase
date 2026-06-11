package com.mobile.base.screens.transfer

import android.annotation.SuppressLint
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.mobile.base.BuildConfig
import com.mobile.base.R
import com.mobile.base.base.BaseFragmentBinding
import com.mobile.base.databinding.FragmentConfirmationBinding
import com.mobile.base.navigation.AppDestination
import com.mobile.base.screens.login.ui.widget.ConfirmDeviceView
import com.mobile.base.utils.ApiConst
import com.mobile.base.utils.extensions.checkShowProgressDialog
import com.mobile.base.utils.extensions.common.Const
import com.mobile.base.utils.extensions.gone
import com.mobile.base.utils.extensions.hideProgressDialog
import com.mobile.base.utils.extensions.hideSoftKeyboard
import com.mobile.base.utils.extensions.launchRepeatOnLifecycle
import com.mobile.base.utils.extensions.visible
import com.mobile.base.utils.view.dialog.CountdownBottomSheetDialog
import com.mobile.base.core.core.domain.source.response.TransactionTransfer
import com.mobile.base.core.utils.extesions.setOnSingleClickListener
import com.mobile.base.data.entities.getBalance

class ConfirmationFragment : BaseFragmentBinding<FragmentConfirmationBinding>(
    FragmentConfirmationBinding::inflate
) {

    private lateinit var confirmDeviceView: ConfirmDeviceView

    private val isIntrabank by lazy {
        arguments?.getBoolean(ApiConst.KEY_TYPE_TRANSFER_INTRABANK) ?: false
    }

    override fun isPaddingBottom(): Boolean {
        return true
    }

    override fun initView(view: View) {
        bindViewDefault()
        bindConfirmationView()
        initViewDialogCf()
    }

    private fun initViewDialogCf() {
        confirmDeviceView = ConfirmDeviceView(requireContext())
        binding.flRegisterDevice.addView(confirmDeviceView)
        confirmDeviceView.visibility = View.GONE
    }

    private fun bindViewDefault() {
        with(binding) {
            if (isIntrabank) {
                iclTo.root.visible()
                iclTo.tvLabel.text = getString(R.string.to)
                iclTo.tvValue.text = getString(R.string.SHBAccount)
                iclAccountName.tvLabel.text = getString(R.string.accountName)
            } else {
                iclTo.root.gone()
                iclAccountName.root.gone()
            }
            iclFromAccount.tvLabel.text = getString(R.string.fromAccount)
            iclToAccount.tvLabel.text = getString(R.string.toAccount)
            iclRemarks.tvLabel.text = getString(R.string.remarks)
            iclTransactionDate.tvLabel.text = getString(R.string.transactionDate)
            iclAmount.tvLabel.text = getString(R.string.amount)
            iclFee.tvLabel.text = getString(R.string.fee)
            iclTotalAmount.tvLabel.text = getString(R.string.totalAmount)
            iclExchangeRate.apply {
                tvLabel.text = getString(R.string.exchangeRate)
                tvValueSup.visible()
                tvCurrencyCodeSup.visible()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun bindConfirmationView() {
        homeViewModel.confirmModel?.let { cf ->
            with(binding) {
                iclFromAccount.tvValue.text =
                    cf.fromAccount.productDescription.plus(Const.SEPARATOR_DASH)
                        .plus(cf.fromAccount.accountNumber)
                iclToAccount.tvValue.text =
                    cf.toAccount.productDescription.plus(Const.SEPARATOR_DASH)
                        .plus(cf.toAccount.accountNumber)
                iclRemarks.tvValue.text = cf.remarks
                iclTransactionDate.tvValue.text = cf.transactionDate
                iclAmount.apply {
                    tvValue.text = cf.amount.first.getBalance()
                    tvCurrencyCode.text = cf.amount.second
                }
                iclFee.apply {
                    root.gone()
                    tvValue.text = cf.fee.getBalance()
                    tvCurrencyCode.text = cf.fromAccount.currencyCode
                }
                iclTotalAmount.apply {
                    tvValue.text = cf.totalAmount.first.getBalance()
                    tvCurrencyCode.text = cf.totalAmount.second
                }
                if (isIntrabank) {
                    iclAccountName.tvValue.text = cf.toAccount.customerName
                }
                if (cf.fromAccount.currencyCode != cf.toAccount.currencyCode) {
                    iclExchangeRate.apply {
                        root.visible()
                        tvValue.text = "1"
                        tvCurrencyCode.text = Const.USD
                        tvValueSup.text = "≈${cf.exchangeRateScreen}"
                        tvCurrencyCodeSup.text = Const.KHR
                    }
                } else {
                    iclExchangeRate.root.gone()
                }
            }
        }
    }

    override fun initObserve() {
        launchRepeatOnLifecycle {
            with(homeViewModel) {

                launch {
                    onConfirmLoading.collect { stateLoading ->
                        if (stateLoading) {
                            checkShowProgressDialog()
                        } else {
                            hideProgressDialog()
                        }
                    }
                }

                launch {
                    stateTransactionTransferConfirm.collectLatest {
                        if (it != null) {
                            homeViewModel.confirmSuccessData = it
                            safeNavigate(
                                AppDestination.PaymentTransfer(
                                    bundleOf(
                                        ApiConst.KEY_ACCOUNT_NO_TRANSACTION to confirmModel!!.fromAccount.accountNumber,
                                        ApiConst.KEY_TYPE_TRANSFER_INTRABANK to isIntrabank
                                    )
                                )
                            )
                        }
                        hideConfirmOtpTransaction()
                    }
                }

                launch {
                    stateTransferConfirmError.collectLatest {
                        when (it.errorCode) {
                            ApiConst.TRAN_017 ->
                                confirmDeviceView.showErrorInvalidOtp(getString(R.string.incorrectOtpPleaseTryAgain))

                            ApiConst.TRAN_014 -> {
                                hideConfirmOtpTransaction()
                                CountdownBottomSheetDialog(
                                    message = R.string.otpIncorrectly5TimesTransfer,
                                    remainingSeconds = it.remainingSeconds ?: 1,
                                    maxRequest = it.maxAttempts
                                ).show(childFragmentManager, CountdownBottomSheetDialog.TAG)
                            }

                            else -> {
                                hideConfirmOtpTransaction()
                                safeNavigate(
                                    AppDestination.PaymentTransfer(
                                        bundleOf(ApiConst.KEY_CONFIRM_ERROR to true)
                                    )
                                )
                            }
                        }
                    }
                }

                launch {
                    statePostTransferError.collectLatest {
                        hideConfirmOtpTransaction()
                        when (it.errorCode) {
                            ApiConst.TRAN_015 -> {
                                CountdownBottomSheetDialog(
                                    message = R.string.requestOtpMore5TimesTransfer,
                                    remainingSeconds = it.remainingSeconds ?: 1,
                                    maxRequest = it.maxOtpRequestsPerWindow
                                ).show(childFragmentManager, CountdownBottomSheetDialog.TAG)
                            }

                            ApiConst.TRAN_014 -> {
                                CountdownBottomSheetDialog(
                                    message = R.string.otpIncorrectly5TimesTransfer,
                                    remainingSeconds = it.remainingSeconds ?: 1,
                                    maxRequest = it.maxAttempts
                                ).show(childFragmentManager, CountdownBottomSheetDialog.TAG)
                            }

                            else -> {
                                handleErrorHome(it)
                            }
                        }
                    }
                }

                launch {
                    stateTransactionTransfer.collectLatest {
                        if (it?.paymentType.equals(ApiConst.SELF)) {
                            homeViewModel.confirmTransactionTransfer(Const.EMPTY)
                        } else {
                            if (it?.paymentType.equals(ApiConst.INTRA)) {
                                showConfirmOtpTransaction(it)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun showConfirmOtpTransaction(result: TransactionTransfer?) {
        hideProgressDialog()
        val totalTime = result?.otpRemainingSeconds
        confirmDeviceView.setup(
            phoneNumber = result?.authSms ?: "",
            totalTime = totalTime?.times(1000L),
            onConfirm = { otp ->
                if (otp.isNotEmpty()) {
                    homeViewModel.confirmTransactionTransfer(otp)
                }
                hideSoftKeyboard()
            },
            resendCode = {
                postTransactions(result?.transactionId)
            },
            onFinishCB = {
                hideSoftKeyboard()
            },
            onClose = {
                // Handle close
            },
            otpDefault = if (BuildConfig.DEBUG)
                result?.otp ?: ""
            else "",
            title = getString(R.string.confirmTransactionOTP)
        )
        showConfirmOtpTransaction()
    }

    override fun initListener() {
        with(binding) {
            tvConfirmation.setOnSingleClickListener {
                backPress()
            }
            tvConfirm.setOnSingleClickListener {
                postTransactions()
            }
        }
    }

    private fun postTransactions(transactionId: Int? = null) {
        homeViewModel.confirmModel?.let { cf ->
            homeViewModel.postTransactionTransfer(
                cf.paymentType,
                cf.fromAccount.accountNumber, cf.toAccount.accountNumber,
                cf.amount.first, cf.amount.second, cf.remarks, transactionId
            )
        }
    }

    private fun hideConfirmOtpTransaction() {
//        binding.flRegisterDevice.gone()
        if (confirmDeviceView.isVisible) {
            confirmDeviceView.hide()
        }
    }

    private fun showConfirmOtpTransaction() {
//        binding.flRegisterDevice.visible()
        if (!confirmDeviceView.isVisible) {
            confirmDeviceView.show()
        }
    }

}
