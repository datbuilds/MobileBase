package vn.shb.lao.screens.home

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
import vn.shb.data.entities.AccountBase
import vn.shb.data.entities.home.AccountInfo
import vn.shb.lao.R
import vn.shb.lao.base.BaseBottomDialogBinding
import vn.shb.lao.databinding.DialogSelectAccountBinding
import vn.shb.lao.databinding.DialogSelectBeneficiaryBinding
import vn.shb.lao.screens.home.helper.SelectAccountAdapter
import vn.shb.lao.utils.extensions.common.Const
import vn.shb.lao.utils.extensions.launchRepeatOnLifecycle

class DialogSelectBeneficiary(private val listAccount: List<AccountBase>) :
    BaseBottomDialogBinding<DialogSelectBeneficiaryBinding>(DialogSelectBeneficiaryBinding::inflate) {

    private var onAction: ((AccountBase) -> Unit)? = null

    private var selectAccount: AccountBase? = null

    companion object {
        const val TAG = "DialogSelectAccount"
    }

    class Build(
        val list: List<AccountBase>,
        val selectedAccount: AccountBase? = null,
        val action: (AccountBase) -> Unit
    ) {
        fun build() = DialogSelectBeneficiary(list).apply {
            selectAccount = selectedAccount
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
                skipCollapsed = false
                isCancelable = true
            }
            behavior.isDraggable = true
        }
    }

    override fun initView(view: View) {
        binding.rcvBeneficiary.apply {
            layoutManager = LinearLayoutManager(context)
            listAccount.forEach {
                it.setSelected(it.accountNumber == selectAccount?.accountNumber)
            }
            adapter = SelectAccountAdapter(listAccount) { account ->
                selectAccount = account
                val statusDone =
                    if (selectAccount is AccountInfo) selectAccount?.accountType == Const.CURRENT_ACCOUNT else true
                binding.tvDone.isVisible = statusDone
            }
            setHasFixedSize(true)
        }
    }

    override fun initListener() {
        with(binding) {
            tvDone.setOnSingleClickListener {
                lifecycleScope.launch {
                    selectAccount?.let { ac -> onAction?.invoke(ac) }
                    delay(300)
                    dismiss()
                }
            }

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