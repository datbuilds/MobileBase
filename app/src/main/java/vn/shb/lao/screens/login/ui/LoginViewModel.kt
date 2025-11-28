package vn.shb.lao.screens.login.ui

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import androidx.core.net.toUri
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import vn.shb.core.core.delivery.onResultHandle
import vn.shb.core.core.delivery.reason.AppReason
import vn.shb.core.core.domain.usecases.None
import vn.shb.core.core.domain.usecases.login.UseCaseLogin
import vn.shb.core.core.domain.usecases.login.UseCaseLogout
import vn.shb.core.core.security.encrypt.AndroidSecureStorage
import vn.shb.lao.R
import vn.shb.lao.base.BaseViewModel
import vn.shb.lao.databinding.LayoutBranchListBinding
import vn.shb.lao.screens.login.helper.BranchAdapter
import vn.shb.lao.screens.login.model.Branch
import vn.shb.lao.screens.login.state.LoginUiState
import vn.shb.lao.screens.login.state.LogoutUiState
import vn.shb.lao.utils.view.dialog.BottomSheetDialogHelper

class LoginViewModel(
    private val storage: AndroidSecureStorage,
    private val useCaseLogin: UseCaseLogin,
    private val useCaseLogout: UseCaseLogout
) : BaseViewModel() {

    private val _state = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val stateLogin = _state.asStateFlow()

    private val _stateLogout = MutableStateFlow<LogoutUiState>(LogoutUiState.Idle)
    val stateLogout = _stateLogout.asStateFlow()

    fun login(param: UseCaseLogin.Params) {
        viewModelScope.launch {
            useCaseLogin(param).collect {
                it.onResultHandle(loadingBlock = {
                    _state.value = LoginUiState.Loading
                }, failureBlock = { reason ->
                    _state.value = LoginUiState.Error(reason)
                }, successBlock = { state ->
                    _state.value = LoginUiState.Success(state)
                })
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            useCaseLogout(None).collect {
                it.onResultHandle(loadingBlock = {
                    _stateLogout.value = LogoutUiState.Loading
                }, failureBlock = { reason ->
                    _stateLogout.value = LogoutUiState.Error(reason as AppReason)
                }, successBlock = { state ->
                    _stateLogout.value = LogoutUiState.Success(state)
                })
            }
        }
    }

    fun clearLoginState() {
        _state.value = LoginUiState.Idle
    }

    //endregion
    override fun onCleared() {
        super.onCleared()
    }

    fun showDialogForgotPassword(context: Context) {
        context.apply {

            val bindingSup = LayoutBranchListBinding.inflate(LayoutInflater.from(this))

            //mock data
            val branches = getListAddress()

            bindingSup.rvBranches.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = BranchAdapter(branches) { typeClick, branch ->
                    if (typeClick == BranchAdapter.CLICK_HOTLINE) {
                        val intent = Intent(Intent.ACTION_DIAL, "tel:${branch.tel}".toUri())
                        context.startActivity(intent)
                    } else {
                        val query = "${branch.latitude},${branch.latitude}"
                        val mapIntent = Intent(
                            Intent.ACTION_VIEW,
                            "geo:$query?q=$query(${branch.address})".toUri()
                        )
                        context.startActivity(mapIntent)
                    }
                }
            }
            BottomSheetDialogHelper(context).message(
                title = getString(R.string.passwordResetInstructions), supView = bindingSup.root, isClose = true
            )
        }
    }

    private fun getListAddress() : List<Branch> {
        return listOf(
            Branch(
                "1. SAI GON - HA NOI BANK LAO SOLE CO., LIMITED",
                "Unit 1, Lane Xang Avenue, Vientiane Captital, Laos P.D.R",
                "+856 21 82 8888",
                "17.96729",
                "102.61563"
            ),
            Branch(
                "2. SAI GON - HA NOI BANK LAO SOLE CO., LIMITED, CHAMPASAK BRANCH",
                "336, 337, 338 Pakse New Market, Phonekung, Pakse, Champasack, Laos P.D.R",
                "+856 31 257 167",
                "15.1136",
                "105.81568"
            ),
            Branch(
                "3. SAI GON - HA NOI BANK LAO SOLE CO., LIMITED, SAVANNAKHET BRANCH ",
                "Unit 25, Lattanalangsy Neua Village, Kaisone Phomvihan City, Savannakhet Province, Laos ",
                "+856 30 925 6666",
                "16.56037",
                "104.75389"
            )
        )
    }
}
