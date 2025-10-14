package vn.shb.core.core.domain.source.repository

import android.graphics.Typeface
import android.os.CountDownTimer
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import vn.shb.core.core.delivery.ActionDone
import vn.shb.core.core.delivery.ResultSHB
import vn.shb.core.core.delivery.reason.AppReason
import vn.shb.core.core.domain.source.response.LoginResponse
import vn.shb.core.core.domain.source.response.LogoutResponse
import vn.shb.core.core.domain.source.service.ServiceAuth
import vn.shb.core.core.domain.usecases.login.RepositoryAuth
import vn.shb.core.core.domain.usecases.login.StateLogin
import vn.shb.core.core.domain.usecases.login.UseCaseLogin
import vn.shb.core.core.domain.usecases.login.UseCaseRefreshToken
import vn.shb.core.core.security.encrypt.AndroidSecureStorage
import vn.shb.data.entities.login.UserLog
import java.util.concurrent.TimeUnit

class RepositoryAuthImpl(
    private val storage: AndroidSecureStorage,
    private val serviceAuth: ServiceAuth,
) : RepositoryAuth {

    /**
     * logout
     */
    override suspend fun logout() =
        resultLogout(result = serviceAuth.logout())

    private fun resultLogout(result: ResultSHB<LogoutResponse>): ResultSHB<ActionDone> {
        return when (result) {
            is ResultSHB.Success -> {
                val contentResult = result.successData
                if (contentResult.isSuccess()) {
                    val content = contentResult.data
                    ResultSHB.Success(content ?: ActionDone)
                } else {
                    ResultSHB.Failure(
                        AppReason(
                            message = contentResult.errorMessage,
                            code = contentResult.errorCode
                        )
                    )
                }
            }

            is ResultSHB.Failure -> {
                ResultSHB.Failure(
                    AppReason(
                        message = result.reason.errMessage,
                        code = result.reason.errorCode
                    )
                )
            }

            else -> {
                ResultSHB.Loading
            }
        }
    }

    override suspend fun login(param: UseCaseLogin.Params) =
        resultLogin(param, result = serviceAuth.login(params = param))

    private fun resultLogin(
        param: UseCaseLogin.Params,
        result: ResultSHB<LoginResponse>
    ) = when (result) {
        is ResultSHB.Success -> {
            val contentResult = result.successData
            if (contentResult.isSuccess()) {
                val content = contentResult.data
                if (content != null) {
                    val user = UserLog(
                        id_token = content.id_token,
                        access_token = content.access_token,
                        expires_in = content.expires_in,
                        refresh_expires_in = content.refresh_expires_in,
                        refresh_token = content.refresh_token,
                        scope = content.scope,
                        session_state = content.session_state,
                        token_type = content.token_type,
                        username = content.username,
                        userLog = param.username,
                        title = content.title
                    )
                    saveData(user)
                    ResultSHB.Success(StateLogin.OpenDashboard)
                } else {
                    resultLoginFail(contentResult)
                }
            } else {
                resultLoginFail(contentResult)
            }
        }

        is ResultSHB.Failure -> {
            ResultSHB.Failure(
                AppReason(
                    message = result.reason.errMessage,
                    code = result.reason.errorCode
                )
            )
        }

        else -> ResultSHB.Loading
    }

    private var countDownTimer: CountDownTimer? = null

    private fun showLockMessage(totalMillis: Long = 15 * 60 * 1000L) {
        val message = "You have exceeded 5 failed login attempts.\nPlease try again in "

        countDownTimer?.cancel()
        countDownTimer = object : CountDownTimer(totalMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val minutes = (millisUntilFinished / 1000) / 60
                val seconds = (millisUntilFinished / 1000) % 60
                val timeFormatted = String.format("%02d:%02d", minutes, seconds)

                val fullText = "$message $timeFormatted"

                val spannable = SpannableString(fullText)
                val start = fullText.indexOf(timeFormatted)
                val end = start + timeFormatted.length

                // làm đậm phần thời gian
                spannable.setSpan(
                    StyleSpan(Typeface.BOLD),
                    start,
                    end,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )

                tvLockMessage.text = spannable
            }

            override fun onFinish() {
                tvLockMessage.text = "You can try logging in again."
            }
        }.start()
    }


    private fun saveData(user: UserLog) {
        storage.apply {
            setUserLog(user.toUserString())
            setToken(user.access_token)
            setRfToken(user.refresh_token)
            setExpireTime(TimeUnit.SECONDS.toMinutes(user.expireIn()).toInt())
            firstOpened(isFirst = true)
        }
    }

    override suspend fun refreshToken(params: UseCaseRefreshToken.Params) =
        resultRFLogin(serviceAuth.refreshToken(params = params))

    private fun resultRFLogin(result: ResultSHB<LoginResponse>): ResultSHB<UserLog> {
        return when (result) {
            is ResultSHB.Success -> {
                val contentResult = result.successData
                if (contentResult.isSuccess()) {
                    val content = contentResult.data
                    ResultSHB.Success(content ?: UserLog())
                } else {
                    resultLoginFail(contentResult)
                }
            }

            is ResultSHB.Failure -> {
                ResultSHB.Failure(
                    AppReason(
                        message = result.reason.errMessage,
                        code = result.reason.errorCode
                    )
                )
            }

            else -> {
                ResultSHB.Loading
            }
        }
    }
}