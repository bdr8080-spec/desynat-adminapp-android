package com.desynatadmin.ui.home.brandManagement.response


import com.google.gson.annotations.SerializedName

data class FileUploadResponse(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
) {
    data class Data(
        @SerializedName("url")
        val url: String
    )
}