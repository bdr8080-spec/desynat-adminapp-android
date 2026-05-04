package com.desynatadmin.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.desynatadmin.R
import com.desynatadmin.databinding.ItemPromoCodeBinding
import com.desynatadmin.ui.home.promoCodeManagement.response.GetPromoCodeResponse
import com.desynatadmin.utils.Constants

class PromoCodeAdapter(var context:Context, var promoCodeList: ArrayList<GetPromoCodeResponse.Data.Data>, var promoCodeClick:PromoCodeClick) : RecyclerView.Adapter<PromoCodeAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemPromoCodeBinding) :
        RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int) = with(binding){
            val promoCodeData = promoCodeList[position]

                tvName.text = if(Constants.language=="ar") promoCodeData.nameArabic  else promoCodeData.nameEnglish
                tvFeature.text = "${context.getString(R.string.flat)} ${promoCodeData.discountPercent}% ${
                    context.getString(
                        R.string.off_on_minimum_order
                    )
                } ${promoCodeData.minOrderValue} KD"
                tvDescription.text = if(Constants.language=="ar") promoCodeData.descriptionArabic  else promoCodeData.descriptionEnglish

            root.setOnClickListener{
                promoCodeClick.onPromoCodeClick(position)
            }
        }
        }

    interface PromoCodeClick{
        fun onPromoCodeClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemPromoCodeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return promoCodeList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
       holder.bind(position)
    }


}
