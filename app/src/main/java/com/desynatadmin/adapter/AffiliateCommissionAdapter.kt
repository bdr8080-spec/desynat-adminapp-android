package com.desynatadmin.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.desynatadmin.databinding.ItemAffiliateCommissionBinding
import com.desynatadmin.ui.home.affiliateCommission.response.GetAffiliateCommissionResponse
import com.desynatadmin.utils.CommonMethod

class AffiliateCommissionAdapter(var context:Context, var affiliateCommissionList: ArrayList<GetAffiliateCommissionResponse.Data>, var clickOnItem:ClickOnItem) : RecyclerView.Adapter<AffiliateCommissionAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemAffiliateCommissionBinding) :
        RecyclerView.ViewHolder(binding.root)

    interface ClickOnItem{
        fun onClickItem(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemAffiliateCommissionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return affiliateCommissionList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var affiliateCommissionData = affiliateCommissionList[position]

        holder.binding.apply {
            tvCountry.text = "${affiliateCommissionData?.flag}  ${affiliateCommissionData.name}"
            tvCommissionValue.text = "${CommonMethod.formatDoubleValue(affiliateCommissionData?.affiliateCommissionInfo?.commission?.toDouble())}%"
            ivEdit.setOnClickListener {
                clickOnItem.onClickItem(position)
            }
        }




    }

}
