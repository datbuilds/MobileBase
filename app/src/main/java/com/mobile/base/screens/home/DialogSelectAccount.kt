package com.mobile.base.screens.home

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.mobile.base.core.utils.extesions.setOnSingleClickListener
import com.mobile.base.data.entities.AccountBase
import com.mobile.base.data.entities.home.AccountInfo
import com.mobile.base.R
import com.mobile.base.base.BaseBottomDialogBinding
import com.mobile.base.databinding.DialogSelectAccountBinding
import com.mobile.base.screens.home.helper.SelectAccountAdapter
import com.mobile.base.utils.extensions.common.Const
import com.mobile.base.utils.extensions.launchRepeatOnLifecycle

class DialogSelectAccount(private val listAccount: List<AccountBase>) :
    BaseBottomDialogBinding<DialogSelectAccountBinding>(DialogSelectAccountBinding::inflate) {

    private var onAction: ((AccountBase) -> Unit)? = null

    private var sctAccount: AccountBase? = null

    private var canSelect = true

    companion object {
        const val TAG = "DialogSelectAccount"
    }

    class Build(
        val list: List<AccountBase>,
        val selectedAccount: AccountBase? = null,
        val isCanSelect : Boolean = true,
        val action: (AccountBase) -> Unit
    ) {
        fun build() = DialogSelectAccount(list).apply {
            sctAccount = selectedAccount
            onAction = action
            canSelect = isCanSelect
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
        binding.tvDone.isVisible = canSelect
        if (!canSelect) binding.tvSelectAccount.text = getString(R.string.listAccount)
        binding.rcvAccount.apply {
            layoutManager = LinearLayoutManager(context)
            listAccount.forEach {
                it.setSelected(it.accountNumber == sctAccount?.accountNumber)
            }
            adapter = SelectAccountAdapter(listAccount, canSelect) { account ->
                sctAccount = account
                val statusDone =
                    if (sctAccount is AccountInfo) sctAccount?.accountType == Const.CURRENT_ACCOUNT else true
                binding.tvDone.isVisible = statusDone && canSelect
            }
            setHasFixedSize(true)
        }
    }

    override fun initListener() {
        with(binding) {
            binding.tvDone.setOnSingleClickListener {
                lifecycleScope.launch {
                    sctAccount?.let { ac -> onAction?.invoke(ac) }
                    delay(300)
                    dismiss()
                }
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