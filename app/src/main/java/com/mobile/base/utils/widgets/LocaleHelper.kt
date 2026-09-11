package com.mobile.base.utils.widgets

import android.content.Context
import android.content.res.Configuration
import androidx.core.content.edit
import com.mobile.base.R
import com.mobile.base.base.view.FontManager
import com.mobile.base.core.utils.AppLanguage
import java.util.Locale

object LocaleHelper {

    private const val PREFS_NAME = "app_prefs"
    private const val KEY_LANGUAGE = "key_language"
    private const val DEFAULT_LANGUAGE = AppLanguage.DEFAULT

    /**
     * Dùng trong Application.attachBaseContext()
     * Lấy ngôn ngữ hiện tại từ SharedPreferences và trả về Context đã bọc.
     */
    fun getLanguageContext(base: Context): Context {
        return setLocale(base, getCurrentLanguage(base))
    }

    fun setLocale(context: Context, language: String): Context {
        val locale = Locale(AppLanguage.normalize(language))
        Locale.setDefault(locale)

        val config = Configuration()
        config.setLocale(locale)
        val newContext = context.createConfigurationContext(config)

        FontManager.init(newContext)

        return newContext
    }

    fun saveLanguage(context: Context, language: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit { putString(KEY_LANGUAGE, AppLanguage.normalize(language)) }
    }

    fun getCurrentLanguage(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val stored = prefs.getString(KEY_LANGUAGE, DEFAULT_LANGUAGE)
        val supported = AppLanguage.normalize(stored)
        if (stored != supported) {
            prefs.edit { putString(KEY_LANGUAGE, supported) }
        }
        return supported
    }

    fun getResourceLocale(
        type: String,
        res: (Int) -> Unit
    ) {
        when (type) {
            "en" -> {
                res(R.drawable.ic_logo_uk)
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

