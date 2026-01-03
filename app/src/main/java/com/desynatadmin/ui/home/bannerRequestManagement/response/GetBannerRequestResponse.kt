package com.desynatadmin.ui.home.bannerRequestManagement.response


import com.google.gson.annotations.SerializedName

data class GetBannerRequestResponse(
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
            @SerializedName("Admin_verify")
            var adminVerify: String,
            @SerializedName("banner_position")
            val bannerPosition: String,
            @SerializedName("cancelled_reason")
            var cancelledReason: String,
            @SerializedName("category")
            val category: String,
            @SerializedName("category_details")
            val categoryDetails: CategoryDetails,
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
            @SerializedName("status")
            val status: Int,
            @SerializedName("price")
            val price: String,
            @SerializedName("seller_details")
            val sellerDetails: SellerDetails,
            @SerializedName("seller_id")
            val sellerId: Int,
            @SerializedName("start_date")
            val startDate: String,
            @SerializedName("subcategory_details")
            val subcategoryDetails: SubcategoryDetails,
            @SerializedName("subcatgory")
            val subcatgory: String,
            @SerializedName("total_days")
            val totalDays: String,
            @SerializedName("transaction_id")
            val transactionId: String,
            @SerializedName("updated_at")
            val updatedAt: String,
            @SerializedName("upload_banner")
            val uploadBanner: String
        ) {
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

            data class SellerDetails(
                @SerializedName("accept_terms")
                val acceptTerms: String,
                @SerializedName("admin_verify")
                val adminVerify: String,
                @SerializedName("affilate_commission")
                val affilateCommission: String,
                @SerializedName("bank_information")
                val bankInformation: String,
                @SerializedName("country_code")
                val countryCode: String,
                @SerializedName("country_details")
                val countryDetails: CountryDetails,
                @SerializedName("created_at")
                val createdAt: String,
                @SerializedName("device_token")
                val deviceToken: String,
                @SerializedName("device_type")
                val deviceType: String,
                @SerializedName("email")
                val email: String,
                @SerializedName("first_name")
                val firstName: String,
                @SerializedName("id")
                val id: Int,
                @SerializedName("is_block")
                val isBlock: Int,
                @SerializedName("lang")
                val lang: Any,
                @SerializedName("last_name")
                val lastName: String,
                @SerializedName("mobile")
                val mobile: String,
                @SerializedName("otp")
                val otp: String,
                @SerializedName("otp_verify")
                val otpVerify: String,
                @SerializedName("password")
                val password: String,
                @SerializedName("profile_pic")
                val profilePic: String,
                @SerializedName("reject_reason")
                val rejectReason: Any,
                @SerializedName("store_information")
                val storeInformation: String,
                @SerializedName("updated_at")
                val updatedAt: String
            ) {
                data class CountryDetails(
                    @SerializedName("created_at")
                    val createdAt: String,
                    @SerializedName("currency_code")
                    val currencyCode: String,
                    @SerializedName("currency_name")
                    val currencyName: String,
                    @SerializedName("currency_symbol")
                    val currencySymbol: String,
                    @SerializedName("dial_code")
                    val dialCode: String,
                    @SerializedName("exchange_rate_to_kwd")
                    val exchangeRateToKwd: String,
                    @SerializedName("flag")
                    val flag: String,
                    @SerializedName("flag1")
                    val flag1: String,
                    @SerializedName("id")
                    val id: Int,
                    @SerializedName("iso_code")
                    val isoCode: String,
                    @SerializedName("name")
                    val name: String,
                    @SerializedName("updated_at")
                    val updatedAt: String
                )
            }

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