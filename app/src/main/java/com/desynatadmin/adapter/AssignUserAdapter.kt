package com.desynatadmin.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.desynatadmin.R
import com.desynatadmin.databinding.ItemSelectNotificationUserBinding
import com.desynatadmin.ui.home.notificationManagement.response.AssignUserResponse
import com.desynatadmin.ui.home.userManagement.response.GetUserResponse

class AssignUserAdapter(var context:Context, private var userList: ArrayList<AssignUserResponse.Data>, var onUserClick:OnUserClick) : RecyclerView.Adapter<AssignUserAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemSelectNotificationUserBinding) :
        RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int) = with(binding){
            val userData = userList[position]

                tvUser.text = userData.fullName

                if (userData.isSelected){
                    ivRadio.setImageResource(R.drawable.ic_checkbox)
                }
                else{
                    ivRadio.setImageResource(R.drawable.ic_checkbox_unselected)
                }

            root.setOnClickListener {
                userList[position].isSelected = !userList[position].isSelected
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
        return userList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
       holder.bind(position)
    }

}
