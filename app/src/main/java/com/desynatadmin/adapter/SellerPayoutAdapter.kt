package com.desynatadmin.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.desynatadmin.R
import com.desynatadmin.databinding.ItemSellerPayoutBinding
import com.desynatadmin.ui.home.sellerPayout.response.GetSellerPayoutResponse
import com.desynatadmin.utils.CommonMethod

class SellerPayoutAdapter(
    var context: Context,
    var payoutList: ArrayList<GetSellerPayoutResponse.Data.Sellers.SellerItem>,
    var onClick: OnClick
) : RecyclerView.Adapter<SellerPayoutAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemSellerPayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) = with(binding) {
            val payoutData = payoutList[position]

            val displayName =  payoutData.storeInfo?.storeName
                ?: payoutData.name
                ?: listOfNotNull(payoutData.firstName, payoutData.lastName).joinToString(" ").trim().ifEmpty { null }
                ?: payoutData.nameEnglish
                ?: ""

            tvName.text = displayName
            tvLocation.text = payoutData.countryName ?: ""

            if (!payoutData.lastPayoutDate.isNullOrEmpty()) {
                val formattedDate = CommonMethod.customizeChangeDateFormat(
                    payoutData.lastPayoutDate,
                    outputFormat = "dd/MM/yyyy"
                )
                tvLastPayoutDate.text = "Last Payout on $formattedDate"
            } else {
                tvLastPayoutDate.text = "Last Payout on N/A"
            }

            val totalEarningVal = payoutData.totalEarning?.toDoubleOrNull() ?: 0.0
            tvTotalEarning.text = "${CommonMethod.formatPrice(totalEarningVal)} KD"

            val lastPayoutVal = (payoutData.lastPayout ?:"0")?.toDoubleOrNull() ?: 0.0
            tvLastPayoutValue.text = "${CommonMethod.formatPrice(lastPayoutVal)} KD"

            val pendingAmountVal = payoutData.pendingAmount?.toDoubleOrNull() ?: 0.0
            tvPendingAmount.text = "${CommonMethod.formatPrice(pendingAmountVal)} KD"

            val imageUrl = payoutData.profilePic ?: payoutData.image ?: payoutData.storeInfo?.storeImage
            ivProfileImage.load(imageUrl) {
                crossfade(true)
                placeholder(R.drawable.ic_user)
                error(R.drawable.ic_user)
            }

            val sellerId = payoutData.sellerId ?: payoutData.id ?: 0

            root.setOnClickListener {
                onClick.OnItemClick(position, sellerId)
            }

            btnManagePayout.setOnClickListener {
                onClick.OnItemClick(position, sellerId)
            }
        }
    }

    interface OnClick {
        fun OnItemClick(position: Int, sellerId: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemSellerPayoutBinding.inflate(
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

    fun setData(newList: ArrayList<GetSellerPayoutResponse.Data.Sellers.SellerItem>) {
        payoutList = newList
        notifyDataSetChanged()
    }
}
