package com.desynatadmin.ui.home.subscriptionPlan.response


import com.google.gson.annotations.SerializedName

data class GetAllPlanResponse(
    @SerializedName("data")
    val `data`: List<Data>,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
) {
    data class Data(
        @SerializedName("commission")
        val commission: Int,
        @SerializedName("commission_percentage")
        val commissionPercentage: Double,
        @SerializedName("country_id")
        val countryId: Int,
        @SerializedName("country_info")
        val countryInfo: CountryInfo,
        @SerializedName("created_at")
        val createdAt: String,
        @SerializedName("duration")
        val duration: Int,
        @SerializedName("duration_type")
        val durationType: Int,
        @SerializedName("new_duration")
        val newDuration: Int,
        @SerializedName("new_duration_type")
        val newDurationType: Int,

        @SerializedName("id")
        val id: Int,
        @SerializedName("no_of_employee")
        val noOfEmployee: Int,
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
        val price: Double,
        @SerializedName("discounted_price")
        val discountedPrice: Double,
        @SerializedName("status")
        val status: Int,
        @SerializedName("stories")
        val stories: Int,
        @SerializedName("updated_at")
        val updatedAt: String,

        @SerializedName("product_type")
        val productType: Int=1,//0-defined,1-unlimited
        @SerializedName("is_free_months")
        val isFreeMonths: Int=0, //0-no,1-yes
        @SerializedName("show_in_recommended_stores")
        val showInRecommendedStores: Int=0, //0-no,1-yes
        @SerializedName("no_of_free_months")
        val noOfFreeMonths: String? = null //if is_free_month is 1
    ) {
        data class CountryInfo(
            @SerializedName("created_at")
            val createdAt: String,
            @SerializedName("currency_code")
            val currencyCode: String,
            @SerializedName("currency_name")
            val currencyName: String,
            @SerializedName("currency_symbol")
            val currencySymbol: String,
            @SerializedName("dial_code")
            val dialCode: String,
            @SerializedName("exchange_rate_to_kwd")
            val exchangeRateToKwd: String,
            @SerializedName("flag")
            val flag: String,
            @SerializedName("flag1")
            val flag1: String,
            @SerializedName("id")
            val id: Int,
            @SerializedName("iso_code")
            val isoCode: String,
            @SerializedName("name")
            val name: String,
            @SerializedName("updated_at")
            val updatedAt: String
        )
    }
}