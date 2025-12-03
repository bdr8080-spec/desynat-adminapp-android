package com.desynatadmin.ui.home.giftCardManagement.response


import com.google.gson.annotations.SerializedName

data class UpdateGiftCardResponse(
    @SerializedName("data")
    val `data`: GetAllGiftCardResponse.Data.Data,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
)