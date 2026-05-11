package com.desynatadmin.ui.home.shippingAndTaxManagement.response


import com.google.gson.annotations.SerializedName

data class GetShippingTaxResponse(
    @SerializedName("data")
    val `data`: List<Data>,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
) {
    data class Data(
        @SerializedName("created_at")
        val createdAt: String,
        @SerializedName("currency_code")
        val currencyCode: String,
        @SerializedName("currency_name")
        val currencyName: String,
        @SerializedName("currency_symbol")
        val currencySymbol: String,
        @SerializedName("dial_code")
        val dialCode: String,
        @SerializedName("exchange_rate_to_kwd")
        val exchangeRateToKwd: String,
        @SerializedName("flag")
        val flag: String,
        @SerializedName("flag1")
        val flag1: String,
        @SerializedName("id")
        val id: Int,
        @SerializedName("iso_code")
        val isoCode: String,
        @SerializedName("name")
        val name: String,
        @SerializedName("name_ar")
        val nameAr: String,
        @SerializedName("shipping_info")
        val shippingInfo: List<ShippingInfo>,
        @SerializedName("tax_info")
        var taxInfo: TaxInfo,
        @SerializedName("updated_at")
        val updatedAt: String
    ) {
        data class ShippingInfo(
            @SerializedName("country_id")
            val countryId: Int,
            @SerializedName("created_at")
            val createdAt: String,
            @SerializedName("id")
            val id: Int,
            @SerializedName("range_from")
            val rangeFrom: String,
            @SerializedName("range_to")
            val rangeTo: String,
            @SerializedName("shipping_fee")
            val shippingFee: String,
            @SerializedName("status")
            var status: Int,
            @SerializedName("updated_at")
            val updatedAt: String
        )

        data class TaxInfo(
            @SerializedName("country_id")
            val countryId: Int,
            @SerializedName("created_at")
            val createdAt: String,
            @SerializedName("id")
            val id: Int,
            @SerializedName("status")
            val status: Int,
            @SerializedName("tax")
            val tax: String,
            @SerializedName("updated_at")
            val updatedAt: String
        )
    }
}