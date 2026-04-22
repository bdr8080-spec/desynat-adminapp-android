package com.desynatadmin.ui.home.subAdminManagement.response


import com.google.gson.annotations.SerializedName

data class UpdateSubAdminStatusResponse(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
) {
    data class Data(
        @SerializedName("app_notification")
        val appNotification: Int,
        @SerializedName("country_code")
        val countryCode: String,
        @SerializedName("country_id")
        val countryId: String,
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
        @SerializedName("full_name_ar")
        val fullNameAr: String,
        @SerializedName("id")
        val id: Int,
        @SerializedName("mobile")
        val mobile: String,
        @SerializedName("notification")
        val notification: Int,
        @SerializedName("otp")
        val otp: Any,
        @SerializedName("otp_verify")
        val otpVerify: String,
        @SerializedName("password")
        val password: String,
        @SerializedName("profile_pic")
        val profilePic: String,
        @SerializedName("remember_token")
        val rememberToken: Any,
        @SerializedName("status")
        val status: String,
        @SerializedName("type")
        val type: String,
        @SerializedName("unhashed_password")
        val unhashedPassword: String,
        @SerializedName("updated_at")
        val updatedAt: String
    )
}