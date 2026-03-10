package com.desynatadmin.ui.home.affiliateWithdrawalRequest.response

data class PayoutResponse(
    val IsSuccess: Boolean,
    val Message: String?,
    val Data: PayoutData?
)

data class PayoutData(
    val ReferenceId: String?,
    val TransactionId: String?,
    val Status: String?
)