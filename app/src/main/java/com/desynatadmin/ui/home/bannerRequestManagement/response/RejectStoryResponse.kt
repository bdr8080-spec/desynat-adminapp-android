package com.desynatadmin.ui.home.bannerRequestManagement.response

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable


@Serializable
data class RejectStoryResponse(

    @SerializedName("data")
    val data: Data?,

    @SerializedName("message")
    val message: String?,

    @SerializedName("status")
    val status: String?
) {

    @Serializable
    data class Data(

        @SerializedName("Admin_verify")
        val adminVerify: String?,

        @SerializedName("admin_verify")
        val adminVerifyStatus: Int?,

        @SerializedName("amount")
        val amount: String?,

        @SerializedName("created_at")
        val createdAt: String?,

        @SerializedName("end_date")
        val endDate: String?,

        @SerializedName("file")
        val file: String?,

        @SerializedName("id")
        val id: Int?,

        @SerializedName("is_promotional")
        val isPromotional: String?,

        @SerializedName("payment_status")
        val paymentStatus: String?,

        @SerializedName("reject_reason")
        val rejectReason: String?,

        @SerializedName("seller_id")
        val sellerId: Int?,

        @SerializedName("start_date")
        val startDate: String?,

        @SerializedName("status")
        val status: Int?,

        @SerializedName("thumbnail")
        val thumbnail: String?,

        @SerializedName("total_days")
        val totalDays: String?,

        @SerializedName("transaction_id")
        val transactionId: String?,

        @SerializedName("type")
        val type: Int?,

        @SerializedName("updated_at")
        val updatedAt: String?
    )
}



