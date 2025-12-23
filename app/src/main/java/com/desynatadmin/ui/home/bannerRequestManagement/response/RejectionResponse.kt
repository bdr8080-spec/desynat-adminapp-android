package com.desynatadmin.ui.home.bannerRequestManagement.response


import com.google.gson.annotations.SerializedName

data class RejectionResponse(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
) {
    data class Data(
        @SerializedName("Admin_verify")
        val adminVerify: String,
        @SerializedName("banner_position")
        val bannerPosition: String,
        @SerializedName("cancelled_reason")
        val cancelledReason: String,
        @SerializedName("category")
        val category: Any,
        @SerializedName("created_at")
        val createdAt: String,
        @SerializedName("end_date")
        val endDate: String,
        @SerializedName("id")
        val id: Int,
        @SerializedName("order_id")
        val orderId: String,
        @SerializedName("payment_status")
        val paymentStatus: String,
        @SerializedName("price")
        val price: String,
        @SerializedName("seller_id")
        val sellerId: Int,
        @SerializedName("start_date")
        val startDate: String,
        @SerializedName("subcatgory")
        val subcatgory: Any,
        @SerializedName("total_days")
        val totalDays: String,
        @SerializedName("transaction_id")
        val transactionId: Any,
        @SerializedName("updated_at")
        val updatedAt: String,
        @SerializedName("upload_banner")
        val uploadBanner: String
    )
}