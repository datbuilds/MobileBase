package vn.shb.cam.navigation

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import java.util.UUID

class AppNavigator(
    private val fragmentManager: FragmentManager,
    private val containerId: Int,
) : Navigator {

    override val currentDestination: AppDestination?
        get() = fragmentManager.findFragmentById(containerId)?.toAppDestination()

    override fun open(
        destination: AppDestination,
        clearBackStack: Boolean,
        addToBackStack: Boolean,
        transition: NavigationTransition?,
        sharedElements: List<NavigationSharedElement>,
    ) {
        val resolvedTransition = transition ?: defaultTransition(
            destination = destination,
            addToBackStack = addToBackStack,
            clearBackStack = clearBackStack,
        )

        if (clearBackStack) {
            fragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }

        fragmentManager.commit {
            setReorderingAllowed(true)
            setCustomAnimations(
                resolvedTransition.enter,
                resolvedTransition.exit,
                resolvedTransition.popEnter,
                resolvedTransition.popExit,
            )
            sharedElements.forEach { sharedElement ->
                addSharedElement(sharedElement.view, sharedElement.transitionName)
            }
            val fragmentTag = createFragmentTag(destination.route)
            replace(containerId, destination.toFragment(), fragmentTag)
            if (addToBackStack) {
                addToBackStack(fragmentTag)
            }
        }
    }

    override fun popTo(
        destination: AppDestination,
        inclusive: Boolean,
    ): Boolean {
        return when (destination) {
            is AppDestination.HomeArg,
            is AppDestination.Login,
            AppDestination.Splash -> {
                popToRoot(destination)
            }

            else -> {
                val backStackName = findBackStackEntryName(destination.route) ?: return false
                fragmentManager.popBackStackImmediate(
                    backStackName,
                    if (inclusive) FragmentManager.POP_BACK_STACK_INCLUSIVE else 0,
                )
            }
        }
    }

    override fun goBack(): Boolean {
        if (fragmentManager.backStackEntryCount <= 0) {
            return false
        }
        fragmentManager.popBackStack()
        return true
    }

    private fun defaultTransition(
        destination: AppDestination,
        addToBackStack: Boolean,
        clearBackStack: Boolean,
    ): NavigationTransition {
        if (clearBackStack || !addToBackStack) {
            return AppNavigationTransition.Fade
        }
        return when (destination.motionStyle) {
            NavigationMotionStyle.Root -> AppNavigationTransition.Fade
            NavigationMotionStyle.Forward -> AppNavigationTransition.Forward
            NavigationMotionStyle.Result -> AppNavigationTransition.Zoom
            NavigationMotionStyle.Modal -> AppNavigationTransition.Modal
        }
    }

    private fun popToRoot(destination: AppDestination): Boolean {
        if (fragmentManager.backStackEntryCount > 0) {
            fragmentManager.popBackStackImmediate(
                null,
                FragmentManager.POP_BACK_STACK_INCLUSIVE,
            )
            if (currentDestination?.route == destination.route) {
                return true
            }
        }

        open(
            destination = destination,
            clearBackStack = true,
            addToBackStack = false,
            transition = AppNavigationTransition.Fade,
        )
        return true
    }

    private fun findBackStackEntryName(route: String): String? {
        for (index in fragmentManager.backStackEntryCount - 1 downTo 0) {
            val name = fragmentManager.getBackStackEntryAt(index).name ?: continue
            if (name.isDestinationTag(route)) {
                return name
            }
        }
        return findFragmentTagByRoute(route)
    }

    private fun findFragmentTagByRoute(route: String): String? {
        return fragmentManager.fragments
            .asReversed()
            .firstOrNull { fragment -> fragment.matchesRoute(route) }
            ?.tag
            ?.takeIf { it.isDestinationTag(route) }
    }

    private fun createFragmentTag(route: String): String {
        return "$route#${UUID.randomUUID()}"
    }

    private fun String?.isDestinationTag(route: String): Boolean {
        return this?.startsWith("$route#") == true
    }

    private fun Fragment.matchesRoute(route: String): Boolean {
        return arguments?.getString(AppDestination.ARG_INTERNAL_ROUTE) == route
    }
}
