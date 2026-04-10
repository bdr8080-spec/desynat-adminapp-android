package com.desynatadmin.ui.home.paymentManagement.response


import com.google.gson.annotations.SerializedName

data class GetBannerPaymentResponse(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
) {
    data class Data(
        @SerializedName("orders")
        val orders: Orders,
        @SerializedName("total_revenue")
        val totalRevenue: Double
    ) {
        data class Orders(
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
            val nextPageUrl: String,
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
                @SerializedName("added_by")
                val addedBy: String,
                @SerializedName("Admin_verify")
                val adminVerify: String,
                @SerializedName("banner_position")
                val bannerPosition: String,
                @SerializedName("cancelled_reason")
                val cancelledReason: Any,
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
                @SerializedName("payment_method")
                val paymentMethod: String,
                @SerializedName("seller_details")
                val sellerDetails: SellerDetails,
                @SerializedName("seller_id")
                val sellerId: Int,
                @SerializedName("start_date")
                val startDate: String,
                @SerializedName("status")
                val status: String,
                @SerializedName("subcatgory")
                val subcatgory: Any,
                @SerializedName("total_days")
                val totalDays: String,
                @SerializedName("transaction_id")
                val transactionId: String,
                @SerializedName("updated_at")
                val updatedAt: String,
                @SerializedName("upload_banner")
                val uploadBanner: String
            ) {
                data class SellerDetails(
                    @SerializedName("accept_terms")
                    val acceptTerms: String,
                    @SerializedName("admin_verify")
                    val adminVerify: String,
                    @SerializedName("affilate_commission")
                    val affilateCommission: Any,
                    @SerializedName("bank_information")
                    val bankInformation: String,
                    @SerializedName("country_code")
                    val countryCode: String,
                    @SerializedName("created_at")
                    val createdAt: String,
                    @SerializedName("device_token")
                    val deviceToken: Any,
                    @SerializedName("device_type")
                    val deviceType: String,
                    @SerializedName("email")
                    val email: String,
                    @SerializedName("fcm_token")
                    val fcmToken: String,
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
                    @SerializedName("profile_qr")
                    val profileQr: String,
                    @SerializedName("qrSellerUrl")
                    val qrSellerUrl: String,
                    @SerializedName("reject_reason")
                    val rejectReason: Any,
                    @SerializedName("store_info")
                    val storeInfo: StoreInfo,
                    @SerializedName("store_information")
                    val storeInformation: String,
                    @SerializedName("updated_at")
                    val updatedAt: String
                ) {
                    data class StoreInfo(
                        @SerializedName("address_line1")
                        val addressLine1: String,
                        @SerializedName("address_line2")
                        val addressLine2: String,
                        @SerializedName("bio")
                        val bio: String,
                        @SerializedName("country")
                        val country: String,
                        @SerializedName("country_code")
                        val countryCode: String,
                        @SerializedName("country_info")
                        val countryInfo: CountryInfo,
                        @SerializedName("created_at")
                        val createdAt: String,
                        @SerializedName("id")
                        val id: Int,
                        @SerializedName("let")
                        val let: String,
                        @SerializedName("long")
                        val long: String,
                        @SerializedName("seller_id")
                        val sellerId: Int,
                        @SerializedName("store_address")
                        val storeAddress: String,
                        @SerializedName("Store_Availability")
                        val storeAvailability: Int,
                        @SerializedName("store_image")
                        val storeImage: String,
                        @SerializedName("store_mail_id")
                        val storeMailId: String,
                        @SerializedName("store_name")
                        val storeName: String,
                        @SerializedName("store_phone")
                        val storePhone: String,
                        @SerializedName("updated_at")
                        val updatedAt: String
                    ) {
                        data class CountryInfo(
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
                }
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
}