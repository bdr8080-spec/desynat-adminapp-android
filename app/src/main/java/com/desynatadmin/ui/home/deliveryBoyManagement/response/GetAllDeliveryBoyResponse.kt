package com.desynatadmin.ui.home.deliveryBoyManagement.response


import com.google.gson.annotations.SerializedName

data class GetAllDeliveryBoyResponse(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
) {
    data class Data(
        @SerializedName("current_page")
        val currentPage: Int,
        @SerializedName("data")
        val `data`: List<Data>,
        @SerializedName("first_page_url")
        val firstPageUrl: String,
        @SerializedName("from")
        val from: Int,
        @SerializedName("last_page")
        val lastPage: Int,
        @SerializedName("last_page_url")
        val lastPageUrl: String,
        @SerializedName("links")
        val links: List<Link>,
        @SerializedName("next_page_url")
        val nextPageUrl: Any,
        @SerializedName("path")
        val path: String,
        @SerializedName("per_page")
        val perPage: Int,
        @SerializedName("prev_page_url")
        val prevPageUrl: Any,
        @SerializedName("to")
        val to: Int,
        @SerializedName("total")
        val total: Int
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
            @SerializedName("driving_license")
            val drivingLicense: List<DrivingLicense>,
            @SerializedName("email")
            val email: String,
            @SerializedName("full_name_arabic")
            val fullNameArabic: String,
            @SerializedName("full_name_english")
            val fullNameEnglish: String,
            @SerializedName("id")
            val id: Int,
            @SerializedName("identity_proof")
            val identityProof: List<IdentityProof>,
            @SerializedName("let")
            val let: Double,
            @SerializedName("long")
            val long: Double,
            @SerializedName("mobile")
            val mobile: String,
            @SerializedName("otp")
            val otp: String,
            @SerializedName("otp_verify")
            val otpVerify: String,
            @SerializedName("password")
            val password: String,
            @SerializedName("pollution_paper")
            val pollutionPaper: List<PollutionPaper>,
            @SerializedName("profile_pic")
            val profilePic: String,
            @SerializedName("registration_certificate")
            val registrationCertificate: List<RegistrationCertificate>,
            @SerializedName("state")
            val state: String,
            @SerializedName("status")
            var status: String,
            @SerializedName("updated_at")
            val updatedAt: String,
            @SerializedName("vehicle_image")
            val vehicleImage: List<VehicleImage>,
            @SerializedName("vehicle_insurance")
            val vehicleInsurance: List<VehicleInsurance>,
            @SerializedName("vehicle_name")
            val vehicleName: String,
            @SerializedName("vehicle_reg_no")
            val vehicleRegNo: String,
            @SerializedName("zone_id")
            val zoneId: String,
            @SerializedName("zone_info")
            val zoneInfo: ZoneInfo
        ) {
            data class DrivingLicense(
                @SerializedName("created_at")
                val createdAt: String,
                @SerializedName("delivery_boy_id")
                val deliveryBoyId: Int,
                @SerializedName("document_path")
                val documentPath: String,
                @SerializedName("document_type")
                val documentType: String,
                @SerializedName("id")
                val id: Int,
                @SerializedName("is_front")
                val isFront: Int,
                @SerializedName("status")
                val status: Int,
                @SerializedName("updated_at")
                val updatedAt: String
            )

            data class PollutionPaper(
                @SerializedName("created_at")
                val createdAt: String,
                @SerializedName("delivery_boy_id")
                val deliveryBoyId: Int,
                @SerializedName("document_path")
                val documentPath: String,
                @SerializedName("document_type")
                val documentType: String,
                @SerializedName("id")
                val id: Int,
                @SerializedName("is_front")
                val isFront: Int,
                @SerializedName("status")
                val status: Int,
                @SerializedName("updated_at")
                val updatedAt: String
            )

            data class RegistrationCertificate(
                @SerializedName("created_at")
                val createdAt: String,
                @SerializedName("delivery_boy_id")
                val deliveryBoyId: Int,
                @SerializedName("document_path")
                val documentPath: String,
                @SerializedName("document_type")
                val documentType: String,
                @SerializedName("id")
                val id: Int,
                @SerializedName("is_front")
                val isFront: Int,
                @SerializedName("status")
                val status: Int,
                @SerializedName("updated_at")
                val updatedAt: String
            )

            data class IdentityProof(
                @SerializedName("created_at")
                val createdAt: String,
                @SerializedName("delivery_boy_id")
                val deliveryBoyId: Int,
                @SerializedName("document_path")
                val documentPath: String,
                @SerializedName("document_type")
                val documentType: String,
                @SerializedName("id")
                val id: Int,
                @SerializedName("is_front")
                val isFront: Int,
                @SerializedName("status")
                val status: Int,
                @SerializedName("updated_at")
                val updatedAt: String
            )

            data class VehicleImage(
                @SerializedName("created_at")
                val createdAt: String,
                @SerializedName("delivery_boy_id")
                val deliveryBoyId: Int,
                @SerializedName("document_path")
                val documentPath: String,
                @SerializedName("document_type")
                val documentType: String,
                @SerializedName("id")
                val id: Int,
                @SerializedName("is_front")
                val isFront: Int,
                @SerializedName("status")
                val status: Int,
                @SerializedName("updated_at")
                val updatedAt: String
            )

            data class VehicleInsurance(
                @SerializedName("created_at")
                val createdAt: String,
                @SerializedName("delivery_boy_id")
                val deliveryBoyId: Int,
                @SerializedName("document_path")
                val documentPath: String,
                @SerializedName("document_type")
                val documentType: String,
                @SerializedName("id")
                val id: Int,
                @SerializedName("is_front")
                val isFront: Int,
                @SerializedName("status")
                val status: Int,
                @SerializedName("updated_at")
                val updatedAt: String
            )

            data class ZoneInfo(
                @SerializedName("city_id")
                val cityId: Int,
                @SerializedName("coordinates")
                val coordinates: List<Coordinate>,
                @SerializedName("country_id")
                val countryId: Int,
                @SerializedName("created_at")
                val createdAt: String,
                @SerializedName("id")
                val id: Int,
                @SerializedName("name_arabic")
                val nameArabic: String,
                @SerializedName("name_english")
                val nameEnglish: String,
                @SerializedName("status")
                val status: Int,
                @SerializedName("updated_at")
                val updatedAt: String
            ) {
                data class Coordinate(
                    @SerializedName("lat")
                    val lat: Double,
                    @SerializedName("lng")
                    val lng: Double
                )
            }
        }

        data class Link(
            @SerializedName("active")
            val active: Boolean,
            @SerializedName("label")
            val label: String,
            @SerializedName("url")
            val url: String
        )
    }
}