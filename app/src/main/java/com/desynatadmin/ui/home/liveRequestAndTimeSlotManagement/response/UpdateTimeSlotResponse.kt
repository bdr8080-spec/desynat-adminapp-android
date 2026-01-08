package com.desynatadmin.ui.home.liveRequestAndTimeSlotManagement.response


import com.google.gson.annotations.SerializedName

data class UpdateTimeSlotResponse(
    @SerializedName("data")
    val `data`: GetTimeSlotResponse.Data,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
)