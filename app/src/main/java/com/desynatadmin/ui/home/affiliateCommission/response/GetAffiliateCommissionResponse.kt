package com.desynatadmin.ui.home.affiliateCommission.response

import com.google.gson.annotations.SerializedName

data class GetAffiliateCommissionResponse(
    @SerializedName("data")
    val `data`: List<Data>,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
) {
    data class Data(
        @SerializedName("affiliate_commission_info")
        var affiliateCommissionInfo: AffiliateCommissionInfo,
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
        @SerializedName("updated_at")
        val updatedAt: String
    ) {
        data class AffiliateCommissionInfo(
            @SerializedName("commission")
            val commission: String,
            @SerializedName("country_id")
            val countryId: Int,
            @SerializedName("created_at")
            val createdAt: String,
            @SerializedName("id")
            val id: Int,
            @SerializedName("status")
            val status: Int,
            @SerializedName("updated_at")
            val updatedAt: String
        )
    }
}