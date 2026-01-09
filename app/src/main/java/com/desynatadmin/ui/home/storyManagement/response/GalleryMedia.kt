package com.desynatadmin.ui.home.storyManagement.response

import android.net.Uri

sealed class GalleryMedia {
    data class Image(val uri: Uri) : GalleryMedia()
    data class Video(val uri: Uri) : GalleryMedia()
}