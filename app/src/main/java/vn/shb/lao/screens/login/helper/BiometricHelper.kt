package vn.shb.lao.screens.login.helper

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyPermanentlyInvalidatedException
import android.security.keystore.KeyProperties
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

class BiometricHelper(private val activity: AppCompatActivity) {

    private val KEY_ALIAS = "biometric_key"

    fun isBiometricAvailable(): Boolean {
        val biometricManager = BiometricManager.from(activity)
        return biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG) == BiometricManager.BIOMETRIC_SUCCESS
    }

    fun createBiometricKey(): Boolean {
        return try {
            val keyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }

            if (keyStore.containsAlias(KEY_ALIAS)) {
                // Đã có key rồi, không tạo lại
                return true
            }

            val keyGenerator = KeyGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore"
            )

            val keySpec = KeyGenParameterSpec.Builder(
                KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            ).apply {
                setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                setUserAuthenticationRequired(true)
                setInvalidatedByBiometricEnrollment(true) // 🔥 Quan trọng!
            }.build()

            keyGenerator.init(keySpec)
            keyGenerator.generateKey()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun getCryptoObject(): BiometricPrompt.CryptoObject? {
        return try {
            val keyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
            val secretKey = keyStore.getKey(KEY_ALIAS, null) as SecretKey
            val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding")
            cipher.init(Cipher.ENCRYPT_MODE, secretKey)
            BiometricPrompt.CryptoObject(cipher)
        } catch (e: KeyPermanentlyInvalidatedException) {
            // 👉 Vân tay đã thay đổi
            null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun authenticate(
        activity: FragmentActivity,
        title: String = "Xác thực vân tay",
        subtitle: String = "Sử dụng vân tay đã đăng ký",
        negativeButtonText: String = "Hủy",
        onSuccess: () -> Unit,
        onFailed: (String) -> Unit,
        onChanged: () -> Unit // 🔥 Gọi khi phát hiện vân tay thay đổi
    ) {
        val executor = ContextCompat.getMainExecutor(activity)

        val prompt = BiometricPrompt(
            activity,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    onSuccess()
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    onFailed(errString.toString())
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    onFailed("Xác thực thất bại")
                }
            })

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
            .setSubtitle(subtitle)
            .setNegativeButtonText(negativeButtonText)
            .build()

        val cryptoObject = getCryptoObject()
        if (cryptoObject == null) {
            // Không thể lấy key => Vân tay đã thay đổi
            onChanged()
        } else {
            prompt.authenticate(promptInfo, cryptoObject)
        }
    }

    fun deleteKey() {
        try {
            val keyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
            if (keyStore.containsAlias(KEY_ALIAS)) {
                keyStore.deleteEntry(KEY_ALIAS)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * =================================
     */
    fun using() {
        val biometricHelper = BiometricHelper(activity)

        // 1. Tạo key lần đầu (gọi 1 lần khi đăng nhập lần đầu)
        biometricHelper.createBiometricKey()

        // 2. Gọi xác thực
        biometricHelper.authenticate(
            activity = activity,
            onSuccess = {
                Toast.makeText(activity, "Xác thực thành công", Toast.LENGTH_SHORT).show()
            },
            onFailed = { message ->
                Toast.makeText(activity, "Thất bại: $message", Toast.LENGTH_SHORT).show()
            },
            onChanged = {
                Toast.makeText(
                    activity,
                    "Vân tay đã thay đổi, yêu cầu đăng nhập lại!",
                    Toast.LENGTH_LONG
                ).show()
                // Gợi ý: gọi logout hoặc xóa dữ liệu nhạy cảm
            }
        )
    }
}

