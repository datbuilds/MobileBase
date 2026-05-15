package vn.shb.cam.screens.beneficiary

import android.os.Build
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.setFragmentResult
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import vn.shb.cam.R
import vn.shb.cam.base.BaseFragmentBinding
import vn.shb.cam.base.view.FontManager
import vn.shb.cam.databinding.FragmentEditBeneficiaryBinding
import vn.shb.cam.navigation.AppDestination
import vn.shb.cam.utils.ApiConst
import vn.shb.cam.utils.BankType
import vn.shb.cam.utils.extensions.CustomToastShowOnTop
import vn.shb.cam.utils.extensions.checkShowProgressDialog
import vn.shb.cam.utils.extensions.hideProgressDialog
import vn.shb.cam.utils.extensions.launchRepeatOnLifecycle
import vn.shb.cam.utils.extensions.setLatinAlphanumericFilter
import vn.shb.cam.utils.extensions.visible
import vn.shb.cam.utils.view.dialog.BottomSheetDialogHelper
import vn.shb.core.core.delivery.ResultSHB
import vn.shb.core.core.domain.source.request.BeneficiaryRequest
import vn.shb.core.utils.extesions.setOnSingleClickListener
import vn.shb.data.entities.beneficiary.Bank
import vn.shb.data.entities.beneficiary.Beneficiary

class EditBeneficiaryFragment :
    BaseFragmentBinding<FragmentEditBeneficiaryBinding>(FragmentEditBeneficiaryBinding::inflate) {

    private val MAX_LENGHT_INPUT_NAME = 20
    private val MAX_LENGHT_INPUT_REMARK = 200
    private val TIME_SHOW_SNACK_BAR = 3000L

    private val viewModel: BeneficiaryViewModel by viewModel()

    companion object {
        const val ADD_NEW = 0
        const val EDIT = 1
    }

    private val isEdit by lazy { arguments?.getInt(ApiConst.KEY_TO_EDIT_BENEFICIARY) == EDIT }
    private val beneficiary by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable(ApiConst.KEY_BENEFICIARY_DATA, Beneficiary::class.java)
        } else {
            arguments?.getParcelable(ApiConst.KEY_BENEFICIARY_DATA)
        }
    }

    private val listBeneficiary by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelableArrayList(
                ApiConst.KEY_LIST_BENEFICIARY_DATA,
                Beneficiary::class.java
            )
        } else {
            arguments?.getParcelableArrayList(ApiConst.KEY_LIST_BENEFICIARY_DATA)
        }
    }

    private var selectedBank: Bank? = null
    private var isAccountValidated = true

    override fun initView(view: View) {
        initTitle()
        setupCommonViews()
        viewModel.getBanks() // Ensure banks are loaded
        if (isEdit) {
            binding.tvConfirmation.text = getString(R.string.confirm)
            binding.tvEditBeneficiary.text = getString(R.string.editBeneficiary)
            setupEditMode()
        } else {
            binding.tvConfirmation.text = getString(R.string.addNew)
            binding.tvEditBeneficiary.text = getString(R.string.addNewBeneficiary)
            setupAddMode()
        }
        setupFieldValidation()
    }

    private fun setupCommonViews() {
        with(binding) {
            // Account Number (Ensured Editable)
            iclAccountNumber.edtValue.isEnabled = true
            iclAccountNumber.edtValue.inputType = android.text.InputType.TYPE_CLASS_NUMBER

            // Validation Filters
            iclNickName.edtValue.setLatinAlphanumericFilter(MAX_LENGHT_INPUT_NAME)
            iclDefaultRemarks.edtValue.setLatinAlphanumericFilter(MAX_LENGHT_INPUT_REMARK)
        }
    }

    private fun setupEditMode() {
        beneficiary?.let { data ->
            with(binding) {


//            DataTest
//            val accountNumber="1001751687"
//            val accountNumber="1001751690"
                iclNickName.edtValue.setText(data.accountNick)
                iclDefaultRemarks.edtValue.setText(data.remark)

                // Edit Mode specific UI
                FontManager.semi_bold?.let { iclBank.edtValue.setTypeFaceFont(it) }
                iclBank.edtValue.setTextAndDisableFocus(data.accountNumber)
                iclBank.ivExpandDown.visibility = View.GONE
                iclBank.ivLogo.visibility = View.VISIBLE
                iclBank.ivLogo.setImageResource(BankType.getIconByCode(data.bankCode))

                // Disable Account number editing
                iclAccountNumber.edtValue.setTextAndDisableFocus(data.accountNumber)

                // Disable Account name editing
                iclAccountName.edtValue.setTextAndDisableFocus(data.accountName)
            }
        }
    }

    private fun setupAddMode() {
        isAccountValidated = false
        with(binding) {
            FontManager.medium?.let { iclBank.edtValue.setTypeFaceFont(it) }

            iclBank.edtValue.isFocusable = false
            iclBank.edtValue.isFocusableInTouchMode = false
            iclBank.edtValue.isClickable = true
            iclBank.edtValue.isCursorVisible = false


            iclBank.root.alpha = 1.0f
            iclBank.ivExpandDown.visibility = View.VISIBLE
            iclBank.ivLogo.visibility = View.GONE

            iclAccountName.edtValue.setText("")
            iclAccountName.edtValue.isEnabled = false
            iclAccountName.edtValue.setTextColor(context?.getColor(R.color.neutral6) ?: 0)
            iclAccountName.edtValue.clearFocus()

            iclAccountNumber.edtValue.setText("")
            iclDefaultRemarks.edtValue.setText("")
            iclNickName.edtValue.setText("")
            iclBank.edtValue.setText("")

            // Click Listener for Bank Selection
            val showBankDialog = {
                val banks = viewModel.stateBanks.value
                if (banks.isNotEmpty()) {
                    DialogSelectBank.Build(
                        list = banks,
                        currentBank = selectedBank,
                        action = { bank ->
                            selectedBank = bank
                            iclBank.edtValue.setText(bank.bankCode?:bank.shortName)
                            iclBank.ivLogo.visibility = View.VISIBLE
                            iclBank.ivLogo.setImageResource(BankType.getIconByCode(bank.bankCode))
                            if (iclAccountNumber.edtValue.text.toString().isNotEmpty()) {
                                viewModel.validateAccount(
                                    iclAccountNumber.edtValue.text.toString(),
                                    bank.bankCode ?: "SHB"
                                )
                            }
                            // Hide error if selected
                            iclBank.tvError.visibility = View.GONE
                            validateInputs()
                        },
                        actionDismiss = {
                            if (selectedBank == null) {
                                iclBank.tvError.visible()
                                iclBank.tvError.text = getString(R.string.error_select_bank)
                            }
                        }
                    ).build().show(parentFragmentManager, DialogSelectBank.TAG)
                } else {
                    viewModel.getBanks()
                }
            }

            iclBank.edtValue.setOnClickListener { showBankDialog() }
            iclBank.ivExpandDown.setOnClickListener { showBankDialog() }
        }
    }

    override fun initListener() {
        with(binding) {
            btnBack.setOnSingleClickListener {
                backPress()
            }

            // continuous validation
            // Listeners are setup in initTitle -> setupFieldValidation

            // Initial validation state
            validateInputs()

            tvConfirmation.setOnSingleClickListener {
                // check nickname exist
                val accountNick = iclNickName.edtValue.text.toString().trim()
                val accountNumber = iclAccountNumber.edtValue.text.toString().trim()

                val isExist =
                    listBeneficiary?.find { it.accountNick == accountNick && it.accountNumber != accountNumber }

                if (isExist == null) {
                    // case not exist
                    doAddOrUpdate()
                    return@setOnSingleClickListener
                }

                BottomSheetDialogHelper(requireContext()).message(
                    title = getString(R.string.confirmation),
                    message = getString(
                        R.string.nicknameAlreadyExists
                    ),
                    textNegative = getString(R.string.dontAllowRemoveBeneficiary),
                    textPositive = getString(R.string.allowRemoveBeneficiary),
                    positiveAction = {
                        doAddOrUpdate()
                    }
                )
            }

            ivHome.setOnSingleClickListener {
                popBackTo(AppDestination.HomeArg())
            }
        }
    }

    private fun doAddOrUpdate() = with(binding) {
        val accountNumber = iclAccountNumber.edtValue.text.toString().trim()
        val accountName = iclAccountName.edtValue.text.toString().trim()
        val remark = iclDefaultRemarks.edtValue.text.toString().trim()
        val accountNick = iclNickName.edtValue.text.toString().trim()

        val request = BeneficiaryRequest(
            accountNumber = accountNumber,
            accountName = accountName,
            remark = remark.trim().ifEmpty { return@ifEmpty null },
            accountNick = accountNick.trim().ifEmpty { return@ifEmpty null }
        )

        if (isEdit) {
            // Update person
            beneficiary?.let { data ->
                request.bankCode = data.bankCode
                viewModel.updateBeneficiary(
                    request,
                    onLoading = {
                        checkShowProgressDialog()
                        tvConfirmation.isEnabled = false
                    },
                    onFinish = {
                        hideProgressDialog()
                        tvConfirmation.isEnabled = true
                    })
            }
        } else {
            // Add New person
            request.bankCode = selectedBank?.bankCode

            viewModel.createBeneficiary(
                request,
                onLoading = {
                    checkShowProgressDialog()
                    tvConfirmation.isEnabled = false
                },
                onFinish = {
                    hideProgressDialog()
                    tvConfirmation.isEnabled = true
                })
        }
    }

    private fun validateInputs(shouldCheckError: Boolean = false) {
        with(binding) {
            val accountNumber = iclAccountNumber.edtValue.text.toString().trim()
            val accountName = iclAccountName.edtValue.text.toString().trim()
            val bankName = iclBank.edtValue.text.toString().trim()

            val isValid =
                accountNumber.isNotEmpty()
                        && accountName.isNotEmpty()
                        && bankName.isNotEmpty()
                        && isAccountValidated

            tvConfirmation.isEnabled = isValid
            tvConfirmation.alpha = if (isValid) 1.0f else 0.5f

            if (shouldCheckError) {
                // Bank Validation
                if (bankName.isEmpty()) {
                    iclBank.tvError.visible()
                    iclBank.tvError.text = getString(R.string.error_select_bank)
                } else {
                    iclBank.tvError.visibility = View.GONE
                }

                // Account Number Validation
                if (iclAccountNumber.edtValue.text.toString().isEmpty()) {
                    iclAccountNumber.tvError.visible()
                    iclAccountNumber.tvError.text = getString(R.string.pleaseEnterTheAccountNumber)

                    //clear data accountName
                    iclAccountName.edtValue.setText("")
                    iclAccountName.edtValue.isEnabled = false
                    iclAccountName.edtValue.setTextColor(context?.getColor(R.color.neutral6) ?: 0)
                    iclAccountName.edtValue.clearFocus()
                } else {
                    iclAccountNumber.tvError.visibility = View.GONE
                }

//                validateField(
//                    iclAccountNumber.edtValue.text.toString(),
//                    iclAccountNumber.tvError,
//                    iclAccountNumber.llEdit,
//                    getString(R.string.pleaseEnterTheAccountNumber)
//                )

                // Account Name Validation
//                validateField(
//                    iclAccountName.edtValue.text.toString(),
//                    iclAccountName.tvError,
//                    iclAccountName.llEdit,
//                    getString(R.string.error_enter_account_name)
//                )

            }
        }
    }

    private fun validateField(
        text: String,
        tvError: android.widget.TextView,
        bgView: View,
        errorMessage: String
    ) {
        if (text.isEmpty()) {
            tvError.visible()
            tvError.text = errorMessage
        } else {
            tvError.visibility = View.GONE
        }
    }

    private fun setupFieldValidation() {
        with(binding) {
            // Helper function to setup listeners
            fun setupListener(
                includeLayout: vn.shb.cam.databinding.ItemEditBeneficiaryBinding,
                errorMessage: String,
                callBackError: () -> Unit = {}
            ) {
                includeLayout.edtValue.setOnEditorActionListener { v, actionId, event ->
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        includeLayout.edtValue.clearFocus()
                    }
                    false
                }

                includeLayout.edtValue.setOnFocusChangeListener { _, hasFocus ->
                    if (!hasFocus) {
                        validateField(
                            includeLayout.edtValue.text.toString(),
                            includeLayout.tvError,
                            includeLayout.llEdit,
                            errorMessage
                        )

                        if (includeLayout == iclAccountNumber && !isEdit && includeLayout.edtValue.text?.isNotEmpty() == true) {
                            val bankCode = selectedBank?.bankCode
                            if (!bankCode.isNullOrEmpty()) {
                                viewModel.validateAccount(
                                    includeLayout.edtValue.text.toString(),
                                    bankCode
                                )
                            }
                        }
                    }
                }


            }

            iclNickName.edtValue.doAfterTextChanged {
                validateInputs()
            }

            iclAccountNumber.edtValue.doAfterTextChanged {
                if (!isEdit) {
                    isAccountValidated = false
                }
                validateInputs() // Update button state
                if (it.toString().isNotEmpty()) {
                    iclAccountNumber.tvError.visibility = View.GONE
                } else {
                    // Only show error if we are "dirty" ?
                    // Requirement: "show error when input done but text empty (case delete all text)"
                    // So if it BECOMES empty, show error.
//                    validateField(
//                        iclAccountNumber.edtValue.text.toString(),
//                        iclAccountNumber.tvError,
//                        iclAccountNumber.llEdit,
//                        getString(R.string.pleaseEnterTheAccountNumber)
//                    )

                    if (iclAccountNumber.edtValue.text.toString().isEmpty()) {
                        iclAccountNumber.tvError.visible()
                        iclAccountNumber.tvError.text =
                            getString(R.string.pleaseEnterTheAccountNumber)

                        //clear data accountName
                        iclAccountName.edtValue.setText("")
                        iclAccountName.edtValue.isEnabled = false
                        iclAccountName.edtValue.setTextColor(
                            context?.getColor(R.color.neutral6) ?: 0
                        )
                        iclAccountName.edtValue.clearFocus()
                    } else {
                        iclAccountNumber.tvError.visibility = View.GONE
                    }
                }
            }

            setupListener(iclAccountNumber, getString(R.string.pleaseEnterTheAccountNumber))
//            setupListener(iclAccountName, getString(R.string.error_enter_account_name))
        }
    }

    override fun initObserve() {
        launchRepeatOnLifecycle {
            launch {
                viewModel.stateAction.collect { success ->
                    if (success == true) {
                        val message =
                            if (isEdit) getString(R.string.beneficiaryUpdatedSuccessfully) else getString(
                                R.string.newBeneficiaryAddedSuccessfully
                            )
                        setFragmentResult(
                            ApiConst.KEY_RESULT_BENEFICIARY,
                            android.os.Bundle().apply {
                                putString(ApiConst.KEY_MESSAGE, message)
                                putBoolean(ApiConst.KEY_CONFIRM_ERROR, false)
                            }
                        )
                        backPress()
                    }
                }
            }

            launch {
                viewModel.stateError.collect {
                    showSnackBarTop(it.errMessage, false)
                    binding.tvConfirmation.isEnabled = false
                    binding.tvConfirmation.alpha = 0.5f
                }
            }

            launch {
                viewModel.stateBanks.collect { banks ->
                    if (isEdit && banks.isNotEmpty()) {
                        beneficiary?.let { data ->
                            val bankOfBeneficiary = banks.find { it.bankCode == data.bankCode }
                            binding.iclBank.edtValue.setText(
                                bankOfBeneficiary?.bankCode?:bankOfBeneficiary?.bankName
                            )
                        }
                    }
                }
            }

            launch {
                viewModel.stateValidateAccount.collect { data ->
                    when (data) {
                        is ResultSHB.Failure -> {
                            isAccountValidated = false
                            validateInputs()
                            binding.iclAccountNumber.tvError.text =
                                getString(R.string.invalidAccountNumber)
                            binding.iclAccountNumber.tvError.visible()

                            binding.iclAccountName.edtValue.setText("")
                            binding.iclAccountName.edtValue.isEnabled = false
                            binding.iclAccountName.edtValue.setTextColor(
                                context?.getColor(R.color.neutral6) ?: 0
                            )
                            binding.iclAccountName.edtValue.clearFocus()
                        }

                        ResultSHB.Loading -> {
                            //do nothing
                        }

                        is ResultSHB.Success -> {
                            binding.iclAccountName.edtValue.setTextAndDisableFocus(data.successData.accountName)

                            isAccountValidated = true
                            validateInputs()
                            val nameUser = getCurrentUser()?.username
                            binding.iclDefaultRemarks.edtValue.setText(
                                getString(
                                    R.string.remark_default_value,
                                    nameUser
                                )
                            )
                        }
                    }

                }
            }
        }
    }

    fun showSnackBarTop(text: String, isSuccess: Boolean = true) {
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
                duration = TIME_SHOW_SNACK_BAR,
                textColor = R.color.neutral1,
                iconClose = R.color.neutral1
            )
            toast.show()
        }

    }

    private fun initTitle() {
        with(binding) {
            iclAccountNumber.tvTitle.text = getString(R.string.accountNumber)
            iclBank.tvTitle.text = getString(R.string.bank)
            iclNickName.tvTitle.text = getString(R.string.titleNickname)
            iclAccountName.tvTitle.text = getString(R.string.accountName)
            iclDefaultRemarks.tvTitle.text = getString(R.string.remarksDefault)
            iclBank.ivLogo.visibility = View.VISIBLE

            iclAccountNumber.edtValue.hint = getString(R.string.enterAccountNumber)
            iclBank.edtValue.hint = getString(R.string.hint_select_bank)
            iclAccountName.edtValue.hint = getString(R.string.accountName)
            iclNickName.edtValue.hint = getString(R.string.enter_nickname)
            iclDefaultRemarks.edtValue.hint = getString(R.string.enterRemarksDefault)

        }
    }

}
