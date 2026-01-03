package com.desynatadmin.ui.home.bannerRequestManagement.response


import com.google.gson.annotations.SerializedName

data class UpdateBannerRequestStatusResponse(
    @SerializedName("data")
    val `data`: GetBannerRequestResponse.Data.Data,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
)