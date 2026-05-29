package com.desynatadmin.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.desynatadmin.R
import com.desynatadmin.databinding.ReelItemBinding
import com.desynatadmin.ui.home.storyManagement.response.GetStoryResponse

class ReelAdapter(val context: Context,val reelList: ArrayList<GetStoryResponse.Data.Data>,var click:OnItemClick) :
    RecyclerView.Adapter<ReelAdapter.ViewHolder>() {

    interface OnItemClick{
        fun clickOnItem(position: Int,type:Int)
    }

    inner class ViewHolder(val binding: ReelItemBinding) :
        RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int) = with(binding){
                Glide.with(context).load(reelList[position].file).placeholder(R.drawable.ic_placeholder).into(img)

            root.setOnClickListener {
                click.clickOnItem(position,0)
            }

            icDelete.setOnClickListener {
                click.clickOnItem(position,1)
            }
        }
        }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ReelAdapter.ViewHolder {
        val binding = ReelItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return reelList.size
    }

}