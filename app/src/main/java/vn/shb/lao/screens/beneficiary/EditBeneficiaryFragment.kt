package vn.shb.lao.screens.beneficiary

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import vn.shb.core.utils.extesions.setOnSingleClickListener
import vn.shb.data.entities.beneficiary.Beneficiary
import vn.shb.lao.R
import vn.shb.lao.base.BaseFragmentBinding
import vn.shb.lao.databinding.FragmentEditBeneficiaryBinding
import vn.shb.lao.utils.ApiConst
import vn.shb.lao.utils.BankType

import org.koin.androidx.viewmodel.ext.android.viewModel
import vn.shb.data.entities.beneficiary.Bank
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.setFragmentResult
import vn.shb.lao.utils.extensions.setLatinAlphanumericFilter

class EditBeneficiaryFragment : BaseFragmentBinding<FragmentEditBeneficiaryBinding>(FragmentEditBeneficiaryBinding::inflate) {

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
                            
                            // Keep expand down visible as per user flow (can re-select), 
                            // or hide it? 
                            // User request: "Edit Mode: Hide ivExpandDown...". 
                            // Add Mode: "Click ivExpandDown...". 
                            // If selected, it might look like Add Mode still but with data.
                            // I'll keep ivExpandDown visible for re-selection.
                        }
                    ).build().show(parentFragmentManager, DialogSelectBank.TAG)
                }
            }

            iclBank.edtValue.setOnClickListener { showBankDialog() }
            iclBank.ivExpandDown.setOnClickListener { showBankDialog() }
            // If iclBank is an include, ensure clicks are propagated or set on root?
            // Since MyEditText might consume click if not carefully handled (isFocusable=false helps).
        }
    }

    override fun initListener() {
        with(binding) {
            tvEditBeneficiary.setOnSingleClickListener {
                backPress()
            }

            // continuous validation
            // continuous validation
            iclAccountNumber.edtValue.doAfterTextChanged { validateInputs() }
            iclAccountName.edtValue.doAfterTextChanged { validateInputs() }
            iclDefaultRemarks.edtValue.doAfterTextChanged { validateInputs() }
            iclBank.edtValue.doAfterTextChanged { validateInputs() }

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
                         val request = vn.shb.core.core.domain.source.request.BeneficiaryRequest(
                             accountNumber = accountNumber,
                             accountName = accountName,
                             remark = remark,
                             bankCode = data.bankCode
                         )
                         viewModel.updateBeneficiary(data.id.toString(), request)
                     }
                } else {
                    // Add New
                    val bankCode = selectedBank?.bankCode // selectedBank might be null if pre-filled manually? No, add mode relies on selection.
                    // Wait, if I type in iclBank? It's disabled. 
                    
                    val request = vn.shb.core.core.domain.source.request.BeneficiaryRequest(
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
    
    private fun validateInputs() {
        with(binding) {
            val accountNumber = iclAccountNumber.edtValue.text.toString().trim()
            val accountName = iclAccountName.edtValue.text.toString().trim()
            val remark = iclDefaultRemarks.edtValue.text.toString().trim()
            val bankName = iclBank.edtValue.text.toString().trim()

            val isValid = accountNumber.isNotEmpty() && accountName.isNotEmpty() && remark.isNotEmpty() && bankName.isNotEmpty()
            
            tvConfirmation.isEnabled = isValid
            tvConfirmation.alpha = if (isValid) 1.0f else 0.5f
        }
    }
    
    override fun initObserve() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.stateAction.collect { success ->
                    if (success == true) {
                        val message = if (isEdit) getString(R.string.beneficiaryUpdatedSuccessfully) else getString(R.string.newBeneficiaryAddedSuccessfully)
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

        with(binding){
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