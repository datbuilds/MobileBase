package com.mobile.base.screens.home

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.mobile.base.R
import com.mobile.base.base.BaseBottomDialogBinding
import com.mobile.base.databinding.DialogSelectBeneficiaryBinding
import com.mobile.base.screens.home.helper.SelectBeneficiaryAdapter
import com.mobile.base.core.utils.extesions.setOnSingleClickListener
import com.mobile.base.data.entities.beneficiary.Beneficiary

class DialogSelectBeneficiary(private val listBeneficiary: List<Beneficiary>) :
    BaseBottomDialogBinding<DialogSelectBeneficiaryBinding>(DialogSelectBeneficiaryBinding::inflate) {

    private var onAction: ((Beneficiary) -> Unit)? = null
    private var filteredList = listBeneficiary
    private var beneficiarySelected: Beneficiary? = null
    private var adapter: SelectBeneficiaryAdapter? = null

    companion object {
        const val TAG = "DialogSelectBeneficiary"
    }

    class Build(
        val list: List<Beneficiary>,
        val beneficiaryS: Beneficiary? = null,
        val action: (Beneficiary) -> Unit
    ) {
        fun build() = DialogSelectBeneficiary(list).apply {
            onAction = action
            beneficiarySelected = beneficiaryS
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomDialog_Rounded)
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog as? BottomSheetDialog ?: return
        val bottomSheet =
            dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        bottomSheet?.let {
            val displayMetrics = context?.resources?.displayMetrics
            val height = displayMetrics?.heightPixels
            val maxHeight = (height?.times(0.7))?.toInt()

            val layoutParams = it.layoutParams
            if (maxHeight != null) {
                layoutParams.height = maxHeight
            }
            it.layoutParams = layoutParams

            val behavior = BottomSheetBehavior.from(it).apply {
                state = BottomSheetBehavior.STATE_EXPANDED
                skipCollapsed = false
                isCancelable = true
            }
            behavior.isDraggable = true
        }
    }

    override fun initView(view: View) {
        filteredList = listBeneficiary
        binding.rcvBeneficiary.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = SelectBeneficiaryAdapter(filteredList, beneficiarySelected) { beneficiary ->
                onAction?.invoke(beneficiary)
                dismiss()
            }
            this@DialogSelectBeneficiary.adapter = adapter as SelectBeneficiaryAdapter
            setHasFixedSize(true)
        }
    }

    override fun initListener() {
        with(binding) {
            ivClose.setOnSingleClickListener {
                dismiss()
            }

            edtSearchBeneficiary.addTextChangedListener(object : android.text.TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: android.text.Editable?) {
                    filter(s.toString())
                }
            })
        }
    }

    private fun filter(text: String) {
        val query = text.lowercase(java.util.Locale.getDefault())
        filteredList = if (query.isEmpty()) {
            listBeneficiary
        } else {
            listBeneficiary.filter { ben ->
                (ben.accountName ?: "").lowercase().contains(query)
                        || (ben.accountNumber ?: "").lowercase().contains(query)
                        || (ben.accountNick ?: "").lowercase().contains(query)
                        || (ben.bankName ?: "").lowercase().contains(query)
            }
        }
        adapter?.updateData(filteredList)
    }

    override fun initObserve() {
    }
}