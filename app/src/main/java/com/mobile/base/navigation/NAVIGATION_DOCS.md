# Navigation

The app has three destinations: `AppDestination.Splash`, `AppDestination.Login()` and `AppDestination.HomeArg()`.

Splash opens Login. Successful authentication opens Home and clears the back stack.
Logout and session expiry return to Login. Feature screens outside this flow have been removed.

Use `requireNavigator().open(destination, clearBackStack = true, addToBackStack = false)` for root navigation.
