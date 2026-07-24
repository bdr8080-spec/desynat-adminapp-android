package com.desynatadmin.ui.home.subscriptionPlan.response


import com.google.gson.annotations.SerializedName

data class AddPlanRequest(
    @SerializedName("commission")
    val commission: String,
    @SerializedName("commission_percentage")
    val commission_percentage: String,
    @SerializedName("country_id")
    val countryId: String,
    @SerializedName("duration")
    val duration: String,
    @SerializedName("duration_type")
    val durationType: String,
    @SerializedName("no_of_highlights")
    val noOfHighlights: String,
    @SerializedName("no_of_employee")
    val noOfEmployee: String,
    @SerializedName("no_of_products")//required if product_type is 0
    val noOfProducts: String? = null,
    @SerializedName("no_of_videos")
    val noOfVideos: String,
    @SerializedName("plan_name_arabic")
    val planNameArabic: String,
    @SerializedName("plan_name_english")
    val planNameEnglish: String,
    @SerializedName("price")
    val price: String,
    @SerializedName("stories")
    val stories: String,
    @SerializedName("plan_id")
    val plan_id: String? = null,
    @SerializedName("discounted_price")
    val discounted_price: String? = null,
    @SerializedName("new_duration")
    val new_duration: String? = null,
    @SerializedName("new_duration_type")
    val new_duration_type: String? = null,

    @SerializedName("product_type")
    val productType: String? = null,//0-defined,1-unlimited
    @SerializedName("is_free_months")
    val isFreeMonths: String? = null, //0-no,1-yes
    @SerializedName("is_recommended_store")
    val showInRecommendedStores: String? = null, //0-no,1-yes
    @SerializedName("no_of_free_months")
    val noOfFreeMonths: String? = null //if is_free_month is 1
)