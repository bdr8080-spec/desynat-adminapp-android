package com.desynatadmin.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.desynatadmin.R
import com.desynatadmin.databinding.ItemMyNotificationBinding

import com.desynatadmin.model.DataX
import com.desynatadmin.utils.CommonMethod
import com.desynatadmin.utils.Constants

class MyNotificationAdapter(
    var context: Context,
    var notificationList: ArrayList<DataX>,
    var clickOnItem: ClickOnItem
) : RecyclerView.Adapter<MyNotificationAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemMyNotificationBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) = with(binding) {
            val notificationData = notificationList[position]
            tvTitle.text = if (Constants.language == "ar") notificationData.titleArabic
                ?: notificationData.titleEnglish else notificationData.titleEnglish
            tvDescription.text = if (Constants.language == "ar") notificationData.descriptionArabic
                ?: notificationData.descriptionEnglish else notificationData.descriptionEnglish
            tvDate.text = CommonMethod.customizeChangeDateFormat(
                notificationData.createdAt.toString(),
                outputFormat = "dd/MM/yyyy"
            )
            if (notificationData.isRead == 1) {
                ivBell.setImageResource(R.drawable.icon_read_bell)
                clMain.setBackgroundResource(R.drawable.notification_read_background)
            } else {
                ivBell.setImageResource(R.drawable.icon_unread_bell)
                clMain.setBackgroundResource(R.drawable.notification_unread_background)
            }

            clMain.setOnClickListener {
                clickOnItem.onClickItem(position)
            }
        }
    }

    interface ClickOnItem {
        fun onClickItem(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemMyNotificationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return notificationList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

}
