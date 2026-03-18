package com.desynatadmin.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.desynatadmin.R
import com.desynatadmin.databinding.ItemDriverBinding
import com.desynatadmin.databinding.ItemSelectNotificationUserBinding
import com.desynatadmin.ui.home.deliveryBoyManagement.response.GetAllDeliveryBoyResponse
import com.desynatadmin.ui.home.userManagement.response.GetUserResponse

class AssignDeliveryBoyAdapter(var context:Context, private var driverList: ArrayList<GetAllDeliveryBoyResponse.Data.Data>, var onClick:OnClick) : RecyclerView.Adapter<AssignDeliveryBoyAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemSelectNotificationUserBinding) :
        RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int) = with(binding){
            val driverData = driverList[position]

            tvUser.text = driverData.fullNameEnglish

            if (driverData.isSelected){
                ivRadio.setImageResource(R.drawable.ic_checkbox)
            }
            else{
                ivRadio.setImageResource(R.drawable.ic_checkbox_unselected)
            }

            root.setOnClickListener {
                driverList[position].isSelected = !driverList[position].isSelected
                notifyDataSetChanged()

                onClick.onClick(position)

            }
        }
    }

    interface OnClick{
        fun onClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemSelectNotificationUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return driverList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

}

