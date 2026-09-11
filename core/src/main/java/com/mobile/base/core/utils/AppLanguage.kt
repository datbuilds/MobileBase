package com.mobile.base.core.utils

import java.util.Locale

object AppLanguage {
    const val DEFAULT = "en"

    fun normalize(language: String?): String = when (language?.trim()?.lowercase(Locale.ROOT)) {
        "vi" -> "vi"
        else -> DEFAULT
    }
}
