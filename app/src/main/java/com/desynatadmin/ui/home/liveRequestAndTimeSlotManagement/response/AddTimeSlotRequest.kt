package com.desynatadmin.ui.home.liveRequestAndTimeSlotManagement.response


import com.google.gson.annotations.SerializedName

data class AddTimeSlotRequest(
    @SerializedName("country_id")
    val countryId: String,
    @SerializedName("date")
    val date: String,
    @SerializedName("from")
    val from: String,
    @SerializedName("to")
    val to: String,
    @SerializedName("timeslot_id")
    val timeslotId: String?=null,
)