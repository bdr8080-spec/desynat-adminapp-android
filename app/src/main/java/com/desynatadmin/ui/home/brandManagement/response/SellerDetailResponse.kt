package com.desynatadmin.ui.home.brandManagement.response

data class SellerDetailResponse(
    var `data`: Data,
    var message: String,
    var status: String
) {
    data class Data(
        var accept_terms: String,
        var admin_verify: String,
        var bank_information: String,
        var country_code: String,
        var created_at: String,
        var device_token: Any,
        var device_type: Any,
        var email: String,
        var first_name: String,
        var id: Int,
        var is_block: Int,
        var lang: Any,
        var last_name: String,
        var mobile: String,
        var otp: String,
        var otp_verify: String,
        var owner_document: List<OwnerDocument>,
        var password: String,
        var profile_pic: String,
        var reject_reason: String,
        var seller_bank_details: SellerBankDetails,
        var store_document: List<StoreDocument>,
        var store_info: StoreInfo,
        var store_information: String,
        var subcription_plan: List<SubcriptionPlan>,
        var updated_at: String
    ) {
        data class OwnerDocument(
            var created_at: String,
            var document_path: String,
            var document_type: String,
            var id: Int,
            var seller_id: Int,
            var status: String,
            var updated_at: String
        )

        data class SellerBankDetails(
            var account_holder_name: String,
            var account_type: String,
            var bank_name: String,
            var created_at: String,
            var iban: String,
            var id: Int,
            var swift: String,
            var type: String,
            var updated_at: String,
            var upload_proof: String,
            var user_id: String
        )

        data class StoreDocument(
            var created_at: String,
            var document_path: String,
            var document_type: String,
            var id: Int,
            var seller_id: Int,
            var status: String,
            var updated_at: String
        )

        data class StoreInfo(
            var address_line1: String,
            var address_line2: String,
            var bio: String,
            var country: String,
            var country_code: String,
            var country_info: CountryInfo,
            var created_at: String,
            var id: Int,
            var let: String,
            var long: String,
            var seller_id: Int,
            var store_address: String,
            var store_image: String,
            var store_mail_id: String,
            var store_name: String,
            var store_phone: String,
            var updated_at: String
        ) {
            data class CountryInfo(
                var created_at: String,
                var currency_code: String,
                var currency_name: String,
                var currency_symbol: String,
                var dial_code: String,
                var exchange_rate_to_kwd: String,
                var flag: String,
                var flag1: String,
                var id: Int,
                var iso_code: String,
                var name: String,
                var name_ar: String,
                var updated_at: String
            )
        }

        data class SubcriptionPlan(
            var amount: String,
            var created_at: String,
            var id: Int,
            var online_transaction_id: Any,
            var payment_method: String,
            var payment_status: String,
            var plan_end_date: String,
            var plan_id: Int,
            var plan_info: PlanInfo,
            var plan_start_date: String,
            var seller_id: Int,
            var status: String,
            var transaction_id: String,
            var updated_at: String
        ) {
            data class PlanInfo(
                var commission: Int,
                var commission_percentage: Any,
                var country_id: Int,
                var created_at: String,
                var duration: String,
                var duration_type: Int,
                var id: Int,
                var no_of_employee: Int,
                var no_of_highlights: Int,
                var no_of_products: Int,
                var no_of_videos: Int,
                var plan_name_arabic: String,
                var plan_name_english: String,
                var price: Double,
                var status: Int,
                var stories: Int,
                var updated_at: String
            )
        }
    }
}