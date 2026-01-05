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
        if (isEdit) {
            beneficiary?.let { data ->
                with(binding) {
                    iclAccountNumber.edtValue.setText(data.accountNumber)
                    iclAccountName.edtValue.setText(data.accountName)
                    iclDefaultRemarks.edtValue.setText(data.defaultTransactionDescription)
                    iclBank.edtValue.setText(data.bankName ?: data.bankCode)
                }
            }
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
            iclDefaultRemarks.tvTitle.text = getString(R.string.defaultRemarks)
        }
    }

}