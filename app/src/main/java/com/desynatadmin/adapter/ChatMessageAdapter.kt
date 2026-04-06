package com.desynatadmin.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.desynatadmin.R
import com.desynatadmin.databinding.ItemMsgListBinding
import com.desynatadmin.ui.home.supportChatManagement.response.ChatMessage
import com.desynatadmin.ui.home.supportChatManagement.toSafeInt
import com.desynatadmin.ui.home.supportChatManagement.toSafeString
import com.desynatadmin.utils.CommonMethod
import com.desynatadmin.utils.SharedPreference

class ChatMessageAdapter(var context:Context, var chatList: ArrayList<ChatMessage>, var chatClick:ChatClick) : RecyclerView.Adapter<ChatMessageAdapter.ViewHolder>() {

    interface ChatClick{
        fun onChatClick(position: Int)
    }
    inner class ViewHolder(val binding: ItemMsgListBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemMsgListBinding.inflate(
                LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return chatList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chatData = chatList[position]
        holder.binding.apply {
            val isArabicApp = SharedPreference.get(context).language == "ar"
            val arText = chatData.messageAr.toSafeString()
            val enText = chatData.message.toSafeString()

            if (chatData.by.toSafeString() == "admin") {
                relMy.isVisible = true
                relOther.isVisible = false

                if (chatData.type.toSafeInt()==1){
                    ivMyMsg.isVisible = true
                    tvMyMsg.isVisible = false
                    Glide.with(context).load(chatData.message.toSafeString()).placeholder(R.drawable.ic_placeholder).into(ivMyMsg)
                }
                else{
                    ivMyMsg.isVisible = false
                    tvMyMsg.isVisible = true
                    tvMyMsg.text = when {
                        isArabicApp && arText.isNotEmpty() -> arText
                        enText.isNotEmpty() -> enText
                        else -> arText
                    }                }

                if (chatData.read.toSafeInt()==1){
                    ivRead.isVisible = true
                }
                else{
                    ivRead.isVisible = false
                }
                tvMyMsgTime.text = CommonMethod.customizeChangeDateFormat(chatData.times.toSafeString(),"yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'","hh:mma")

            }
            else{
                relMy.isVisible = false
                relOther.isVisible = true
                if (chatData.type.toSafeInt()==1){
                    ivOtherMsg.isVisible = true
                    tvOtherMsg.isVisible = false
                    Glide.with(context).load(chatData.message.toSafeString()).placeholder(R.drawable.ic_placeholder).into(ivOtherMsg)
                }
                else{
                    ivOtherMsg.isVisible = false
                    tvOtherMsg.isVisible = true
                    tvOtherMsg.text = when {
                        isArabicApp && arText.isNotEmpty() -> arText
                        enText.isNotEmpty() -> enText
                        else -> arText
                    }
                }
                tvOtherMsgTime.text = CommonMethod.customizeChangeDateFormat(chatData.times.toSafeString(),"yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'","hh:mma")
            }

            ivMyMsg.setOnClickListener {
                chatClick.onChatClick(position)
            }

            ivOtherMsg.setOnClickListener {
                chatClick.onChatClick(position)
            }

        }

    }
}
