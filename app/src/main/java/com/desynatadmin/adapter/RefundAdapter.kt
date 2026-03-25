package com.desynatadmin.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.desynatadmin.databinding.ItemRefundBinding

class RefundAdapter(var context:Context, var refundList: ArrayList<String>, var onClick:OnClick) : RecyclerView.Adapter<RefundAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemRefundBinding) :
        RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int) = with(binding){
           var refundData = refundList[position]
//
//                tvName.text = payoutData.nameEnglish
//                tvLocation.text = payoutData.nameEnglish
//            tvLastPayoutDate.text = "Last Payout on ${CommonMethod.customizeChangeDateFormat(payoutData, outputFormat = "dd/MM/yyyy")}"
//            tvTotalEarning.text = "${CommonMethod.formatPrice(payoutData)} KD"
//            tvLastPayoutValue.text = "${CommonMethod.formatPrice(payoutData)} KD"
//            tvPendingAmount.text = "${CommonMethod.formatPrice(payoutData)} KD"
//                ivProfileImage.load(payoutData.image) {
//                    crossfade(true)
//                    placeholder(R.drawable.ic_placeholder)
//                    error(R.drawable.ic_placeholder)
//                }


        }
        }

    interface OnClick{
        fun OnItemClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemRefundBinding.inflate(
                LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return refundList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
       holder.bind(position)
    }

    fun setData(newList: ArrayList<String>) {
        refundList = newList
        notifyDataSetChanged()
    }

}
