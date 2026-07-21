package com.desynatadmin.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.desynatadmin.R
import com.desynatadmin.databinding.ItemSubAdminDrawerBinding
import com.desynatadmin.ui.home.subAdminManagement.response.GetSubAdminDetailResponse

class SubAdminDrawerAdapter(
    var context: Context,
    var drawerList: ArrayList<GetSubAdminDetailResponse.Data.PermissionDetail>,
    var clickOnItem: ClickOnItem
) : RecyclerView.Adapter<SubAdminDrawerAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemSubAdminDrawerBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) = with(binding) {
            val data = drawerList[position]
            tvDrawer.text = "${data?.moduleDetails?.displayName}"

            when (data?.moduleDetails?.name) {
                "dashboard" -> {
                    ivDrawer.setImageResource(R.drawable.ic_drawer_dashboard)
                    tvDrawer.text = context.getString(R.string.dashboard)
                }

                "brand_management" -> {
                    tvDrawer.text = context.getString(R.string.brand_management)
                    ivDrawer.setImageResource(R.drawable.ic_brand_management)
                }

                "category_management" -> {
                    tvDrawer.text = context.getString(R.string.category_and_sub_category_management)
                    ivDrawer.setImageResource(R.drawable.ic_category_management)
                }

                "seller_management" -> {
                    tvDrawer.text = context.getString(R.string.seller_management)
                    ivDrawer.setImageResource(R.drawable.ic_seller_management)
                }

                "country_management" -> {
                    tvDrawer.text = context.getString(R.string.country_city_and_zone_management)
                    ivDrawer.setImageResource(R.drawable.ic_country_city_management)
                }

                "plan_management" -> {
                    tvDrawer.text = context.getString(R.string.plan_management)
                    ivDrawer.setImageResource(R.drawable.ic_drawer_plan)
                }

                "user_management" -> {
                    tvDrawer.text = context.getString(R.string.user_management)
                    ivDrawer.setImageResource(R.drawable.ic_user_management)
                }

                "affiliate_user_management" -> {
                    tvDrawer.text = context.getString(R.string.affiliate_user_management)
                    ivDrawer.setImageResource(R.drawable.ic_user_management)
                }

                "delivery_boy_management" -> {
                    tvDrawer.text = context.getString(R.string.delivery_boy_management)
                    ivDrawer.setImageResource(R.drawable.ic_delivery_boy_management)
                }

                "reason_management" -> {
                    tvDrawer.text = context.getString(R.string.reason_management)
                    ivDrawer.setImageResource(R.drawable.ic_reason_management)
                }

                "shipping_management" -> {
                    tvDrawer.text = context.getString(R.string.delivery_charge_and_tax_management)
                    ivDrawer.setImageResource(R.drawable.ic_shipping_tax_management)
                }

                "gift_card_management" -> {
                    tvDrawer.text = context.getString(R.string.gift_card_category_management)
                    ivDrawer.setImageResource(R.drawable.ic_gift)
                }

                "banner_per_day_price_management" -> {
                    tvDrawer.text = context.getString(R.string.banner_per_day_price)
                    ivDrawer.setImageResource(R.drawable.ic_banner_per_day_price)
                }

                "promo_code_management" -> {
                    tvDrawer.text = context.getString(R.string.promocode_management)
                    ivDrawer.setImageResource(R.drawable.ic_promocode_management)
                }

                "order_management" -> {
                    tvDrawer.text = context.getString(R.string.order_management)
                    ivDrawer.setImageResource(R.drawable.ic_order_management)
                }

                "affiliate_commission_management" -> {
                    tvDrawer.text = context.getString(R.string.affiliate_commission_percentage)
                    ivDrawer.setImageResource(R.drawable.ic_affilate_commision)
                }

                "banner_request_management" -> {
                    tvDrawer.text = context.getString(R.string.banner_requests_management)
                    ivDrawer.setImageResource(R.drawable.ic_banner_request_management)
                }

                "live_request_management" -> {
                    tvDrawer.text = context.getString(R.string.live_request_and_timeslot_management)
                    ivDrawer.setImageResource(R.drawable.ic_live_request)
                }

                "story_management" -> {
                    tvDrawer.text = context.getString(R.string.story_management)
                    ivDrawer.setImageResource(R.drawable.ic_story_management)
                }

                "deal_management" -> {
                    tvDrawer.text = context.getString(R.string.deals_management)
                    ivDrawer.setImageResource(R.drawable.ic_deal_management)
                }

                "full_screen_banner_management" -> {
                    tvDrawer.text = context.getString(R.string.banner_management)
                    ivDrawer.setImageResource(R.drawable.ic_banner_request_management)
                }

                "product_management" -> {
                    tvDrawer.text = context.getString(R.string.product_management)
                    ivDrawer.setImageResource(R.drawable.ic_drawer_product)
                }

                "affiliate_withdraw_request_management" -> {
                    tvDrawer.text = context.getString(R.string.affiliate_withdrawal_requests)
                    ivDrawer.setImageResource(R.drawable.ic_affilate_withdrawal_request)
                }

                "cms_management" -> {
                    tvDrawer.text = context.getString(R.string.cms)
                    ivDrawer.setImageResource(R.drawable.ic_cms)
                }

                "notification_management" -> {
                    tvDrawer.text = context.getString(R.string.notification_management)
                    ivDrawer.setImageResource(R.drawable.ic_notification)
                }

                "ticket_management" -> {
                    tvDrawer.text = context.getString(R.string.ticket_management)
                    ivDrawer.setImageResource(R.drawable.ic_ticket_management)
                }

                "payment_management" -> {
                    tvDrawer.text = context.getString(R.string.payment_management)
                    ivDrawer.setImageResource(R.drawable.ic_drawer_payment)
                }

                "seller_livestream_management" -> {
                    tvDrawer.text = context.getString(R.string.current_live_seller)
                    ivDrawer.setImageResource(R.drawable.ic_live_request)
                }

                "support_chat" -> {
                    tvDrawer.text = context.getString(R.string.support_chat)
                    ivDrawer.setImageResource(R.drawable.ic_support_chat)
                }

                "account_setting" -> {
                    tvDrawer.text = context.getString(R.string.account_setting)
                    ivDrawer.setImageResource(R.drawable.ic_user_management)
                }

                "sub_admin_management" -> {
                    tvDrawer.text = context.getString(R.string.sub_admin_management)
                    ivDrawer.setImageResource(R.drawable.ic_subadmin_management)
                }

                "refund_management" -> {
                    tvDrawer.text = context.getString(R.string.refund_management)
                    ivDrawer.setImageResource(R.drawable.ic_refund_management)
                }

            }
            root.setOnClickListener {
                clickOnItem.onClickItem(position)
            }
        }
    }

    interface ClickOnItem {
        fun onClickItem(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemSubAdminDrawerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return drawerList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

}
