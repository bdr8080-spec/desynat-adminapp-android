package com.desynatadmin.ui.home.affiliateUserManagement.response


import com.google.gson.annotations.SerializedName

data class BlockUnblockAffiliateUserResponse(
    @SerializedName("data")
    val data: Data,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
) {
    data class Data(
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
        @SerializedName("reject_reason")
        val rejectReason: String,
        @SerializedName("remember_token")
        val rememberToken: String,
        @SerializedName("status")
        val status: String,
        @SerializedName("updated_at")
        val updatedAt: String,
        @SerializedName("user_id")
        val userId: String)
}