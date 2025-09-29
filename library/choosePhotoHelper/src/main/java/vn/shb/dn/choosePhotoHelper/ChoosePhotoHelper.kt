@file:Suppress("unused")

package vn.shb.dn.choosePhotoHelper

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import vn.shb.dn.choosePhotoHelper.callback.ChoosePhotoCallback
import vn.shb.dn.choosePhotoHelper.utils.grantedUri
import vn.shb.dn.choosePhotoHelper.utils.hasPermissions
import vn.shb.dn.choosePhotoHelper.utils.modifyOrientationSuspending
import vn.shb.dn.choosePhotoHelper.utils.pathFromUri
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * @author aminography
 */
class ChoosePhotoHelper private constructor(
    private val activity: Activity?,
    private val fragment: Fragment?,
    private val outputType: OutputType,
    private val callback: ChoosePhotoCallback<*>,
    private var filePath: String? = null,
    private var cameraFilePath: String? = null,
    private val alwaysShowRemoveOption: Boolean? = null,
) {
    private val context get() = activity ?: fragment?.requireContext()!!

    // Launcher cho camera
    private val takePhotoLauncher = registerForIntentResult { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            filePath = cameraFilePath
            deliverResult(filePath)
        }
    }

    // Launcher cho gallery
    private val pickPhotoLauncher = registerForIntentResult { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uri = result.data?.data
            filePath = uri?.let { pathFromUri(context, it) }
            deliverResult(filePath)
        }
    }

    // Launcher xin nhiều quyền (dùng cho cả camera và gallery)
    private val permissionLauncher = registerForPermissionResult { grants ->
        val allGranted = grants.values.all { it } // check tất cả quyền
        if (allGranted) {
            when (pendingAction) {
                ActionProfile.CAMERA -> startCameraInternal()
                ActionProfile.GALLERY -> startGalleryInternal()
                else -> {}
            }
        } else {
            // Nếu user chọn "Deny", kiểm tra có quyền nào bị "Don't ask again" không
            val shouldShowRationale = grants.keys.any { perm ->
                fragment?.shouldShowRequestPermissionRationale(perm)
                    ?: (activity as? ComponentActivity)?.shouldShowRequestPermissionRationale(perm)
                    ?: false
            }

            if (shouldShowRationale) {
                showRationalePopup()
            }
        }
    }


    private var pendingAction: ActionProfile? = null

    enum class ActionProfile { CAMERA, GALLERY }

    // Cho camera / gallery (Intent)
    private fun registerForIntentResult(
        callback: (ActivityResult) -> Unit
    ): ActivityResultLauncher<Intent> {
        val contract = ActivityResultContracts.StartActivityForResult()
        return when {
            fragment != null -> fragment.registerForActivityResult(contract, callback)
            activity is ComponentActivity -> activity.registerForActivityResult(contract, callback)
            else -> throw IllegalStateException("Must be used with Fragment or ComponentActivity")
        }
    }

    private fun registerForPermissionResult(
        callback: (Map<String, Boolean>) -> Unit
    ): ActivityResultLauncher<Array<String>> {
        val contract = ActivityResultContracts.RequestMultiplePermissions()
        return fragment?.registerForActivityResult(contract, callback)
            ?: (activity as? ComponentActivity)?.registerForActivityResult(contract, callback)
            ?: throw IllegalStateException("Must be used with Fragment or ComponentActivity")
    }


    fun takePhoto() {
        pendingAction = ActionProfile.CAMERA
        if (hasPermissions(context, *TAKE_PHOTO_PERMISSIONS)) {
            startCameraInternal()
        } else {
            permissionLauncher.launch(TAKE_PHOTO_PERMISSIONS)
        }
    }

    fun chooseFromGallery() {
        pendingAction = ActionProfile.GALLERY
        if (hasPermissions(context, *PICK_PHOTO_PERMISSIONS)) {
            startGalleryInternal()
        } else {
            permissionLauncher.launch(PICK_PHOTO_PERMISSIONS)
        }
    }

    // Xử lý camera/gallery
    private fun startCameraInternal() {
        val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val file = File.createTempFile(
            "JPEG_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())}",
            ".jpg",
            storageDir
        )
        cameraFilePath = file.absolutePath

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            putExtra(MediaStore.EXTRA_OUTPUT, file.grantedUri(context))
            addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        takePhotoLauncher.launch(intent)
    }

    private fun startGalleryInternal() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        pickPhotoLauncher.launch(Intent.createChooser(intent, "Choose a Photo"))
    }

    // Trả kết quả
    private fun deliverResult(path: String?) {
        path?.let {
            @Suppress("UNCHECKED_CAST")
            when (outputType) {
                OutputType.FILE_PATH -> (callback as ChoosePhotoCallback<String>).onChoose(it)
                OutputType.URI -> (callback as ChoosePhotoCallback<Uri>)
                    .onChoose(Uri.fromFile(File(it)))

                OutputType.BITMAP -> {
                    CoroutineScope(Dispatchers.IO).launch {
                        var bitmap = BitmapFactory.decodeFile(it)
                        try {
                            bitmap = modifyOrientationSuspending(bitmap, it)
                        } catch (_: IOException) {
                        }
                        withContext(Dispatchers.Main) {
                            (callback as ChoosePhotoCallback<Bitmap>).onChoose(bitmap)
                        }
                    }
                }
            }
        }
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE_TAKE_PHOTO -> {
                    filePath = cameraFilePath
                }

                REQUEST_CODE_PICK_PHOTO -> {
                    filePath = pathFromUri(
                        context,
                        Uri.parse(intent?.data?.toString())
                    )
                }
            }
            filePath?.let {
                @Suppress("UNCHECKED_CAST")
                when (outputType) {
                    OutputType.FILE_PATH -> {
                        (callback as ChoosePhotoCallback<String>).onChoose(it)
                    }

                    OutputType.URI -> {
                        val uri = Uri.fromFile(File(it))
                        (callback as ChoosePhotoCallback<Uri>).onChoose(uri)
                    }

                    OutputType.BITMAP -> {
                        CoroutineScope(Dispatchers.IO).launch {
                            var bitmap = BitmapFactory.decodeFile(it)
                            try {
                                bitmap = modifyOrientationSuspending(bitmap, it)
                            } catch (e: IOException) {
                                e.printStackTrace()
                            }
                            withContext(Dispatchers.Main) {
                                (callback as ChoosePhotoCallback<Bitmap>).onChoose(bitmap)
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Call this method in Activity#onSaveInstanceState or Fragment#onSaveInstanceState
     * to save ChoosePhotoHelper state that can be later restored by withState(Bundle)
     */
    fun onSaveInstanceState(outState: Bundle) {
        outState.putString(FILE_PATH, filePath)
        outState.putString(CAMERA_FILE_PATH, cameraFilePath)
    }

    private fun showRationalePopup() {
        AlertDialog.Builder(context).apply {
            setMessage(R.string.required_permission_is_not_granted)
            setNegativeButton(R.string.action_close, null)
            setPositiveButton(
                R.string.action_setting
            ) { dialog, _ ->
                dialog.dismiss()
                val intent =
                    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri: Uri = Uri.fromParts("package", context.packageName, null)
                intent.data = uri
                context.startActivity(intent)
            }
            val dialog = create()
            dialog.show()
        }
    }

    enum class OutputType {
        FILE_PATH,
        URI,
        BITMAP
    }

    abstract class BaseRequestBuilder<T> internal constructor(
        private val activity: Activity?,
        private val fragment: Fragment?,
        private val outputType: OutputType
    ) {

        private var filePath: String? = null
        private var cameraFilePath: String? = null
        private var alwaysShowRemoveOption: Boolean? = null


        /**
         * Use this method to restore the state previously saved on onSaveInstanceState
         */
        fun withState(state: Bundle?): BaseRequestBuilder<T> {
            filePath = state?.getString(FILE_PATH)
            cameraFilePath = state?.getString(CAMERA_FILE_PATH)
            return this
        }

        fun alwaysShowRemoveOption(show: Boolean): BaseRequestBuilder<T> {
            alwaysShowRemoveOption = show
            return this
        }

        fun build(callback: ChoosePhotoCallback<T>): ChoosePhotoHelper {
            return ChoosePhotoHelper(
                activity,
                fragment,
                outputType,
                callback,
                filePath,
                cameraFilePath,
                alwaysShowRemoveOption
            )
        }
    }

    class FilePathRequestBuilder internal constructor(
        activity: Activity?,
        fragment: Fragment?,
    ) : BaseRequestBuilder<String>(activity, fragment, OutputType.FILE_PATH)

    class UriRequestBuilder internal constructor(
        activity: Activity?,
        fragment: Fragment?,
    ) : BaseRequestBuilder<Uri>(activity, fragment, OutputType.URI)

    class BitmapRequestBuilder internal constructor(
        activity: Activity?,
        fragment: Fragment?,
    ) : BaseRequestBuilder<Bitmap>(activity, fragment, OutputType.BITMAP)

    class RequestBuilder(
        private val activity: Activity? = null,
        private val fragment: Fragment? = null,
    ) {

        fun asFilePath(): FilePathRequestBuilder {
            return FilePathRequestBuilder(activity, fragment)
        }

        fun asUri(): UriRequestBuilder {
            return UriRequestBuilder(activity, fragment)
        }

        fun asBitmap(): BitmapRequestBuilder {
            return BitmapRequestBuilder(activity, fragment)
        }
    }

    companion object {

        private const val KEY_TITLE = "title"
        private const val KEY_ICON = "icon"

        private const val CAMERA_MAX_FILE_SIZE_BYTE = 2 * 1024 * 1024
        private const val REQUEST_CODE_TAKE_PHOTO = 101
        private const val REQUEST_CODE_PICK_PHOTO = 102

        const val REQUEST_CODE_TAKE_PHOTO_PERMISSION = 103
        private val TAKE_PHOTO_PERMISSIONS = if (Build.VERSION.SDK_INT <= 28) arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) else arrayOf(
            Manifest.permission.CAMERA
        )

        private const val REQUEST_CODE_PICK_PHOTO_PERMISSION = 104
        private val PICK_PHOTO_PERMISSIONS: Array<String> =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
            } else {
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
            }


        private const val FILE_PATH = "filePath"
        private const val CAMERA_FILE_PATH = "cameraFilePath"

        @JvmStatic
        fun with(activity: Activity): RequestBuilder =
            RequestBuilder(activity = activity)

        @JvmStatic
        fun with(fragment: Fragment): RequestBuilder =
            RequestBuilder(fragment = fragment)

    }

}
