package com.desynatadmin.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.desynatadmin.R
import com.desynatadmin.databinding.ItemUserBinding
import com.desynatadmin.ui.home.userManagement.response.GetUserResponse
import com.desynatadmin.utils.CommonMethod
import com.desynatadmin.utils.Constants
import com.desynatadmin.utils.SharedPreference
import com.desynatadmin.utils.formatUtcDateToDdMmYyyy

class UserAdapter(var context:Context, var userList: ArrayList<GetUserResponse.Data.Data>, var userClick:UserClick) : RecyclerView.Adapter<UserAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemUserBinding) :
        RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int) = with(binding){
            var actionAccess = true
            val userData = userList[position]

            val userInfo = SharedPreference.get(context).getUser()
            if (userInfo?.type=="2"){
                val subAdminInfo = SharedPreference.get(context).getSubAdmin()
                val module = subAdminInfo?.permissionDetails
                    ?.firstOrNull {
                        it.moduleDetails.name.equals("user_management", true)
                    }

                actionAccess = module?.canAction == 1
                ivSwitch.isClickable = actionAccess
            }

            tvName.text = userData.fullName
            if (userData.walletInfo!=null){
                tvWalletBalance.text = "${CommonMethod.formatAmount(userData.walletInfo.amount)} KD"
            }
            tvLocation.text = if (Constants.language=="ar") userData.countryInfo.nameAr else userData.countryInfo.name
            tvJoinedOn.text = "${context.getString(R.string.joined_on)} ${formatUtcDateToDdMmYyyy(userData.createdAt)}"
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
                if (actionAccess) {
                    userClick.onUserClick(position, 2)
                }
            }

            tvViewDetail.setOnClickListener {
                userClick.onUserClick(position,1)
            }
        }
        }

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
        holder.bind(position)
    }

    fun setData(newList: ArrayList<GetUserResponse.Data.Data>) {
        userList = newList
        notifyDataSetChanged()
    }

}
