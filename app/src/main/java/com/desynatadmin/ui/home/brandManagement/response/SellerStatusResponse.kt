package com.desynatadmin.ui.home.brandManagement.response

data class SellerStatusResponse(
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
        var password: String,
        var profile_pic: String,
        var reject_reason: String,
        var store_information: String,
        var updated_at: String
    )
}