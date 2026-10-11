package base.plugin

import base.plugin.configure.configureAndroidApplication
import base.plugin.configure.configureAndroidDynamicFeature
import base.plugin.configure.configureAndroidLibrary
import com.android.build.gradle.AppPlugin
import com.android.build.gradle.DynamicFeaturePlugin
import com.android.build.gradle.LibraryPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.gradle.kotlin.dsl.withType

/**
 * https://youtu.be/sQC9-Rj2yLI?t=429
 * https://github.com/gbsendhil/AndroidBuildSrcPlugin
 */
class AppPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        with(project) {
            plugins.all {
                when (this) {
                    is LibraryPlugin -> configureAndroidLibrary()
                    is AppPlugin -> configureAndroidApplication()
                    is DynamicFeaturePlugin -> configureAndroidDynamicFeature()
                }
            }

            tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
                compilerOptions.jvmTarget.set(JvmTarget.JVM_17)
                compilerOptions.freeCompilerArgs.set(listOf(
//                    "-Xuse-experimental=kotlinx.coroutines.ExperimentalCoroutinesApi",
                    "-opt-in=kotlinx.coroutines.FlowPreview"
                ))
            }


        }
    }
}
