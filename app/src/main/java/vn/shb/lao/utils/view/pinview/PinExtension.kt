package vn.shb.lao.utils.view.pinview

import android.content.Context
import androidx.core.content.ContextCompat
import vn.shb.lao.R

fun PinView.statusBackgroundPinView(context: Context, edtAction: EDIT_ACTION = EDIT_ACTION.NORMAL) {
    val resId = when (edtAction) {
        EDIT_ACTION.NORMAL -> vn.shb.lao.ui.R.drawable.bg_edt_normal
        EDIT_ACTION.FOCUS -> vn.shb.lao.ui.R.drawable.bg_edt_focus
        EDIT_ACTION.ERROR -> vn.shb.lao.ui.R.drawable.bg_edt_disable
    }
    this.setItemBackground(ContextCompat.getDrawable(context, resId))
}

enum class EDIT_ACTION {
    FOCUS, NORMAL, ERROR
}