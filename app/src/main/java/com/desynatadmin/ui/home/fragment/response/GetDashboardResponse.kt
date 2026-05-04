package com.desynatadmin.ui.home.fragment.response


import com.google.gson.annotations.SerializedName

data class GetDashboardResponse(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
) {
    data class Data(
        @SerializedName("total_active_affiliate_users")
        val totalActiveAffiliateUsers: Int,
        @SerializedName("total_active_delivery_boy")
        val totalActiveDeliveryBoy: Int,
        @SerializedName("total_active_sellers")
        val totalActiveSellers: Int,
        @SerializedName("total_active_users")
        val totalActiveUsers: Int,
        @SerializedName("total_affiliate_orders_revenue")
        val totalAffiliateOrdersRevenue: String,
        @SerializedName("total_affiliate_users")
        val totalAffiliateUsers: Int,
        @SerializedName("total_app_visitor_users")
        val totalAppVisitorUsers: Int,
        @SerializedName("total_banner_revenue")
        val totalBannerRevenue: String,
        @SerializedName("total_cancelled_orders")
        val totalCancelledOrders: Int,
        @SerializedName("total_completed_orders")
        val totalCompletedOrders: Int,
        @SerializedName("total_delivery_boy")
        val totalDeliveryBoy: Int,
        @SerializedName("total_inactive_affiliate_users")
        val totalInactiveAffiliateUsers: Int,
        @SerializedName("total_inactive_delivery_boy")
        val totalInactiveDeliveryBoy: Int,
        @SerializedName("total_inactive_sellers")
        val totalInactiveSellers: Int,
        @SerializedName("total_inactive_users")
        val totalInactiveUsers: Int,
        @SerializedName("total_ongoing_orders")
        val totalOngoingOrders: Int,
        @SerializedName("total_online_users")
        val totalOnlineUsers: Int,
        @SerializedName("total_order_revenue")
        val totalOrderRevenue: String,
        @SerializedName("total_orders")
        val totalOrders: Int,
        @SerializedName("total_resolved_tickets")
        val totalResolvedTickets: Int,
        @SerializedName("total_returned_orders")
        val totalReturnedOrders: Int,
        @SerializedName("total_revenue")
        val totalRevenue: String,
        @SerializedName("total_sellers")
        val totalSellers: Int,
        @SerializedName("total_subscription_plan_revenue")
        val totalSubscriptionPlanRevenue: String,
        @SerializedName("total_tickets")
        val totalTickets: Int,
        @SerializedName("total_unresolved_tickets")
        val totalUnresolvedTickets: Int,
        @SerializedName("total_users")
        val totalUsers: Int,
        @SerializedName("total_website_visitor_users")
        val totalWebsiteVisitorUsers: Int
    )
}