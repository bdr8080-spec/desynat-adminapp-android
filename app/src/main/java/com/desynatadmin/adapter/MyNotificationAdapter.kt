package com.desynatadmin.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.desynatadmin.R
import com.desynatadmin.databinding.ItemMyNotificationBinding

class MyNotificationAdapter(var context:Context, var notificationList: ArrayList<String>, var clickOnItem:ClickOnItem) : RecyclerView.Adapter<MyNotificationAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemMyNotificationBinding) :
        RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int) = with(binding){
            val notificationData = notificationList[position]
            tvTitle.text = ""
            tvDescription.text = ""
            tvDate.text = ""
//            if (notificationData.isRead){
//                ivBell.setImageResource(R.drawable.icon_read_bell)
//                clMain.setBackgroundResource(R.drawable.notification_read_background)
//            }
//            else{
//                ivBell.setImageResource(R.drawable.icon_unread_bell)
//                clMain.setBackgroundResource(R.drawable.notification_unread_background)
//            }
        }
        }

    interface ClickOnItem{
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
