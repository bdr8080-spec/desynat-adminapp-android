package com.desynatadmin.ui.home.dealsManagement.response


import com.google.gson.annotations.SerializedName

data class UpdateDealStatusResponse(
    @SerializedName("data")
    val `data`: GetActiveDealResponse.Data.Data,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
)