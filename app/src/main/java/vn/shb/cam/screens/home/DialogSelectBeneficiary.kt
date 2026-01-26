package vn.shb.cam.screens.home

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import vn.shb.core.utils.extesions.setOnSingleClickListener
import vn.shb.data.entities.beneficiary.Beneficiary
import vn.shb.cam.R
import vn.shb.cam.base.BaseBottomDialogBinding
import vn.shb.cam.databinding.DialogSelectBeneficiaryBinding
import vn.shb.cam.screens.home.helper.SelectBeneficiaryAdapter

class DialogSelectBeneficiary(private val listBeneficiary: List<Beneficiary>) :
    BaseBottomDialogBinding<DialogSelectBeneficiaryBinding>(DialogSelectBeneficiaryBinding::inflate) {

    private var onAction: ((Beneficiary) -> Unit)? = null
    private var filteredList = listBeneficiary
    private var adapter: SelectBeneficiaryAdapter? = null

    companion object {
        const val TAG = "DialogSelectBeneficiary"
    }

    class Build(
        val list: List<Beneficiary>,
        val action: (Beneficiary) -> Unit
    ) {
        fun build() = DialogSelectBeneficiary(list).apply {
            onAction = action
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
            adapter = SelectBeneficiaryAdapter(filteredList) { beneficiary ->
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
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

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
                (ben.accountName?.lowercase(java.util.Locale.getDefault())?.contains(query) == true) ||
                        (ben.accountNumber?.lowercase(java.util.Locale.getDefault())?.contains(query) == true)
            }
        }
        adapter?.updateData(filteredList)
    }

    override fun initObserve() {
    }
}