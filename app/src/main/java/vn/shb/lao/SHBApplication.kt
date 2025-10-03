package vn.shb.lao

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.kongqw.network.monitor.NetworkMonitorManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import timber.log.Timber
import vn.shb.lao.base.view.FontManager
import vn.shb.lao.di.appComponent
import vn.shb.lao.utils.widgets.LocaleHelper

class SHBApplication : Application(), LifecycleEventObserver {
    // No need to cancel this scope as it'll be torn down with the process.
    // see
    // https://medium.com/androiddevelopers/coroutines-patterns-for-work-that-shouldnt-be-cancelled-e26c40f142ad
    val applicationScope = CoroutineScope(SupervisorJob())
//    private val networkFlipperPlugin: NetworkFlipperPlugin by inject()

    override fun attachBaseContext(base: Context?) {
        val context = base?.let { LocaleHelper.getLanguageContext(it) }
        super.attachBaseContext(context)
    }

    override fun onCreate() {
        super.onCreate()
        FontManager.init(this, "onest")
        NetworkMonitorManager.getInstance().init(this)

        startKoin {
            androidLogger(if (BuildConfig.DEBUG) Level.ERROR else Level.NONE)
            androidContext(this@SHBApplication)
            modules(appComponent(this@SHBApplication))
        }

        ProcessLifecycleOwner.get().lifecycle.addObserver(this)

        initFlipper()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        registerActivityLifecycleCallbacks(
            object : ActivityLifecycleCallbacks {
                override fun onActivityCreated(
                    activity: Activity,
                    savedInstanceState: Bundle?
                ) {
                    currentActivity = activity
                }

                override fun onActivityStarted(activity: Activity) {}

                override fun onActivityResumed(activity: Activity) {
                    currentActivity = activity
                    isAppInBackground = false
                }

                override fun onActivityPaused(activity: Activity) {}

                override fun onActivityStopped(activity: Activity) {
                    isAppInBackground = true
                }

                override fun onActivitySaveInstanceState(
                    activity: Activity,
                    outState: Bundle
                ) {
                }

                override fun onActivityDestroyed(activity: Activity) {
                    if (currentActivity == activity) {
                        currentActivity = null
                    }
                }
            }
        )

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
    }

    private fun initFlipper() {
//        SoLoader.init(this.applicationContext, false)
//
//        if (FlipperUtils.shouldEnableFlipper(this)) {
//            val client = AndroidFlipperClient.getInstance(this)
//            client.addPlugin(
//                InspectorFlipperPlugin(
//                    this,
//                    DescriptorMapping.withDefaults()
//                )
//            )
//            client.addPlugin(networkFlipperPlugin)
//            client.addPlugin(DatabasesFlipperPlugin(this))
//            client.addPlugin(SharedPreferencesFlipperPlugin(this, "encrypted_prefs"))
//            client.addPlugin(CrashReporterPlugin.getInstance());
//            client.start()
//        }
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        Timber.e("onStateChanged= $event")
    }

    fun isForeground(): Boolean {
        val currentState = ProcessLifecycleOwner.get().lifecycle.currentState
        val isForeground =
            currentState == Lifecycle.State.STARTED || currentState == Lifecycle.State.RESUMED
        Timber.d("currentState= $currentState isForeground= $isForeground")
        return isForeground
    }

    var currentActivity: Activity? = null
    var isAppInBackground = false

    companion object {
        @JvmStatic
        fun context(): Context? {
            return context()
        }
    }
}
