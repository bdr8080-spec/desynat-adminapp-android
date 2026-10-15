package com.desynatadmin.ui.home.sellerPayout.response

import com.google.gson.annotations.SerializedName

//data class SendPaymentRequest(
//    @SerializedName("CustomerName") val customerName: String,
//    @SerializedName("InvoiceValue") val invoiceValue: Double,
//    @SerializedName("NotificationOption") val notificationOption: String,
//    @SerializedName("CallBackUrl") val callBackUrl: String? = null,
//    @SerializedName("ErrorUrl") val errorUrl: String? = null,
//    @SerializedName("Language") val language: String? = "en",
//    @SerializedName("CurrencyIso") val currencyIso: String? = "KWD"
//)

data class SendPaymentRequest(
    @SerializedName("CustomerName") val customerName: String,
    @SerializedName("InvoiceValue") val invoiceValue: Double,
    @SerializedName("NotificationOption") val notificationOption: String,
    @SerializedName("CallBackUrl") val callBackUrl: String? = null,
    @SerializedName("ErrorUrl") val errorUrl: String? = null,
    @SerializedName("Language") val language: String? = "en",
    @SerializedName("DisplayCurrencyIso") val displayCurrencyIso: String? = "KWD",
//    @SerializedName("CustomerReference") val customerReference: String? = null
)