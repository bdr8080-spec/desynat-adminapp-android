package com.desynatadmin.ui.home.affiliateWithdrawalRequest.response


import com.desynatadmin.ui.home.affiliateUserManagement.response.GetNewAffiliateUserResponse.Data.Data.AddressDetails
import com.google.gson.annotations.SerializedName

data class GetWithdrawalRequestResponse(
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
            @SerializedName("admin_verify")
            val adminVerify: Int,
            @SerializedName("affiliate__details")
            val affiliateDetails: AffiliateDetails,
            @SerializedName("amount")
            val amount: String,
            @SerializedName("bank_details")
            val bankDetails: BankDetails,
            @SerializedName("bankdetails_id")
            val bankdetailsId: Int,
            @SerializedName("created_at")
            val createdAt: String,
            @SerializedName("id")
            val id: Int,
            @SerializedName("payment_status")
            val paymentStatus: String,
            @SerializedName("reject_reason")
            val rejectReason: String,
            @SerializedName("request_id")
            val requestId: String,
            @SerializedName("total_earning")
            val totalEarning: String,
            @SerializedName("transaction_id")
            val transactionId: Any,
            @SerializedName("type")
            val type: Int,
            @SerializedName("updated_at")
            val updatedAt: String,
            @SerializedName("user_id")
            val userId: Int
        ) {
            data class AffiliateDetails(
                @SerializedName("address_update_status")
                val addressUpdateStatus: String,
                @SerializedName("admin_verify")
                val adminVerify: String,
                @SerializedName("bank_update_status")
                val bankUpdateStatus: String,
                @SerializedName("country_code")
                val countryCode: String,
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
                @SerializedName("lang")
                val lang: String,
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
                @SerializedName("password_old")
                val passwordOld: String,
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
                val userId: Int,
                @SerializedName("address_details")
                val addressDetails: AddressDetails,
            )
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
                val updatedAt: String,
                @SerializedName("country_details")
                val countryDetails: CountryDetails,
            )
            {
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
                @SerializedName("default_account")
                val defaultAccount: String,
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
                val uploadProof: String,
                @SerializedName("user_id")
                val userId: String
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