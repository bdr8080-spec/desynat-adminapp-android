package com.desynatadmin.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.desynatadmin.databinding.ApprovedItemBinding
import com.desynatadmin.ui.home.brandManagement.response.ApprovedSellerResponse
import com.desynatadmin.ui.home.brandManagement.response.NewSellerResponse
import com.desynatadmin.utils.formatUtcDateToDdMmYyyy

class ApprovedAdapter(var list : List<ApprovedSellerResponse.Data.Data>, val click : OnCLick) :
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
            tvName.text = item.first_name
            tvLocation.text = item.store_info.store_address
            tvCall.text = item.country_code + "-" + item.mobile
            Glide.with(ivProfileImage).load(item.profile_pic).into(ivProfileImage)
            tvEmail.text = item.email
            tvJoinedOn.text = "Joined at ${formatUtcDateToDdMmYyyy(item.updated_at)}"

            root.setOnClickListener {
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


