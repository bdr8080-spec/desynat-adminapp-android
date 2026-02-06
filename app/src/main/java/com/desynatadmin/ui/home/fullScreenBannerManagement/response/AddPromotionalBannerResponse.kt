package com.desynatadmin.ui.home.fullScreenBannerManagement.response


import com.google.gson.annotations.SerializedName

data class AddPromotionalBannerResponse(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
) {
    data class Data(
        @SerializedName("added_by")
        val addedBy: String,
        @SerializedName("Admin_verify")
        val adminVerify: String,
        @SerializedName("banner_position")
        val bannerPosition: String,
        @SerializedName("category")
        val category: String,
        @SerializedName("created_at")
        val createdAt: String,
        @SerializedName("end_date")
        val endDate: String,
        @SerializedName("id")
        val id: String,
        @SerializedName("order_id")
        val orderId: String,
        @SerializedName("payment_status")
        val paymentStatus: String,
        @SerializedName("start_date")
        val startDate: String,
        @SerializedName("subcatgory")
        val subcatgory: String,
        @SerializedName("total_days")
        val totalDays: String,
        @SerializedName("updated_at")
        val updatedAt: String,
        @SerializedName("upload_banner")
        val uploadBanner: String ,
    )
}