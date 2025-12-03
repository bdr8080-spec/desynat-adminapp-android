package com.desynatadmin.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.desynatadmin.R
import com.desynatadmin.databinding.ItemBrandBinding
import com.desynatadmin.databinding.ItemCardBinding
import com.desynatadmin.databinding.ItemShippingFeeBinding
import com.desynatadmin.databinding.ItemShippingTaxBinding
import com.desynatadmin.ui.home.brandManagement.response.GetBrandResponse
import com.desynatadmin.ui.home.categorySubCategoryManagement.response.GetSubCategoryResponse
import com.desynatadmin.ui.home.shippingAndTaxManagement.response.GetShippingTaxResponse
import com.desynatadmin.utils.CommonMethod

class ShippingCountryTaxAdapter(var context:Context, var shippingTaxList: ArrayList<GetShippingTaxResponse.Data>, var clickOnItem:ClickOnItem) : RecyclerView.Adapter<ShippingCountryTaxAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemShippingTaxBinding) :
        RecyclerView.ViewHolder(binding.root)

    interface ClickOnItem{
        fun onClickItem(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemShippingTaxBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return shippingTaxList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var shippingTaxData = shippingTaxList[position]

        holder.binding.apply {
            tvCountry.text = shippingTaxData.name
            tvTax.text = "${CommonMethod.formatDoubleValue(shippingTaxData?.taxInfo?.tax?.toDouble())}%"
            ivFlag.load(shippingTaxData.flag1)
        }

        holder.itemView.setOnClickListener {
            clickOnItem.onClickItem(position)
        }


    }

}
