package com.desynatadmin.ui.home.affiliateUserManagement.response


import com.google.gson.annotations.SerializedName

data class GetNewAffiliateUserResponse(
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
        val data: List<Data>,
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
            @SerializedName("address_details")
            val addressDetails: AddressDetails,
            @SerializedName("address_update_status")
            val addressUpdateStatus: String,
            @SerializedName("admin_verify")
            val adminVerify: String,
            @SerializedName("bank_details")
            val bankDetails: ArrayList<BankDetails>,
            @SerializedName("bank_update_status")
            val bankUpdateStatus: String,
            @SerializedName("country_code")
            val countryCode: String,
            @SerializedName("country_details")
            val countryDetails: CountryDetails,
            @SerializedName("created_at")
            val createdAt: String,
            @SerializedName("device_token")
            val deviceToken: String,
            @SerializedName("device_type")
            val deviceType: String,
            @SerializedName("document1")
            val document1: String,
            @SerializedName("document2")
            val document2: String,
            @SerializedName("email")
            val email: String,
            @SerializedName("email_verified_at")
            val emailVerifiedAt: String,
            @SerializedName("full_name")
            val fullName: String,
            @SerializedName("id")
            val id: String,
            @SerializedName("let")
            val let: Double,
            @SerializedName("long")
            val long: Double,
            @SerializedName("mobile")
            val mobile: String,
            @SerializedName("otp")
            val otp: String,
            @SerializedName("otp_verify")
            val otpVerify: String,
            @SerializedName("password")
            val password: String,
            @SerializedName("profile_image")
            val profileImage: String,
            @SerializedName("reject_type") //complete or document
            var rejectType: String,
            @SerializedName("reject_reason")
            var rejectReason: String,
            @SerializedName("remember_token")
            val rememberToken: String,
            @SerializedName("status")
            var status: String,
            @SerializedName("updated_at")
            val updatedAt: String,
            @SerializedName("user_id")
            val userId: Int,
            @SerializedName("isSelected")
            var isSelected: Boolean=false
        ) {
            data class AddressDetails(
                @SerializedName("address1")
                val address1: String,
                @SerializedName("address2")
                val address2: String,
                @SerializedName("affiliate_id")
                val affiliateId: String,
                @SerializedName("city")
                val city: String,
                @SerializedName("country")
                val country: String,
                @SerializedName("created_at")
                val createdAt: String,
                @SerializedName("id")
                val id: String,
                @SerializedName("let")
                val let: String,
                @SerializedName("locations")
                val locations: String,
                @SerializedName("long")
                val long: String,
                @SerializedName("pincode")
                val pincode: String,
                @SerializedName("state")
                val state: String,
                @SerializedName("town")
                val town: String,
                @SerializedName("updated_at")
                val updatedAt: String
            )

            data class BankDetails(
                @SerializedName("account_holder_name")
                val accountHolderName: String,
                @SerializedName("account_no")
                val accountNo: String,
                @SerializedName("account_type")
                val accountType: String,
                @SerializedName("bank_name")
                val bankName: String,
                @SerializedName("created_at")
                val createdAt: String,
                @SerializedName("iban")
                val iban: String,
                @SerializedName("id")
                val id: String,
                @SerializedName("swift")
                val swift: String,
                @SerializedName("type")
                val type: String,
                @SerializedName("updated_at")
                val updatedAt: String,
                @SerializedName("upload_proof")
                val uploadProof: String,
                @SerializedName("user_id")
                val userId: String
            )

            data class CountryDetails(
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
                val id: String,
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