package com.desynatadmin.ui.home.affiliateWithdrawalRequest.response

data class PayoutRequest(
    val CustomerName: String,
    val Amount: Double,
    val CurrencyCode: String,
    val IBAN: String,
    val BankCode: String,
    val AccountNumber: String
)