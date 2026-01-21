package com.desynatadmin.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.desynatadmin.databinding.ItemLiveRequestBinding
import com.desynatadmin.ui.home.liveRequestAndTimeSlotManagement.response.GetLiveRequestResponse
import com.desynatadmin.utils.CommonMethod

class LiveRequestAdapter(var context:Context, var liveRequestList: ArrayList<GetLiveRequestResponse.Data.Data>, var onClick:OnClick) : RecyclerView.Adapter<LiveRequestAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemLiveRequestBinding) :
        RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int) = with(binding){
            val liveRequestData = liveRequestList[position]

                tvName.text = liveRequestData?.sellerDetails?.firstName+" "+ liveRequestData?.sellerDetails?.lastName
                tvReasonValue.text = liveRequestData?.rejectReason
                tvCountry.text = liveRequestData?.sellerDetails?.countryDetails?.name
                tvRequestId.text = "#${liveRequestData?.orderId}"
                tvLiveRequestValue.text = "${CommonMethod.formatDate(liveRequestData?.timeslotDetails?.date.toString(),"yyyy-MM-dd","dd/MM/yyyy")} | ${liveRequestData?.timeslotDetails?.from}"
                tvRequestOnValue.text = CommonMethod.formatDate(liveRequestData?.createdAt.toString(),"yyyy-MM-dd","dd/MM/yyyy")

                when(liveRequestData?.adminVerify){
                    0->{
                        tvStatusValue.text = "Pending"
                        tvStatusValue.setTextColor(Color.parseColor("#FFA41B"))
                        btnAccept.isVisible = true
                        btnReject.isVisible = true
                        viewStatus.isVisible = true
                        tvReason.isVisible = false
                        tvReasonValue.isVisible = false
                        tvPaymentMode.isVisible = false
                        tvPaymentModeValue.isVisible = false
                    }
                    1->{
                        tvStatusValue.text = "Confirmed"
                        tvStatusValue.setTextColor(Color.parseColor("#17854C"))
                        viewStatus.isVisible = false
                        btnAccept.isVisible = false
                        btnReject.isVisible = false
                        tvReason.isVisible = false
                        tvReasonValue.isVisible = false
                        tvPaymentMode.isVisible = true
                        tvPaymentModeValue.isVisible = true
                    }
                    2->{
                        tvStatusValue.text = "Rejected"
                        tvStatusValue.setTextColor(Color.parseColor("#D91A1B"))
                        btnAccept.isVisible = false
                        btnReject.isVisible = false
                        viewStatus.isVisible = true
                        tvReason.isVisible = true
                        tvReasonValue.isVisible = true
                        tvPaymentMode.isVisible = true
                        tvPaymentModeValue.isVisible = true
                    }
                }

                btnAccept.setOnClickListener {
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
            ItemLiveRequestBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return liveRequestList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
     holder.bind(position)
    }

    fun setData(newList: ArrayList<GetLiveRequestResponse.Data.Data>) {
        liveRequestList = newList
        notifyDataSetChanged()
    }

}
