# QA Report

## Build

- Variant: `devDebug`
- Device: Pixel 7 Pro (`2A201FDH3005MX`)
- Package: `com.mobile`
- Command: `./gradlew :app:testDevDebugUnitTest :app:assembleDevDebug --stacktrace`
- Result: passed

## Refresh token lifecycle

- Confirmed Splash/Login can open without starting a token refresh.
- Confirmed no invalid-token bottom sheet is shown before authentication.
- Refresh starts only from an authenticated Home flow with non-empty access and refresh tokens.
- Logout and Activity teardown stop the handler and cancel the active refresh coroutine.
- Re-entering Splash, Onboarding, or Login stops any existing refresh manager.

## Bottom sheet

- Verified the redesigned notification sheet on the Pixel 7 Pro.
- Confirmed 20dp horizontal content padding, 48dp icon/close targets, 52dp actions, readable wrapping, navigation-bar clearance, and brand-color contrast.
- QA preview hook was removed before the final build.

## Screenshots

- `qa/screenshots/bottom-sheet-redesign.png`
- `qa/screenshots/final-auth-flow-no-auto-sheet.png`
- `qa/screenshots/final-onboarding-no-auto-sheet.png`

## Notes

- Gradle reports existing string-format and deprecation warnings; they do not fail this build and are unrelated to this change.
