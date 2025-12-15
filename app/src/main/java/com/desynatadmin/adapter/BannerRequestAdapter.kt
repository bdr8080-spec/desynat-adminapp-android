package com.desynatadmin.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.desynatadmin.databinding.ItemBannerPriceBinding
import com.desynatadmin.databinding.ItemBannerRequestBinding
import com.desynatadmin.ui.home.banner.response.GetBannerPricePerDayResponse
import com.desynatadmin.ui.home.brandManagement.response.GetBrandResponse
import com.desynatadmin.ui.home.subscriptionPlan.response.GetCountryResponse

class BannerRequestAdapter(var context:Context, var bannerList: ArrayList<String>, var onClick:OnClick) : RecyclerView.Adapter<BannerRequestAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemBannerRequestBinding) :
        RecyclerView.ViewHolder(binding.root)

    interface OnClick{
        fun onItemClick(position: Int,type:Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemBannerRequestBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return bannerList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var bannerRequestData = bannerList[position]
        holder.binding.apply {

            var type = 1
            when(type){
                1->{
                    tvStatus.isVisible = false
                    tvStatusValue.isVisible = false
                    btnReject.isVisible = true
                    btnAccept.isVisible = true
                    tvBannerPosition.isVisible = false
                    tvBannerPositionValue.isVisible = false
                    tvAmount.isVisible = false
                    tvAmountValue.isVisible = false

                    tvReason.isVisible = false
                    tvReasonValue.isVisible = false
                }
                2->{
                    tvStatus.isVisible = true
                    tvStatusValue.isVisible = true
                    btnReject.isVisible = false
                    btnAccept.isVisible = false
                    tvBannerPosition.isVisible = true
                    tvBannerPositionValue.isVisible = true
                    tvAmount.isVisible = true
                    tvAmountValue.isVisible = true

                    tvReason.isVisible = false
                    tvReasonValue.isVisible = false

                    tvStatusValue.text = "Active"
                    tvStatusValue.setTextColor(Color.parseColor("#68CA44"))
                }
                3->{
                    tvStatus.isVisible = true
                    tvStatusValue.isVisible = true
                    btnReject.isVisible = false
                    btnAccept.isVisible = false
                    tvStatusValue.text = "Rejected"
                    tvStatusValue.setTextColor(Color.parseColor("#D53B36"))

                    tvReason.isVisible = true
                    tvReasonValue.isVisible = true

                    tvAmount.isVisible = true
                    tvAmountValue.isVisible = true
                    tvDuration.text = "Bank Acc no."
                    tvTotalAmount.text = "Available Amount"
                    tvAmount.text = "Requested Amount"
                }

            }
            btnAccept.setOnClickListener {
                onClick.onItemClick(position,1)
            }

            btnReject.setOnClickListener {
                onClick.onItemClick(position,2)
            }
        }

        holder.itemView.setOnClickListener {
            onClick.onItemClick(position,3)
        }

    }

    fun setData(newList: ArrayList<String>) {
        bannerList = newList
        notifyDataSetChanged()
    }
}
