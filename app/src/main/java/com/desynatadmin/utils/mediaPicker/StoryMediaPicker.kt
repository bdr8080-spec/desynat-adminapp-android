package com.desynatadmin.utils.mediaPicker

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File

class StoryMediaPicker(
    private val context: Context,
    private val imageCameraLauncher: ActivityResultLauncher<Uri>,
    private val imageGalleryLauncher: ActivityResultLauncher<String>,
    private val videoCameraLauncher: ActivityResultLauncher<Intent>,
    private val videoGalleryLauncher: ActivityResultLauncher<String>,
    private val permissionLauncher: ActivityResultLauncher<String>,
    private val callback: (Uri?, String) -> Unit
) {

    private var cameraUri: Uri? = null

    fun showCameraStoryPicker() {
        val options = arrayOf("Camera Photo", "Record Video")
        AlertDialog.Builder(context)
            .setTitle("Add Story")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> openCameraImage()
                    1 -> openCameraVideo()
                }
            }.show()
    }

//    fun showCameraStoryPicker() {
//        val options = arrayOf("Camera Photo", "Record Video","Gallery Photo", "Gallery Video")
//        AlertDialog.Builder(context)
//            .setTitle("Add Story")
//            .setItems(options) { _, which ->
//                when (which) {
//                    0 -> openCameraImage()
//                    1 -> openCameraVideo()
//                    2 -> openGalleryImage()
//                    3 -> openGalleryVideo()
//                }
//            }.show()
//    }


    private fun openCameraImage() {
        if (!checkCameraPermission()) return

        cameraUri = createUri("IMG", "jpg")

        imageCameraLauncher.launch(cameraUri!!)
    }


    /*
        private fun openCameraImage() {
            if (!checkCameraPermission()) return
            cameraUri = createUri("IMG", "jpg")
            cameraUri?.let { uri ->
                imageCameraLauncher.launch(uri)
            }
        }
    */

    private fun openCameraVideo() {
        if (!checkCameraPermission()) return

        cameraUri = createUri("VID", "mp4")

        val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE).apply {
            putExtra(MediaStore.EXTRA_OUTPUT, cameraUri)
            putExtra(MediaStore.EXTRA_DURATION_LIMIT, 30)
            putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1)

            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        }

        videoCameraLauncher.launch(intent)
    }


    /*private fun openCameraVideo() {
        if (!checkCameraPermission()) return

        cameraUri = createUri("VID", "mp4")

        val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE).apply {
            putExtra(MediaStore.EXTRA_OUTPUT, cameraUri)
            putExtra(MediaStore.EXTRA_DURATION_LIMIT, 30)
            putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1)
        }

        videoCameraLauncher.launch(intent)
    }
*/
    private fun openGalleryImage() {
        imageGalleryLauncher.launch("image/*")
    }

    private fun openGalleryVideo() {
        videoGalleryLauncher.launch("video/*")
    }

    fun onImageCameraResult(success: Boolean) {
        if (success) callback(cameraUri, "image")
        else callback(null, "")
        cameraUri = null
    }

    fun onVideoCameraResult(success: Boolean) {
        if (success) callback(cameraUri, "video")
        else callback(null, "")
        cameraUri = null
    }

    fun onImageGalleryResult(uri: Uri?) {
        callback(uri, "image")
    }

    fun onVideoGalleryResult(uri: Uri?) {
        callback(uri, "video")
    }

    fun onPermissionResult(granted: Boolean) {
        if (!granted) callback(null, "")
    }

    private fun checkCameraPermission(): Boolean {
        return if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) true
        else {
            permissionLauncher.launch(Manifest.permission.CAMERA)
            false
        }
    }

    private fun createUri(prefix: String, ext: String): Uri {
        val file = File(context.cacheDir, "${prefix}_${System.currentTimeMillis()}.$ext")
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )
    }
}

