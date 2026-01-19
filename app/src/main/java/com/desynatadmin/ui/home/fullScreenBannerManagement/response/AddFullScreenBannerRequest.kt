package com.desynatadmin.ui.home.fullScreenBannerManagement.response


import com.google.gson.annotations.SerializedName

data class AddFullScreenBannerRequest(
    @SerializedName("country_id")
    val countryId: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("end_date")
    val endDate: String,
    @SerializedName("image")
    val image: String,
    @SerializedName("start_date")
    val startDate: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("banner_id")
    val bannerId: String?=null
)