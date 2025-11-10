package com.desynatadmin.ui.home.brandManagement.response

data class AddZoneRequest(
    var city_id: Int,
    var coordinates: List<Coordinate>,
    var country_id: Int,
    var name_arabic: String,
    var name_english: String
) {
    data class Coordinate(
        var lat: Double,
        var lng: Double
    )
}