package vn.shb.lao.utils.extensions

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.telephony.TelephonyManager

object NetworkUtils {

    @SuppressLint("MissingPermission")
    @Suppress("DEPRECATION")
    fun getNetworkType(context: Context): String {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val networkCapabilities = connectivityManager.activeNetwork ?: return "OFFLINE"
            val actNw =
                connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return "OFFLINE"
            return when {
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
//                    val w = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
//                    val wifi = w.connectionInfo.ssid
                    return "WIFI"
                }

                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                    return "MOBILE"
                }

                actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> return "ETHERNET"
                else -> "UNKNOWN"
            }
        } else {
            connectivityManager.run {
                connectivityManager.activeNetworkInfo?.run {
                    return when (type) {
                        ConnectivityManager.TYPE_WIFI -> return "WIFI"
                        ConnectivityManager.TYPE_MOBILE -> return "MOBILE"
                        ConnectivityManager.TYPE_ETHERNET -> return "ETHERNET"
                        else -> "UNKNOWN"
                    }
                }
            }
        }

        return "OFFLINE"
    }

    fun getNetworkProvider(context: Context) =
        (context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager).networkOperatorName

}