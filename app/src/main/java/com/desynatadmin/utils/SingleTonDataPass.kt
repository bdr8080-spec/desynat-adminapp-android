package com.desynatadmin.utils

import com.desynatadmin.ui.home.brandManagement.response.AddZoneRequest

object SingleTonDataPass {
    var zoneId = ""
    var countryId = ""
    var cityId = ""
    var enName = ""
    var arName = ""
    var isEdit = false
    var coordinate  = emptyList<AddZoneRequest.Coordinate>()
}