package com.mobile.base.utils.extensions

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityOptions
import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.WindowManager
import androidx.fragment.app.Fragment
import com.mobile.base.R
import com.mobile.base.utils.view.dialog.ProgressDialogUtil

//for calling activity on lock

fun Activity.showProgressDialog() {
    ProgressDialogUtil.show(this)
}

@SuppressLint("SuspiciousIndentation")
fun Activity.hideProgressDialog() {
    if (!isDestroyed && isFinishing) return
    ProgressDialogUtil.dismiss()
}

fun Activity.turnScreenOnAndKeyguardOff() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
        setShowWhenLocked(true)
        setTurnScreenOn(true)

        window.addFlags(
            WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON or WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
        )
    } else {
        window.addFlags(
            WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON or WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
        )
    }

    with(getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            requestDismissKeyguard(this@turnScreenOnAndKeyguardOff, null)
        }
    }
}

fun Activity.turnScreenOffAndKeyguardOn() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
        setShowWhenLocked(false)
        setTurnScreenOn(false)
        window.clearFlags(
            WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON or WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
        )
    } else {
        window.clearFlags(
            WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON or WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
        )
    }
}

fun Activity.nextActivity(targetActivity: Intent) {
    val animationBundle = ActivityOptions.makeCustomAnimation(
        this, R.anim.slide_in_right, R.anim.slide_out_left
    ).toBundle()
    this.startActivity(targetActivity, animationBundle)
}

fun Fragment.nextActivity(targetActivity: Intent) {
    val animationBundle = ActivityOptions.makeCustomAnimation(
        requireActivity(), R.anim.slide_in_right, R.anim.slide_out_left
    ).toBundle()
    this.startActivity(targetActivity, animationBundle)
}

fun Activity.nextActivityFadeAnim(targetActivity: Intent) {
    val animationBundle = ActivityOptions.makeCustomAnimation(
        this, R.anim.fade_in, R.anim.fade_out
    ).toBundle()
    this.startActivity(targetActivity, animationBundle)
}

fun Fragment.nextActivityFadeAnim(targetActivity: Intent) {
    val animationBundle = ActivityOptions.makeCustomAnimation(
        requireActivity(), R.anim.fade_in, R.anim.fade_out
    ).toBundle()
    this.startActivity(targetActivity, animationBundle)
}

fun Activity.returnActivity(targetActivity: Intent) {
    val animationBundle = ActivityOptions.makeCustomAnimation(
        this, R.anim.fade_in, R.anim.fade_out
    ).toBundle()
    this.startActivity(targetActivity, animationBundle)
}

fun Fragment.returnActivity(targetActivity: Intent) {
    val animationBundle = ActivityOptions.makeCustomAnimation(
        requireContext(), R.anim.fade_in, R.anim.fade_out
    ).toBundle()
    this.startActivity(targetActivity, animationBundle)
}

