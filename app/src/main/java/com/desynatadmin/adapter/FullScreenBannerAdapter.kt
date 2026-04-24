package com.desynatadmin.adapter

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.desynatadmin.R
import com.desynatadmin.databinding.ItemFullScreenBannerBinding
import com.desynatadmin.ui.home.fullScreenBannerManagement.response.GetFullScreenBannerResponse
import com.desynatadmin.utils.CommonMethod
import com.desynatadmin.utils.SharedPreference
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class FullScreenBannerAdapter(var context:Context, var bannerList: ArrayList<GetFullScreenBannerResponse.Data.Data>, var bannerClick:BannerClick) : RecyclerView.Adapter<FullScreenBannerAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemFullScreenBannerBinding) :
        RecyclerView.ViewHolder(binding.root){
        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(position: Int) = with(binding){
            val bannerData = bannerList[position]
            tvTitleName.text = bannerData?.title
                tvCountryName.text = bannerData?.countryInfo?.name
                tvStartDateValue.text = CommonMethod.formatDate(bannerData?.startDate.toString(),"yyyy-MM-dd","dd/MM/yyyy")
                tvEndDateValue.text = CommonMethod.formatDate(bannerData?.endDate.toString(),"yyyy-MM-dd","dd/MM/yyyy")
                Glide.with(context).load(bannerData?.image).placeholder(R.drawable.ic_placeholder).into(ivBanner)
                tvTypeValue.text = if (bannerData?.type=="2") "Web Banner" else "App Banner"
                when(getBannerStatus(bannerData?.startDate.toString(),bannerData?.endDate.toString())){
                    "Upcoming"->{
                        tvStatusValue.text = "Upcoming"
                        tvStatusValue.setTextColor(Color.parseColor("#1A900D"))
                    }
                    "Active" ->{
                        tvStatusValue.text = "Active"
                        tvStatusValue.setTextColor(Color.parseColor("#1A900D"))
                    }
                    "Expired"->{
                        tvStatusValue.text = "Expired"
                        tvStatusValue.setTextColor(Color.parseColor("#D53B36"))
                    }
                }

            root.setOnClickListener {
                bannerClick.onBannerClick(position)
            }
        }
        }

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

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
      holder.bind(position)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getBannerStatus(startDate: String, endDate: String): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        val start = LocalDate.parse(startDate, formatter)
        val end = LocalDate.parse(endDate, formatter)
        val today = LocalDate.now()

        return when {
            today.isBefore(start) -> "Upcoming"
            (today.isEqual(start) || today.isAfter(start)) &&
                    (today.isEqual(end) || today.isBefore(end)) -> "Active"
            else -> "Expired"
        }
    }

    fun setData(newList: ArrayList<GetFullScreenBannerResponse.Data.Data>) {
        bannerList = newList
        notifyDataSetChanged()
    }

}
