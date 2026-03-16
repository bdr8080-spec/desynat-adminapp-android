package com.desynatadmin.ui.home.notificationManagement.response


import com.google.gson.annotations.SerializedName

data class SendNotificationResponse(
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
        @SerializedName("notification_type")
        val notificationType: Any,
        @SerializedName("receiver_id")
        val receiverId: String,
        @SerializedName("receiver_type")
        val receiverType: String,
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
    )
}