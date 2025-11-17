package com.desynatadmin.ui.home.userManagement.response


import com.google.gson.annotations.SerializedName

data class GetUserDetailResponse(
    @SerializedName("data")
    val `data`: GetUserResponse.Data.Data,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
)