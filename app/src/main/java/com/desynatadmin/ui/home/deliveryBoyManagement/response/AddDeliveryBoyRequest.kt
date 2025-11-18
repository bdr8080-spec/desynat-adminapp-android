package com.desynatadmin.ui.home.deliveryBoyManagement.response


import com.google.gson.annotations.SerializedName

data class AddDeliveryBoyRequest(
    @SerializedName("address_line1")
    val addressLine1: String,
    @SerializedName("address_line2")
    val addressLine2: String,
    @SerializedName("city")
    val city: String,
    @SerializedName("country_code")
    val countryCode: String,
    @SerializedName("driving_license_back")
    val drivingLicenseBack: String?=null,
    @SerializedName("driving_license_front")
    val drivingLicenseFront: String?=null,
    @SerializedName("email")
    val email: String,
    @SerializedName("full_name_arabic")
    val fullNameArabic: String,
    @SerializedName("full_name_english")
    val fullNameEnglish: String,
    @SerializedName("identity_proof_back")
    val identityProofBack: String?=null,
    @SerializedName("identity_proof_front")
    val identityProofFront: String?=null,
    @SerializedName("let")
    val let: Double,
    @SerializedName("long")
    val long: Double,
    @SerializedName("mobile")
    val mobile: String,
    @SerializedName("password")
    val password: String,
    @SerializedName("confirm_password")
    val confirmPassword: String,
    @SerializedName("pollution_paper")
    val pollutionPaper: String?=null,
    @SerializedName("profile_pic")
    val profilePic: String?=null,
    @SerializedName("registration_certificate_back")
    val registrationCertificateBack: String?=null,
    @SerializedName("registration_certificate_front")
    val registrationCertificateFront: String?=null,
    @SerializedName("state")
    val state: String,
    @SerializedName("vehicle_image_back")
    val vehicleImageBack: String?=null,
    @SerializedName("vehicle_image_front")
    val vehicleImageFront: String?=null,
    @SerializedName("vehicle_insurance")
    val vehicleInsurance: String?=null,
    @SerializedName("vehicle_name")
    val vehicleName: String,
    @SerializedName("vehicle_reg_no")
    val vehicleRegNo: String,
    @SerializedName("zone_id")
    val zoneId: String?=null,
    @SerializedName("delivery_boy_id")
    val deliveryBoyId: String?=null
)