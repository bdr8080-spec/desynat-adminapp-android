package com.desynatadmin.ui.home.shippingAndTaxManagement.response


import com.google.gson.annotations.SerializedName

data class AddTaxResponse(
    @SerializedName("data")
    val `data`: GetShippingTaxResponse.Data.TaxInfo,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
)