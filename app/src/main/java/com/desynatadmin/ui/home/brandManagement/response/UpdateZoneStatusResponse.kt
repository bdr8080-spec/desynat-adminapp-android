package com.desynatadmin.ui.home.brandManagement.response

data class UpdateZoneStatusResponse(
    var `data`: Data,
    var message: String,
    var status: String
) {
    data class Data(
        var city_id: Int,
        var coordinates: List<Coordinate>,
        var country_id: Int,
        var created_at: String,
        var id: Int,
        var name_arabic: String,
        var name_english: String,
        var status: Int,
        var updated_at: String
    ) {
        data class Coordinate(
            var lat: Double,
            var lng: Double
        )
    }
}