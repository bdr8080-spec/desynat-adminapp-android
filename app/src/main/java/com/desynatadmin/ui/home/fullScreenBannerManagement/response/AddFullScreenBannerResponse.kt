package com.desynatadmin.ui.home.fullScreenBannerManagement.response


import com.google.gson.annotations.SerializedName

data class AddFullScreenBannerResponse(
    @SerializedName("data")
    val `data`: GetFullScreenBannerResponse.Data.Data,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
)