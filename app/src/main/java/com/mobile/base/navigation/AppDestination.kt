package com.mobile.base.navigation

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.mobile.base.screens.home.HomeFragment
import com.mobile.base.screens.login.ui.LoginFragment
import com.mobile.base.screens.splash.ui.SplashFragment

sealed interface AppDestination {

    val route: String
    val motionStyle: NavigationMotionStyle
        get() = NavigationMotionStyle.Forward
    val arguments: Bundle?
        get() = null

    data object Splash : AppDestination {
        override val route: String = ROUTE_SPLASH
        override val motionStyle: NavigationMotionStyle = NavigationMotionStyle.Root
    }

    data class Login(
        val showSessionExpired: Boolean = false,
    ) : AppDestination {
        override val route: String = ROUTE_LOGIN
        override val motionStyle: NavigationMotionStyle = NavigationMotionStyle.Root
        override val arguments: Bundle = Bundle().apply {
            putBoolean(ARG_SHOW_SESSION_EXPIRED, showSessionExpired)
        }
    }

    data class HomeArg(val dayPassExpire: Int? = null) : AppDestination {
        override val route: String = ROUTE_HOME
        override val motionStyle: NavigationMotionStyle = NavigationMotionStyle.Root
        override val arguments: Bundle = Bundle().apply {
            dayPassExpire?.let { putInt(ARG_DAY_PASS_EXPIRE, it) }
        }
    }

    companion object {
        const val ARG_SHOW_SESSION_EXPIRED = "arg_show_session_expired"
        const val ARG_DAY_PASS_EXPIRE = "arg_day_pass_expire"
        internal const val ARG_INTERNAL_ROUTE = "__nav_internal_route"
        internal const val ARG_INTERNAL_ROUTE_ARGS = "__nav_internal_route_args"

        private const val ROUTE_SPLASH = "splash"
        private const val ROUTE_LOGIN = "login"
        private const val ROUTE_HOME = "home"

        fun fromFragment(fragment: Fragment): AppDestination? {
            val args = fragment.arguments
            val route = args?.getString(ARG_INTERNAL_ROUTE) ?: return null
            val routeArgs = args.getBundle(ARG_INTERNAL_ROUTE_ARGS)
            return fromRoute(route, routeArgs)
        }

        fun fromRoute(
            route: String,
            routeArgs: Bundle? = null,
        ): AppDestination? = when (route) {
            ROUTE_SPLASH -> Splash
            ROUTE_LOGIN -> Login(
                showSessionExpired = routeArgs?.getBoolean(ARG_SHOW_SESSION_EXPIRED) == true,
            )

            ROUTE_HOME -> HomeArg(
                dayPassExpire = routeArgs?.getInt(ARG_DAY_PASS_EXPIRE),
            )
            else -> null
        }
    }
}

enum class NavigationMotionStyle {
    Root,
    Forward,
    Result,
    Modal,
}

fun Fragment.toAppDestination(): AppDestination? = AppDestination.fromFragment(this)

fun AppDestination.toFragment(): Fragment = when (this) {
    AppDestination.Splash -> SplashFragment()
    is AppDestination.Login -> LoginFragment()
    is AppDestination.HomeArg -> HomeFragment()
}.apply {
    arguments = buildFragmentArguments()
}

private fun AppDestination.buildFragmentArguments(): Bundle {
    return Bundle().apply {
        this@buildFragmentArguments.arguments?.let(::putAll)
        putString(AppDestination.ARG_INTERNAL_ROUTE, route)
        this@buildFragmentArguments.arguments?.let {
            putBundle(AppDestination.ARG_INTERNAL_ROUTE_ARGS, Bundle(it))
        }
    }
}
