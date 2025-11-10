package com.desynatadmin.ui.home.brandManagement.response

data class CountryREsponse(
    var `data`: ArrayList<Data>,
    var message: String,
    var status: String
) {
    data class Data(
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
        var updated_at: String
    )
}