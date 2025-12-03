package com.desynatadmin.ui.home.banner.response


import com.google.gson.annotations.SerializedName

data class UpdateBannerPriceResponse(
    @SerializedName("data")
    val `data`: GetBannerPricePerDayResponse.Data,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
)