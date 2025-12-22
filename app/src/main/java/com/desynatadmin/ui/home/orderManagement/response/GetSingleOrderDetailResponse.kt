package com.desynatadmin.ui.home.orderManagement.response


import com.google.gson.annotations.SerializedName

data class GetSingleOrderDetailResponse(
    @SerializedName("data")
    val `data`: List<GetActiveOrderResponse.Data.Data>,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
)