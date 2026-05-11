package com.desynatadmin.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.desynatadmin.databinding.ItemBannerPriceBinding
import com.desynatadmin.ui.home.banner.response.GetBannerPricePerDayResponse
import com.desynatadmin.ui.home.subscriptionPlan.response.GetCountryResponse
import com.desynatadmin.utils.Constants
import com.desynatadmin.utils.SharedPreference

class BannerPerDayPriceAdapter(var context:Context, var bannerList: ArrayList<GetBannerPricePerDayResponse.Data>, var countryMap: Map<Int, GetCountryResponse.Data>, var onClick:OnClick) : RecyclerView.Adapter<BannerPerDayPriceAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemBannerPriceBinding) :
        RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int) = with(binding){
            val bannerData = bannerList[position]
            var actionAccess : Boolean = true
            val userInfo = SharedPreference.get(context).getUser()
            if (userInfo?.type=="2"){
                val subAdminInfo = SharedPreference.get(context).getSubAdmin()
                val module = subAdminInfo?.permissionDetails
                    ?.firstOrNull {
                        it.moduleDetails.name.equals("banner_per_day_price_management", true)
                    }

                actionAccess = module?.canAction == 1

                ivEdit.isVisible = actionAccess

            }
            val countryData = countryMap[bannerData.countryId]
                tvCountry.text = "${if (Constants.language=="ar") countryData?.nameAr else countryData?.name}"
                tvPrice.text = "${bannerData.perDayPrice} KD"
                ivFlag.text = "${countryData?.flag}"

                ivEdit.setOnClickListener {
                    onClick.onItemClick(position)
                }
        }
        }

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
      holder.bind(position)
    }
}
