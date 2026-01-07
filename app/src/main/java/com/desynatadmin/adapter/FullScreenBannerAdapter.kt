package com.desynatadmin.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.desynatadmin.R
import com.desynatadmin.databinding.ItemFullScreenBannerBinding
import com.desynatadmin.ui.home.fullScreenBannerManagement.response.GetFullScreenBannerResponse
import com.desynatadmin.utils.CommonMethod

class FullScreenBannerAdapter(var context:Context, var bannerList: ArrayList<GetFullScreenBannerResponse.Data.Data>, var bannerClick:BannerClick) : RecyclerView.Adapter<FullScreenBannerAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemFullScreenBannerBinding) :
        RecyclerView.ViewHolder(binding.root)

    interface BannerClick{
        fun onBannerClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemFullScreenBannerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return bannerList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var bannerData = bannerList[position]

        holder.binding.apply {
            tvTitleName.text = bannerData?.title
            tvCountryName.text = bannerData?.countryInfo?.name
            tvStartDateValue.text = CommonMethod.formatDate(bannerData?.startDate.toString(),"yyyy-MM-dd","dd/MM/yyyy")
            tvEndDateValue.text = CommonMethod.formatDate(bannerData?.endDate.toString(),"yyyy-MM-dd","dd/MM/yyyy")
            ivBanner.load(bannerData?.image) {
                crossfade(true)
                placeholder(R.drawable.ic_placeholder)
                error(R.drawable.ic_placeholder)
            }
        }

        holder.itemView.setOnClickListener {
            bannerClick.onBannerClick(position)

        }
    }

    fun setData(newList: ArrayList<GetFullScreenBannerResponse.Data.Data>) {
        bannerList = newList
        notifyDataSetChanged()
    }

}
