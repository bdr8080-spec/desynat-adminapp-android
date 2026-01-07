package com.desynatadmin.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.desynatadmin.databinding.ItemStoryBinding
import com.desynatadmin.ui.home.storyManagement.response.GetStoryResponse

class StoryAdapter(val context:Context,val storyList: ArrayList<GetStoryResponse.Data.Data>,var click:OnClick) : RecyclerView.Adapter<StoryAdapter.ViewHolder>() {

    interface OnClick{
        fun onClick(position: Int,type:Int)
    }

    class ViewHolder(val binding: ItemStoryBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ):ViewHolder {
        val binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.apply {
            ivPlay.setOnClickListener {
                click.onClick(position,1)
            }

            ivDelete.setOnClickListener {
                click.onClick(position,2)
            }

            when(storyList[position].status){
                1->{
                   tvStatus.text = "Active"
                    tvStatus.setTextColor(Color.parseColor("#68CA44"))
                }
                else -> {
                    tvStatus.text = "Expired"
                    tvStatus.setTextColor(Color.parseColor("#D53B36"))
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return storyList.size
    }

}