package com.desynatadmin.ui.home.deliveryBoyManagement.response


import com.google.gson.annotations.SerializedName

data class GetKuwaitZoneResponse(
    @SerializedName("data")
    val `data`: List<Data>,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
) {
    data class Data(
        @SerializedName("city_id")
        val cityId: Int,
        @SerializedName("coordinates")
        val coordinates: List<Coordinate>,
        @SerializedName("country_id")
        val countryId: Int,
        @SerializedName("created_at")
        val createdAt: String,
        @SerializedName("id")
        val id: Int,
        @SerializedName("name_arabic")
        val nameArabic: String,
        @SerializedName("name_english")
        val nameEnglish: String,
        @SerializedName("status")
        val status: Int,
        @SerializedName("updated_at")
        val updatedAt: String
    ) {
        data class Coordinate(
            @SerializedName("lat")
            val lat: Double,
            @SerializedName("lng")
            val lng: Double
        )
    }
}