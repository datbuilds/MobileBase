package vn.shb.dn.choosePhotoHelper.utils

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.Size
import androidx.core.content.ContextCompat

/**
 * @author aminography
 */

fun hasPermissions(
    context: Context,
    @Size(min = 1) vararg permissionList: String
): Boolean {
    for (permission in permissionList) {
        if (ContextCompat.checkSelfPermission(
                context,
                permission
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return false
        }
    }
    return true
}