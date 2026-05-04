package com.desynatadmin.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.desynatadmin.R
import com.desynatadmin.databinding.ItemStoryBinding
import com.desynatadmin.ui.home.storyManagement.response.GetStoryResponse
import com.desynatadmin.utils.CommonMethod
import com.desynatadmin.utils.SharedPreference

class StoryAdapter(
    val context: Context,
    val storyList: ArrayList<GetStoryResponse.Data.Data>,
    var click: OnClick
) : RecyclerView.Adapter<StoryAdapter.ViewHolder>() {

    interface OnClick {
        fun onClick(position: Int, type: Int)
    }

    inner class ViewHolder(private val binding: ItemStoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private var actionAccess: Boolean = true

        fun bind(position: Int) = with(binding) {

            val userInfo = SharedPreference.get(context).getUser()
            if (userInfo?.type=="2"){
                val subAdminInfo = SharedPreference.get(context).getSubAdmin()
                val module = subAdminInfo?.permissionDetails
                    ?.firstOrNull {
                        it.moduleDetails.name.equals("story_management", true)
                    }

                actionAccess = module?.canAction == 1
                ivDelete.isVisible = actionAccess
                clAddReel.isClickable = actionAccess
            }

            ivStory.setImageDrawable(null)
            ivStory.setImageResource(R.drawable.ic_placeholder)
            ivPlay.isVisible = false

            if (position == 0) {
                clAddReel.isVisible = true
                clReel.isVisible = false

                clAddReel.setOnClickListener {
                    if (!actionAccess) return@setOnClickListener
                    click.onClick(0, 1)
                }
                return
            }

            clAddReel.isVisible = false
            clReel.isVisible = true

            val storyIndex = position - 1
            val story = storyList[storyIndex]

            ivPlay.setOnClickListener {
                click.onClick(storyIndex, 2)
            }

            ivDelete.setOnClickListener {
                click.onClick(storyIndex, 3)
            }

            Glide.with(context)
                .load(story.file)
                .placeholder(R.drawable.ic_placeholder)
                .into(ivStory)

            ivPlay.isVisible = isVideo(story.file)

            tvPostedOn.text = CommonMethod.customizeChangeDateFormat(
                story.createdAt,
                outputFormat = "dd/MM/yyyy, hh:mma"
            )

            when (story.status) {
                1 -> {
                    tvStatus.text = context.getString(R.string.active)
                    tvStatus.setTextColor(Color.parseColor("#68CA44"))
                }
                else -> {
                    tvStatus.text = context.getString(R.string.expired)
                    tvStatus.setTextColor(Color.parseColor("#D53B36"))
                }
            }

            root.setOnClickListener {
                if (position == 0) return@setOnClickListener
                if (isVideo(story.file)) {
                    click.onClick(storyIndex, 2)
                } else {
                    click.onClick(storyIndex, 4)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return storyList.size + 1
    }

    fun isVideo(url: String): Boolean {
        val lower = url.lowercase()
        return lower.endsWith(".mp4")
                || lower.endsWith(".mkv")
                || lower.endsWith(".3gp")
                || lower.endsWith(".webm")
                || lower.endsWith(".mov")
    }
}
