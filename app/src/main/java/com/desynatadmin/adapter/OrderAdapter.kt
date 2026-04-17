package com.desynatadmin.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.desynatadmin.databinding.ItemOrderBinding
import com.desynatadmin.ui.home.orderManagement.response.GetActiveOrderResponse
import com.desynatadmin.utils.CommonMethod

class OrderAdapter(var context:Context, var orderList: ArrayList<GetActiveOrderResponse.Data.Data>, var onClick:OnClick) : RecyclerView.Adapter<OrderAdapter.ViewHolder>() {
    var orderType = 0
    inner class ViewHolder(val binding: ItemOrderBinding) :
        RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int) = with(binding){
            val orderData = orderList[position]

                tvName.text = orderData?.productDetails?.sellerDetails?.firstName+" "+orderData?.productDetails?.sellerDetails?.lastName
                tvOrderID.text = "#${orderData?.id}"
                if (orderData?.order?.paymentMethod=="1"){
                    tvTransactionId.text = "#${orderData?.order?.transactionId}"
                }
                else{
                    tvTransaction.isVisible = false
                    tvTransactionId.isVisible = false
                }

                tvOrderOnValue.text = CommonMethod.customizeChangeDateFormat(orderData.createdAt, outputFormat = "dd/MM/yyyy 'at' hh:mm a")
                tvTotalAmount.text = "${CommonMethod.formatPrice(orderData?.amount?:0.0)} KD"
                tvOrderTypeValue.text =  if (orderData?.order?.orderType=="1") "Normal Order" else "Custom Order"

                when(orderType) {
                    1,2 -> {
                        if (orderData?.assignStatus=="1"){
                            btnAssignOrder.isVisible = false
                        }
                        else if(orderData?.deliveryStatus=="2.1"){
                            btnAssignOrder.isVisible = true
                        }
                        else{
                            btnAssignOrder.isVisible = false
                        }
                    }
                    else ->{
                        btnAssignOrder.isVisible = false
                    }
                }
                btnAssignOrder.setOnClickListener {
                    onClick.onItemClick(position,1)
                }

           root.setOnClickListener {
                onClick.onItemClick(position,2)
            }
        }
        }

    interface OnClick{
        fun onItemClick(position: Int,type:Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return orderList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    fun setData(newList: ArrayList<GetActiveOrderResponse.Data.Data>) {
        orderList = newList
        notifyDataSetChanged()
    }
    fun setType(type:Int){
        orderType = type
        notifyDataSetChanged()
    }
}
