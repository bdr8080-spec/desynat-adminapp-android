package com.desynatadmin.ui.home.promoCodeManagement.response


import com.google.gson.annotations.SerializedName

data class AddPromoCodeRequest(
    @SerializedName("applicable_for")
    val applicableFor: String,//1-for first order,2-for every order
    @SerializedName("country_id")
    val countryId: String,
    @SerializedName("description_arabic")
    val descriptionArabic: String,
    @SerializedName("description_english")
    val descriptionEnglish: String,
    @SerializedName("discount_percent")
    val discountPercent: String,
    @SerializedName("end_date")
    val endDate: String,
    @SerializedName("min_order_value")
    val minOrderValue: String,
    @SerializedName("name_arabic")
    val nameArabic: String,
    @SerializedName("name_english")
    val nameEnglish: String,
    @SerializedName("start_date")
    val startDate: String,
    @SerializedName("upto_discount")
    val uptoDiscount: String,
    @SerializedName("promocode_id")
    val promocodeId: String?=null
)