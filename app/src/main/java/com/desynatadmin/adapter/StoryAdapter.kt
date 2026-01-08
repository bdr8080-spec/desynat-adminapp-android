package com.desynatadmin.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.desynatadmin.R
import com.desynatadmin.databinding.ItemStoryBinding
import com.desynatadmin.ui.home.storyManagement.response.GetStoryResponse
import com.desynatadmin.utils.CommonMethod

class StoryAdapter(
    val context: Context,
    val storyList: ArrayList<GetStoryResponse.Data.Data>,
    var click: OnClick
) : RecyclerView.Adapter<StoryAdapter.ViewHolder>() {

    interface OnClick {
        fun onClick(position: Int, type: Int)
    }

    class ViewHolder(val binding: ItemStoryBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.apply {

            if (position == 0) {
                clAddReel.isVisible = true
                clReel.isVisible = false

                clAddReel.setOnClickListener {
                    click.onClick(0, 1)
                }
                return
            }

            clAddReel.isVisible = false
            clReel.isVisible = true

            val story = storyList[position - 1]

            ivPlay.setOnClickListener {
                click.onClick(position - 1, 2)
            }

            ivDelete.setOnClickListener {
                click.onClick(position - 1, 3)
            }

            ivStory.load(story.file){
                crossfade(true)
                placeholder(R.drawable.ic_placeholder)
                error(R.drawable.ic_placeholder)
            }

            tvPostedOn.text = CommonMethod.customizeChangeDateFormat(story.createdAt, outputFormat = "dd/MM/yyyy, hh:mma")

            when (story.status) {
                1 -> {
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
        return storyList.size + 1
    }
}
