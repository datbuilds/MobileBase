package vn.shb.lao.screens.beneficiary

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import vn.shb.core.utils.extesions.setOnSingleClickListener
import vn.shb.lao.R
import vn.shb.lao.base.BaseFragmentBinding
import vn.shb.lao.databinding.FragmentEditBeneficiaryBinding

class EditBeneficiaryFragment : BaseFragmentBinding<FragmentEditBeneficiaryBinding>(FragmentEditBeneficiaryBinding::inflate) {
    override fun initView(view: View) {
        initTitle()
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