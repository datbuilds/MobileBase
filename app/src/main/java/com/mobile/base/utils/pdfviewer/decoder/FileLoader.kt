package com.mobile.base.utils.pdfviewer.decoder

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.annotation.RawRes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileNotFoundException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import javax.net.ssl.SSLHandshakeException

class FileLoader {

    companion object {

        private const val FILE_NAME = "temp.pdf"

        private fun getTempFile(context: Context): File {
            return File(context.cacheDir, FILE_NAME)
        }

        suspend fun loadFile(context: Context, @RawRes resId: Int): File {
            return withContext(Dispatchers.IO) {
                val input = context.resources
                    .openRawResource(
                        context.resources
                            .getIdentifier(
                                context.resources.getResourceName(resId),
                                context.resources.getResourceTypeName(resId),
                                context.resources.getResourcePackageName(resId)
                            )
                    )

                LoadFileDelegate(input = input, file = getTempFile(context)).doLoadFile()
            }
        }

        suspend fun loadFile(context: Context, url: String): File = withContext(Dispatchers.IO) {
            try {
                val connection = (URL(url).openConnection() as HttpURLConnection).apply {
                    connectTimeout = 10000
                    readTimeout = 10000
                    setRequestProperty("User-Agent", "Mozilla/5.0")
                }

                connection.inputStream.use { input ->
                    val file = getTempFile(context)
                    LoadFileDelegate(input = input, file = file).doLoadFile()
                    return@withContext file
                }
            } catch (e: SSLHandshakeException) {
                Log.e("loadFile", "SSL Error: ${e.message}")
                throw e
            } catch (e: Exception) {
                Log.e("loadFile", "Download error: ${e.message}")
                throw e
            }
        }


        suspend fun loadFile(context: Context, input: InputStream): File {
            return withContext(Dispatchers.IO) {
                LoadFileDelegate(input = input, file = getTempFile(context)).doLoadFile()
            }
        }

        suspend fun loadFile(context: Context,  uri: Uri): File {
            return withContext(Dispatchers.IO) {
                val input = context.contentResolver.openInputStream(uri)
                input?.let {
                    LoadFileDelegate(input = input, file = getTempFile(context)).doLoadFile()
                } ?: throw FileNotFoundException()
            }
        }
    }
}