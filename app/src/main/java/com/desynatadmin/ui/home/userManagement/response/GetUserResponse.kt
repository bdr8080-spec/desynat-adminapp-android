package com.desynatadmin.ui.home.userManagement.response


import com.google.gson.annotations.SerializedName

data class GetUserResponse(
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
        val nextPageUrl: String,
        @SerializedName("path")
        val path: String,
        @SerializedName("per_page")
        val perPage: Int,
        @SerializedName("prev_page_url")
        val prevPageUrl: String,
        @SerializedName("to")
        val to: Int,
        @SerializedName("total")
        val total: Int
    ) {
        data class Data(
            @SerializedName("country_code")
            val countryCode: String,
            @SerializedName("country_info")
            val countryInfo: CountryInfo,
            @SerializedName("created_at")
            val createdAt: String,
            @SerializedName("device_token")
            val deviceToken: String,
            @SerializedName("device_type")
            val deviceType: String,
            @SerializedName("email")
            val email: String,
            @SerializedName("email_verified_at")
            val emailVerifiedAt: String,
            @SerializedName("full_name")
            val fullName: String,
            @SerializedName("id")
            val id: Int,
            @SerializedName("let")
            val let: String,
            @SerializedName("long")
            val long: String,
            @SerializedName("mobile")
            val mobile: String,
            @SerializedName("otp")
            val otp: String,
            @SerializedName("otp_verify")
            val otpVerify: String,
            @SerializedName("profile_image")
            val profileImage: String,
            @SerializedName("status")
            var status: String,
            @SerializedName("updated_at")
            val updatedAt: String,
            @SerializedName("wallet_info")
            val walletInfo: WalletInfo,
            @SerializedName("isSelected")
            var isSelected: Boolean = false
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

            data class WalletInfo(
                @SerializedName("amount")
                val amount: String="0",
                @SerializedName("created_at")
                val createdAt: String,
                @SerializedName("id")
                val id: Int,
                @SerializedName("updated_at")
                val updatedAt: String,
                @SerializedName("user_id")
                val userId: Int
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