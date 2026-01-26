package vn.shb.cam.utils.extensions

import android.app.Activity
import android.content.Context
import android.util.DisplayMetrics
import android.util.TypedValue

fun Context.getDisplayMetrics(): DisplayMetrics {
    val outMetrics = DisplayMetrics()

    val activity = this as Activity
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
        val display = activity.display
        display?.getRealMetrics(outMetrics)
    } else {
        @Suppress("DEPRECATION")
        val display = activity.windowManager.defaultDisplay
        @Suppress("DEPRECATION")
        display.getMetrics(outMetrics)
    }
    return outMetrics
}

fun Context.getScreenHeight() = resources.displayMetrics.heightPixels

fun Context.getStatusBarHeight(): Int {
    val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
    return if (resourceId > 0) resources.getDimensionPixelSize(resourceId) else 0
}

fun Context.getActionBarHeight() = with(TypedValue().also {
    theme.resolveAttribute(
        android.R.attr.actionBarSize,
        it,
        true
    )
}) {
    TypedValue.complexToDimensionPixelSize(this.data, resources.displayMetrics)
}

fun Context.getNavigationBarHeight(): Int {
    val resourceNavId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
    return if (resourceNavId > 0) resources.getDimensionPixelSize(resourceNavId) else 0
}