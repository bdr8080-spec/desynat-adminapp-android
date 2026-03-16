package com.desynatadmin.ui.home.notificationManagement.response


import com.google.gson.annotations.SerializedName

data class GetSendNotificationListResponse(
    @SerializedName("data")
    val `data`: List<Data>,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
) {
    data class Data(
        @SerializedName("created_at")
        val createdAt: String,
        @SerializedName("description_arabic")
        val descriptionArabic: String,
        @SerializedName("description_english")
        val descriptionEnglish: String,
        @SerializedName("id")
        val id: Int,
        @SerializedName("is_read")
        val isRead: Int,
        @SerializedName("notification_type")
        val notificationType: Any,
        @SerializedName("receiver_details")
        val receiverDetails: ReceiverDetails,
        @SerializedName("receiver_id")
        val receiverId: Int,
        @SerializedName("receiver_type")
        val receiverType: Int,
        @SerializedName("sender_details")
        val senderDetails: SenderDetails,
        @SerializedName("sender_id")
        val senderId: Int,
        @SerializedName("sender_type")
        val senderType: Int,
        @SerializedName("title_arabic")
        val titleArabic: String,
        @SerializedName("title_english")
        val titleEnglish: String,
        @SerializedName("updated_at")
        val updatedAt: String
    ) {
        data class ReceiverDetails(
            @SerializedName("accept_terms")
            val acceptTerms: String,
            @SerializedName("accessToken")
            val accessToken: Any,
            @SerializedName("address_line1")
            val addressLine1: String,
            @SerializedName("address_line2")
            val addressLine2: String,
            @SerializedName("admin_verify")
            val adminVerify: String,
            @SerializedName("affilate_commission")
            val affilateCommission: Any,
            @SerializedName("affiliated_userdetails")
            val affiliatedUserdetails: Any,
            @SerializedName("bank_information")
            val bankInformation: String,
            @SerializedName("banner_view")
            val bannerView: String,
            @SerializedName("city")
            val city: String,
            @SerializedName("country_code")
            val countryCode: String,
            @SerializedName("country_id")
            val countryId: Int,
            @SerializedName("created_at")
            val createdAt: String,
            @SerializedName("device_token")
            val deviceToken: String,
            @SerializedName("device_type")
            val deviceType: String,
            @SerializedName("document_information")
            val documentInformation: String,
            @SerializedName("email")
            val email: String,
            @SerializedName("email_verified_at")
            val emailVerifiedAt: Any,
            @SerializedName("fcm_token")
            val fcmToken: Any,
            @SerializedName("first_name")
            val firstName: String,
            @SerializedName("full_name")
            val fullName: String,
            @SerializedName("full_name_arabic")
            val fullNameArabic: String,
            @SerializedName("full_name_english")
            val fullNameEnglish: String,
            @SerializedName("id")
            val id: Int,
            @SerializedName("is_block")
            val isBlock: Int,
            @SerializedName("lang")
            val lang: String,
            @SerializedName("last_name")
            val lastName: String,
            @SerializedName("let")
            val let: String,
            @SerializedName("long")
            val long: String,
            @SerializedName("mobile")
            val mobile: String,
            @SerializedName("on_duty")
            val onDuty: String,
            @SerializedName("otp")
            val otp: String,
            @SerializedName("otp_verify")
            val otpVerify: String,
            @SerializedName("password")
            val password: String,
            @SerializedName("profile_image")
            val profileImage: Any,
            @SerializedName("profile_pic")
            val profilePic: String,
            @SerializedName("reject_reason")
            val rejectReason: Any,
            @SerializedName("state")
            val state: String,
            @SerializedName("status")
            val status: String,
            @SerializedName("store_information")
            val storeInformation: String,
            @SerializedName("unhashed_password")
            val unhashedPassword: String,
            @SerializedName("updated_at")
            val updatedAt: String,
            @SerializedName("vehicle_name")
            val vehicleName: String,
            @SerializedName("vehicle_reg_no")
            val vehicleRegNo: String,
            @SerializedName("zone_id")
            val zoneId: Int
        )

        data class SenderDetails(
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
            val id: Int,
            @SerializedName("mobile")
            val mobile: String,
            @SerializedName("otp")
            val otp: Any,
            @SerializedName("otp_verify")
            val otpVerify: String,
            @SerializedName("profile_pic")
            val profilePic: Any,
            @SerializedName("updated_at")
            val updatedAt: String
        )
    }
}