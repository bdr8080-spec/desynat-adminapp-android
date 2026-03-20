package com.desynatadmin.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.desynatadmin.R
import com.desynatadmin.databinding.ItemSellerPayoutBinding
import com.desynatadmin.utils.CommonMethod

class SellerPayoutAdapter(var context:Context, var payoutList: ArrayList<String>, var onClick:OnClick) : RecyclerView.Adapter<SellerPayoutAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemSellerPayoutBinding) :
        RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int) = with(binding){
            val payoutData = payoutList[position]
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

            btnManagePayout.setOnClickListener {
                onClick.OnItemClick(position)

            }
        }
        }

    interface OnClick{
        fun OnItemClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemSellerPayoutBinding.inflate(
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
