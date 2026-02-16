package com.desynatadmin.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.desynatadmin.databinding.ItemAffiliateWithdrawalRequestBinding
import com.desynatadmin.ui.home.liveRequestAndTimeSlotManagement.response.GetLiveRequestResponse
import com.desynatadmin.utils.CommonMethod

class AffiliateWithdrawalRequestAdapter(var context:Context, var requestList: ArrayList<GetLiveRequestResponse.Data.Data>, var onClick:OnClick) : RecyclerView.Adapter<AffiliateWithdrawalRequestAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemAffiliateWithdrawalRequestBinding) :
        RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int) = with(binding){
            val liveRequestData = requestList[position]

                tvRequestByValue.text = liveRequestData?.sellerDetails?.firstName+" "+ liveRequestData?.sellerDetails?.lastName+" | "+liveRequestData?.sellerDetails?.countryDetails?.name
                tvReasonValue.text = liveRequestData?.rejectReason
                tvRequestId.text = "#${liveRequestData?.orderId}"
//                tvRequestedAmountValue.text = "${CommonMethod.formatDoubleValue(liveRequestData?.orderId)} KD"
//                tvAvailableAmount.text = "${CommonMethod.formatDoubleValue(liveRequestData?.orderId)} KD"
                tvRequestOnValue.text = CommonMethod.customizeChangeDateFormat(liveRequestData?.createdAt.toString(), outputFormat = "dd/MM/yyyy 'at' hh:mm a" )

                when(liveRequestData?.adminVerify){
                    0->{
                        tvStatusValue.text = "Pending"
                        tvStatusValue.setTextColor(Color.parseColor("#FFA41B"))
                        btnApprove.isVisible = true
                        btnReject.isVisible = true
                        tvReason.isVisible = false
                        tvReasonValue.isVisible = false
                    }
                    1->{
                        tvStatusValue.text = "Completed"
                        tvStatusValue.setTextColor(Color.parseColor("#17854C"))
                        btnApprove.isVisible = false
                        btnReject.isVisible = false
                        tvReason.isVisible = false
                        tvReasonValue.isVisible = false
                    }
                    2->{
                        tvStatusValue.text = "Rejected"
                        tvStatusValue.setTextColor(Color.parseColor("#D91A1B"))
                        btnApprove.isVisible = false
                        btnReject.isVisible = false
                        tvReason.isVisible = true
                        tvReasonValue.isVisible = true
                    }
                }

            btnApprove.setOnClickListener {
                    onClick.click(position,1)
                }

                btnReject.setOnClickListener {
                    onClick.click(position,2)
                }
            }
        }

    interface OnClick{
        fun click(position: Int,type:Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemAffiliateWithdrawalRequestBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return requestList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
     holder.bind(position)
    }

    fun setData(newList: ArrayList<GetLiveRequestResponse.Data.Data>) {
        requestList = newList
        notifyDataSetChanged()
    }
}
