
object AndroidConfig {
    const val APPLICATION_ID = "vn.shb.lao"

    // Prepare the version name.
    // Version name scheme: major.minor.patch
    private const val versionMajor = 1
    private const val versionMinor = 0
    private const val versionPatch = 0

    /**
     * Prepare the version name in [versionMajor].[versionMinor].[versionPatch] format.
     */
    const val VERSION_NAME = "$versionMajor.$versionMinor.$versionPatch"

    /**
     *
     */
    const val VERSION_CODE = 1

    /**
     *
     */
    const val archivesBaseName = "SHB SAHA LAOS - ${VERSION_NAME}(${VERSION_CODE})"

    const val VIEW_BINDING_ENABLED = true
    const val DATA_BINDING_ENABLED = true

    const val TEST_INSTRUMENTATION_RUNNER = "androidx.test.runner.AndroidJUnitRunner"
}