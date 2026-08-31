package com.desynatadmin.model

import com.google.gson.annotations.SerializedName

data class GetAllNotification(
    @SerializedName("data")
    val data: Data?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("status")
    val status: String?
)

data class Data(
    @SerializedName("current_page")
    val currentPage: Int?,
    @SerializedName("data")
    val data: List<DataX>?,
    @SerializedName("first_page_url")
    val firstPageUrl: String?,
    @SerializedName("from")
    val from: Int?,
    @SerializedName("last_page")
    val lastPage: Int?,
    @SerializedName("last_page_url")
    val lastPageUrl: String?,
    @SerializedName("links")
    val links: List<Link>?,
    @SerializedName("next_page_url")
    val nextPageUrl: String?,
    @SerializedName("path")
    val path: String?,
    @SerializedName("per_page")
    val perPage: Int?,
    @SerializedName("prev_page_url")
    val prevPageUrl: Any?,
    @SerializedName("to")
    val to: Int?,
    @SerializedName("total")
    val total: Int?
)

data class DataX(
    @SerializedName("created_at")
    val createdAt: String?,
    @SerializedName("description_arabic")
    val descriptionArabic: String?,
    @SerializedName("description_english")
    val descriptionEnglish: String?,
    @SerializedName("id")
    val id: Int?,
    @SerializedName("is_read")
    val isRead: Int?,
    @SerializedName("notification_type")
    val notificationType: String?,
    @SerializedName("receiver_details")
    val receiverDetails: ReceiverDetails?,
    @SerializedName("receiver_id")
    val receiverId: Int?,
    @SerializedName("receiver_type")
    val receiverType: Int?,
    @SerializedName("sender_details")
    val senderDetails: SenderDetails?,
    @SerializedName("sender_id")
    val senderId: Int?,
    @SerializedName("sender_type")
    val senderType: Int?,
    @SerializedName("title_arabic")
    val titleArabic: String?,
    @SerializedName("title_english")
    val titleEnglish: String?,
    @SerializedName("updated_at")
    val updatedAt: String?
)

data class Link(
    @SerializedName("active")
    val active: Boolean?,
    @SerializedName("label")
    val label: String?,
    @SerializedName("url")
    val url: String?
)

data class ReceiverDetails(
    @SerializedName("app_notification")
    val appNotification: Int?,
    @SerializedName("country_code")
    val countryCode: String?,
    @SerializedName("country_id")
    val countryId: String?,
    @SerializedName("created_at")
    val createdAt: String?,
    @SerializedName("device_type")
    val deviceType: String?,
    @SerializedName("email")
    val email: String?,
    @SerializedName("email_verified_at")
    val emailVerifiedAt: Any?,
    @SerializedName("fcm_token")
    val fcmToken: String?,
    @SerializedName("full_name")
    val fullName: String?,
    @SerializedName("full_name_ar")
    val fullNameAr: Any?,
    @SerializedName("id")
    val id: Int?,
    @SerializedName("lang")
    val lang: String?,
    @SerializedName("mobile")
    val mobile: String?,
    @SerializedName("notification")
    val notification: Int?,
    @SerializedName("otp")
    val otp: String?,
    @SerializedName("otp_verify")
    val otpVerify: String?,
    @SerializedName("password")
    val password: String?,
    @SerializedName("profile_pic")
    val profilePic: String?,
    @SerializedName("remember_token")
    val rememberToken: Any?,
    @SerializedName("status")
    val status: String?,
    @SerializedName("type")
    val type: String?,
    @SerializedName("unhashed_password")
    val unhashedPassword: String?,
    @SerializedName("updated_at")
    val updatedAt: String?
)

data class SenderDetails(
    @SerializedName("accept_terms")
    val acceptTerms: String?,
    @SerializedName("accessToken")
    val accessToken: Any?,
    @SerializedName("address_line1")
    val addressLine1: String?,
    @SerializedName("address_line2")
    val addressLine2: String?,
    @SerializedName("admin_verify")
    val adminVerify: String?,
    @SerializedName("affilate_commission")
    val affilateCommission: Any?,
    @SerializedName("affiliated_userdetails")
    val affiliatedUserdetails: Any?,
    @SerializedName("app_notification")
    val appNotification: Int?,
    @SerializedName("bank_information")
    val bankInformation: String?,
    @SerializedName("banner_view")
    val bannerView: String?,
    @SerializedName("city")
    val city: String?,
    @SerializedName("country_code")
    val countryCode: String?,
    @SerializedName("country_id")
    val countryId: Int?,
    @SerializedName("created_at")
    val createdAt: String?,
    @SerializedName("deleted_at")
    val deletedAt: Any?,
    @SerializedName("device_token")
    val deviceToken: Any?,
    @SerializedName("device_type")
    val deviceType: String?,
    @SerializedName("document_information")
    val documentInformation: String?,
    @SerializedName("email")
    val email: String?,
    @SerializedName("email_verified_at")
    val emailVerifiedAt: Any?,
    @SerializedName("fcm_token")
    val fcmToken: String?,
    @SerializedName("first_name")
    val firstName: String?,
    @SerializedName("full_name")
    val fullName: String?,
    @SerializedName("full_name_arabic")
    val fullNameArabic: String?,
    @SerializedName("full_name_english")
    val fullNameEnglish: String?,
    @SerializedName("id")
    val id: Int?,
    @SerializedName("is_block")
    val isBlock: Int?,
    @SerializedName("is_login")
    val isLogin: Int?,
    @SerializedName("is_visit_app")
    val isVisitApp: Int?,
    @SerializedName("is_visit_website")
    val isVisitWebsite: Int?,
    @SerializedName("lang")
    val lang: String?,
    @SerializedName("last_name")
    val lastName: String?,
    @SerializedName("let")
    val let: String?,
    @SerializedName("long")
    val long: String?,
    @SerializedName("mobile")
    val mobile: String?,
    @SerializedName("name_ar")
    val nameAr: Any?,
    @SerializedName("notification")
    val notification: Int?,
    @SerializedName("on_duty")
    val onDuty: String?,
    @SerializedName("otp")
    val otp: String?,
    @SerializedName("otp_verify")
    val otpVerify: String?,
    @SerializedName("parent_id")
    val parentId: Any?,
    @SerializedName("password")
    val password: String?,
    @SerializedName("profile_image")
    val profileImage: String?,
    @SerializedName("profile_pic")
    val profilePic: String?,
    @SerializedName("profile_qr")
    val profileQr: Any?,
    @SerializedName("qrSellerUrl")
    val qrSellerUrl: Any?,
    @SerializedName("reject_reason")
    val rejectReason: Any?,
    @SerializedName("save_password")
    val savePassword: Any?,
    @SerializedName("select_category")
    val selectCategory: Any?,
    @SerializedName("selected_country")
    val selectedCountry: Int?,
    @SerializedName("selected_currency")
    val selectedCurrency: Int?,
    @SerializedName("socialType")
    val socialType: Any?,
    @SerializedName("state")
    val state: String?,
    @SerializedName("status")
    val status: String?,
    @SerializedName("store_information")
    val storeInformation: String?,
    @SerializedName("type")
    val type: String?,
    @SerializedName("unhashed_password")
    val unhashedPassword: String?,
    @SerializedName("unique_id")
    val uniqueId: Any?,
    @SerializedName("updated_at")
    val updatedAt: String?,
    @SerializedName("vehicle_name")
    val vehicleName: String?,
    @SerializedName("vehicle_reg_no")
    val vehicleRegNo: String?,
    @SerializedName("zone_id")
    val zoneId: Int?
)
