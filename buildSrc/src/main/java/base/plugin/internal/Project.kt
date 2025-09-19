package base.plugin.internal

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.DynamicFeatureExtension
import com.android.build.api.dsl.LibraryExtension
import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.api.Project
import org.gradle.kotlin.dsl.the

/*
VersionCatalog typesafe accessors aren't available in precompiled script plugins - see https://github.com/gradle/gradle/issues/15383
These are workarounds
 */
internal val Project.libs
    get() = the<LibrariesForLibs>()

internal val Project.libraryExtension
    get() = the<LibraryExtension>()

internal val Project.applicationExtension
    get() = the<ApplicationExtension>()

internal val Project.dynamicFeatureExtension
    get() = the<DynamicFeatureExtension>()