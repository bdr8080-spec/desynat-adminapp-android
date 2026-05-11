package com.desynatadmin.ui.home.dealsManagement.response


import com.desynatadmin.ui.home.categorySubCategoryManagement.response.GetCategoryResponse
import com.desynatadmin.ui.home.categorySubCategoryManagement.response.GetSubCategoryResponse
import com.google.gson.annotations.SerializedName

data class GetDealDetailResponse(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
) {
    data class Data(
        @SerializedName("category_details")
        val categoryDetails: GetCategoryResponse.Data,
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
        @SerializedName("subCategory_details")
        val subCategoryDetails: List<GetSubCategoryResponse.Data.Data>,
        @SerializedName("sub_category_id")
        val subCategoryId: List<String>,
        @SerializedName("to")
        val to: String,
        @SerializedName("updated_at")
        val updatedAt: String
    ) {
        data class SellerDealrecord(
            @SerializedName("deal_id")
            val dealId: Int,
            @SerializedName("product_count")
            val productCount: Int,
            @SerializedName("seller_details")
            val sellerDetails: SellerDetails,
            @SerializedName("seller_id")
            val sellerId: String
        ) {
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
                    @SerializedName("name_ar")
                    val nameAr: String,
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