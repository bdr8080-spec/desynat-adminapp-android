package com.desynatadmin.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.desynatadmin.R
import com.desynatadmin.databinding.RejectedItemBinding
import com.desynatadmin.ui.home.brandManagement.response.RejectedListResponse
import com.desynatadmin.utils.formatUtcDateToDdMmYyyy

class RejectedAdapter(var list : List<RejectedListResponse.Data.Data>, val click : OnClick) : RecyclerView.Adapter<RejectedAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: RejectedItemBinding) :
        RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int) = with(binding){
            root.setOnClickListener {
                click.onClick(position)
            }
            val item = list[position]
            tvName.text = item.first_name
            tvLocation.text = item?.store_info?.country_info?.name
            tvCall.text = item.country_code + "-" + item.mobile
            tvEmail.text = item.email
            Glide.with(ivProfileImage).load(item.profile_pic)
                .transform(CircleCrop()).placeholder(R.drawable.ic_user)
                .into(ivProfileImage)
            RejectionDescription.text = item.reject_reason
            tvJoinedOn.text = "Joined on ${formatUtcDateToDdMmYyyy(item?.updated_at.toString())}"

        }
        }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RejectedAdapter.ViewHolder {
        val binding = RejectedItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
           holder.bind(position)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    interface OnClick{
        fun onClick(position: Int)
    }

}


