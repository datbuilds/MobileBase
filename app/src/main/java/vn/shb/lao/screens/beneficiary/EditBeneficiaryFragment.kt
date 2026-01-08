package vn.shb.lao.screens.beneficiary

import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.setFragmentResult
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import vn.shb.core.core.domain.source.request.BeneficiaryRequest
import vn.shb.core.utils.extesions.setOnSingleClickListener
import vn.shb.data.entities.beneficiary.Bank
import vn.shb.data.entities.beneficiary.Beneficiary
import vn.shb.lao.R
import vn.shb.lao.base.BaseFragmentBinding
import vn.shb.lao.databinding.FragmentEditBeneficiaryBinding
import vn.shb.lao.utils.ApiConst
import vn.shb.lao.utils.BankType
import vn.shb.lao.utils.extensions.launchRepeatOnLifecycle
import vn.shb.lao.utils.extensions.setLatinAlphanumericFilter
import vn.shb.lao.utils.extensions.visible

class EditBeneficiaryFragment :
    BaseFragmentBinding<FragmentEditBeneficiaryBinding>(FragmentEditBeneficiaryBinding::inflate) {

    private val viewModel: BeneficiaryViewModel by viewModel()

    companion object {
        const val ADD_NEW = 0
        const val EDIT = 1
    }

    private val isEdit by lazy { arguments?.getInt(ApiConst.KEY_TO_EDIT_BENEFICIARY) == EDIT }
    private val beneficiary by lazy {
        arguments?.getParcelable(ApiConst.KEY_BENEFICIARY_DATA) as? Beneficiary
    }

    private var selectedBank: Bank? = null

    override fun initView(view: View) {
        initTitle()
        setupCommonViews()
        viewModel.getBanks() // Ensure banks are loaded
        if (isEdit) {
            binding.tvEditBeneficiary.text = getString(R.string.editBeneficiary)
            setupEditMode()
        } else {
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
            iclAccountName.edtValue.setLatinAlphanumericFilter(50)
            iclDefaultRemarks.edtValue.setLatinAlphanumericFilter(200)
        }
    }

    private fun setupEditMode() {
        beneficiary?.let { data ->
            with(binding) {
                iclAccountNumber.edtValue.setText(data.accountNumber)
                iclAccountName.edtValue.setText(data.accountName)

                val defaultRemark = if (data.remark.isNullOrEmpty()) {
                    getString(R.string.remark_default_value, data.accountName)
                } else {
                    data.remark
                }
                iclDefaultRemarks.edtValue.setText(defaultRemark)

                iclBank.edtValue.setText(data.bankName ?: data.bankCode)

                // Edit Mode specific UI
                iclBank.edtValue.isEnabled = false
                iclBank.root.alpha = 0.6f
                iclBank.ivExpandDown.visibility = View.GONE

                iclBank.ivLogo.visibility = View.VISIBLE
                iclBank.ivLogo.setImageResource(BankType.getIconByCode(data.bankCode))
            }
        }
    }

    private fun setupAddMode() {
        with(binding) {
            // Add Mode specific UI
            iclBank.edtValue.isEnabled = false // Not editable by text, only click
            iclBank.edtValue.isFocusable = false
            iclBank.edtValue.isClickable = true

            iclBank.root.alpha = 1.0f
            iclBank.ivExpandDown.visibility = View.VISIBLE
            iclBank.ivLogo.visibility = View.GONE

            // Clear fields
            iclAccountNumber.edtValue.setText("")
            iclAccountName.edtValue.setText("")
            iclDefaultRemarks.edtValue.setText("")
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
                            iclBank.edtValue.setText(bank.bankName ?: bank.bankCode)
                            iclBank.ivLogo.visibility = View.VISIBLE
                            iclBank.ivLogo.setImageResource(BankType.getIconByCode(bank.bankCode))

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
                }
            }

            iclBank.edtValue.setOnClickListener { showBankDialog() }
            iclBank.ivExpandDown.setOnClickListener { showBankDialog() }
        }
    }

    override fun initListener() {
        with(binding) {
            tvEditBeneficiary.setOnSingleClickListener {
                backPress()
            }

            // continuous validation
            // Listeners are setup in initTitle -> setupFieldValidation

            // Initial validation state
            validateInputs()

            tvConfirmation.setOnSingleClickListener {
                // ... (keep existing click logic, but now button is only enabled if valid)
                // We can keep the manual checks inside just in case, or rely on isEnabled.
                // Keeping them is safer for logic, but UI prevents click.

                val accountNumber = iclAccountNumber.edtValue.text.toString().trim()
                val accountName = iclAccountName.edtValue.text.toString().trim()
                var remark = iclDefaultRemarks.edtValue.text.toString().trim()

                // ... (rest of the logic)
                if (isEdit) {
                    // Update
                    beneficiary?.let { data ->
                        val request = BeneficiaryRequest(
                            accountNumber = accountNumber,
                            accountName = accountName,
                            remark = remark,
                            bankCode = data.bankCode
                        )
                        viewModel.updateBeneficiary(data.id.toString(), request)
                    }
                } else {
                    // Add New
                    val bankCode =
                        selectedBank?.bankCode // selectedBank might be null if pre-filled manually? No, add mode relies on selection.
                    // Wait, if I type in iclBank? It's disabled. 

                    val request = BeneficiaryRequest(
                        accountNumber = accountNumber,
                        accountName = accountName,
                        remark = remark,
                        bankCode = bankCode
                    )
                    viewModel.createBeneficiary(request)
                }
            }

            ivHome.setOnSingleClickListener {
                popBackTo(R.id.homeFragment)
            }
        }
    }

    private fun validateInputs(shouldCheckError: Boolean = false) {
        with(binding) {
            val accountNumber = iclAccountNumber.edtValue.text.toString().trim()
            val accountName = iclAccountName.edtValue.text.toString().trim()
            val remark = iclDefaultRemarks.edtValue.text.toString().trim()
            val bankName = iclBank.edtValue.text.toString().trim()

            val isValid =
                accountNumber.isNotEmpty() && accountName.isNotEmpty() && remark.isNotEmpty() && bankName.isNotEmpty()

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
                validateField(
                    iclAccountNumber.edtValue.text.toString(),
                    iclAccountNumber.tvError,
                    iclAccountNumber.llEdit,
                    getString(R.string.error_enter_account_number)
                )

                // Account Name Validation
                validateField(
                    iclAccountName.edtValue.text.toString(),
                    iclAccountName.tvError,
                    iclAccountName.llEdit,
                    getString(R.string.error_enter_account_name)
                )

                // Remark Validation
                validateField(
                    iclDefaultRemarks.edtValue.text.toString(),
                    iclDefaultRemarks.tvError,
                    iclDefaultRemarks.llEdit,
                    getString(R.string.error_enter_remark)
                )
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
                includeLayout: vn.shb.lao.databinding.ItemEditBeneficiaryBinding,
                errorMessage: String
            ) {
                includeLayout.edtValue.setOnFocusChangeListener { _, hasFocus ->
                    if (!hasFocus) {
                        validateField(
                            includeLayout.edtValue.text.toString(),
                            includeLayout.tvError,
                            includeLayout.llEdit,
                            errorMessage
                        )
                    }
                }

                includeLayout.edtValue.doAfterTextChanged {
                    validateInputs() // Update button state
                    if (it.toString().isNotEmpty()) {
                        includeLayout.tvError.visibility = View.GONE
                    } else {
                        // Only show error if we are "dirty" ?
                        // Requirement: "show error when input done but text empty (case delete all text)"
                        // So if it BECOMES empty, show error.
                        validateField(
                            includeLayout.edtValue.text.toString(),
                            includeLayout.tvError,
                            includeLayout.llEdit,
                            errorMessage
                        )
                    }
                }
            }

            setupListener(iclAccountNumber, getString(R.string.error_enter_account_number))
            setupListener(iclAccountName, getString(R.string.error_enter_account_name))
            setupListener(iclDefaultRemarks, getString(R.string.error_enter_remark))
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
                            }
                        )
                        viewModel.resetActionState()
                        backPress()
                    }
                }

            }
        }
    }

    private fun initTitle() {

        with(binding) {
            iclAccountNumber.tvTitle.text = getString(R.string.accountNumber)
            iclBank.tvTitle.text = getString(R.string.bank)
            iclAccountName.tvTitle.text = getString(R.string.accountName)
            iclDefaultRemarks.tvTitle.text = getString(R.string.remarks)
            iclBank.ivLogo.visibility = View.VISIBLE

            iclAccountNumber.edtValue.hint = getString(R.string.hint_enter_account_number)
            iclBank.edtValue.hint = getString(R.string.hint_select_bank)
            iclAccountName.edtValue.hint = getString(R.string.hint_enter_account_name)
            iclDefaultRemarks.edtValue.hint = getString(R.string.hint_enter_remarks)

        }
    }

}