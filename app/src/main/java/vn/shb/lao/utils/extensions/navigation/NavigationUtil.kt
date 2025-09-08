package vn.shb.lao.utils.extensions.navigation

import android.net.Uri
import android.os.Bundle
import androidx.annotation.IdRes
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions
import vn.shb.core.utils.logE

fun NavController.safeNavigate(direction: NavDirections) {
    currentDestination?.getAction(direction.actionId)?.run {
        navigate(direction)
    } ?: logE(
        "safeNavigate",
        "cannot navigate ${direction.actionId} currentDestination = ${currentDestination?.label}"
    )
}

fun NavController.safeNavigate(
    @IdRes currentDestinationId: Int,
    @IdRes id: Int,
    args: Bundle? = null,
    options: NavOptions? = null
) {
    if (currentDestinationId == currentDestination?.id) {
        navigate(id, args, options)
    }
}

fun NavController.safeNavigate(deepLink: Uri) {
    try {
        navigate(deepLink)
    } catch (ex: Exception) {
        ex.printStackTrace()
    }
}