package com.desynatadmin.ui.home.subscriptionPlan.response


import com.google.gson.annotations.SerializedName

data class AddPlanRequest(
    @SerializedName("commission")
    val commission: Int,
    @SerializedName("country_id")
    val countryId: Int,
    @SerializedName("duration")
    val duration: Int,
    @SerializedName("duration_type")
    val durationType: Int,
    @SerializedName("no_of_highlights")
    val noOfHighlights: Int,
    @SerializedName("no_of_products")
    val noOfProducts: Int,
    @SerializedName("no_of_videos")
    val noOfVideos: Int,
    @SerializedName("plan_name_arabic")
    val planNameArabic: String,
    @SerializedName("plan_name_english")
    val planNameEnglish: String,
    @SerializedName("price")
    val price: Int,
    @SerializedName("stories")
    val stories: Int
)