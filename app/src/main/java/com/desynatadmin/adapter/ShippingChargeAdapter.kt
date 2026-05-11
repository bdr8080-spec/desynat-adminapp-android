package com.desynatadmin.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.desynatadmin.R
import com.desynatadmin.databinding.ItemShippingFeeBinding
import com.desynatadmin.ui.home.shippingAndTaxManagement.response.GetShippingTaxResponse
import com.desynatadmin.utils.CommonMethod

class ShippingChargeAdapter(var context:Context, var shippingChargeList: ArrayList<GetShippingTaxResponse.Data.ShippingInfo>, var clickOnItem:ClickOnItem) : RecyclerView.Adapter<ShippingChargeAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemShippingFeeBinding) :
        RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int) = with(binding){
            val shippingChargeData = shippingChargeList[position]

                tvDistance.text = "${CommonMethod.formatDoubleValue(shippingChargeData?.rangeFrom?.toDouble())}${context.getString(R.string.km)} - ${CommonMethod.formatDoubleValue(shippingChargeData?.rangeTo?.toDouble())}${
                    context.getString(
                        R.string.km
                    )
                }"


                tvPrice.text = "${CommonMethod.formatDoubleValue(shippingChargeData?.shippingFee?.toDouble())} KD"

                if (shippingChargeData.status==1){
                    ivSwitch.setImageResource(R.drawable.switch_on)
                }
                else{
                    ivSwitch.setImageResource(R.drawable.switch_off)
                }

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
      holder.bind(position)
    }

    fun setData(newList: ArrayList<GetShippingTaxResponse.Data.ShippingInfo>) {
        shippingChargeList = newList
        notifyDataSetChanged()
    }

}
