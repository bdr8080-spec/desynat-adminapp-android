package com.desynatadmin.ui.home.subscriptionPlan.response


import com.google.gson.annotations.SerializedName

data class UpdatePlanStatusResponse(
    @SerializedName("data")
    val `data`: GetAllPlanResponse.Data,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
)