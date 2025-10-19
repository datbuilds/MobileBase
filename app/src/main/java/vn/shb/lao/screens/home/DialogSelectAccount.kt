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
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import vn.shb.core.utils.extesions.setOnSingleClickListener
import vn.shb.data.entities.home.AccountInfo
import vn.shb.lao.R
import vn.shb.lao.base.BaseBottomDialogBinding
import vn.shb.lao.databinding.DialogSelectAccountBinding
import vn.shb.lao.screens.home.helper.SelectAccountAdapter
import vn.shb.lao.utils.extensions.common.Const
import vn.shb.lao.utils.extensions.launchRepeatOnLifecycle

class DialogSelectAccount(private val listAccount: List<AccountInfo>) :
    BaseBottomDialogBinding<DialogSelectAccountBinding>(DialogSelectAccountBinding::inflate) {

    private val homeViewModel: HomeViewModel by sharedViewModel()
    private var onAction: ((AccountInfo) -> Unit)? = null

    private var selectAccount: AccountInfo? = null

    companion object {
        const val TAG = "DialogSelectAccount"
    }

    class Build(
        val list: List<AccountInfo>,
        val selectedAccount: AccountInfo? = null,
        val action: (AccountInfo) -> Unit
    ) {
        fun build() = DialogSelectAccount(list).apply {
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
                skipCollapsed = true
                isCancelable = false
            }
            behavior.isDraggable = false
        }
    }

    override fun initView(view: View) {
        binding.rcvAccount.apply {
            layoutManager = LinearLayoutManager(context)
            listAccount.forEach {
                it.isSelected = it.accountNumber == selectAccount?.accountNumber
            }
            adapter = SelectAccountAdapter(listAccount) { account ->
                selectAccount = account
                binding.tvDone.isVisible = selectAccount?.accountType != Const.LOAN_ACCOUNT
            }
            setHasFixedSize(true)
        }
    }

    override fun initListener() {
        with(binding) {
            binding.tvDone.setOnSingleClickListener {
                lifecycleScope.launch {
                    selectAccount?.let { ac -> onAction?.invoke(ac) }
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