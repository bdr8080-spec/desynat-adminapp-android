package com.desynatadmin.ui.home.paymentManagement.response


import com.google.gson.annotations.SerializedName

data class GetOrderPaymentResponse(
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
        @SerializedName("total_commission")
        val totalCommission: Double,
        @SerializedName("total_custom_order_revenue")
        val totalCustomOrderRevenue: Double,
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
                @SerializedName("admin_commission")
                val adminCommission: Any,
                @SerializedName("affiliate_admin_commission")
                val affiliateAdminCommission: String,
                @SerializedName("affiliate_commission")
                val affiliateCommission: String,
                @SerializedName("affiliate_id")
                val affiliateId: Any,
                @SerializedName("amount")
                val amount: String,
                @SerializedName("assign_date")
                val assignDate: String,
                @SerializedName("assign_status")
                val assignStatus: String,
                @SerializedName("cancel_reason")
                val cancelReason: Any,
                @SerializedName("created_at")
                val createdAt: String,
                @SerializedName("delivery_boy_id")
                val deliveryBoyId: String,
                @SerializedName("delivery_status")
                val deliveryStatus: String,
                @SerializedName("exchange_new_size")
                val exchangeNewSize: Any,
                @SerializedName("exchange_old_size")
                val exchangeOldSize: Any,
                @SerializedName("exchange_reason")
                val exchangeReason: Any,
                @SerializedName("giftcard_amount")
                val giftcardAmount: Any,
                @SerializedName("giftcard_id")
                val giftcardId: Any,
                @SerializedName("id")
                val id: Int,
                @SerializedName("is_picked")
                val isPicked: String,
                @SerializedName("not_delivered_reason")
                val notDeliveredReason: Any,
                @SerializedName("order")
                val order: Order,
                @SerializedName("order_id")
                val orderId: Int,
                @SerializedName("order_status")
                val orderStatus: Any,
                @SerializedName("order_type")
                val orderType: String,
                @SerializedName("p_orderdetails_id")
                val pOrderdetailsId: String,
                @SerializedName("payment_status")
                val paymentStatus: String,
                @SerializedName("product_details")
                val productDetails: ProductDetails,
                @SerializedName("product_id")
                val productId: Int,
                @SerializedName("promocode_id")
                val promocodeId: Any,
                @SerializedName("promocode_value")
                val promocodeValue: Any,
                @SerializedName("quantity")
                val quantity: Int,
                @SerializedName("r_e_approve")
                val rEApprove: String,
                @SerializedName("r_e_type")
                val rEType: String,
                @SerializedName("reference_link")
                val referenceLink: Any,
                @SerializedName("return_image")
                val returnImage: Any,
                @SerializedName("return_reason")
                val returnReason: Any,
                @SerializedName("return_type")
                val returnType: String,
                @SerializedName("returntostore")
                val returntostore: String,
                @SerializedName("seller_id")
                val sellerId: String,
                @SerializedName("shipping_fees")
                val shippingFees: String,
                @SerializedName("status")
                val status: String,
                @SerializedName("tax")
                val tax: String,
                @SerializedName("total_discount")
                val totalDiscount: Int,
                @SerializedName("unique_id")
                val uniqueId: Any,
                @SerializedName("updated_at")
                val updatedAt: String,
                @SerializedName("user_details")
                val userDetails: UserDetails,
                @SerializedName("user_id")
                val userId: Int,
                @SerializedName("variants_id")
                val variantsId: Int,
                @SerializedName("variation_details")
                val variationDetails: VariationDetails
            ) {
                data class Order(
                    @SerializedName("assign_date")
                    val assignDate: Any,
                    @SerializedName("assign_status")
                    val assignStatus: String,
                    @SerializedName("cancel_reason")
                    val cancelReason: Any,
                    @SerializedName("created_at")
                    val createdAt: String,
                    @SerializedName("delivery_address")
                    val deliveryAddress: Int,
                    @SerializedName("delivery_boy_id")
                    val deliveryBoyId: Any,
                    @SerializedName("delivery_status")
                    val deliveryStatus: String,
                    @SerializedName("discount_mrp")
                    val discountMrp: String,
                    @SerializedName("gift_card_details")
                    val giftCardDetails: Any,
                    @SerializedName("giftcard_amount")
                    val giftcardAmount: String,
                    @SerializedName("giftcard_id")
                    val giftcardId: Any,
                    @SerializedName("id")
                    val id: Int,
                    @SerializedName("order_id")
                    val orderId: String,
                    @SerializedName("order_type")
                    val orderType: String,
                    @SerializedName("payment_method")
                    val paymentMethod: String,
                    @SerializedName("payment_status")
                    val paymentStatus: String,
                    @SerializedName("promocode_details")
                    val promocodeDetails: Any,
                    @SerializedName("promocode_id")
                    val promocodeId: Any,
                    @SerializedName("promocode_status")
                    val promocodeStatus: String,
                    @SerializedName("promocode_value")
                    val promocodeValue: Any,
                    @SerializedName("shipping_fees")
                    val shippingFees: String,
                    @SerializedName("tax")
                    val tax: String,
                    @SerializedName("total_amount")
                    val totalAmount: String,
                    @SerializedName("total_mrp")
                    val totalMrp: String,
                    @SerializedName("transaction_id")
                    val transactionId: Any,
                    @SerializedName("unique_id")
                    val uniqueId: Any,
                    @SerializedName("updated_at")
                    val updatedAt: String,
                    @SerializedName("user_id")
                    val userId: Int,
                    @SerializedName("wallet_amount")
                    val walletAmount: Any
                )

                data class ProductDetails(
                    @SerializedName("affiliated_commission")
                    val affiliatedCommission: Any,
                    @SerializedName("affiliated_product")
                    val affiliatedProduct: String,
                    @SerializedName("brand_id")
                    val brandId: String,
                    @SerializedName("cash_on_delivery")
                    val cashOnDelivery: String,
                    @SerializedName("category_id")
                    val categoryId: String,
                    @SerializedName("created_at")
                    val createdAt: String,
                    @SerializedName("customized_product")
                    val customizedProduct: String,
                    @SerializedName("description_ar")
                    val descriptionAr: String,
                    @SerializedName("description_en")
                    val descriptionEn: String,
                    @SerializedName("exchangable_days")
                    val exchangableDays: String,
                    @SerializedName("id")
                    val id: Int,
                    @SerializedName("max_card_quantity")
                    val maxCardQuantity: String,
                    @SerializedName("min_card_quantity")
                    val minCardQuantity: String,
                    @SerializedName("other_ar")
                    val otherAr: Any,
                    @SerializedName("other_en")
                    val otherEn: Any,
                    @SerializedName("product_active_status")
                    val productActiveStatus: Int,
                    @SerializedName("product_exchangable")
                    val productExchangable: String,
                    @SerializedName("product_name_ar")
                    val productNameAr: String,
                    @SerializedName("product_name_en")
                    val productNameEn: String,
                    @SerializedName("product_return")
                    val productReturn: String,
                    @SerializedName("ready_to_ship")
                    val readyToShip: String,
                    @SerializedName("return_days")
                    val returnDays: String,
                    @SerializedName("review_rating")
                    val reviewRating: String,
                    @SerializedName("seller_details")
                    val sellerDetails: SellerDetails,
                    @SerializedName("seller_id")
                    val sellerId: String,
                    @SerializedName("store_id")
                    val storeId: String,
                    @SerializedName("subcategory_id")
                    val subcategoryId: String,
                    @SerializedName("updated_at")
                    val updatedAt: String,
                    @SerializedName("upload_size_chart")
                    val uploadSizeChart: String
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
                        @SerializedName("store_information")
                        val storeInformation: String,
                        @SerializedName("updated_at")
                        val updatedAt: String
                    )
                }

                data class UserDetails(
                    @SerializedName("accessToken")
                    val accessToken: String,
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
                    val fcmToken: String,
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

                data class VariationDetails(
                    @SerializedName("actual_price")
                    val actualPrice: String,
                    @SerializedName("bust")
                    val bust: Any,
                    @SerializedName("color_id")
                    val colorId: String,
                    @SerializedName("created_at")
                    val createdAt: String,
                    @SerializedName("discount_type")
                    val discountType: String,
                    @SerializedName("discount_value")
                    val discountValue: String,
                    @SerializedName("final_price")
                    val finalPrice: String,
                    @SerializedName("hip")
                    val hip: Any,
                    @SerializedName("id")
                    val id: Int,
                    @SerializedName("length")
                    val length: Any,
                    @SerializedName("parent_child")
                    val parentChild: String,
                    @SerializedName("product_id")
                    val productId: Int,
                    @SerializedName("shoulder")
                    val shoulder: Any,
                    @SerializedName("size_id")
                    val sizeId: String,
                    @SerializedName("sku")
                    val sku: String,
                    @SerializedName("sku_threshold_value")
                    val skuThresholdValue: Any,
                    @SerializedName("sleeve_length")
                    val sleeveLength: Any,
                    @SerializedName("status")
                    val status: String,
                    @SerializedName("total_stock")
                    val totalStock: String,
                    @SerializedName("updated_at")
                    val updatedAt: String,
                    @SerializedName("variant_image")
                    val variantImage: List<String>,
                    @SerializedName("waist")
                    val waist: Any
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
}