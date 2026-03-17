package com.desynatadmin.ui.home.notificationManagement.response


import com.google.gson.annotations.SerializedName

data class SendNotificationRequest(
    @SerializedName("affiliate_id")
    val affiliateId: List<String>?=null,
    @SerializedName("description_arabic")
    val descriptionArabic: String,
    @SerializedName("description_english")
    val descriptionEnglish: String,
    @SerializedName("driver_id")
    val driverId: List<String>?=null,
    @SerializedName("seller_id")
    val sellerId: List<String>?=null,
    @SerializedName("sender_type")
    val senderType: Int,
    @SerializedName("title_arabic")
    val titleArabic: String,
    @SerializedName("title_english")
    val titleEnglish: String,
    @SerializedName("user_id")
    val userId: List<String>?=null
)