package com.desynatadmin.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.desynatadmin.R
import com.desynatadmin.databinding.ItemDriverBinding
import com.desynatadmin.ui.home.orderManagement.response.GetAvailableDeliveryBoyResponse

class AssignDriverAdapter(var context:Context, private var driverList: ArrayList<GetAvailableDeliveryBoyResponse.Data>, var driverClick:DriverClick) : RecyclerView.Adapter<AssignDriverAdapter.ViewHolder>() {
    var selected = -1
    inner class ViewHolder(val binding: ItemDriverBinding) :
        RecyclerView.ViewHolder(binding.root)

    interface DriverClick{
        fun onDriverClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemDriverBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return driverList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var driverData = driverList[position]

        holder.binding.apply {
            tvDrive.text = driverData.fullNameEnglish

            if (selected==position){
                ivRadio.setImageResource(R.drawable.ic_radio_selected)
            }
            else{
                ivRadio.setImageResource(R.drawable.ic_radio_unselected)
            }
        }

        holder.itemView.setOnClickListener {
            val previousSelected = selected
            selected = position

            if (previousSelected != -1) {
                notifyItemChanged(previousSelected)
            }
            notifyItemChanged(selected)

            driverClick.onDriverClick(position)

        }
    }

}
