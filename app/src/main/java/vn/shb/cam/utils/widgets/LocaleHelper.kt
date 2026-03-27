package vn.shb.cam.utils.widgets

import android.content.Context
import android.content.res.Configuration
import android.util.Log
import androidx.annotation.DrawableRes
import androidx.core.content.edit
import vn.shb.cam.R
import vn.shb.cam.base.view.FontManager
import vn.shb.core.core.security.encrypt.AndroidSecureStorage
import java.util.Locale

object LocaleHelper {

    private const val PREFS_NAME = "app_prefs"
    private const val KEY_LANGUAGE = "key_language"
    private const val DEFAULT_LANGUAGE = "km"

    /**
     * Dùng trong Application.attachBaseContext()
     * Lấy ngôn ngữ hiện tại từ SharedPreferences và trả về Context đã bọc.
     */
    fun getLanguageContext(base: Context): Context {
        val prefs = base.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val lang = prefs.getString(KEY_LANGUAGE, DEFAULT_LANGUAGE) ?: DEFAULT_LANGUAGE
        return setLocale(base, lang)
    }

    fun setLocale(context: Context, language: String): Context {
        val locale = Locale(language)
        Locale.setDefault(locale)

        val config = Configuration()
        config.setLocale(locale)
        val newContext = context.createConfigurationContext(config)

//        if (language.equals("km", ignoreCase = true)) {
//            FontManager.init(newContext)
//        } else {
            FontManager.init(newContext, "inter")
//        }

        return newContext
    }

    fun saveLanguage(context: Context, language: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit { putString(KEY_LANGUAGE, language) }
    }

    fun getCurrentLanguage(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_LANGUAGE, DEFAULT_LANGUAGE) ?: DEFAULT_LANGUAGE
    }

    fun getResourceLocale(
        type: String,
        res: (Int) -> Unit
    ) {
        when (type) {
            "en" -> {
                res(R.drawable.ic_logo_uk)
            }

            "km" -> {
                res(R.drawable.ic_logo_cam)
            }

            "vi" -> {
                res(R.drawable.ic_logo_vn)
            }

            else -> {
                res(R.drawable.ic_logo_uk)
            }
        }
    }

}

