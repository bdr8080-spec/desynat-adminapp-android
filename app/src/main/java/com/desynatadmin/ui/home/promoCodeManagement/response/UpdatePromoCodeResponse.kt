package com.desynatadmin.ui.home.promoCodeManagement.response


import com.google.gson.annotations.SerializedName

data class UpdatePromoCodeResponse(
    @SerializedName("data")
    val `data`: GetPromoCodeResponse.Data,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
)