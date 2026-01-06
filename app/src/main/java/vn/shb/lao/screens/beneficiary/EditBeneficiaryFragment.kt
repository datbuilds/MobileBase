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

class EditBeneficiaryFragment : BaseFragmentBinding<FragmentEditBeneficiaryBinding>(FragmentEditBeneficiaryBinding::inflate) {

    companion object {
        const val ADD_NEW = 0
        const val EDIT = 1
    }

    private val isEdit by lazy { arguments?.getInt(ApiConst.KEY_TO_EDIT_BENEFICIARY) == EDIT }
    private val beneficiary by lazy {
        arguments?.getParcelable(ApiConst.KEY_BENEFICIARY_DATA) as? Beneficiary
    }

    override fun initView(view: View) {
        initTitle()
        setupCommonViews()
        if (isEdit) {
            setupEditMode()
        } else {
            setupAddMode()
        }
    }

    private fun setupCommonViews() {
        with(binding) {
            // Account Number (Ensured Editable)
            iclAccountNumber.edtValue.isEnabled = true

            // Validation Filters
            val latinNumberFilter = android.text.InputFilter { source, start, end, dest, dstart, dend ->
                for (i in start until end) {
                    if (!source[i].toString().matches(Regex("[a-zA-Z0-9 ]"))) {
                        return@InputFilter ""
                    }
                }
                null
            }

            // Configure Account Name
            iclAccountName.edtValue.filters = arrayOf(
                android.text.InputFilter.LengthFilter(50),
                latinNumberFilter
            )

            // Configure Remarks
            iclDefaultRemarks.edtValue.filters = arrayOf(
                android.text.InputFilter.LengthFilter(200),
                latinNumberFilter
            )
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
            iclBank.edtValue.isEnabled = true
            iclBank.root.alpha = 1.0f
            iclBank.ivExpandDown.visibility = View.VISIBLE
            iclBank.ivLogo.visibility = View.GONE
            
            // Clear fields (optional if fragment is fresh, but good for safety)
            iclAccountNumber.edtValue.setText("")
            iclAccountName.edtValue.setText("")
            iclDefaultRemarks.edtValue.setText("")
            iclBank.edtValue.setText("")
        }
    }

    override fun initListener() {
    }

    override fun initObserve() {
        with(binding){
            tvEditBeneficiary.setOnSingleClickListener {
                backPress()
            }

            tvConfirmation.setOnSingleClickListener {

            }

            ivHome.setOnSingleClickListener {
                popBackTo(R.id.homeFragment)
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