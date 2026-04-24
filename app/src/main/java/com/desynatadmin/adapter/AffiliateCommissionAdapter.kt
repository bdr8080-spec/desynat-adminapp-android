package com.desynatadmin.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.desynatadmin.databinding.ItemAffiliateCommissionBinding
import com.desynatadmin.ui.home.affiliateCommission.response.GetAffiliateCommissionResponse
import com.desynatadmin.utils.CommonMethod
import com.desynatadmin.utils.SharedPreference

class AffiliateCommissionAdapter(var context:Context, var affiliateCommissionList: ArrayList<GetAffiliateCommissionResponse.Data>, var clickOnItem:ClickOnItem) : RecyclerView.Adapter<AffiliateCommissionAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemAffiliateCommissionBinding) :
        RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int) = with(binding){
            val affiliateCommissionData = affiliateCommissionList[position]
                tvCountry.text = "${affiliateCommissionData?.flag}  ${affiliateCommissionData.name}"
                tvCommissionValue.text = "${CommonMethod.formatDoubleValue(affiliateCommissionData?.affiliateCommissionInfo?.commission?.toDouble())}%"

            val userInfo = SharedPreference.get(context).getUser()
            if (userInfo?.type=="2") {
                val subAdminInfo = SharedPreference.get(context).getSubAdmin()
                val module = subAdminInfo?.permissionDetails
                    ?.firstOrNull {
                        it.moduleDetails.name.equals("affiliate_commission_management", true)
                    }

                val actionAccess = module?.canAction == 1
                ivEdit.isVisible = actionAccess
            }
                ivEdit.setOnClickListener {
                    clickOnItem.onClickItem(position)
                }
        }
        }

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
        holder.bind(position)
    }

}
