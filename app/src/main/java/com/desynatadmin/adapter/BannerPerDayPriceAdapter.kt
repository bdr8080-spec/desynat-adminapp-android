package com.desynatadmin.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.desynatadmin.databinding.ItemBannerPriceBinding
import com.desynatadmin.ui.home.banner.response.GetBannerPricePerDayResponse
import com.desynatadmin.ui.home.subscriptionPlan.response.GetCountryResponse

class BannerPerDayPriceAdapter(var context:Context, var bannerList: ArrayList<GetBannerPricePerDayResponse.Data>, var countryMap: Map<Int, GetCountryResponse.Data>, var onClick:OnClick) : RecyclerView.Adapter<BannerPerDayPriceAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemBannerPriceBinding) :
        RecyclerView.ViewHolder(binding.root)

    interface OnClick{
        fun onItemClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemBannerPriceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return bannerList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var bannerData = bannerList[position]
        val countryData = countryMap[bannerData.countryId]
        holder.binding.apply {
            tvCountry.text = countryData?.name
            tvPrice.text = "${bannerData.perDayPrice} KD"
            ivFlag.load(countryData?.flag1)

            ivEdit.setOnClickListener {
                onClick.onItemClick(position)
            }
        }

    }
}
