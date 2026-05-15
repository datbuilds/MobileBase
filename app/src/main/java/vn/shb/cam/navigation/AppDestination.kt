package vn.shb.cam.navigation

import android.os.Bundle
import androidx.fragment.app.Fragment
import vn.shb.cam.screens.account.AccountDetailFragment
import vn.shb.cam.screens.beneficiary.BeneficiaryFragment
import vn.shb.cam.screens.beneficiary.EditBeneficiaryFragment
import vn.shb.cam.screens.home.HomeFragment
import vn.shb.cam.screens.login.ui.LoginFragment
import vn.shb.cam.screens.paste2pay.Paste2PayFragment
import vn.shb.cam.screens.profile.ChangePasswordFragment
import vn.shb.cam.screens.profile.ChangePasswordSuccessFragment
import vn.shb.cam.screens.profile.ProfileFragment
import vn.shb.cam.screens.splash.ui.SplashFragment
import vn.shb.cam.screens.transaction.TransactionDetailFragment
import vn.shb.cam.screens.transaction.TransactionHistoryFragment
import vn.shb.cam.screens.transfer.ConfirmationFragment
import vn.shb.cam.screens.transfer.MoneyTransferFragment
import vn.shb.cam.screens.transfer.PaymentTransferFragment

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

    data object Profile : AppDestination {
        override val route: String = ROUTE_PROFILE
    }

    data class ChangePassword(val isFromLogin: Boolean = false) : AppDestination {
        override val route: String = ROUTE_CHANGE_PASSWORD
        override val arguments: Bundle = Bundle().apply {
            putBoolean(ARG_FROM_LOGIN, isFromLogin)
        }
    }

    data object ChangePasswordSuccess : AppDestination {
        override val route: String = ROUTE_CHANGE_PASSWORD_SUCCESS
        override val motionStyle: NavigationMotionStyle = NavigationMotionStyle.Result
    }

    data object AccountDetail : AppDestination {
        override val route: String = ROUTE_ACCOUNT_DETAIL
    }

    data object TransactionHistory : AppDestination {
        override val route: String = ROUTE_TRANSACTION_HISTORY
    }

    data object TransactionDetail : AppDestination {
        override val route: String = ROUTE_TRANSACTION_DETAIL
    }

    data class MoneyTransfer(
        private val routeArgs: Bundle? = null,
    ) : AppDestination {
        override val route: String = ROUTE_MONEY_TRANSFER
        override val arguments: Bundle? = routeArgs?.let(::Bundle)
    }

    data class Confirmation(
        private val routeArgs: Bundle? = null,
    ) : AppDestination {
        override val route: String = ROUTE_CONFIRMATION
        override val arguments: Bundle? = routeArgs?.let(::Bundle)
    }

    data class PaymentTransfer(
        private val routeArgs: Bundle? = null,
    ) : AppDestination {
        override val route: String = ROUTE_PAYMENT_TRANSFER
        override val motionStyle: NavigationMotionStyle = NavigationMotionStyle.Result
        override val arguments: Bundle? = routeArgs?.let(::Bundle)
    }

    data object Beneficiary : AppDestination {
        override val route: String = ROUTE_BENEFICIARY
    }

    data class EditBeneficiary(
        private val routeArgs: Bundle? = null,
    ) : AppDestination {
        override val route: String = ROUTE_EDIT_BENEFICIARY
        override val arguments: Bundle? = routeArgs?.let(::Bundle)
    }

    data object Paste2Pay : AppDestination {
        override val route: String = ROUTE_PASTE2PAY
    }

    companion object {
        const val ARG_SHOW_SESSION_EXPIRED = "arg_show_session_expired"
        const val ARG_FROM_LOGIN = "arg_from_login"
        const val ARG_DAY_PASS_EXPIRE = "arg_day_pass_expire"
        internal const val ARG_INTERNAL_ROUTE = "__nav_internal_route"
        internal const val ARG_INTERNAL_ROUTE_ARGS = "__nav_internal_route_args"

        private const val ROUTE_SPLASH = "splash"
        private const val ROUTE_LOGIN = "login"
        private const val ROUTE_HOME = "home"
        private const val ROUTE_PROFILE = "profile"
        private const val ROUTE_CHANGE_PASSWORD = "change_password"
        private const val ROUTE_CHANGE_PASSWORD_SUCCESS = "change_password_success"
        private const val ROUTE_ACCOUNT_DETAIL = "account_detail"
        private const val ROUTE_TRANSACTION_HISTORY = "transaction_history"
        private const val ROUTE_TRANSACTION_DETAIL = "transaction_detail"
        private const val ROUTE_MONEY_TRANSFER = "money_transfer"
        private const val ROUTE_CONFIRMATION = "confirmation"
        private const val ROUTE_PAYMENT_TRANSFER = "payment_transfer"
        private const val ROUTE_BENEFICIARY = "beneficiary"
        private const val ROUTE_EDIT_BENEFICIARY = "edit_beneficiary"
        private const val ROUTE_PASTE2PAY = "paste2pay"

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
            ROUTE_PROFILE -> Profile
            ROUTE_CHANGE_PASSWORD -> ChangePassword(
                isFromLogin = routeArgs?.getBoolean(ARG_FROM_LOGIN) == true,
            )

            ROUTE_CHANGE_PASSWORD_SUCCESS -> ChangePasswordSuccess
            ROUTE_ACCOUNT_DETAIL -> AccountDetail
            ROUTE_TRANSACTION_HISTORY -> TransactionHistory
            ROUTE_TRANSACTION_DETAIL -> TransactionDetail
            ROUTE_MONEY_TRANSFER -> MoneyTransfer(routeArgs)
            ROUTE_CONFIRMATION -> Confirmation(routeArgs)
            ROUTE_PAYMENT_TRANSFER -> PaymentTransfer(routeArgs)
            ROUTE_BENEFICIARY -> Beneficiary
            ROUTE_EDIT_BENEFICIARY -> EditBeneficiary(routeArgs)
            ROUTE_PASTE2PAY -> Paste2Pay
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
    AppDestination.Profile -> ProfileFragment()
    is AppDestination.ChangePassword -> ChangePasswordFragment()
    AppDestination.ChangePasswordSuccess -> ChangePasswordSuccessFragment()
    AppDestination.AccountDetail -> AccountDetailFragment()
    AppDestination.TransactionHistory -> TransactionHistoryFragment()
    AppDestination.TransactionDetail -> TransactionDetailFragment()
    is AppDestination.MoneyTransfer -> MoneyTransferFragment()
    is AppDestination.Confirmation -> ConfirmationFragment()
    is AppDestination.PaymentTransfer -> PaymentTransferFragment()
    AppDestination.Beneficiary -> BeneficiaryFragment()
    is AppDestination.EditBeneficiary -> EditBeneficiaryFragment()
    AppDestination.Paste2Pay -> Paste2PayFragment()
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
