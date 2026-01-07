package vn.shb.lao.screens.beneficiary

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import vn.shb.core.utils.extesions.setOnSingleClickListener
import vn.shb.data.entities.beneficiary.Bank
import vn.shb.lao.R
import vn.shb.lao.base.BaseBottomDialogBinding
import vn.shb.lao.databinding.DialogSelectBankBinding
import vn.shb.lao.screens.beneficiary.helper.SelectBankAdapter
import java.util.Locale

class DialogSelectBank(private val listBank: List<Bank>) :
    BaseBottomDialogBinding<DialogSelectBankBinding>(DialogSelectBankBinding::inflate) {

    private var onAction: ((Bank) -> Unit)? = null
    private var selectedBank: Bank? = null
    private var adapter: SelectBankAdapter? = null
    private var filteredList = listBank

    companion object {
        const val TAG = "DialogSelectBank"
    }

    class Build(
        val list: List<Bank>,
        val currentBank: Bank? = null,
        val action: (Bank) -> Unit
    ) {
        fun build() = DialogSelectBank(list).apply {
            selectedBank = currentBank
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
        filteredList = listBank // Init filtered list
        binding.rcvBanks.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = SelectBankAdapter(filteredList, selectedBank?.bankCode) { bank ->
                onAction?.invoke(bank)
                dismiss()
            }
            this@DialogSelectBank.adapter = adapter as SelectBankAdapter
            setHasFixedSize(true)
        }
    }

    override fun initListener() {
        with(binding) {
            ivClose.setOnSingleClickListener {
                dismiss()
            }

            edtSearch.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    filter(s.toString())
                }
            })
        }
    }

    private fun filter(text: String) {
        val query = text.lowercase(Locale.getDefault())
        filteredList = if (query.isEmpty()) {
            listBank
        } else {
            listBank.filter { bank ->
                (bank.bankCode?.lowercase(Locale.getDefault())?.contains(query) == true) ||
                        (bank.bankName?.lowercase(Locale.getDefault())?.contains(query) == true) ||
                        (bank.shortName?.lowercase(Locale.getDefault())?.contains(query) == true)
            }
        }
        adapter?.updateData(filteredList)
    }

    override fun initObserve() {
    }
}
