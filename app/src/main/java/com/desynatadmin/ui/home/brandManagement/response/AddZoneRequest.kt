package com.desynatadmin.ui.home.brandManagement.response

data class AddZoneRequest(
    var zone_id: String? = null,
    var city_id: String,
    var coordinates: List<Coordinate>,
    var country_id: String,
    var name_arabic: String,
    var name_english: String
) {
    data class Coordinate(
        var lat: Double,
        var lng: Double
    )
}

