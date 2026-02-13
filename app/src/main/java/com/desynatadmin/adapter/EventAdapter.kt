package com.desynatadmin.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.desynatadmin.databinding.ApprovedItemBinding
import com.desynatadmin.databinding.ItemEventBinding
import com.desynatadmin.ui.home.brandManagement.response.ApprovedSellerResponse
import com.desynatadmin.ui.home.brandManagement.response.NewSellerResponse
import com.desynatadmin.utils.formatUtcDateToDdMmYyyy

class EventAdapter(var context: Context,var list : List<String>, val click : OnCLick) :
    RecyclerView.Adapter<EventAdapter.ViewHolder>() {
    class ViewHolder(val binding: ItemEventBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): EventAdapter.ViewHolder {
        val binding = ItemEventBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.apply {
            val item = list[position]
//            tvName.text = item.first_name
//            tvLocation.text = item.store_info.country_info.name
//            Glide.with(ivProfileImage).load(item.profile_pic).transform(CircleCrop()).into(ivProfileImage)
//            tvJoinedOn.text = "Joined at ${formatUtcDateToDdMmYyyy(item.updated_at)}"

            btnWatchLive.setOnClickListener {
                click.onClick(position)
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    interface OnCLick{
        fun onClick(position: Int)
    }

}


