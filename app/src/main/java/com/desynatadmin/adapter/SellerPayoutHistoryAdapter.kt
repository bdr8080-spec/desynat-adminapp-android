package com.desynatadmin.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.desynatadmin.databinding.ItemPayoutHistoryBinding
import com.desynatadmin.ui.home.sellerPayout.response.GetSellerPayoutHistoryResponse
import com.desynatadmin.utils.CommonMethod

class SellerPayoutHistoryAdapter(
    var context: Context,
    var payoutList: ArrayList<GetSellerPayoutHistoryResponse.Data.PayoutHistory.PayoutHistoryItem>,
    var onClick: OnClick
) : RecyclerView.Adapter<SellerPayoutHistoryAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemPayoutHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) = with(binding) {
            val payoutData = payoutList[position]

            val amountVal = payoutData.amount?.toDoubleOrNull() ?: 0.0
            tvPayoutAmount.text = "${CommonMethod.formatPrice(amountVal)} KD"

            if (!payoutData.payoutDate.isNullOrEmpty()) {
                val formattedDate = CommonMethod.customizeChangeDateFormat(
                    payoutData.payoutDate,
                    inputFormat = "yyyy-MM-dd HH:mm:ss",
                    outputFormat = "dd/MM/yyyy"
                )
                tvPayoutOnValue.text = formattedDate.ifEmpty { payoutData.payoutDate }
            } else {
                tvPayoutOnValue.text = "N/A"
            }

            tvTransactionId.text = "#${payoutData.transactionId?: ""}"
        }
    }

    interface OnClick {
        fun OnItemClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemPayoutHistoryBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return payoutList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    fun setData(newList: ArrayList<GetSellerPayoutHistoryResponse.Data.PayoutHistory.PayoutHistoryItem>) {
        payoutList = newList
        notifyDataSetChanged()
    }
}
