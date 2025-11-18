package com.desynatadmin.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.desynatadmin.R
import com.desynatadmin.databinding.ItemUserBinding
import com.desynatadmin.ui.home.userManagement.response.GetUserResponse
import com.desynatadmin.utils.CommonMethod
import com.desynatadmin.utils.formatUtcDateToDdMmYyyy

class UserAdapter(var context:Context, var userList: ArrayList<GetUserResponse.Data.Data>, var userClick:UserClick) : RecyclerView.Adapter<UserAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemUserBinding) :
        RecyclerView.ViewHolder(binding.root)

    interface UserClick{
        fun onUserClick(position: Int,type:Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var userData = userList[position]

        holder.binding.apply {
            tvName.text = userData.fullName
            if (userData.walletInfo!=null){
                tvWalletBalance.text = "${CommonMethod.formatAmount(userData.walletInfo.amount)} KD"
            }
            tvLocation.text = userData.countryInfo.name
            tvJoinedOn.text = "Joined at ${formatUtcDateToDdMmYyyy(userData.createdAt)}"
            tvEmail.text = userData.email
            tvCall.text = userData.countryCode+"-"+userData.mobile
         /*   tvCompletedOrder.text = userData.fullName
            tvCancelledOrder.text = userData.fullName*/
            Glide.with(ivProfileImage).load(userData.profileImage).placeholder(R.drawable.ic_user).error(R.drawable.ic_user).transform(CircleCrop()).into(ivProfileImage)
            if(userData.status=="1"){
                ivSwitch.setImageResource(R.drawable.switch_on)
            }
            else{
                ivSwitch.setImageResource(R.drawable.switch_off)
            }
            ivSwitch.setOnClickListener {
                userClick.onUserClick(position,2)
            }

            tvViewDetail.setOnClickListener {
                userClick.onUserClick(position,1)
            }
        }
    }

    fun setData(newList: ArrayList<GetUserResponse.Data.Data>) {
        userList = newList
        notifyDataSetChanged()
    }

}
