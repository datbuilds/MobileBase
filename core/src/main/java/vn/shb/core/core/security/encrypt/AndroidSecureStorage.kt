package vn.shb.core.core.security.encrypt

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import vn.shb.core.utils.DeviceManager

class AndroidSecureStorage(private val context: Context, private val deviceManager: DeviceManager) {
    companion object {
        private const val PREFS_NAME = "encrypted_prefs"
        private const val EXPIRE_TIME = "EXPIRE_TIME"
        private const val FIRST_INSTALL = "FIRST_INSTALL"
        private const val FIRST_TIME_OPEN_APP = "FIRST_TIME_OPEN_APP"
        private const val FCM_TOKEN = "FCM_TOKEN"
        private const val DEVICE_ID = "device_id"
        private const val CHANGE_PASSWORD = "CHANGE_PASSWORD"
        private const val USER_INFO = "USER_INFO"
        private const val BACKGROUND_LOGIN = "BACKGROUND_LOGIN"
        private const val AUTH_TOKEN = "AUTH_TOKEN"
        private const val REFRESH_TOKEN = "REFRESH_TOKEN"
        private const val REFRESH_TOKEN_FAIL = "REFRESH_TOKEN_FAIL"
        private const val AUTH_TOKEN_WSO2 = "AUTH_TOKEN_WSO2"

        private const val REFRESH_TOKEN_WSO2 = "REFRESH_TOKEN_WSO2"
        private const val EXPIRE_TIME_WSO2 = "EXPIRE_TIME_WSO2"
        private const val REFRESH_TOKEN_FAIL_WSO2 = "REFRESH_TOKEN_FAIL_WSO2"
    }

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val preferences = EncryptedSharedPreferences.create(
        context,
        PREFS_NAME,
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    private inline fun <reified T> putPreference(key: String, value: T) {
        with(preferences.edit()) {
            when (value) {
                is String -> putString(key, value)
                is Int -> putInt(key, value)
                is Boolean -> putBoolean(key, value)
                else -> throw IllegalArgumentException("Unsupported type")
            }.apply()
        }
    }

    private inline fun <reified T> getPreference(key: String, defaultValue: T): T {
        return with(preferences) {
            when (T::class) {
                String::class -> getString(key, defaultValue as? String ?: "") as T
                Int::class -> getInt(key, defaultValue as? Int ?: -1) as T
                Boolean::class -> getBoolean(key, defaultValue as? Boolean ?: false) as T
                else -> throw IllegalArgumentException("Unsupported type")
            }
        }
    }

    /**
     * RefreshTokenFail
     */

    fun setTokenInvalid(isFail: Boolean) = putPreference(REFRESH_TOKEN_FAIL, isFail)
    fun isTokenInvalid() = getPreference(REFRESH_TOKEN_FAIL, false)

    /**
     * change password
     */
    fun setChangePassword(value: Boolean) = putPreference(CHANGE_PASSWORD, value)
    fun isChangePassword() = getPreference(CHANGE_PASSWORD, false)

    /**
     * UserInfo
     */
    fun setUserLog(user: String) = putPreference(USER_INFO, user)
    fun getUserLog() = getPreference(USER_INFO, "")

    fun setPathAvatarUser(key: String, path: String) = putPreference(key, path)

    fun getPathAvatarUser(key: String) = getPreference(key, "")

    /**
     * ---------------------End user--->
     */


    fun setExpireTime(data: Int) = putPreference(EXPIRE_TIME, data)
    fun getExpireTime() = getPreference(EXPIRE_TIME, 5)

    /**
     * Background Login
     */
    fun setBackgroundLogin(data: String) = putPreference(BACKGROUND_LOGIN, data)
    fun getBackgroundLogin() = getPreference(BACKGROUND_LOGIN, "")

    /**
     * Token
     */
    fun setToken(token: String) = putPreference(AUTH_TOKEN, token)
    fun getToken() = getPreference(AUTH_TOKEN, "")

    fun setRfToken(rfToken: String) = putPreference(REFRESH_TOKEN, rfToken)
    fun getRfToken() = getPreference(REFRESH_TOKEN, "")

    fun setExpireTimeWso2(data: Int) = putPreference(EXPIRE_TIME_WSO2, data)
    fun getExpireTimeWso2() = getPreference(EXPIRE_TIME_WSO2, 5)

    fun setTokenWso2(token: String) = putPreference(AUTH_TOKEN_WSO2, token)
    fun getTokenWso2() = getPreference(AUTH_TOKEN_WSO2, "")
    fun setRfTokenWso2(rfToken: String) = putPreference(REFRESH_TOKEN_WSO2, rfToken)
    fun getRfTokenWso2() = getPreference(REFRESH_TOKEN_WSO2, "")

    fun setTokenInvalidWso2(isFail: Boolean) = putPreference(REFRESH_TOKEN_FAIL_WSO2, isFail)
    fun isTokenInvalidWso2() = getPreference(REFRESH_TOKEN_FAIL_WSO2, false)

    /**
     * -------------------end token----
     */
    fun firstOpened(isFirst: Boolean = false) = putPreference(FIRST_TIME_OPEN_APP, isFirst)

    fun isFirstOpen(): Boolean {
        val isFirstOpenApp = getPreference(FIRST_INSTALL, false)
        return if (isFirstOpenApp) false else getPreference(FIRST_TIME_OPEN_APP, false)
    }

    fun setFcmToken(token: String) = putPreference(FCM_TOKEN, token)

    fun getFcmToken() = getPreference(FCM_TOKEN, "")

    fun getDeviceId() = getPreference(DEVICE_ID, newDeviceId())

    fun newDeviceId() = deviceManager.getDevicesIds() ?: ""

    fun setDeviceId(deviceId: String) = putPreference(DEVICE_ID, deviceId)

    private fun removeKey(key: String) {
        preferences.edit().remove(key).apply()
    }

    fun resetToken() {
        removeKey(AUTH_TOKEN)
        removeKey(REFRESH_TOKEN)
        setTokenInvalid(false)
        removeKey(AUTH_TOKEN_WSO2)
        removeKey(REFRESH_TOKEN_WSO2)
        setTokenInvalidWso2(false)
    }

    fun resetUser() {
//        removeKey(USER_INFO) // Lỗi 999 - User đăng nhập trên thiết bị khác
        removeKey(AUTH_TOKEN)
        removeKey(REFRESH_TOKEN)
        removeKey(AUTH_TOKEN_WSO2)
        removeKey(REFRESH_TOKEN_WSO2)
    }

}
