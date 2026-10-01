package com.desynatadmin.ui.home.sellerPayout.response

import com.google.gson.annotations.SerializedName

data class GetSellerPayoutResponse(
    @SerializedName("data")
    val `data`: Data? = null,
    @SerializedName("message")
    val message: String? = null,
    @SerializedName("status")
    val status: String? = null
) {
    data class Data(
        @SerializedName("sellers")
        val sellers: Sellers? = null,
        @SerializedName("total_payout_pending")
        val totalPayoutPending: String? = null
    ) {
        data class Sellers(
            @SerializedName("current_page")
            val currentPage: Int? = null,
            @SerializedName("data")
            val `data`: List<SellerItem>? = null,
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
            data class SellerItem(
                @SerializedName("id")
                val id: Int? = null,
                @SerializedName("seller_id")
                val sellerId: Int? = null,
                @SerializedName("name")
                val name: String? = null,
                @SerializedName("first_name")
                val firstName: String? = null,
                @SerializedName("last_name")
                val lastName: String? = null,
                @SerializedName("name_english")
                val nameEnglish: String? = null,
                @SerializedName("email")
                val email: String? = null,
                @SerializedName("mobile")
                val mobile: String? = null,
                @SerializedName("profile_pic")
                val profilePic: String? = null,
                @SerializedName("image")
                val image: String? = null,
                @SerializedName("location")
                val location: String? = null,
                @SerializedName("country_name")
                val countryName: String? = null,
                @SerializedName("last_payout_date")
                val lastPayoutDate: String? = null,
                @SerializedName("total_earning")
                val totalEarning: String? = null,
                @SerializedName("last_payout_value")
                val lastPayoutValue: String? = null,
                @SerializedName("last_payout_amount")
                val lastPayoutAmount: String? = null,
                @SerializedName("pending_amount")
                val pendingAmount: String? = null,
                @SerializedName("store_info")
                val storeInfo: StoreInfo? = null
            ) {
                data class StoreInfo(
                    @SerializedName("store_name")
                    val storeName: String? = null,
                    @SerializedName("store_image")
                    val storeImage: String? = null
                )
            }

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
