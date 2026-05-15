package com.desynatadmin.ui.home.affiliateCommission.response

import com.google.gson.annotations.SerializedName

data class UpdateAffiliateCommissionResponse(
    @SerializedName("data")
    val `data`: GetAffiliateCommissionResponse.Data.AffiliateCommissionInfo,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
)