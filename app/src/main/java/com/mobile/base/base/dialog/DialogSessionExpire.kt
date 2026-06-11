package com.mobile.base.base.dialog

import android.content.Context
import com.mobile.base.R
import com.mobile.base.utils.view.dialog.BottomSheetDialogHelper

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