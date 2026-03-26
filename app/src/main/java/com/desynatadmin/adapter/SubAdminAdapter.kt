package com.desynatadmin.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.desynatadmin.R
import com.desynatadmin.databinding.ItemSubAdminBinding
import com.desynatadmin.ui.home.userManagement.response.GetUserResponse
import com.desynatadmin.utils.formatUtcDateToDdMmYyyy

class SubAdminAdapter(var context:Context, var adminList: ArrayList<GetUserResponse.Data.Data>, var subAdminClick:SubAdminClick) : RecyclerView.Adapter<SubAdminAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemSubAdminBinding) :
        RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int) = with(binding){
            val adminData = adminList[position]

            tvName.text = adminData.fullName

            tvLocation.text = adminData.countryInfo.name
            tvJoinedOn.text = "Added on ${formatUtcDateToDdMmYyyy(adminData.createdAt)}"
            tvEmail.text = adminData.email
            tvCall.text = adminData.countryCode+"-"+adminData.mobile
            Glide.with(ivProfileImage).load(adminData.profileImage).placeholder(R.drawable.ic_user).error(R.drawable.ic_user).transform(CircleCrop()).into(ivProfileImage)
            if(adminData.status=="1"){
                ivSwitch.setImageResource(R.drawable.switch_on)
            }
            else{
                ivSwitch.setImageResource(R.drawable.switch_off)
            }
            ivSwitch.setOnClickListener {
                subAdminClick.onSubAdminClick(position,2)
            }

        }
        }

    interface SubAdminClick{
        fun onSubAdminClick(position: Int,type:Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemSubAdminBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return adminList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    fun setData(newList: ArrayList<GetUserResponse.Data.Data>) {
        adminList = newList
        notifyDataSetChanged()
    }

}
