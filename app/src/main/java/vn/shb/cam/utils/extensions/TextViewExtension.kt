package vn.shb.cam.utils.extensions

import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes

fun TextView.drawableColor(@ColorInt color: Int) {
    for (drawable in compoundDrawables) {
        if (drawable != null) {
            drawable.colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN)
        }
    }
}

fun TextView.topDrawable(drawable: Drawable?) {
    setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null)
}

fun TextView.topDrawable(@DrawableRes resId: Int) {
    setCompoundDrawablesWithIntrinsicBounds(0, resId, 0, 0)
}

fun TextView.leftDrawable(@DrawableRes resId: Int) {
    setCompoundDrawablesWithIntrinsicBounds(resId, 0, 0, 0)
}

fun TextView.rightDrawable(@DrawableRes resId: Int) {
    setCompoundDrawablesWithIntrinsicBounds(0, 0, resId, 0)
}

fun TextView.color(@ColorInt color: Int) {
    setTextColor(color)
//    drawableColor(color)
}