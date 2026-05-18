package com.desynatadmin.ui.home.brandManagement.response

data class ApprovedSellerResponse(
    var `data`: Data,
    var message: String,
    var status: String
) {
    data class Data(
        var current_page: Int,
        var `data`: ArrayList<Data>,
        var first_page_url: String,
        var from: Int,
        var last_page: Int,
        var last_page_url: String,
        var links: List<Link>,
        var next_page_url: String,
        var path: String,
        var per_page: Int,
        var prev_page_url: Any,
        var to : Int,
        var total: Int
    ) {
        data class Data(
            var accept_terms: String,
            var admin_verify: String,
            var bank_information: String,
            var country_code: String,
            var created_at: String,
            var device_token: String,
            var device_type: String,
            var email: String,
            var first_name: String,
            var id: String,
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
            var subcription_plan: Any,
            var updated_at: String,
            var total_earning: String,
            var total_completed_orders: Int,
            var total_cancelled_orders: Int,
            var isSelected: Boolean = false
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
        }

        data class Link(
            var active: Boolean,
            var label: String,
            var url: String
        )
    }
}