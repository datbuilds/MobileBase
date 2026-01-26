package vn.shb.cam.base.dialog

import android.content.Context
import vn.shb.cam.R
import vn.shb.cam.utils.view.dialog.BottomSheetDialogHelper

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