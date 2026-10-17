package com.mobile.base.dialog

import android.content.Context
import com.mobile.R
import com.mobile.utils.view.dialog.BottomSheetDialogHelper

class DialogSessionExpire {
    fun show(
        context: Context,
        onClickLogout: (() -> Unit)? = null
    ) {
        with(context) {
            BottomSheetDialogHelper(context).message(
                getString(R.string.notification),
                getString(R.string.sessionExpired),
                getString(R.string.close),
                positiveAction = {
                    onClickLogout?.invoke()
                }
            )
        }
    }
}