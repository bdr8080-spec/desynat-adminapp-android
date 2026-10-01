package com.desynatadmin.ui.home.sellerPayout.response

import com.google.gson.annotations.SerializedName

data class GetSellerPayoutHistoryResponse(
    @SerializedName("data")
    val `data`: Data? = null,
    @SerializedName("message")
    val message: String? = null,
    @SerializedName("status")
    val status: String? = null
) {
    data class Data(
        @SerializedName("seller_id")
        val sellerId: Any? = null,
        @SerializedName("total_paid")
        val totalPaid: String? = null,
        @SerializedName("pending_amount")
        val pendingAmount: String? = null,
        @SerializedName("payout_history")
        val payoutHistory: PayoutHistory? = null
    ) {
        data class PayoutHistory(
            @SerializedName("current_page")
            val currentPage: Int? = null,
            @SerializedName("data")
            val `data`: List<PayoutHistoryItem>? = null,
            @SerializedName("first_page_url")
            val firstPageUrl: String? = null,
            @SerializedName("from")
            val from: Int? = null,
            @SerializedName("last_page")
            val lastPage: Int? = null,
            @SerializedName("last_page_url")
            val lastPageUrl: String? = null,
            @SerializedName("links")
            val links: List<Link>? = null,
            @SerializedName("next_page_url")
            val nextPageUrl: String? = null,
            @SerializedName("path")
            val path: String? = null,
            @SerializedName("per_page")
            val perPage: Int? = null,
            @SerializedName("prev_page_url")
            val prevPageUrl: String? = null,
            @SerializedName("to")
            val to: Int? = null,
            @SerializedName("total")
            val total: Int? = null
        ) {
            data class PayoutHistoryItem(
                @SerializedName("id")
                val id: Int? = null,
                @SerializedName("request_id")
                val requestId: String? = null,
                @SerializedName("seller_id")
                val sellerId: Any? = null,
                @SerializedName("amount")
                val amount: String? = null,
                @SerializedName("transaction_id")
                val transactionId: String? = null,
                @SerializedName("payout_date")
                val payoutDate: String? = null
            )

            data class Link(
                @SerializedName("active")
                val active: Boolean? = null,
                @SerializedName("label")
                val label: String? = null,
                @SerializedName("url")
                val url: String? = null
            )
        }
    }
}
