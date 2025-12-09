package com.desynatadmin.ui.home.shippingAndTaxManagement.response


import com.google.gson.annotations.SerializedName

data class GetShippingChargeResponse(
    @SerializedName("data")
    val data: List<GetShippingTaxResponse.Data.ShippingInfo>? = emptyList(),
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
)