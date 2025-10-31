package com.desynatadmin.ui.auth


import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
) {
    data class Data(
        @SerializedName("accessToken")
        val accessToken: String,
        @SerializedName("country_code")
        val countryCode: String,
        @SerializedName("created_at")
        val createdAt: String,
        @SerializedName("device_type")
        val deviceType: String,
        @SerializedName("email")
        val email: String,
        @SerializedName("email_verified_at")
        val emailVerifiedAt: Any,
        @SerializedName("fcm_token")
        val fcmToken: Any,
        @SerializedName("full_name")
        val fullName: String,
        @SerializedName("id")
        val id: String,
        @SerializedName("mobile")
        val mobile: String,
        @SerializedName("otp")
        val otp: Any,
        @SerializedName("otp_verify")
        val otpVerify: String,
        @SerializedName("updated_at")
        val updatedAt: String
    )
}