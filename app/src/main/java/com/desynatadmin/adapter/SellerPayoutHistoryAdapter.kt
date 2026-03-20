package com.desynatadmin.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.desynatadmin.databinding.ItemPayoutHistoryBinding
import com.desynatadmin.utils.CommonMethod

class SellerPayoutHistoryAdapter(var context:Context, var payoutList: ArrayList<String>, var onClick:OnClick) : RecyclerView.Adapter<SellerPayoutHistoryAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemPayoutHistoryBinding) :
        RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int) = with(binding){
            val payoutData = payoutList[position]
//
//            tvPayoutOnValue.text = "${CommonMethod.customizeChangeDateFormat(payoutData, outputFormat = "dd/MM/yyyy")}"
//            tvPayoutAmount.text = "${CommonMethod.formatPrice(payoutData)} KD"
//            tvTransactionId.text = "#${payoutData}"
        }
        }

    interface OnClick{
        fun OnItemClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemPayoutHistoryBinding.inflate(
                LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return payoutList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
       holder.bind(position)
    }

    fun setData(newList: ArrayList<String>) {
        payoutList = newList
        notifyDataSetChanged()
    }

}
