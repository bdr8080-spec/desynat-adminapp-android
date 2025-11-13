package com.desynatadmin.ui.home.brandManagement.response

data class ChangeCityStatusResponse(
    var `data`: Data,
    var message: String,
    var status: String
) {
    data class Data(
        var city_id: Int,
        var country_id: Int,
        var created_at: String,
        var id: Int,
        var name: String,
        var status: Int,
        var updated_at: String
    )
}