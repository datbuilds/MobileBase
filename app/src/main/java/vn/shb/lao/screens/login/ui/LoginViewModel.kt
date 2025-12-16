package vn.shb.lao.screens.login.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import androidx.core.net.toUri
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import vn.shb.core.core.delivery.onFailure
import vn.shb.core.core.delivery.onLoading
import vn.shb.core.core.delivery.onResultHandle
import vn.shb.core.core.delivery.onSuccess
import vn.shb.core.core.delivery.reason.AppReason
import vn.shb.core.core.domain.usecases.None
import vn.shb.core.core.domain.usecases.login.UseCaseLogin
import vn.shb.core.core.domain.usecases.login.UseCaseLogout
import vn.shb.core.core.domain.usecases.wso2.UseCaseGetTokenWso2
import vn.shb.core.core.security.encrypt.AndroidSecureStorage
import vn.shb.lao.BuildConfig
import vn.shb.lao.R
import vn.shb.lao.base.BaseViewModel
import vn.shb.lao.databinding.LayoutBranchListBinding
import vn.shb.lao.screens.login.helper.BranchAdapter
import vn.shb.lao.screens.login.model.Branch
import vn.shb.lao.screens.login.state.LoginUiState
import vn.shb.lao.screens.login.state.LogoutUiState
import vn.shb.lao.utils.view.dialog.BottomSheetDialogHelper
import java.util.concurrent.TimeUnit

class LoginViewModel(
    private val storage: AndroidSecureStorage,
    private val useCaseLogin: UseCaseLogin,
    private val useCaseLogout: UseCaseLogout,
    private val useCaseGetTokenWso2: UseCaseGetTokenWso2,
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

    fun getTokenWso2(param: UseCaseLogin.Params) {
        viewModelScope.launch {
            if (BuildConfig.FLAVOR == "pro") {
                login(param)
            } else {
                val paramsWso2 = UseCaseGetTokenWso2.InputParams(
                    BuildConfig.AUTHORIZATION, UseCaseGetTokenWso2.Params(
                        grant_type = BuildConfig.GRANT_TYPE,
                        username = BuildConfig.USERNAME,
                        password = BuildConfig.PASSWORD
                    )
                )
                useCaseGetTokenWso2(paramsWso2).collect { result ->
                    result.onSuccess { trans ->
                        storage.setTokenWso2(trans.access_token)
                        storage.setRfTokenWso2(trans.refresh_token)
                        storage.updateExpireTime(
                            TimeUnit.SECONDS.toMinutes(trans.expireIn()).toInt()
                        )
                        delay(300)
                        login(param)
                    }
                    result.onFailure { error ->
                        _state.value = LoginUiState.Error(error)
                    }
                    result.onLoading {
                        _state.value = LoginUiState.Loading
                    }
                }
            }

        }
    }

    fun clearLoginState() {
        _state.value = LoginUiState.Idle
    }

    fun showDialogForgotPassword(context: Context) {
        context.apply {

            val bindingSup = LayoutBranchListBinding.inflate(LayoutInflater.from(this))

            //mock data
            val branches = getListAddress(context)

            bindingSup.rvBranches.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = BranchAdapter(branches) { typeClick, branch ->
                    if (typeClick == BranchAdapter.CLICK_HOTLINE) {
                        val intent = Intent(Intent.ACTION_DIAL, "tel:${branch.tel}".toUri())
                        context.startActivity(intent)
                    } else {
                        openMap(context, branch.latitude, branch.longitude, branch.address)
                    }
                }
            }
            BottomSheetDialogHelper(context).message(
                title = getString(R.string.passwordResetInstructions),
                supView = bindingSup.root,
                isClose = true
            )
        }
    }

    fun openMap(context: Context, latitude: String, longitude: String, placeName: String) {
        val uri = "$latitude,$longitude"

        // Thử mở Google Maps trước
        val gmmIntentUri = Uri.parse("geo:$uri?q=$uri($placeName)")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")

        if (mapIntent.resolveActivity(context.packageManager) != null) {
            context.startActivity(mapIntent)
            return
        }

        // Fallback: mở app bản đồ mặc định
        val fallbackUri = Uri.parse("geo:$uri?q=$uri($placeName)")
        val fallbackIntent = Intent(Intent.ACTION_VIEW, fallbackUri)

        context.startActivity(fallbackIntent)
    }

    private fun getListAddress(context: Context): List<Branch> {
        return listOf(
            Branch(
                context.getString(R.string.shbBranch1),
                context.getString(R.string.shbBranch1Address),
                "+856 21 82 8888",
                "17.96729",
                "102.61563"
            ),
            Branch(
                context.getString(R.string.shbBranch2),
                context.getString(R.string.shbBranch2Address),
                "+856 31 257 167",
                "15.1136",
                "105.81568"
            ),
            Branch(
                context.getString(R.string.shbBranch3),
                context.getString(R.string.shbBranch3Address),
                "+856 30 925 6666",
                "16.56037",
                "104.75389"
            )
        )
    }
}
