package com.desynatadmin.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.desynatadmin.databinding.ItemSendImgBinding
import com.desynatadmin.ui.home.supportChatManagement.UploadImageType

class UploadMsgImageAdapter(var context:Context, var imageList: ArrayList<UploadImageType>, var click:Click) : RecyclerView.Adapter<UploadMsgImageAdapter.ViewHolder>() {

    interface Click{
        fun onClick(position: Int)
    }
    inner class ViewHolder(val binding: ItemSendImgBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemSendImgBinding.inflate(
                LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return imageList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.apply {
            ivImage.setImageURI(imageList[position].image)
        }

        holder.binding.ivImage.setOnClickListener {
            click.onClick(position)
        }
    }


}
