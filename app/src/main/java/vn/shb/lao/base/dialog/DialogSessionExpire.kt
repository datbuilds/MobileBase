package vn.shb.lao.base.dialog

import android.content.Context
import vn.shb.lao.R
import vn.shb.lao.utils.view.dialog.BottomSheetDialogHelper

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