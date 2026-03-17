package com.desynatadmin.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.desynatadmin.R
import com.desynatadmin.databinding.ItemSelectNotificationUserBinding

class AssignUserAdapter(var context:Context, private var driverList: ArrayList<String>, var onUserClick:OnUserClick) : RecyclerView.Adapter<AssignUserAdapter.ViewHolder>() {
    var selected = -1
    inner class ViewHolder(val binding: ItemSelectNotificationUserBinding) :
        RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int) = with(binding){
            val driverData = driverList[position]

               // tvUser.text = driverData.fullNameEnglish

                if (selected==position){
                    ivRadio.setImageResource(R.drawable.ic_radio_selected)
                }
                else{
                    ivRadio.setImageResource(R.drawable.ic_radio_unselected)
                }

            root.setOnClickListener {
                val previousSelected = selected
                selected = position

                if (previousSelected != -1) {
                    notifyItemChanged(previousSelected)
                }
                notifyItemChanged(selected)

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
        return driverList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
       holder.bind(position)
    }

}
