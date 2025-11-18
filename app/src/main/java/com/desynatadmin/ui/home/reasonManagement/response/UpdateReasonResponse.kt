package com.desynatadmin.ui.home.reasonManagement.response


import com.google.gson.annotations.SerializedName

data class UpdateReasonResponse(
    @SerializedName("data")
    val `data`: GetReasonResponse.Data.Data,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
)