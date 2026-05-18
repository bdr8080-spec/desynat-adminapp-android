package com.desynatadmin.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.desynatadmin.R
import com.desynatadmin.databinding.HighlightsItemBinding
import com.desynatadmin.ui.home.storyManagement.response.GetStoryResponse

class HighlightsAdapter(val context: Context, val highlightList: ArrayList<GetStoryResponse.Data.Data>,var click:OnItemClick) : RecyclerView.Adapter<HighlightsAdapter.ViewHolder>() {

    interface OnItemClick{
        fun clickOnItem(position: Int)
    }

    inner class ViewHolder(val binding: HighlightsItemBinding) :
        RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int) = with(binding){
            Glide.with(context).load(highlightList[position].file).placeholder(R.drawable.ic_placeholder).into(ivProfileImage)
            tvTitle.text = highlightList[position].title

            root.setOnClickListener {
                click.clickOnItem(position)
            }
        }
        }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HighlightsAdapter.ViewHolder {
        val binding = HighlightsItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)

    }

    override fun getItemCount(): Int {
        return highlightList.size
    }

}
