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
                    _state.value = LoginUiState.Error(reason as AppReason)
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
                        val mapIntent = Intent(
                            Intent.ACTION_VIEW,
                            "geo:0,0?q=${branch.address}".toUri()
                        )
                        context.startActivity(mapIntent)
                    }
                }
            }
            BottomSheetDialogHelper(context).message(
                title = getString(R.string.passwordResetInstruction),
                textNegative = getString(R.string.closeLabel),
                negativeAction = {

                }, supView = bindingSup.root
            )
        }
    }

    private fun getListAddress() : List<Branch> {
        return listOf(
            Branch(
                "1. SAIGON – HANOI BANK LAO LIMITED",
                "No.1, Lane Xang Avenue, Vientiane Capital, Laos P.D.R",
                "(+85621) 968888"
            ),
            Branch(
                "2. SAIGON – HANOI BANK LAO LIMITED, CHAMPASAK BRANCH",
                "336, 337, 338 Pakse New Market, Phonekung, Pakse, Champasak, Laos P.D.R",
                "(+85621) 257167"
            ),
            Branch(
                "3. SAIGON – HANOI BANK LAO LIMITED, SAVANNAKHET BRANCH",
                "No. 130/136, Unit 12,13,14, Nongduang Village, Chanthabouly District, Savannakhet, Laos P.D.R",
                "(+85621) 214888"
            ),
            Branch(
                "4. SAIGON – HANOI BANK LAO LIMITED, LUANG PRABANG BRANCH",
                "Ban Wat Xieng Mouane, Luang Prabang, Laos P.D.R",
                "(+85621) 710999"
            ),
            Branch(
                "5. SAIGON – HANOI BANK LAO LIMITED, PAKXAN BRANCH",
                "No. 0236, Unit 1,2,3, Phonthan Village, Pakxan District, Bolikhamxay, Laos P.D.R",
                "(+85621) 216888"
            ),
        )
    }
}
