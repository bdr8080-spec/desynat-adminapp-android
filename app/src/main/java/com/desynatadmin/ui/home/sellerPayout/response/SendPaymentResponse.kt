package com.desynatadmin.ui.home.sellerPayout.response

import com.google.gson.annotations.SerializedName

data class SendPaymentResponse(
    @SerializedName("IsSuccess") val isSuccess: Boolean?,
    @SerializedName("Message") val message: String?,
    @SerializedName("ValidationErrors") val validationErrors: Any?,
    @SerializedName("Data") val data: SendPaymentData?
)

data class SendPaymentData(
    @SerializedName("InvoiceId") val invoiceId: Int?,
    @SerializedName("InvoiceURL") val invoiceURL: String?,
    @SerializedName("CustomerReference") val customerReference: String?,
    @SerializedName("UserDefinedField") val userDefinedField: String?
)