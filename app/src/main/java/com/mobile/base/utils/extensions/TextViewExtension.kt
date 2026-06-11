package com.mobile.base.utils.extensions

import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import android.text.Spannable
import android.text.SpannableString
import android.text.style.RelativeSizeSpan
import android.text.style.SuperscriptSpan
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import com.mobile.base.base.view.MyTextView

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

fun MyTextView.setExchangeRateText(
    value1: String,
    currency1: String,
    value2: String,
    currency2: String
) {
    val text = "$value1 $currency1 ≈ $value2 $currency2"
    val spannable = SpannableString(text)

    fun applyCurrencyStyle(currency: String) {
        val start = text.indexOf(currency)
        val end = start + currency.length

        spannable.setSpan(
            RelativeSizeSpan(0.8f),
            start,
            end,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        spannable.setSpan(
            SuperscriptSpan(),
            start,
            end,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }

    fun applyValueStyle(value: String) {
        val start = text.indexOf(value)
        val end = start + value.length

        spannable.setSpan(
            RelativeSizeSpan(1.1f),
            start,
            end,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }

    applyValueStyle(value1)
    applyValueStyle(value2)

    applyCurrencyStyle(currency1)
    applyCurrencyStyle(currency2)

    this.text = spannable
}