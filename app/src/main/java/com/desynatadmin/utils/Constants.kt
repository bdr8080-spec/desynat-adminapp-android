package com.desynatadmin.utils

object Constants {

    const val BASE_URL = "http://157.241.119.72"

    var language=""
    const val ENGLISH = "en"
    const val ARABIC = "ar"

    var location=""
    var latitude=0.0
    var longitude=0.0
    var DEVICE_TYPE=1

    const val LOGIN = "/api/admin/admin_login"
    const val OTP_VERIFY = "/api/admin/admin_verify_otp"
    const val RESEND_OTP = "/api/admin/resend_otp"
    const val FORGOT_PASSWORD = "/api/admin/admin_forgot_password"
    const val CREATE_PASSWORD = "/api/admin/admin_create_password"

}