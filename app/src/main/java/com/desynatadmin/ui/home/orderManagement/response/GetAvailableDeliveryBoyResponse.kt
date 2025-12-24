package com.desynatadmin.ui.home.orderManagement.response


import com.google.gson.annotations.SerializedName

data class GetAvailableDeliveryBoyResponse(
    @SerializedName("data")
    val `data`: List<Data>,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
) {
    data class Data(
        @SerializedName("address_line1")
        val addressLine1: String,
        @SerializedName("address_line2")
        val addressLine2: String,
        @SerializedName("city")
        val city: String,
        @SerializedName("country_code")
        val countryCode: String,
        @SerializedName("country_id")
        val countryId: Int,
        @SerializedName("created_at")
        val createdAt: String,
        @SerializedName("device_token")
        val deviceToken: Any,
        @SerializedName("device_type")
        val deviceType: String,
        @SerializedName("document_information")
        val documentInformation: String,
        @SerializedName("email")
        val email: String,
        @SerializedName("full_name_arabic")
        val fullNameArabic: String,
        @SerializedName("full_name_english")
        val fullNameEnglish: String,
        @SerializedName("id")
        val id: Int,
        @SerializedName("let")
        val let: String,
        @SerializedName("long")
        val long: String,
        @SerializedName("mobile")
        val mobile: String,
        @SerializedName("on_duty")
        val onDuty: String,
        @SerializedName("otp")
        val otp: String,
        @SerializedName("otp_verify")
        val otpVerify: String,
        @SerializedName("password")
        val password: String,
        @SerializedName("profile_pic")
        val profilePic: String,
        @SerializedName("state")
        val state: String,
        @SerializedName("status")
        val status: String,
        @SerializedName("unhashed_password")
        val unhashedPassword: String,
        @SerializedName("updated_at")
        val updatedAt: String,
        @SerializedName("vehicle_name")
        val vehicleName: String,
        @SerializedName("vehicle_reg_no")
        val vehicleRegNo: String,
        @SerializedName("zone_id")
        val zoneId: Int
    )
}