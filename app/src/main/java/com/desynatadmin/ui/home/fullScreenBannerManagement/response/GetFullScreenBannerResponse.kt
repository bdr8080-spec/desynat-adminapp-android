package com.desynatadmin.ui.home.fullScreenBannerManagement.response


import com.google.gson.annotations.SerializedName

data class GetFullScreenBannerResponse(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
) {
    data class Data(
        @SerializedName("current_page")
        val currentPage: Int,
        @SerializedName("data")
        val `data`: List<Data>,
        @SerializedName("first_page_url")
        val firstPageUrl: String,
        @SerializedName("from")
        val from: Int,
        @SerializedName("last_page")
        val lastPage: Int,
        @SerializedName("last_page_url")
        val lastPageUrl: String,
        @SerializedName("links")
        val links: List<Link>,
        @SerializedName("next_page_url")
        val nextPageUrl: Any,
        @SerializedName("path")
        val path: String,
        @SerializedName("per_page")
        val perPage: Int,
        @SerializedName("prev_page_url")
        val prevPageUrl: Any,
        @SerializedName("to")
        val to: Int,
        @SerializedName("total")
        val total: Int
    ) {
        data class Data(
            @SerializedName("country_id")
            val countryId: Int,
            @SerializedName("country_info")
            val countryInfo: CountryInfo,
            @SerializedName("created_at")
            val createdAt: String,
            @SerializedName("end_date")
            val endDate: String,
            @SerializedName("id")
            val id: Int,
            @SerializedName("image")
            val image: String,
            @SerializedName("start_date")
            val startDate: String,
            @SerializedName("status")
            val status: Int,
            @SerializedName("title")
            val title: String,
            @SerializedName("type")
            val type: String,
            @SerializedName("updated_at")
            val updatedAt: String
        ) {
            data class CountryInfo(
                @SerializedName("code")
                val code: String,
                @SerializedName("currency_code")
                val currencyCode: String,
                @SerializedName("currency_name")
                val currencyName: String,
                @SerializedName("currency_symbol")
                val currencySymbol: String,
                @SerializedName("dial_code")
                val dialCode: String,
                @SerializedName("exchange_rate_to_usd")
                val exchangeRateToUsd: String,
                @SerializedName("flag")
                val flag: String,
                @SerializedName("flag1")
                val flag1: String,
                @SerializedName("id")
                val id: Int,
                @SerializedName("iso_code")
                val isoCode: String,
                @SerializedName("iso_code_number")
                val isoCodeNumber: String,
                @SerializedName("name")
                val name: String,
                @SerializedName("name_ar")
                val nameAr: String
            )
        }

        data class Link(
            @SerializedName("active")
            val active: Boolean,
            @SerializedName("label")
            val label: String,
            @SerializedName("url")
            val url: String
        )
    }
}