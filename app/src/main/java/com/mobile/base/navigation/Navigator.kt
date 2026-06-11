package com.mobile.base.navigation

import android.view.View
import androidx.annotation.AnimRes
import androidx.fragment.app.Fragment
import com.mobile.base.R

data class NavigationSharedElement(
    val view: View,
    val transitionName: String,
)

data class NavigationTransition(
    @param:AnimRes val enter: Int,
    @param:AnimRes val exit: Int,
    @param:AnimRes val popEnter: Int,
    @param:AnimRes val popExit: Int,
)

interface Navigator {

    val currentDestination: AppDestination?

    fun open(
        destination: AppDestination,
        clearBackStack: Boolean = false,
        addToBackStack: Boolean = true,
        transition: NavigationTransition? = null,
        sharedElements: List<NavigationSharedElement> = emptyList(),
    )

    fun popTo(
        destination: AppDestination,
        inclusive: Boolean = false,
    ): Boolean

    fun goBack(): Boolean
}

interface NavigatorHost {
    val navigator: Navigator
}

fun Fragment.requireNavigator(): Navigator {
    val host = activity as? NavigatorHost
        ?: error("Host activity must implement NavigatorHost.")
    return host.navigator
}

object AppNavigationTransition {
    val None = NavigationTransition(
        enter = R.anim.nav_hold,
        exit = R.anim.nav_hold,
        popEnter = R.anim.nav_hold,
        popExit = R.anim.nav_hold,
    )

    val Fade = NavigationTransition(
        enter = R.anim.nav_fade_through_enter,
        exit = R.anim.nav_fade_through_exit,
        popEnter = R.anim.nav_fade_through_enter,
        popExit = R.anim.nav_fade_through_exit,
    )

    val Forward = NavigationTransition(
        enter = R.anim.nav_forward_enter,
        exit = R.anim.nav_forward_exit,
        popEnter = R.anim.nav_backward_enter,
        popExit = R.anim.nav_backward_exit,
    )

    val Backward = NavigationTransition(
        enter = R.anim.nav_backward_enter,
        exit = R.anim.nav_backward_exit,
        popEnter = R.anim.nav_forward_enter,
        popExit = R.anim.nav_forward_exit,
    )

    val Modal = NavigationTransition(
        enter = R.anim.nav_modal_enter,
        exit = R.anim.nav_modal_exit,
        popEnter = R.anim.nav_modal_pop_enter,
        popExit = R.anim.nav_modal_pop_exit,
    )

    val TopDown = NavigationTransition(
        enter = R.anim.nav_top_down_enter,
        exit = R.anim.nav_top_down_exit,
        popEnter = R.anim.nav_top_down_pop_enter,
        popExit = R.anim.nav_top_down_pop_exit,
    )

    val Zoom = NavigationTransition(
        enter = R.anim.nav_zoom_enter,
        exit = R.anim.nav_zoom_exit,
        popEnter = R.anim.nav_zoom_pop_enter,
        popExit = R.anim.nav_zoom_pop_exit,
    )
}
