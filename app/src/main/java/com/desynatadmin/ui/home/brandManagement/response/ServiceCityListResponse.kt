package com.desynatadmin.ui.home.brandManagement.response

data class ServiceCityListResponse(
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
        var next_page_url: Any,
        var path: String,
        var per_page: Int,
        var prev_page_url: Any,
        var to: Int,
        var total: Int
    ) {
        data class Data(
            var city_id: String,
            var country_id: String,
            var created_at: String,
            var id: String,
            var name: String,
            var status: Int,
            var updated_at: String
        )

        data class Link(
            var active: Boolean,
            var label: String,
            var url: String
        )
    }
}