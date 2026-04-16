package com.desynatadmin.ui.home.fullScreenBannerManagement.response


import com.google.gson.annotations.SerializedName

data class AddPromotionalBannerRequest(
   /* @SerializedName("country_id")
    val countryId: String,*/
    @SerializedName("banner_id")
    val bannerId: String?=null,
    @SerializedName("category")
    val category: String?=null,
    @SerializedName("subcatgory")
    val subcategory: String?=null,
    @SerializedName("banner_position")
    val bannerPosition: String,
    @SerializedName("end_date")
    val endDate: String,
    @SerializedName("start_date")
    val startDate: String,
    @SerializedName("total_days")
    val totalDays: String,
    @SerializedName("upload_banner")
    val uploadBanner: String
)