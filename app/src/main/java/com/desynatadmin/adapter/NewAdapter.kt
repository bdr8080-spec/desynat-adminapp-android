package com.desynatadmin.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.desynatadmin.R
import com.desynatadmin.databinding.NewItemBinding
import com.desynatadmin.ui.home.brandManagement.response.NewSellerResponse
import com.desynatadmin.utils.Constants
import com.desynatadmin.utils.SharedPreference
import com.desynatadmin.utils.formatUtcDateToDdMmYyyy
import kotlin.math.cos

class NewAdapter(var context: Context,var list: List<NewSellerResponse.Data.Data>, val click: OnClick) :
    RecyclerView.Adapter<NewAdapter.ViewHolder>() {
    class ViewHolder(val binding: NewItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NewAdapter.ViewHolder {
        val binding = NewItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.apply {
            val item = list[position]
            var actionAccess: Boolean=true
            val userInfo = SharedPreference.get(context).getUser()
            if (userInfo?.type=="2"){
                val subAdminInfo = SharedPreference.get(context).getSubAdmin()
                val module = subAdminInfo?.permissionDetails
                    ?.firstOrNull {
                        it.moduleDetails.name.equals("seller_management", true)
                    }

                actionAccess = module?.canAction == 1
                btnAccept.isVisible = actionAccess
                btnReject.isVisible = actionAccess
                view2.isVisible = actionAccess
            }
            tvName.text = item.first_name
            tvLocation.text = if (Constants.language=="ar")item?.store_info?.country_info?.name_ar?:item?.store_info?.country_info?.name else item?.store_info?.country_info?.name
            tvCall.text = item.country_code + "-" + item.mobile
            Glide.with(ivProfileImage).load(item.profile_pic)
                .transform(CircleCrop()).placeholder(R.drawable.ic_user)
                .into(ivProfileImage)
            tvEmail.text = item.email
            tvJoinedOn.text = "${context.getString(R.string.joined_on)} ${formatUtcDateToDdMmYyyy(item.store_info.updated_at)}"
            root.setOnClickListener {
                    click.onCLick(position)
                }
            btnAccept.setOnClickListener {
                click.onAccept(position)
            }

            btnReject.setOnClickListener {
                click.onDelete(position)
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    interface OnClick {
        fun onCLick(position: Int)
        fun onAccept(position: Int)
        fun onDelete(position: Int)
    }

}

