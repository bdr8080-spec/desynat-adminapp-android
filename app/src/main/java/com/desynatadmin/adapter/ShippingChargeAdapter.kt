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
import com.desynatadmin.ui.home.brandManagement.response.GetBrandResponse
import com.desynatadmin.ui.home.categorySubCategoryManagement.response.GetSubCategoryResponse

class ShippingChargeAdapter(var context:Context, var shippingChargeList: ArrayList<String>, var clickOnItem:ClickOnItem) : RecyclerView.Adapter<ShippingChargeAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemShippingFeeBinding) :
        RecyclerView.ViewHolder(binding.root)

    interface ClickOnItem{
        fun onClickItem(position: Int,type:Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemShippingFeeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return shippingChargeList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var shippingChargeData = shippingChargeList[position]

        holder.binding.apply {
            /*tvDistance.text = shippingChargeData.nameEnglish
            tvPrice.text = shippingChargeData.nameEnglish*/

            ivDelete.setOnClickListener {
                clickOnItem.onClickItem(position,3)
            }

            ivEdit.setOnClickListener {
                clickOnItem.onClickItem(position,1)
            }

            ivSwitch.setOnClickListener {
                clickOnItem.onClickItem(position,2)
            }
        }


    }

    fun setData(newList: ArrayList<String>) {
        shippingChargeList = newList
        notifyDataSetChanged()
    }

}
