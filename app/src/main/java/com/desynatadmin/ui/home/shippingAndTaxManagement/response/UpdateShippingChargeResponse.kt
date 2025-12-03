package com.desynatadmin.ui.home.shippingAndTaxManagement.response


import com.google.gson.annotations.SerializedName

data class UpdateShippingChargeResponse(
    @SerializedName("data")
    val `data`: GetShippingTaxResponse.Data.ShippingInfo,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
)