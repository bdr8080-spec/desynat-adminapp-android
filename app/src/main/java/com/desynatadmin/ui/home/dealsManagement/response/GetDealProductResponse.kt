package com.desynatadmin.ui.home.dealsManagement.response


import com.google.gson.annotations.SerializedName

data class GetDealProductResponse(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
) {
    data class Data(
        @SerializedName("category_details")
        val categoryDetails: CategoryDetails,
        @SerializedName("category_id")
        val categoryId: Int,
        @SerializedName("created_at")
        val createdAt: String,
        @SerializedName("discount")
        val discount: String,
        @SerializedName("from")
        val from: String,
        @SerializedName("id")
        val id: Int,
        @SerializedName("image")
        val image: String,
        @SerializedName("name_arabic")
        val nameArabic: String,
        @SerializedName("name_english")
        val nameEnglish: String,
        @SerializedName("seller_dealrecords")
        val sellerDealrecords: List<SellerDealrecord>,
        @SerializedName("status")
        val status: Int,
        @SerializedName("sub_category_details")
        val subCategoryDetails: SubCategoryDetails,
        @SerializedName("sub_category_id")
        val subCategoryId: Int,
        @SerializedName("to")
        val to: String,
        @SerializedName("updated_at")
        val updatedAt: String
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

        data class SellerDealrecord(
            @SerializedName("deal_id")
            val dealId: Int,
            @SerializedName("product_count")
            val productCount: Int,
            @SerializedName("products")
            val products: List<Product>,
            @SerializedName("seller_details")
            val sellerDetails: SellerDetails,
            @SerializedName("seller_id")
            val sellerId: String
        ) {
            data class Product(
                @SerializedName("created_at")
                val createdAt: String,
                @SerializedName("deal_id")
                val dealId: Int,
                @SerializedName("discount")
                val discount: String,
                @SerializedName("id")
                val id: Int,
                @SerializedName("product_details")
                val productDetails: ProductDetails,
                @SerializedName("product_id")
                val productId: Int,
                @SerializedName("seller_id")
                val sellerId: String,
                @SerializedName("updated_at")
                val updatedAt: String
            ) {
                data class ProductDetails(
                    @SerializedName("affiliated_commission")
                    val affiliatedCommission: String,
                    @SerializedName("affiliated_product")
                    val affiliatedProduct: String,
                    @SerializedName("brand")
                    val brand: Brand,
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
                    val otherAr: String,
                    @SerializedName("other_en")
                    val otherEn: String,
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
                    @SerializedName("product_variants")
                    val productVariants: List<ProductVariant>,
                    @SerializedName("ready_to_ship")
                    val readyToShip: String,
                    @SerializedName("return_days")
                    val returnDays: String,
                    @SerializedName("review_rating")
                    val reviewRating: String,
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
                    data class Brand(
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

                    data class ProductVariant(
                        @SerializedName("actual_price")
                        val actualPrice: Double,
                        @SerializedName("bust")
                        val bust: String,
                        @SerializedName("color_id")
                        val colorId: String,
                        @SerializedName("created_at")
                        val createdAt: String,
                        @SerializedName("deal_discount_price")
                        val dealDiscountPrice: Double,
                        @SerializedName("discount_type")
                        val discountType: String,
                        @SerializedName("discount_value")
                        val discountValue: Double,
                        @SerializedName("final_price")
                        val finalPrice: Double,
                        @SerializedName("hip")
                        val hip: String,
                        @SerializedName("id")
                        val id: Int,
                        @SerializedName("length")
                        val length: String,
                        @SerializedName("parent_child")
                        val parentChild: String,
                        @SerializedName("product_id")
                        val productId: Int,
                        @SerializedName("shoulder")
                        val shoulder: String,
                        @SerializedName("size_id")
                        val sizeId: String,
                        @SerializedName("sku")
                        val sku: String,
                        @SerializedName("sku_threshold_value")
                        val skuThresholdValue: Any,
                        @SerializedName("sleeve_length")
                        val sleeveLength: String,
                        @SerializedName("status")
                        val status: String,
                        @SerializedName("total_stock")
                        val totalStock: String,
                        @SerializedName("updated_at")
                        val updatedAt: String,
                        @SerializedName("variant_image")
                        val variantImage: ArrayList<String>,
                        @SerializedName("waist")
                        val waist: String
                    )
                }
            }

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
                val deviceToken: Any,
                @SerializedName("device_type")
                val deviceType: Any,
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
        }

        data class SubCategoryDetails(
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
}