package com.desynatadmin.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.desynatadmin.databinding.ItemAffiliateWithdrawalRequestBinding
import com.desynatadmin.ui.home.affiliateWithdrawalRequest.response.GetWithdrawalRequestResponse
import com.desynatadmin.utils.CommonMethod

class AffiliateWithdrawalRequestAdapter(
    var context: Context,
    var requestList: ArrayList<GetWithdrawalRequestResponse.Data.Data>,
    var onClick: OnClick
) : RecyclerView.Adapter<AffiliateWithdrawalRequestAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemAffiliateWithdrawalRequestBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) = with(binding) {
            val withdrawalRequestData = requestList[position]

            tvRequestByValue.text = withdrawalRequestData?.affiliateDetails?.fullName + " | " + withdrawalRequestData?.affiliateDetails?.countryDetails?.name
            tvReasonValue.text = withdrawalRequestData?.rejectReason
            tvBankAccount.text = CommonMethod.maskAccountNumber(withdrawalRequestData?.bankDetails?.accountNo.toString(),6)
            tvRequestIdValue.text = "#${withdrawalRequestData?.requestId}"
                tvRequestedAmountValue.text = "${CommonMethod.formatDoubleValue(withdrawalRequestData?.amount?.toDoubleOrNull()?:0.0)} KD"
                tvAvailableAmount.text = "${CommonMethod.formatDoubleValue(withdrawalRequestData?.totalEarning?.toDoubleOrNull()?:0.0)} KD"
            tvRequestOnValue.text = CommonMethod.customizeChangeDateFormat(
                withdrawalRequestData?.createdAt.toString(),
                outputFormat = "dd/MM/yyyy 'at' hh:mm a"
            )

            when (withdrawalRequestData?.adminVerify) {
                0 -> {
                    tvStatus.isVisible = false
                    tvStatusValue.isVisible = false
                    tvStatusValue.text = "Pending"
                    tvStatusValue.setTextColor(Color.parseColor("#FFA41B"))
                    btnApprove.isVisible = true
                    btnReject.isVisible = true
                    tvReason.isVisible = false
                    tvReasonValue.isVisible = false
                }

                1 -> {
                    tvStatus.isVisible = true
                    tvStatusValue.isVisible = true
                    tvStatusValue.text = "Completed"
                    tvStatusValue.setTextColor(Color.parseColor("#17854C"))
                    btnApprove.isVisible = false
                    btnReject.isVisible = false
                    tvReason.isVisible = false
                    tvReasonValue.isVisible = false
                    tvTransactionId.isVisible = true
                    tvTransaction.isVisible = true
                    tvTransactionId.text = "#${withdrawalRequestData?.transactionId?:withdrawalRequestData?.requestId}"
                }

                2 -> {
                    tvStatus.isVisible = true
                    tvStatusValue.isVisible = true
                    tvStatusValue.text = "Rejected"
                    tvStatusValue.setTextColor(Color.parseColor("#D91A1B"))
                    btnApprove.isVisible = false
                    btnReject.isVisible = false
                    tvReason.isVisible = true
                    tvReasonValue.isVisible = true
                }
            }

            btnApprove.setOnClickListener {
                onClick.click(position, 1)
            }

            btnReject.setOnClickListener {
                onClick.click(position, 2)
            }
        }
    }

    interface OnClick {
        fun click(position: Int, type: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemAffiliateWithdrawalRequestBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return requestList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    fun setData(newList: ArrayList<GetWithdrawalRequestResponse.Data.Data>) {
        requestList = newList
        notifyDataSetChanged()
    }
}
