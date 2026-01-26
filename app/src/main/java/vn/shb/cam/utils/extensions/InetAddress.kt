package vn.shb.cam.utils.extensions

import java.net.InetAddress
import java.net.NetworkInterface
import java.util.Collections
import java.util.Locale

fun getIPAddress(useIPv4: Boolean): String {
    try {
        val interfaces: List<NetworkInterface> =
            Collections.list(NetworkInterface.getNetworkInterfaces())

        for (inf in interfaces) {
            val adders: List<InetAddress> = Collections.list(inf.inetAddresses)

            for (adder in adders) {
                if (!adder.isLoopbackAddress) {
                    val sAdder = adder.hostAddress
                    val isIPv4 = sAdder.indexOf(':') < 0

                    if (useIPv4 == isIPv4) {
                        val denim = sAdder.indexOf('%')
                        return if (denim < 0) sAdder.uppercase(Locale.getDefault()) else sAdder.substring(
                            0,
                            denim
                        ).uppercase(Locale.getDefault())
                    }
                }
            }
        }
    } catch (ex: Exception) {
        ex.printStackTrace()
    }
    return ""
}
