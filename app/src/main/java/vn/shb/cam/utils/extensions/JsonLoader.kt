package vn.shb.cam.utils.extensions

import android.content.Context
import java.io.IOException

class JsonLoader {
    fun loadJsonFromRaw(context: Context, resourceId: Int): String? {
        var json: String? = null
        try {
            val resources = context.resources
            val inputStream = resources.openRawResource(resourceId)
            val buffer = ByteArray(inputStream.available())
            inputStream.read(buffer)
            inputStream.close()
            json = String(buffer, Charsets.UTF_8)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return json
    }
}
