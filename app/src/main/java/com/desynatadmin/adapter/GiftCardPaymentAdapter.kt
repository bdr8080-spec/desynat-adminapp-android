package com.desynatadmin.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.desynatadmin.databinding.ItemPaymentBinding

class GiftCardPaymentAdapter(var context:Context, private var paymentList: ArrayList<String>, var paymentClick: PaymentClick) : RecyclerView.Adapter<GiftCardPaymentAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemPaymentBinding) :
        RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int) = with(binding){
            val paymentData = paymentList[position]


            root.setOnClickListener {
                paymentClick.onPaymentClick(position,0)

            }
        }
        }

    interface PaymentClick{
        fun onPaymentClick(position: Int,type:Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemPaymentBinding.inflate(
                LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return paymentList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
       holder.bind(position)
    }

}
