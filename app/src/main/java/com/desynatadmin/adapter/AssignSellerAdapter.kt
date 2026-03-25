package com.desynatadmin.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.desynatadmin.R
import com.desynatadmin.databinding.ItemSelectNotificationUserBinding
import com.desynatadmin.ui.home.brandManagement.response.ApprovedSellerResponse
import com.desynatadmin.ui.home.notificationManagement.response.AssignSellerResponse

class AssignSellerAdapter(var context:Context, private var sellerList: ArrayList<AssignSellerResponse.Data>, var onUserClick:OnUserClick) : RecyclerView.Adapter<AssignSellerAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemSelectNotificationUserBinding) :
        RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int) = with(binding){
            val sellerData = sellerList[position]

                tvUser.text = sellerData?.first_name+" "+sellerData?.last_name

                if (sellerData.isSelected){
                    ivRadio.setImageResource(R.drawable.ic_checkbox)
                }
                else{
                    ivRadio.setImageResource(R.drawable.ic_checkbox_unselected)
                }

            ivRadio.setOnClickListener {
                sellerList[position].isSelected = !sellerList[position].isSelected
                notifyDataSetChanged()

                onUserClick.onClick(position)

            }
        }
        }

    interface OnUserClick{
        fun onClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemSelectNotificationUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return sellerList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
       holder.bind(position)
    }

}
