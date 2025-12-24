package com.desynatadmin.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.desynatadmin.R
import com.desynatadmin.databinding.ItemOrderBinding
import com.desynatadmin.ui.home.orderManagement.response.GetActiveOrderResponse
import com.desynatadmin.utils.CommonMethod

class OrderAdapter(var context:Context, var orderList: ArrayList<GetActiveOrderResponse.Data.Data>, var onClick:OnClick) : RecyclerView.Adapter<OrderAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemOrderBinding) :
        RecyclerView.ViewHolder(binding.root)

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
        var orderData = orderList[position]
        holder.binding.apply {

            tvName.text = orderData?.orderDetails?.get(0)?.productDetails?.sellerDetails?.firstName+" "+orderData?.orderDetails?.get(0)?.productDetails?.sellerDetails?.lastName
            tvOrderID.text = "#${orderData?.id}"
            tvTransactionId.text = "#${orderData?.transactionId}"
            tvOrderOnValue.text = CommonMethod.customizeChangeDateFormat(orderData.createdAt, outputFormat = "dd/MM/yyyy 'at' hh:mm a")
            tvTotalAmount.text = "${CommonMethod.formatPrice(orderData?.totalAmount?:0.0)} KD"
            tvOrderTypeValue.text = "Normal Orders"

            when(orderData?.deliveryStatus) {
                "1" -> {
                    btnAssignOrder.isVisible = true
                }

                "3" -> {
                    btnAssignOrder.isVisible = false
                }
                "7" -> {
                    btnAssignOrder.isVisible = false
                }
            }
            btnAssignOrder.setOnClickListener {
                onClick.onItemClick(position,1)
            }
        }

        holder.itemView.setOnClickListener {
            onClick.onItemClick(position,2)
        }

    }

    fun setData(newList: ArrayList<GetActiveOrderResponse.Data.Data>) {
        orderList = newList
        notifyDataSetChanged()
    }
}
