package com.desynatadmin.utils

import java.util.TimeZone

object Constants {

    // const val BASE_URL = "http://157.241.119.72" //old
//    const val BASE_URL = "http://65.2.12.223" //new
    const val BASE_URL = "https://desynat.com"

    val KUWAIT_TIMEZONE: TimeZone = TimeZone.getTimeZone("Asia/Kuwait")

    var language=""
    const val ENGLISH = "en"
    const val ARABIC = "ar"
    var isLogout = false
    var currentScreen = ""

    var location=""
    var latitude=0.0
    var longitude=0.0
    var DEVICE_TYPE=2

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
    const val REJECT_SELLER = "/api/admin/reject_seller"


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
    const val DELETE_ZONE = "/api/admin/delete_zone"
    const val UPDATE_SELLER_STATUS = "/api/admin/block_unblock_seller"

    const val GET_USER = "/api/admin/get_users"
    const val GET_USER_DETAIL = "/api/admin/get_user_details"
    const val BLOCK_UNBLOCK_USER = "/api/admin/block_unblock_user"
    const val ADD_WALLET_AMOUNT = "/api/admin/add_user_wallet_amount"

    const val GET_REASON = "/api/admin/get_reasons"
    const val ADD_REASON = "/api/admin/add_reason"
    const val UPDATE_REASON = "/api/admin/update_reason"
    const val BLOCK_UNBLOCK_REASON = "/api/admin/change_reason_status"
    const val DELETE_REASON = "/api/admin/delete_reason"


    const val GET_KUWAIT_ZONE = "/api/admin/get_kuwait_zones"
    const val GET_DELIVERY_BOY = "/api/admin/get_delivery_boys"
    const val GET_DELIVERY_BOY_DETAIL = "/api/admin/get_deliveryboy_details"
    const val BLOCK_UNBLOCK_DELIVERY_BOY = "/api/admin/block_unblock_deliveryboy"
    const val ADD_DELIVERY_BOY = "/api/admin/add_deliveryBoy"
    const val UPDATE_DELIVERY_BOY = "/api/admin/update_deliveryBoy"
    const val DELETE_DELIVERY_BOY = "/api/admin/delete_deliveryboy"
    const val GET_USER_COUNTRY = "/api/admin/get_user_countries_list"

    const val GET_PROMO_CODE = "/api/admin/get_promocodes"
    const val ADD_PROMO_CODE = "/api/admin/add_promocode"
    const val UPDATE_PROMO_CODE = "/api/admin/update_promocode"
    const val DELETE_PROMO_CODE = "/api/admin/delete_promocode"
    const val UPDATE_STATUS_PROMO_CODE = "/api/admin/block_unblock_promocode"

    const val GIFT_CARD = "/api/admin/get_giftCards"
    const val ADD_GIFT_CARD = "/api/admin/add_giftCard"
    const val UPDATE_GIFT_CARD = "/api/admin/update_giftCard"
    const val DELETE_GIFT_CARD = "/api/admin/delete_gift_card"
    const val UPDATE_GIFT_CARD_STATUS = "/api/admin/block_unblock_giftCard"

    const val ADD_SHIPPING_CHARGE = "/api/admin/add_shipping_charge"
    const val UPDATE_SHIPPING_CHARGE = "/api/admin/update_shipping_charge"
    const val DELETE_SHIPPING_CHARGE = "/api/admin/delete_shipping_fee"
    const val UPDATE_SHIPPING_CHARGE_STATUS = "/api/admin/blobk_unblock_shippingFee"
    const val SHIPPING_CHARGE = "/api/admin/shipping_charges_by_countryId"

    const val ADD_TAX = "/api/admin/add_tax"
    const val SHIPPING_TAX = "/api/admin/countries_with_tax_shipping"

    const val BANNER_PER_DAY_PRICE = "/api/admin/get_banners"
    const val UPDATE_BANNER_PER_DAY_PRICE = "/api/admin/add_update_banner"

    const val BANNER_REQUEST = "/api/admin/get_banner_request"
    const val UPDATE_BANNER_STATUS = "/api/admin/block_unblock_banner_req"
    const val APPROVE_BANNER_REQUEST = "/api/admin/approve_banner_request"
    const val REJECT_BANNER_REQUEST = "/api/admin/reject_banner_request"
    const val GET_AFFILIATE_COMMISSION = "/api/admin/countries_with_affiliate_commission"
    const val UPDATE_AFFILIATE_COMMISSION = "/api/admin/update_affiliate_commission"
    const val ACTIVE_ORDER_LIST = "/api/admin/active_orders"
    const val RETURN_ORDER_LIST = "/api/admin/return_orders"
    const val COMPLETED_ORDER_LIST = "/api/admin/completed_orders"
    const val CANCELLED_ORDER_LIST = "/api/admin/cancelled_orders"
    const val ORDER_DETAIL = "/api/admin/single_order_detail"
    const val GET_DRIVER_TO_ASSIGN = "/api/admin/get_drivers_to_assign"
    const val ASSIGN_DRIVER = "/api/admin/assign_driver"
    const val INVOICE = "/Invoice"

    const val GET_FULL_BANNER = "/api/admin/get_full_banners"
    const val ADD_FULL_BANNER = "/api/admin/add_full_banner"
    const val UPDATE_FULL_BANNER = "/api/admin/update_full_banner"
    const val DELETE_FULL_BANNER = "/api/admin/delete_full_banner"

    const val GET_ACTIVE_DEALS = "/api/admin/get_active_deals"
    const val GET_EXPIRED_DEALS = "/api/admin/get_expired_deals"
    const val UPDATE_DEAL_STATUS = "/api/admin/block_unblock_deal"
    const val DELETE_DEAL = "/api/admin/delete_deal"
    const val ADD_DEALS = "/api/admin/add_deal"
    const val UPDATE_DEALS = "/api/admin/update_deal"
    const val GET_DEAL_DETAIL = "/api/admin/get_deal_detail"
    const val DEAL_PRODUCT_LIST = "/api/admin/get_deal_seller_product_list"

    const val ADD_STORY = "/api/admin/add_story"
    const val DELETE_STORY = "/api/admin/delete_story"
    const val GET_SELLER_STORY = "/api/admin/get_seller_stories"
    const val GET_ADMIN_STORY = "/api/admin/get_admin_stories"

    const val GET_LIVE_REQUEST = "/api/admin/get_live_requests"
    const val ACCEPT_LIVE_REQUEST = "/api/admin/accept_live_request"
    const val REJECT_LIVE_REQUEST = "/api/admin/reject_live_request"
    const val GET_TIME_SLOT = "/api/admin/get_timeslots"
    const val ADD_TIME_SLOT = "/api/admin/add_timeslot"
    const val UPDATE_TIME_SLOT = "/api/admin/update_timeslot"
    const val DELETE_TIME_SLOT = "/api/admin/delete_timeslot"
    const val UPDATE_TIME_SLOT_STATUS = "/api/admin/block_unblock_timeslot"
    const val UPLOAD_VIDEO_IMAGE = "/api/uploadFile"
    const val UPLOAD_WITH_RESOLUTION = "/api/uploadWithResolution"

    const val GET_NEW_AFFILIATE_USER = "/api/admin/get_new_affilaited_users"
    const val GET_APPROVED_AFFILIATE_USER = "/api/admin/get_approved_affilaited_users"
    const val GET_REJECTED_AFFILIATE_USER = "/api/admin/get_rejected_affilaited_users"
    const val GET_AFFILIATE_USER_DETAIL = "/api/admin/get_affiliate_user_details"
    const val BLOCK_UNBLOCK_AFFILIATE_USER = "/api/admin/block_unblock_affiliate_user"
    const val ACCEPT_AFFILIATE_USER = "/api/admin/accept_affiliate_user"
    const val REJECT_AFFILIATE_USER = "/api/admin/reject_affiliate_user"
    const val GET_PROMOTIONAL_BANNER = "/api/admin/get_promotional_banners"
    const val GET_PROMOTIONAL_DETAIL_BANNER = "/api/admin/get_promotional_banner_detail"
    const val ADD_PROMOTIONAL_BANNER = "/api/admin/add_promotional_banner"
    const val UPDATE_PROMOTIONAL_BANNER = "/api/admin/update_promotional_banner"
    const val DELETE_PROMOTIONAL_BANNER = "/api/admin/delete_promotional_banner"
    const val UPDATE_PROMOTIONAL_BANNER_STATUS = "/api/admin/block_unblock_promotional_banner"
    const val UPDATE_DEFAULT_SHIPPING_FEE = "/api/admin/update_default_shipping_fee"
    const val GET_DEFAULT_SHIPPING_FEE = "/api/admin/get_default_shipping_fee"
    const val GET_ALL_PRODUCT = "/api/admin/get_products"
    const val PRODUCT_DETAIL = "/api/admin/get_product_detail"
    const val UPDATE_PRODUCT_STATUS = "/api/admin/block_unblock_product"
    const val GET_ALL_EVENT = "/api/admin/get_event"
    const val GET_WITHDRAWAL_REQUEST = "/api/admin/get_withdraw_requests"
    const val ACCEPT_WITHDRAWAL_REQUEST = "/api/admin/approve_withdraw_request"
    const val REJECT_WITHDRAWAL_REQUEST = "/api/admin/reject_withdraw_request"
    const val WITHDRAWAL_TRANSFER = "/api/admin/transfer_withdraw_request"

    const val SEND_NOTIFICATION = "/api/admin/send_notification"
    const val GET_SEND_NOTIFICATION_LIST = "/api/admin/get_send_notifications_list"
    const val READ_NOTIFICATION = "/api/admin/mark_notification_as_read"

    const val GET_MY_NOTIFICATION_LIST = "/api/admin/get_my_notifications_list"
    const val MARK_ALL_READ_NOTIFICATION = "/api/admin/mark_all_notification_as_read"
    const val NOTIFICATION_COUNT = "api/admin/get_admin_notifications_count"

    const val GET_TERMS_AND_CONDITION = "/api/admin/get_terms_condition"
    const val CREATE_UPDATE_TERMS_AND_CONDITION = "/api/admin/create_update_terms_condition"

    const val GET_RETURN_POLICY = "/api/admin/get_return_policy"
    const val CREATE_UPDATE_RETURN_POLICY = "/api/admin/create_update_return_policy"

    const val GET_CANCELLATION_POLICY = "/api/admin/get_cancellation_policy"
    const val CREATE_UPDATE_CANCELLATION_POLICY = "/api/admin/create_update_cancellation_policy"

    const val GET_ABOUT_US = "/api/admin/get_about_us"
    const val CREATE_UPDATE_ABOUT_US = "/api/admin/create_update_about_us"

    const val GET_FAQ = "/api/admin/get_faq"
    const val ADD_FAQ = "/api/admin/add_faq"
    const val UPDATE_FAQ = "/api/admin/update_faq"
    const val DELETE_FAQ = "/api/admin/delete_faq"

    const val GET_CONTACT_US = "/api/admin/get_contact_us"
    const val CREATE_UPDATE_CONTACT_US = "/api/admin/create_update_contact_us"

    const val GET_EXCHANGE_POLICY = "/api/admin/get_exchange_policy"
    const val CREATE_UPDATE_EXCHANGE_POLICY = "/api/admin/create_update_exchange_policy"

    const val GET_LIVE_STREAMING_ROOM = "/api/admin/admin-all-live-request"
    const val GET_AGORA_TOKEN = "/api/agora/token"

    const val GET_NEW_TICKET = "/api/admin/get_new_tickets"
    const val GET_ONGOING_TICKET = "/api/admin/get_ongoing_tickets"
    const val GET_RESOLVED_TICKET = "/api/admin/get_resolved_tickets"
    const val UPDATE_TICKET_STATUS = "/api/admin/update_ticket_status"
    const val GET_ORDER_PAYMENT = "/api/admin/get_order_payments"
    const val GET_GIFT_PAYMENT = "/api/admin/get_giftcard_payments"
    const val GET_PLAN_PAYMENT = "/api/admin/get_plan_payments"
    const val GET_BANNER_PAYMENT = "/api/admin/get_banner_payments"

}