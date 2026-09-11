package com.mobile.base.core.utils

import org.junit.Assert.assertEquals
import org.junit.Test

class AppLanguageTest {
    @Test
    fun removedAndUnsupportedLanguagesFallBackToEnglish() {
        listOf(null, "", "km", "KM", "fr").forEach {
            assertEquals("en", AppLanguage.normalize(it))
        }
    }

    @Test
    fun retainedLanguagesArePreserved() {
        assertEquals("en", AppLanguage.normalize("en"))
        assertEquals("vi", AppLanguage.normalize("vi"))
        assertEquals("vi", AppLanguage.normalize(" VI "))
    }
}
