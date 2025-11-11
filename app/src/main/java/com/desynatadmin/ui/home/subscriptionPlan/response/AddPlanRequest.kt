package com.desynatadmin.ui.home.subscriptionPlan.response


import com.google.gson.annotations.SerializedName

data class AddPlanRequest(
    @SerializedName("commission")
    val commission: String,
    @SerializedName("country_id")
    val countryId: String,
    @SerializedName("duration")
    val duration: String,
    @SerializedName("duration_type")
    val durationType: String,
    @SerializedName("no_of_highlights")
    val noOfHighlights: String,
    @SerializedName("no_of_products")
    val noOfProducts: String,
    @SerializedName("no_of_videos")
    val noOfVideos: String,
    @SerializedName("plan_name_arabic")
    val planNameArabic: String,
    @SerializedName("plan_name_english")
    val planNameEnglish: String,
    @SerializedName("price")
    val price: String,
    @SerializedName("stories")
    val stories: String
)