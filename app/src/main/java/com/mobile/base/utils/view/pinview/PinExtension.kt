package com.mobile.base.utils.view.pinview

import android.content.Context
import androidx.core.content.ContextCompat
import com.mobile.base.R

fun PinView.statusBackgroundPinView(context: Context, edtAction: EDIT_ACTION = EDIT_ACTION.NORMAL) {
    val resId = when (edtAction) {
        EDIT_ACTION.NORMAL -> R.drawable.bg_edt_normal
        EDIT_ACTION.FOCUS -> R.drawable.bg_edt_focus
        EDIT_ACTION.ERROR -> R.drawable.bg_edt_disable
    }
    this.setItemBackground(ContextCompat.getDrawable(context, resId))
}

enum class EDIT_ACTION {
    FOCUS, NORMAL, ERROR
}