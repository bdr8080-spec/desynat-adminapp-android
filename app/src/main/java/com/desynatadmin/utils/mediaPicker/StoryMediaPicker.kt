package com.desynatadmin.utils.mediaPicker

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File

class StoryMediaPicker(
    private val context: Context,
    private val imageCameraLauncher: ActivityResultLauncher<Uri>,
    private val imageGalleryLauncher: ActivityResultLauncher<String>,
    private val videoCameraLauncher: ActivityResultLauncher<Uri>,
    private val videoGalleryLauncher: ActivityResultLauncher<String>,
    private val permissionLauncher: ActivityResultLauncher<String>,
    private val callback: (Uri?, String) -> Unit // image / video
) {

    private var cameraUri: Uri? = null

    fun showStoryPicker() {
        val options = arrayOf(
            "Camera Photo",
            "Record Video",
            "Gallery Image",
            "Gallery Video"
        )

        AlertDialog.Builder(context)
            .setTitle("Add Story")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> openCameraImage()
                    1 -> openCameraVideo()
                    2 -> openGalleryImage()
                    3 -> openGalleryVideo()
                }
            }.show()
    }

    fun showCameraStoryPicker() {
        val options = arrayOf(
            "Camera Photo",
            "Record Video",
        )

        AlertDialog.Builder(context)
            .setTitle("Add Story")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> openCameraImage()
                    1 -> openCameraVideo()
                }
            }.show()
    }

    private fun openCameraImage() {
        if (checkCameraPermission()) {
            cameraUri = createUri("IMG", "jpg")
            cameraUri?.let { uri ->
                imageCameraLauncher.launch(uri)
            }
        }
    }

    private fun openGalleryImage() {
        imageGalleryLauncher.launch("image/*")
    }

    private fun openCameraVideo() {
        if (checkCameraPermission()) {
            cameraUri = createUri("VID", "mp4")
            cameraUri?.let { uri ->
                videoCameraLauncher.launch(uri)
            }
        }
    }

    private fun openGalleryVideo() {
        videoGalleryLauncher.launch("video/*")
    }

    private fun checkCameraPermission(): Boolean {
        return if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            true
        } else {
            permissionLauncher.launch(Manifest.permission.CAMERA)
            false
        }
    }

    fun onImageCameraResult(success: Boolean) {
        cameraUri?.let { uri ->
            if (success) callback(uri, "image") else callback(null, "")
        }
    }

    fun onImageGalleryResult(uri: Uri?) {
        callback(uri, "image")
    }

    fun onVideoCameraResult(success: Boolean) {
        cameraUri?.let { uri ->
            if (success) callback(uri, "video") else callback(null, "")
        }
    }

    fun onVideoGalleryResult(uri: Uri?) {
        callback(uri, "video")
    }

    fun onPermissionResult(granted: Boolean) {
        if (!granted) callback(null, "")
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
