import com.android.build.api.dsl.ApplicationProductFlavor
import com.android.build.api.dsl.LibraryProductFlavor
import org.gradle.api.NamedDomainObjectContainer

object FlavorDimensions {
    const val ENVIRONMENT = "environment"
}

const val FLAVOR_PRODUCTION = "pro"
const val FLAVOR_UAT = "uat"
const val FLAVOR_DEV = "dev"

fun ApplicationProductFlavor.configureAppFlavor(
    appName: String,
    baseUrl: String
) {
    resValue("string", "app_name", appName)
    buildConfigField("String", "BASE_URL", "\"$baseUrl\"")
}

fun NamedDomainObjectContainer<ApplicationProductFlavor>.createApplicationFlavor(
    pro: (ApplicationProductFlavor.() -> Unit)? = null,
    uat: (ApplicationProductFlavor.() -> Unit)? = null,
    dev: (ApplicationProductFlavor.() -> Unit)? = null
) {

    create(FLAVOR_PRODUCTION) {
        dimension = FlavorDimensions.ENVIRONMENT
        matchingFallbacks.add(FLAVOR_PRODUCTION)
        pro?.invoke(this)
    }

    create(FLAVOR_UAT) {
        dimension = FlavorDimensions.ENVIRONMENT
        applicationIdSuffix = ".$FLAVOR_UAT"
        versionNameSuffix = "-$FLAVOR_UAT"
        matchingFallbacks.add(FLAVOR_UAT)
        uat?.invoke(this)
    }

    create(FLAVOR_DEV) {
        dimension = FlavorDimensions.ENVIRONMENT
        applicationIdSuffix = ".$FLAVOR_DEV"
        versionNameSuffix = "-$FLAVOR_DEV"
        matchingFallbacks.add(FLAVOR_DEV)
        dev?.invoke(this)
    }
}

fun NamedDomainObjectContainer<LibraryProductFlavor>.createLibraryFlavor(
    pro: (LibraryProductFlavor.() -> Unit)? = null,
    uat: (LibraryProductFlavor.() -> Unit)? = null,
    dev: (LibraryProductFlavor.() -> Unit)? = null
) {
    create(FLAVOR_PRODUCTION) {
        dimension = FlavorDimensions.ENVIRONMENT
        pro?.invoke(this)
    }

    create(FLAVOR_UAT) {
        dimension = FlavorDimensions.ENVIRONMENT
        uat?.invoke(this)
    }

    create(FLAVOR_DEV) {
        dimension = FlavorDimensions.ENVIRONMENT
        dev?.invoke(this)
    }
}
