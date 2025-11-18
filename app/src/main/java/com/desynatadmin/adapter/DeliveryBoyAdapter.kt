package com.desynatadmin.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.desynatadmin.R
import com.desynatadmin.databinding.ItemDeliveryBoyBinding
import com.desynatadmin.databinding.ItemUserBinding
import com.desynatadmin.ui.home.deliveryBoyManagement.response.GetAllDeliveryBoyResponse
import com.desynatadmin.ui.home.userManagement.response.GetUserResponse
import com.desynatadmin.utils.CommonMethod
import com.desynatadmin.utils.formatUtcDateToDdMmYyyy

class DeliveryBoyAdapter(var context:Context, var deliveryBoyList: ArrayList<GetAllDeliveryBoyResponse.Data.Data>, var deliveryBoyClick:DeliveryBoyClick) : RecyclerView.Adapter<DeliveryBoyAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemDeliveryBoyBinding) :
        RecyclerView.ViewHolder(binding.root)

    interface DeliveryBoyClick{
        fun onDeliveryBoyClick(position: Int,type:Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemDeliveryBoyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return deliveryBoyList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var deliveryBoyData = deliveryBoyList[position]

        holder.binding.apply {
            tvName.text = deliveryBoyData.fullNameEnglish
            tvJoinedOn.text = "Joined at ${formatUtcDateToDdMmYyyy(deliveryBoyData.createdAt)}"
            tvEmail.text = deliveryBoyData.email
            tvCall.text = deliveryBoyData.countryCode+"-"+deliveryBoyData.mobile
            Glide.with(ivProfileImage).load(deliveryBoyData.profilePic).placeholder(R.drawable.ic_user).transform(CircleCrop()).into(ivProfileImage)
            if(deliveryBoyData.status=="1"){
                ivSwitch.setImageResource(R.drawable.switch_on)
            }
            else{
                ivSwitch.setImageResource(R.drawable.switch_off)
            }
            ivSwitch.setOnClickListener {
                deliveryBoyClick.onDeliveryBoyClick(position,2)
            }

            btnEdit.setOnClickListener {
                deliveryBoyClick.onDeliveryBoyClick(position,4)
            }

            btnDelete.setOnClickListener {
                deliveryBoyClick.onDeliveryBoyClick(position,3)
            }

            cl1.setOnClickListener {
                deliveryBoyClick.onDeliveryBoyClick(position,1)
            }
        }
    }

    fun setData(newList: ArrayList<GetAllDeliveryBoyResponse.Data.Data>) {
        deliveryBoyList = newList
        notifyDataSetChanged()
    }

}
