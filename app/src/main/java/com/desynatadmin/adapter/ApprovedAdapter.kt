package com.desynatadmin.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.desynatadmin.R
import com.desynatadmin.databinding.ApprovedItemBinding
import com.desynatadmin.ui.home.brandManagement.response.ApprovedSellerResponse
import com.desynatadmin.utils.Constants
import com.desynatadmin.utils.formatUtcDateToDdMmYyyy
import kotlin.plus

class ApprovedAdapter(var context: Context,var list : ArrayList<ApprovedSellerResponse.Data.Data>, val click : OnCLick) :
    RecyclerView.Adapter<ApprovedAdapter.ViewHolder>() {
    class ViewHolder(val binding: ApprovedItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ApprovedAdapter.ViewHolder {
        val binding = ApprovedItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.apply {
            val item = list[position]
            tvName.text = item?.first_name+" "+item?.last_name
            tvLocation.text = if (Constants.language=="ar")item?.store_info?.country_info?.name_ar?:item?.store_info?.country_info?.name else item?.store_info?.country_info?.name
            tvCall.text = item.country_code + "-" + item.mobile
            Glide.with(ivProfileImage).load(item.profile_pic).transform(CircleCrop()).placeholder(R.drawable.ic_user).into(ivProfileImage)
            tvEmail.text = item.email
            tvTotalOrdersValue.text = "${(item?.total_cancelled_orders ?: 0) + (item?.total_completed_orders ?: 0)}"
            tvTotalEarningsValue.text = item.total_earning
            tvEmail.text = item.email
            tvJoinedOn.text = "${context.getString(R.string.joined_at)} ${formatUtcDateToDdMmYyyy(item?.updated_at.toString())}"
            if (!item?.stories.isNullOrEmpty()) {

                ivStoryHighlight?.isVisible = true

                val allStoriesSeen = item?.stories?.all { it.isSeen == 1 } == true

                ivStoryHighlight?.strokeColor = ColorStateList.valueOf(
                    ContextCompat.getColor(
                        context,
                        if (allStoriesSeen) {
                            R.color.light_brown   // All stories read
                        } else {
                            R.color.brown         // At least one unread story
                        }
                    )
                )

            } else {
                ivStoryHighlight?.isInvisible = true
            }

            ivProfileImage.setOnClickListener {
                click.onClick(position,2)
            }
            root.setOnClickListener {
                click.onClick(position,1)
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    interface OnCLick{
        fun onClick(position: Int,type:Int)
    }

}


