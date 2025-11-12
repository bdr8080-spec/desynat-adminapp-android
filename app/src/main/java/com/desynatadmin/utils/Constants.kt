package com.desynatadmin.utils

object Constants {

    const val BASE_URL = "http://157.241.119.72"

    var language=""
    const val ENGLISH = "en"
    const val ARABIC = "ar"
    var isLogout = false

    var location=""
    var latitude=0.0
    var longitude=0.0
    var DEVICE_TYPE=1

    const val UPLOAD = "/api/upload"
    const val LOGIN = "/api/admin/admin_login"
    const val OTP_VERIFY = "/api/admin/admin_verify_otp"
    const val RESEND_OTP = "/api/admin/resend_otp"
    const val FORGOT_PASSWORD = "/api/admin/admin_forgot_password"
    const val CREATE_PASSWORD = "/api/admin/admin_create_password"
    const val LOGOUT = "/api/admin/admin_logout"


    const val CATEGORY = "/api/admin/get_category"
    const val SUB_CATEGORY = "/api/admin/get_subcategories"
    const val ADD_SUB_CATEGORY = "/api/admin/add_subcategory"
    const val UPDATE_SUB_CATEGORY = "/api/admin/update_subcategory"
    const val DELETE_SUB_CATEGORY = "/api/admin/delete_subcategory"
    const val UPDATE_SUB_CATEGORY_STATUS = "/api/admin/change_subcategory_status"
    const val UPDATE_CATEGORY_STATUS = "/api/admin/change_category_status"

    const val BRAND = "/api/admin/get_brand"
    const val ADD_BRAND = "/api/admin/add_brand"
    const val UPDATE_BRAND = "/api/admin/update_brand"
    const val DELETE_BRAND = "/api/admin/delete_brand"
    const val UPDATE_BRAND_STATUS = "/api/admin/change_brand_status"
    const val GET_CITY = "/api/admin/get_cities"
    const val ADD_SERVICE_CITY = "/api/admin/add_service_city"
    const val GET_SERVICE_CITY = "/api/admin/get_service_cities"
    const val GET_CHANGE_SERVICE_STATUS = "/api/admin/change_service_city_status"
    const val GET_DELETE_CITY_SERVICE = "/api/admin/delete_service_city"
    const val ADD_ZONE = "/api/admin/add_zone"

    // seller

    const val NEW_SELLER = "/api/admin/get_new_sellers"
    const val APPROVED_SELLER = "/api/admin/get_approved_sellers"
    const val REJECTED_SELLER = "/api/admin/get_rejected_sellers"
    const val SELLER_DETAIL = "/api/admin/get_seller_details"
    const val ACCEPT_SELLER = "/api/admin/accept_seller"


    const val GET_SUBSCRIPTION_PLAN = "/api/admin/get_plans"
    const val GET_SUBSCRIPTION_PLAN_DETAILS = "/api/admin/get_plan_details"
    const val DELETE_SUBSCRIPTION_PLAN = "/api/admin/delete_plan"
    const val CHANGE_PLAN_STATUS = "/api/admin/change_plan_status"
    const val ADD_SUBSCRIPTION_PLAN = "/api/admin/add_plan"
    const val UPDATE_SUBSCRIPTION_PLAN = "/api/admin/update_plan"
    const val GET_COUNTRY = "/api/admin/get_countries"
    const val GET_ZONE_LIST = "/api/admin/get_zones"
    const val UPDATE_ZONE = "/api/admin/update_zone"
    const val UPDATE_ZONE_STATUS = "/api/admin/change_zone_status"

}