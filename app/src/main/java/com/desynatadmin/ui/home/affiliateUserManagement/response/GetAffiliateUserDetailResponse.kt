package com.desynatadmin.ui.home.affiliateUserManagement.response


import com.google.gson.annotations.SerializedName

data class GetAffiliateUserDetailResponse(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
) {
    data class Data(
        @SerializedName("address_details")
        val addressDetails: AddressDetails,
        @SerializedName("address_update_status")
        val addressUpdateStatus: String,
        @SerializedName("admin_verify")
        val adminVerify: String,
        @SerializedName("bank_details")
        val bankDetails: BankDetails,
        @SerializedName("bank_update_status")
        val bankUpdateStatus: String,
        @SerializedName("country_code")
        val countryCode: String,
        @SerializedName("country_details")
        val countryDetails: CountryDetails,
        @SerializedName("created_at")
        val createdAt: String,
        @SerializedName("device_token")
        val deviceToken: Any,
        @SerializedName("device_type")
        val deviceType: String,
        @SerializedName("document1")
        val document1: String,
        @SerializedName("document2")
        val document2: String,
        @SerializedName("email")
        val email: String,
        @SerializedName("email_verified_at")
        val emailVerifiedAt: Any,
        @SerializedName("full_name")
        val fullName: String,
        @SerializedName("id")
        val id: Int,
        @SerializedName("let")
        val let: Any,
        @SerializedName("long")
        val long: Any,
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
        @SerializedName("reject_reason")
        val rejectReason: Any,
        @SerializedName("remember_token")
        val rememberToken: Any,
        @SerializedName("status")
        val status: String,
        @SerializedName("updated_at")
        val updatedAt: String,
        @SerializedName("user_id")
        val userId: Int
    ) {
        data class AddressDetails(
            @SerializedName("address1")
            val address1: String,
            @SerializedName("address2")
            val address2: String,
            @SerializedName("affiliate_id")
            val affiliateId: Int,
            @SerializedName("city")
            val city: String,
            @SerializedName("country")
            val country: String,
            @SerializedName("created_at")
            val createdAt: String,
            @SerializedName("id")
            val id: Int,
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
            val id: Int,
            @SerializedName("swift")
            val swift: String,
            @SerializedName("type")
            val type: String,
            @SerializedName("updated_at")
            val updatedAt: String,
            @SerializedName("upload_proof")
            val uploadProof: Any,
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
            val id: Int,
            @SerializedName("iso_code")
            val isoCode: String,
            @SerializedName("iso_code_number")
            val isoCodeNumber: String,
            @SerializedName("name")
            val name: String
        )
    }
}