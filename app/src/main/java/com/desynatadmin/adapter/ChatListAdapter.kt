package com.desynatadmin.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.desynatadmin.databinding.ItemChatBinding
import com.desynatadmin.R
import com.desynatadmin.utils.CommonMethod

class ChatListAdapter(var context:Context, var chatList: ArrayList<String>, var chatClick:ChatClick) : RecyclerView.Adapter<ChatListAdapter.ViewHolder>() {

    interface ChatClick{
        fun onChatClick(position: Int)
    }
    inner class ViewHolder(val binding: ItemChatBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemChatBinding.inflate(
                LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return chatList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chatData = chatList[position]
        holder.binding.apply {

//            tvName.text = chatData?.sellerDetails?.firstName+" "+chatData?.sellerDetails?.lastName
//            ivUser.load(chatData?.sellerDetails?.profilePic){
//                crossfade(true)
//                placeholder(R.drawable.ic_user)
//                error(R.drawable.ic_user)
//            }
//            tvLastMsg.text = chatData?.lastChat
//            if (chatData.lastChatTime!=null&&chatData.lastChatTime!="null"&&chatData.lastChatTime!="") {
//                tvTime.text = chatData.lastChatTime
//            }
//            else{
//                tvTime.text = CommonMethod.formatChatTime(CommonMethod.customizeChangeDateFormat(
//                    chatData?.updatedAt.toString(), outputFormat = "dd MMM yyyy, hh:mm a"
//                ) )
//            }
        }

        holder.itemView.setOnClickListener {
            chatClick.onChatClick(position)
        }

    }


}
