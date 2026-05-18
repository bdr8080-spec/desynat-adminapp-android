package com.desynatadmin.ui.home.brandManagement.response

import com.desynatadmin.ui.home.storyManagement.response.GetStoryResponse
import com.google.gson.annotations.SerializedName

data class SellerDetailResponse(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
)
{
    data class Data(
        @SerializedName("accept_terms")
        val acceptTerms: String,
        @SerializedName("admin_verify")
        val adminVerify: String,
        @SerializedName("affilate_commission")
        val affilateCommission: String,
        @SerializedName("app_notification")
        val appNotification: Int,
        @SerializedName("bank_information")
        val bankInformation: String,
        @SerializedName("country_code")
        val countryCode: String,
        @SerializedName("created_at")
        val createdAt: String,
        @SerializedName("deleted_at")
        val deletedAt: Any,
        @SerializedName("device_token")
        val deviceToken: String,
        @SerializedName("device_type")
        val deviceType: String,
        @SerializedName("email")
        val email: String,
        @SerializedName("fcm_token")
        val fcmToken: String,
        @SerializedName("first_name")
        val firstName: String,
        @SerializedName("highlights")
        val highlights: List<GetStoryResponse.Data.Data>,
        @SerializedName("id")
        val id: Int,
        @SerializedName("is_block")
        val isBlock: Int,
        @SerializedName("lang")
        val lang: String,
        @SerializedName("last_name")
        val lastName: String,
        @SerializedName("mobile")
        val mobile: String,
        @SerializedName("name_ar")
        val nameAr: Any,
        @SerializedName("notification")
        val notification: Int,
        @SerializedName("otp")
        val otp: String,
        @SerializedName("otp_verify")
        val otpVerify: String,
        @SerializedName("owner_document")
        val ownerDocument: List<OwnerDocument>,
        @SerializedName("parent_id")
        val parentId: Any,
        @SerializedName("password")
        val password: String,
        @SerializedName("profile_pic")
        val profilePic: String,
        @SerializedName("profile_qr")
        val profileQr: String,
        @SerializedName("qrSellerUrl")
        val qrSellerUrl: String,
        @SerializedName("reels")
        val reels: List<GetStoryResponse.Data.Data>,
        @SerializedName("reject_reason")
        val rejectReason: String,
        @SerializedName("save_password")
        val savePassword: Any,
        @SerializedName("select_category")
        val selectCategory: Any,
        @SerializedName("seller_bank_details")
        val sellerBankDetails: SellerBankDetails,
        @SerializedName("store_document")
        val storeDocument: List<StoreDocument>,
        @SerializedName("store_info")
        val storeInfo: StoreInfo,
        @SerializedName("store_information")
        val storeInformation: String,
        @SerializedName("stories")
        val stories: List<GetStoryResponse.Data.Data>,
        @SerializedName("subcription_plan")
        val subcriptionPlan: List<SubcriptionPlan>,
        @SerializedName("total_cancelled_orders")
        val totalCancelledOrders: Int,
        @SerializedName("total_completed_orders")
        val totalCompletedOrders: Int,
        @SerializedName("total_earning")
        val totalEarning: Int,

        @SerializedName("average_product_rating")
        val averageProductRating: String,
        @SerializedName("average_store_rating")
        val averageStoreRating: String,

        @SerializedName("type")
        val type: String,
        @SerializedName("updated_at")
        val updatedAt: String
    ) {
        data class Highlight(
            @SerializedName("created_at")
            val createdAt: String,
            @SerializedName("file")
            val file: String,
            @SerializedName("id")
            val id: Int,
            @SerializedName("seller_id")
            val sellerId: Int,
            @SerializedName("status")
            val status: Int,
            @SerializedName("thumbnail")
            val thumbnail: String,
            @SerializedName("title")
            val title: String,
            @SerializedName("type")
            val type: Int,
            @SerializedName("updated_at")
            val updatedAt: String
        )

        data class OwnerDocument(
            @SerializedName("created_at")
            val createdAt: String,
            @SerializedName("document_path")
            val documentPath: String,
            @SerializedName("document_type")
            val documentType: String,
            @SerializedName("id")
            val id: Int,
            @SerializedName("seller_id")
            val sellerId: Int,
            @SerializedName("status")
            val status: String,
            @SerializedName("updated_at")
            val updatedAt: String
        )

        data class Reel(
            @SerializedName("caption")
            val caption: String,
            @SerializedName("created_at")
            val createdAt: String,
            @SerializedName("file")
            val `file`: String,
            @SerializedName("id")
            val id: Int,
            @SerializedName("seller_id")
            val sellerId: Int,
            @SerializedName("status")
            val status: Int,
            @SerializedName("thumbnail")
            val thumbnail: String,
            @SerializedName("type")
            val type: Int,
            @SerializedName("updated_at")
            val updatedAt: String
        )

        data class SellerBankDetails(
            @SerializedName("account_holder_name")
            val accountHolderName: String,
            @SerializedName("account_no")
            val accountNo: Any,
            @SerializedName("account_type")
            val accountType: String,
            @SerializedName("bank_name")
            val bankName: String,
            @SerializedName("created_at")
            val createdAt: String,
            @SerializedName("default_account")
            val defaultAccount: String,
            @SerializedName("iban")
            val iban: String,
            @SerializedName("id")
            val id: Int,
            @SerializedName("swift")
            val swift: String,
            @SerializedName("type")
            val type: String,
            @SerializedName("updated_at")
            val updatedAt: String,
            @SerializedName("upload_proof")
            val uploadProof: String,
            @SerializedName("user_id")
            val userId: String
        )

        data class StoreDocument(
            @SerializedName("created_at")
            val createdAt: String,
            @SerializedName("document_path")
            val documentPath: String,
            @SerializedName("document_type")
            val documentType: String,
            @SerializedName("id")
            val id: Int,
            @SerializedName("seller_id")
            val sellerId: Int,
            @SerializedName("status")
            val status: String,
            @SerializedName("updated_at")
            val updatedAt: String
        )

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
                @SerializedName("name_ar")
                val nameAr: String,
                @SerializedName("updated_at")
                val updatedAt: String
            )
        }

        data class Story(
            @SerializedName("created_at")
            val createdAt: String,
            @SerializedName("file")
            val `file`: String,
            @SerializedName("id")
            val id: Int,
            @SerializedName("seller_id")
            val sellerId: Int,
            @SerializedName("status")
            val status: Int,
            @SerializedName("thumbnail")
            val thumbnail: String,
            @SerializedName("type")
            val type: Int,
            @SerializedName("updated_at")
            val updatedAt: String
        )

        data class SubcriptionPlan(
            @SerializedName("amount")
            val amount: String,
            @SerializedName("created_at")
            val createdAt: String,
            @SerializedName("id")
            val id: Int,
            @SerializedName("is_expired")
            val isExpired: String,
            @SerializedName("no_of_employee")
            val noOfEmployee: String,
            @SerializedName("no_of_highlights")
            val noOfHighlights: String,
            @SerializedName("no_of_products")
            val noOfProducts: Any,
            @SerializedName("no_of_videos")
            val noOfVideos: String,
            @SerializedName("online_transaction_id")
            val onlineTransactionId: String,
            @SerializedName("payment_method")
            val paymentMethod: String,
            @SerializedName("payment_status")
            val paymentStatus: String,
            @SerializedName("plan_end_date")
            val planEndDate: String,
            @SerializedName("plan_id")
            val planId: Int,
            @SerializedName("plan_info")
            val planInfo: PlanInfo,
            @SerializedName("plan_start_date")
            val planStartDate: String,
            @SerializedName("product_type")
            val productType: String,
            @SerializedName("seller_id")
            val sellerId: Int,
            @SerializedName("status")
            val status: String,
            @SerializedName("stories")
            val stories: String,
            @SerializedName("transaction_id")
            val transactionId: String,
            @SerializedName("updated_at")
            val updatedAt: String
        ) {
            data class PlanInfo(
                @SerializedName("commission")
                val commission: Int,
                @SerializedName("commission_percentage")
                val commissionPercentage: Int,
                @SerializedName("country_id")
                val countryId: Int,
                @SerializedName("created_at")
                val createdAt: String,
                @SerializedName("discounted_price")
                val discountedPrice: Int,
                @SerializedName("duration")
                val duration: String,
                @SerializedName("duration_type")
                val durationType: Int,
                @SerializedName("id")
                val id: Int,
                @SerializedName("is_free_months")
                val isFreeMonths: String,
                @SerializedName("new_duration")
                val newDuration: Int,
                @SerializedName("new_duration_type")
                val newDurationType: Any,
                @SerializedName("no_of_employee")
                val noOfEmployee: Int,
                @SerializedName("no_of_free_months")
                val noOfFreeMonths: String,
                @SerializedName("no_of_highlights")
                val noOfHighlights: Int,
                @SerializedName("no_of_products")
                val noOfProducts: Any,
                @SerializedName("no_of_videos")
                val noOfVideos: Int,
                @SerializedName("plan_name_arabic")
                val planNameArabic: String,
                @SerializedName("plan_name_english")
                val planNameEnglish: String,
                @SerializedName("price")
                val price: String,
                @SerializedName("product_type")
                val productType: String,
                @SerializedName("status")
                val status: Int,
                @SerializedName("stories")
                val stories: Int,
                @SerializedName("updated_at")
                val updatedAt: String
            )
        }
    }
}