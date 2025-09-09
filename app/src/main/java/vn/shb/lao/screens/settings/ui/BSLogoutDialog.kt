package vn.shb.lao.screens.settings.ui

import android.os.Bundle
import android.view.View
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import vn.shb.core.core.domain.usecases.login.UseCaseLogout
import vn.shb.core.utils.extesions.setOnSingleClickListener
import vn.shb.lao.R
import vn.shb.lao.base.BaseBottomDialogBinding
import vn.shb.lao.databinding.BsLogoutBinding
import vn.shb.lao.screens.login.state.LogoutUiState
import vn.shb.lao.screens.login.ui.LoginViewModel
import vn.shb.lao.utils.extensions.hideProgressDialog
import vn.shb.lao.utils.extensions.launchRepeatOnLifecycle
import vn.shb.lao.utils.extensions.showProgressDialog

class BSLogoutDialog(private val build: Build) :
    BaseBottomDialogBinding<BsLogoutBinding>(BsLogoutBinding::inflate) {

    companion object {
        const val TAG = "BSLogoutDialog"
    }

    class Build(val logoutSuccess: () -> Unit) {
        fun build() = BSLogoutDialog(this)
    }

    private val viewModel: LoginViewModel by inject()
    private var isAll = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, vn.shb.lao.ui.R.style.BottomDialog_Rounded)
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog as? BottomSheetDialog ?: return
        val bottomSheet = dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
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

    }

    override fun initListener() {
        with(binding) {
            btNo.setOnSingleClickListener {
                dismissAllowingStateLoss()
            }

            btLogout.setOnSingleClickListener {
                logout()
            }

            cbLogoutAll.setOnCheckedChangeListener { _, isChecked ->
                isAll = isChecked
                cbLogoutAll.buttonTintList = if (isChecked) {
                    resources.getColorStateList(vn.shb.lao.ui.R.color.accent, null)
                } else {
                    resources.getColorStateList(vn.shb.lao.ui.R.color.gray_600, null)
                }
            }
        }
    }

    private fun logout() {
        val params = UseCaseLogout.Params(type = if (isAll) "ALL" else "SINGLE")
        viewModel.logout(params)
    }

    override fun initObserve() {
        launchRepeatOnLifecycle {
            launch {
                viewModel.stateLogout.collectLatest { state ->
                    if (state is LogoutUiState.Success) {
                        build.logoutSuccess()
                        dismissAllowingStateLoss()
                    }
                    if (state is LogoutUiState.Error) {
                        showDialogError(
                            errCode = state.reason.errorCode,
                            message = state.reason.errMessage
                        )
                        hideProgressDialog()
                    }
                    if (state is LogoutUiState.Loading) {
                        showProgressDialog()
                    }
                }
            }
        }
    }
}