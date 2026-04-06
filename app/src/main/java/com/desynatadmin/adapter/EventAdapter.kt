package com.desynatadmin.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.desynatadmin.databinding.ItemEventBinding
import com.desynatadmin.ui.home.liveCoverage.response.GetLiveStreamingRoomResponse
import com.desynatadmin.utils.CommonMethod
import com.desynatadmin.utils.formatUtcDateToDdMmYyyy

class EventAdapter(var context: Context,var list : List<GetLiveStreamingRoomResponse.Data>, val click : OnCLick) :
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
            tvName.text = item?.sellerDetails?.firstName+" "+item?.sellerDetails?.lastName
            tvLocation.text = item?.timeslotDetails?.countryInfo?.name
            Glide.with(ivProfileImage).load(item?.sellerDetails?.profilePic).transform(CircleCrop()).into(ivProfileImage)
            tvJoinedOn.text = "Joined on ${formatUtcDateToDdMmYyyy(item?.createdAt.toString())}"
            tvLiveTiming.text = "Live Timing ${CommonMethod.customizeChangeDateFormat(item?.timeslotDetails?.date.toString(), inputFormat = "yyyy-MM-dd", outputFormat = "dd/MM/yy")}"

            tvTime.text = "${item?.timeslotDetails?.from}"
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


