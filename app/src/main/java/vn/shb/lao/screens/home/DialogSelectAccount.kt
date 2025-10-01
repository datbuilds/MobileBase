package vn.shb.lao.screens.home

import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import vn.shb.core.utils.extesions.setOnSingleClickListener
import vn.shb.lao.R
import vn.shb.lao.base.BaseBottomDialogBinding
import vn.shb.lao.databinding.DialogChoosePictureBinding
import vn.shb.lao.databinding.DialogSelectAccountBinding
import vn.shb.lao.screens.home.helper.SelectAccountAdapter
import vn.shb.lao.screens.home.model.AccountItem
import vn.shb.lao.utils.extensions.launchRepeatOnLifecycle

class DialogSelectAccount(private val listAccount: List<AccountItem>) :
    BaseBottomDialogBinding<DialogSelectAccountBinding>(DialogSelectAccountBinding::inflate) {

    private var onAction: ((AccountItem) -> Unit)? = null

    companion object {
        const val TAG = "DialogSelectAccount"
    }

    class Build(val list: List<AccountItem>, val action: (AccountItem) -> Unit) {
        fun build() = DialogSelectAccount(list).apply {
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
            val behavior = BottomSheetBehavior.from(it).apply {
                state = BottomSheetBehavior.STATE_EXPANDED
                skipCollapsed = true
                isCancelable = false
            }
            behavior.isDraggable = false
        }
    }

    override fun initView(view: View) {
        binding.rcvAccount.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = SelectAccountAdapter(listAccount) { account ->
                lifecycleScope.launch {
                    onAction?.invoke(account)
                    delay(300)
                    dismiss()
                }

            }
            setHasFixedSize(true)
        }
    }

    override fun initListener() {
        with(binding) {

            ivClose.setOnSingleClickListener {
                dismiss()
            }
        }
    }

    override fun initObserve() {
        launchRepeatOnLifecycle {
            launch {
            }
        }
    }
}