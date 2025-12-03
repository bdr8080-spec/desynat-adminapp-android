package com.desynatadmin.ui.home.banner.response

import com.google.gson.annotations.SerializedName

data class GetBannerPricePerDayResponse(
    @SerializedName("data")
    val data: ArrayList<Data>,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
) {
    data class Data(
        @SerializedName("country_id")
        val countryId: Int,
        @SerializedName("created_at")
        val createdAt: String,
        @SerializedName("id")
        val id: Int,
        @SerializedName("per_day_price")
        val perDayPrice: Int,
        @SerializedName("type")
        val type: Int,
        @SerializedName("updated_at")
        val updatedAt: String
    )
}
