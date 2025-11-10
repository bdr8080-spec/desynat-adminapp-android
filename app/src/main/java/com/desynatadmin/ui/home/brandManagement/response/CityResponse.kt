package com.desynatadmin.ui.home.brandManagement.response

data class CityResponse(
    var `data`: List<Data>,
    var message: String,
    var status: String
) {
    data class Data(
        var country_id: Int,
        var created_at: String,
        var id: Int,
        var name: String,
        var updated_at: String
    )
}