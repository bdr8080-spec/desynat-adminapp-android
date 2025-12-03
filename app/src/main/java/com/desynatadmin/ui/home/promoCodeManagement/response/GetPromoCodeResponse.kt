package com.desynatadmin.ui.home.promoCodeManagement.response

import com.google.gson.annotations.SerializedName

data class GetPromoCodeResponse(
    @SerializedName("data")
    val data: ArrayList<Data>,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
)  {
    data class Data(
        @SerializedName("applicable_for")
        val applicableFor: Int,
        @SerializedName("country_id")
        val countryId: Int,
        @SerializedName("created_at")
        val createdAt: String,
        @SerializedName("description_arabic")
        val descriptionArabic: String,
        @SerializedName("description_english")
        val descriptionEnglish: String,
        @SerializedName("discount_percent")
        val discountPercent: String,
        @SerializedName("end_date")
        val endDate: String,
        @SerializedName("id")
        val id: Int,
        @SerializedName("min_order_value")
        val minOrderValue: String,
        @SerializedName("name_arabic")
        val nameArabic: String,
        @SerializedName("name_english")
        val nameEnglish: String,
        @SerializedName("start_date")
        val startDate: String,
        @SerializedName("status")
        val status: Int,
        @SerializedName("updated_at")
        val updatedAt: String,
        @SerializedName("upto_discount")
        val uptoDiscount: String
    )
}