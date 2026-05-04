package com.desynatadmin.adapter

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.desynatadmin.R
import com.desynatadmin.databinding.ItemLiveRequestBinding
import com.desynatadmin.ui.home.liveRequestAndTimeSlotManagement.response.GetLiveRequestResponse
import com.desynatadmin.utils.CommonMethod
import com.desynatadmin.utils.SharedPreference
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class LiveRequestAdapter(var context:Context, var liveRequestList: ArrayList<GetLiveRequestResponse.Data.Data>, var onClick:OnClick) : RecyclerView.Adapter<LiveRequestAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemLiveRequestBinding) :
        RecyclerView.ViewHolder(binding.root){
        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(position: Int) = with(binding){
            val liveRequestData = liveRequestList[position]
            var actionAccess: Boolean=true
            val userInfo = SharedPreference.get(context).getUser()
            if (userInfo?.type=="2"){
                val subAdminInfo = SharedPreference.get(context).getSubAdmin()
                val module = subAdminInfo?.permissionDetails
                    ?.firstOrNull {
                        it.moduleDetails.name.equals("live_request_management", true)
                    }

                actionAccess = module?.canAction == 1
            }

                tvName.text = liveRequestData?.sellerDetails?.firstName+" "+ liveRequestData?.sellerDetails?.lastName
                tvReasonValue.text = liveRequestData?.rejectReason
                tvCountry.text = liveRequestData?.sellerDetails?.storeInfo?.countryInfo?.name
                tvRequestId.text = "#${liveRequestData?.orderId}"
                tvLiveRequestValue.text = "${CommonMethod.formatDate(liveRequestData?.timeslotDetails?.date.toString(),"yyyy-MM-dd","dd/MM/yyyy")} | ${liveRequestData?.timeslotDetails?.from}"
                tvRequestOnValue.text = CommonMethod.customizeChangeDateFormat(liveRequestData?.createdAt.toString(),  outputFormat= "dd/MM/yyyy | hh:mm a")

                when(liveRequestData?.adminVerify){
                    0->{
                        tvStatusValue.text = context.getString(R.string.pending)
                        tvStatusValue.setTextColor(Color.parseColor("#FFA41B"))
                        btnAccept.isVisible = actionAccess
                        btnReject.isVisible = actionAccess
                        viewStatus.isVisible = actionAccess
                        tvReason.isVisible = false
                        tvReasonValue.isVisible = false
                        tvPaymentMode.isVisible = false
                        tvPaymentModeValue.isVisible = false
                    }
                    1->{
                        tvStatusValue.text = getStatus(liveRequestData?.timeslotDetails?.date.toString(),liveRequestData?.timeslotDetails?.from.toString(),liveRequestData?.timeslotDetails?.to.toString())
                        tvStatusValue.setTextColor(Color.parseColor("#17854C"))
                        viewStatus.isVisible = false
                        btnAccept.isVisible = false
                        btnReject.isVisible = false
                        tvReason.isVisible = false
                        tvReasonValue.isVisible = false
                        tvPaymentMode.isVisible = false
                        tvPaymentModeValue.isVisible = false
                    }
                    2->{
                        tvStatusValue.text = context.getString(R.string.rejected)
                        tvStatusValue.setTextColor(Color.parseColor("#D91A1B"))
                        btnAccept.isVisible = false
                        btnReject.isVisible = false
                        viewStatus.isVisible = true
                        tvReason.isVisible = true
                        tvReasonValue.isVisible = true
                        tvPaymentMode.isVisible = true
                        tvPaymentModeValue.isVisible = true
                        tvPaymentMode.text = context.getString(R.string.rejected_on)
                        tvPaymentModeValue.text = CommonMethod.customizeChangeDateFormat(liveRequestData?.createdAt.toString(),  outputFormat= "dd/MM/yyyy | hh:mm a")
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

    @RequiresApi(Build.VERSION_CODES.O)
    fun getStatus(
        date: String,
        from: String,
        to: String
    ): String {

        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.ENGLISH)

        val slotDate = LocalDate.parse(date.trim(), dateFormatter)
        val toTime = LocalTime.parse(to.trim(), timeFormatter)

        val slotEndDateTime = LocalDateTime.of(slotDate, toTime)
        val currentDateTime = LocalDateTime.now()

        return if (currentDateTime.isAfter(slotEndDateTime)) {
            context.getString(R.string.completed)
        } else {
            context.getString(R.string.confirmed)
        }
    }
}
