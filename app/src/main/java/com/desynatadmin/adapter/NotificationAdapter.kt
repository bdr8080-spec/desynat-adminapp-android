package com.desynatadmin.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.desynatadmin.R
import com.desynatadmin.databinding.ItemNotificationBinding
import com.desynatadmin.ui.home.notificationManagement.response.GetSendNotificationListResponse
import com.desynatadmin.utils.CommonMethod
import com.desynatadmin.utils.Constants

class NotificationAdapter(var context:Context, var notificationList: ArrayList<GetSendNotificationListResponse.Data.Data>, var clickOnItem:ClickOnItem) : RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemNotificationBinding) :
        RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int) = with(binding){
            val notificationData = notificationList[position]
            tvTitle.text = if (Constants.language=="ar") notificationData?.titleArabic?:notificationData?.titleEnglish else notificationData?.titleEnglish
            tvDescription.text = if (Constants.language=="ar") notificationData?.descriptionArabic?:notificationData?.descriptionEnglish else notificationData?.descriptionEnglish
            tvDate.text = CommonMethod.customizeChangeDateFormat(notificationData.createdAt, outputFormat = "dd/MM/yyyy")
//            if (notificationData?.totalReceivers?:0 > 1 ) {
//                tvSentName.text = "Sent To- ${notificationData?.receiverDetails?.get(0)?.fullName?:notificationData?.receiverDetails?.get(0)?.fullNameEnglish} + ${(notificationData?.totalReceivers ?: 0) - 1}"
//            }
//            else {
//                tvSentName.text = "Sent To- ${notificationData?.receiverDetails?.get(0)?.fullName?:notificationData?.receiverDetails?.get(0)?.fullNameEnglish}"
//            }

            val firstReceiver = notificationData?.receiverDetails?.firstOrNull()

            val name =  if (firstReceiver?.fullNameArabic!=null&&firstReceiver?.fullNameArabic!="null"&& Constants.language=="ar"){
                firstReceiver?.fullNameArabic
            }
               else if (firstReceiver?.fullNameEnglish!=null&&firstReceiver?.fullNameEnglish!="null"){
                firstReceiver?.fullNameEnglish
            }
                else if(firstReceiver?.fullName!=null&&firstReceiver?.fullName!="null"){
                firstReceiver?.fullName
            }
            else{
                firstReceiver?.firstName+" "+firstReceiver?.lastName
            }

            val total = notificationData?.totalReceivers ?: 0

            tvSentName.text =
                if (total > 1) {
                    "${context.getString(R.string.send_to)}- $name + ${total - 1}"
                } else {
                    "${context.getString(R.string.send_to)}- $name"
                }

            if (notificationData?.receiverDetails?.get(0)?.status?:0==1){
                ivBell.setImageResource(R.drawable.icon_read_bell)
                clMain.setBackgroundResource(R.drawable.notification_read_background)
                tvSentName.setTextColor(Color.parseColor("#99000000"))
            }
            else{
                ivBell.setImageResource(R.drawable.icon_unread_bell)
                clMain.setBackgroundResource(R.drawable.notification_unread_background)
                tvSentName.setTextColor(Color.parseColor("#6E4527"))
            }
        }
        }

    interface ClickOnItem{
        fun onClickItem(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemNotificationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return notificationList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    fun setData(newList: ArrayList<GetSendNotificationListResponse.Data.Data>) {
        notificationList = newList
        notifyDataSetChanged()
    }

}
