package com.desynatadmin.ui.home.fullScreenBannerManagement.response


import com.desynatadmin.ui.home.categorySubCategoryManagement.response.GetCategoryResponse
import com.desynatadmin.ui.home.categorySubCategoryManagement.response.GetSubCategoryResponse
import com.google.gson.annotations.SerializedName

data class GetPromotionalBannerResponse(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
) {
    data class Data(
        @SerializedName("current_page")
        val currentPage: Int,
        @SerializedName("data")
        val `data`: List<Data>,
        @SerializedName("first_page_url")
        val firstPageUrl: String,
        @SerializedName("from")
        val from: Int,
        @SerializedName("last_page")
        val lastPage: Int,
        @SerializedName("last_page_url")
        val lastPageUrl: String,
        @SerializedName("links")
        val links: List<Link>,
        @SerializedName("next_page_url")
        val nextPageUrl: Any,
        @SerializedName("path")
        val path: String,
        @SerializedName("per_page")
        val perPage: Int,
        @SerializedName("prev_page_url")
        val prevPageUrl: Any,
        @SerializedName("to")
        val to: Int,
        @SerializedName("total")
        val total: Int
    ) {
        data class Data(
            @SerializedName("country_id")
            var countryId: Int,
            @SerializedName("country_info")
            val countryInfo: CountryInfo,
            @SerializedName("added_by")
            val addedBy: String,
            @SerializedName("Admin_verify")
            val adminVerify: String,
            @SerializedName("banner_position")
            var bannerPosition: String,
            @SerializedName("cancelled_reason")
            val cancelledReason: Any,
            @SerializedName("category")
            val category: String,
            @SerializedName("category_details")
            var categoryDetails: GetCategoryResponse.Data?=null,
            @SerializedName("created_at")
            val createdAt: String,
            @SerializedName("end_date")
            var endDate: String,
            @SerializedName("id")
            val id: Int,
            @SerializedName("order_id")
            val orderId: String,
            @SerializedName("payment_status")
            val paymentStatus: String,
            @SerializedName("price")
            val price: Any,
            @SerializedName("seller_id")
            val sellerId: Any,
            @SerializedName("start_date")
            var startDate: String,
            @SerializedName("status")
            var status: String,
            @SerializedName("subcategory_details")
            var subcategoryDetails: GetSubCategoryResponse.Data.Data?=null,
            @SerializedName("subcatgory")
            val subcatgory: String,
            @SerializedName("total_days")
            var totalDays: String,
            @SerializedName("transaction_id")
            val transactionId: Any,
            @SerializedName("updated_at")
            val updatedAt: String,
            @SerializedName("upload_banner")
            var uploadBanner: String,
        ) {
                data class CountryInfo(
                    @SerializedName("code")
                    val code: String,
                    @SerializedName("currency_code")
                    val currencyCode: String,
                    @SerializedName("currency_name")
                    val currencyName: String,
                    @SerializedName("currency_symbol")
                    val currencySymbol: String,
                    @SerializedName("dial_code")
                    val dialCode: String,
                    @SerializedName("exchange_rate_to_usd")
                    val exchangeRateToUsd: String,
                    @SerializedName("flag")
                    val flag: String,
                    @SerializedName("flag1")
                    val flag1: String,
                    @SerializedName("id")
                    val id: Int,
                    @SerializedName("iso_code")
                    val isoCode: String,
                    @SerializedName("iso_code_number")
                    val isoCodeNumber: String,
                    @SerializedName("name")
                    val name: String
                )
            data class CategoryDetails(
                @SerializedName("color_code")
                val colorCode: String,
                @SerializedName("created_at")
                val createdAt: String,
                @SerializedName("description")
                val description: String,
                @SerializedName("id")
                val id: Int,
                @SerializedName("image")
                val image: String,
                @SerializedName("name")
                val name: String,
                @SerializedName("status")
                val status: Int,
                @SerializedName("updated_at")
                val updatedAt: String
            )

            data class SubcategoryDetails(
                @SerializedName("category_id")
                val categoryId: Int,
                @SerializedName("created_at")
                val createdAt: String,
                @SerializedName("id")
                val id: Int,
                @SerializedName("image")
                val image: String,
                @SerializedName("name_arabic")
                val nameArabic: String,
                @SerializedName("name_english")
                val nameEnglish: String,
                @SerializedName("status")
                val status: Int,
                @SerializedName("updated_at")
                val updatedAt: String
            )
        }

        data class Link(
            @SerializedName("active")
            val active: Boolean,
            @SerializedName("label")
            val label: String,
            @SerializedName("url")
            val url: String
        )
    }
}