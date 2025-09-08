package vn.shb.lao.screens.splash.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import vn.shb.core.core.security.encrypt.AndroidSecureStorage
import vn.shb.lao.R
import vn.shb.lao.activity.login.LoginActivity
import vn.shb.lao.base.BaseActivity
import vn.shb.lao.databinding.ActivitySplashBinding
import vn.shb.lao.utils.extensions.launchRepeatOnLifecycle
import vn.shb.lao.utils.extensions.nextActivityFadeAnim
import java.util.Base64

class SplashActivity : BaseActivity<ActivitySplashBinding>(ActivitySplashBinding::inflate) {

    private val storage: AndroidSecureStorage by inject()
    private val viewModel: SplashViewModel by inject()

    private var isShowSystemError = false

    override fun initView() {
        mapBackground()
    }

    private fun mapBackground() {
        // set background login
        val bgLogin = storage.getBackgroundLogin()
        val bgBitmap: Bitmap? = if (bgLogin.isNotEmpty()) {
            prepareBGLogin(bgLogin)
        } else {
            val drawable =
                ContextCompat.getDrawable(this@SplashActivity, R.drawable.img_bg_login_res)
            if (drawable is BitmapDrawable) drawable.bitmap else drawable?.toBitmap()
        }

        bgBitmap?.let {
            binding.ivBackground.setImageBitmap(it)
        }
    }

    @SuppressLint("NewApi")
    private fun prepareBGLogin(bgBase64: String): Bitmap? {
        val default = Base64.getDecoder().decode(bgBase64)
        return BitmapFactory.decodeByteArray(default, 0, default.size)
    }

    override fun initListener() {}

    override fun initObserve() {
        launchRepeatOnLifecycle {
            launch {
                delay(600)
                nextLoginScreen()
            }
        }
    }

    private fun errorDialog() {
        showDialogError(
            title = "Lỗi kết nối",
            message = "Kết nối mạng an toàn không khả dụng. Vui lòng kiểm tra lại.",
            tvAction = "Đóng ứng dụng",
            isCancelable = false
        ) {
            finishAffinity()
        }
    }

    override fun onResume() {
        super.onResume()
        if (isShowSystemError) {
            errorDialog()
        }
    }

    private fun nextLoginScreen() {
        nextActivityFadeAnim(LoginActivity.intent(this))
    }

    override fun onDestroy() {
        super.onDestroy()
        isShowSystemError = false
    }

    companion object {
        @JvmStatic
        fun intent(context: Context): Intent {
            val intent = Intent(context, SplashActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            return intent
        }
    }
}