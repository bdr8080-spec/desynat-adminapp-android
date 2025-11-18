package com.desynatadmin.ui.home.deliveryBoyManagement.response


import com.google.gson.annotations.SerializedName

data class GetDeliveryBoyDetailResponse(
    @SerializedName("data")
    val `data`: GetAllDeliveryBoyResponse.Data.Data,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
)