package com.desynatadmin.ui.home.paymentManagement.response


import com.google.gson.annotations.SerializedName

data class GetGiftCardPaymentResponse(
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
                @SerializedName("amount")
                val amount: String,
                @SerializedName("country_code")
                val countryCode: String,
                @SerializedName("created_at")
                val createdAt: String,
                @SerializedName("gift_card_id")
                val giftCardId: Int,
                @SerializedName("giftCardNo")
                val giftCardNo: String,
                @SerializedName("giftCardPin")
                val giftCardPin: String,
                @SerializedName("giftCardUse")
                val giftCardUse: String,
                @SerializedName("gift_details")
                val giftDetails: GiftDetails,
                @SerializedName("id")
                val id: Int,
                @SerializedName("mobile")
                val mobile: String,
                @SerializedName("notes")
                val notes: String,
                @SerializedName("order_id")
                val orderId: String,
                @SerializedName("payment_method")
                val paymentMethod: String,
                @SerializedName("payment_status")
                val paymentStatus: String,
                @SerializedName("receiver_email")
                val receiverEmail: String,
                @SerializedName("receiver_id")
                val receiverId: Any,
                @SerializedName("receiver_name")
                val receiverName: String,
                @SerializedName("sender_email")
                val senderEmail: Any,
                @SerializedName("sender_name")
                val senderName: String,
                @SerializedName("transaction_id")
                val transactionId: String,
                @SerializedName("updated_at")
                val updatedAt: String,
                @SerializedName("user_details")
                val userDetails: UserDetails,
                @SerializedName("user_id")
                val userId: String,
                @SerializedName("wish_message")
                val wishMessage: String
            ) {
                data class GiftDetails(
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

                data class UserDetails(
                    @SerializedName("accessToken")
                    val accessToken: Any,
                    @SerializedName("affiliated_userdetails")
                    val affiliatedUserdetails: Any,
                    @SerializedName("banner_view")
                    val bannerView: String,
                    @SerializedName("country_code")
                    val countryCode: String,
                    @SerializedName("country_info")
                    val countryInfo: CountryInfo,
                    @SerializedName("created_at")
                    val createdAt: String,
                    @SerializedName("device_token")
                    val deviceToken: Any,
                    @SerializedName("device_type")
                    val deviceType: String,
                    @SerializedName("email")
                    val email: String,
                    @SerializedName("email_verified_at")
                    val emailVerifiedAt: Any,
                    @SerializedName("fcm_token")
                    val fcmToken: Any,
                    @SerializedName("full_name")
                    val fullName: String,
                    @SerializedName("id")
                    val id: Int,
                    @SerializedName("lang")
                    val lang: String,
                    @SerializedName("let")
                    val let: Any,
                    @SerializedName("long")
                    val long: Any,
                    @SerializedName("mobile")
                    val mobile: String,
                    @SerializedName("otp")
                    val otp: String,
                    @SerializedName("otp_verify")
                    val otpVerify: String,
                    @SerializedName("profile_image")
                    val profileImage: String,
                    @SerializedName("status")
                    val status: String,
                    @SerializedName("updated_at")
                    val updatedAt: String
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