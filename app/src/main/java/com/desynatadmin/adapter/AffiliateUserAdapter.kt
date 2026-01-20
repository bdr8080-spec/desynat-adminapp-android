package com.desynatadmin.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.desynatadmin.R
import com.desynatadmin.databinding.ItemAffiliateUserBinding
import com.desynatadmin.ui.home.userManagement.response.GetUserResponse
import com.desynatadmin.utils.formatUtcDateToDdMmYyyy

class AffiliateUserAdapter(var context:Context, var userList: ArrayList<GetUserResponse.Data.Data>, var affiliateUserClickk:AffiliateUserClick) : RecyclerView.Adapter<AffiliateUserAdapter.ViewHolder>() {

   var userType = "1"
    inner class ViewHolder(val binding: ItemAffiliateUserBinding) :
        RecyclerView.ViewHolder(binding.root)

    interface AffiliateUserClick{
        fun onAffiliateUserClick(position: Int,type:Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemAffiliateUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var userData = userList[position]

        holder.binding.apply {
            when(userType){
                "1" ->{
                    btnAccept.isVisible = true
                    btnReject.isVisible = true
                    clEarning.isVisible = false
                    clRejected.isVisible = false
                }
                "2" ->{
                    btnAccept.isVisible = false
                    btnReject.isVisible = false
                    clEarning.isVisible = true
                    clRejected.isVisible = false
                }
                "3" ->{
                    btnAccept.isVisible = false
                    btnReject.isVisible = false
                    clEarning.isVisible = false
                    clRejected.isVisible = true
                }
            }


            tvName.text = userData.fullName
            tvLocation.text = userData.countryInfo.name
            tvJoinedOn.text = "Joined at ${formatUtcDateToDdMmYyyy(userData.createdAt)}"
            tvEmail.text = userData.email
            tvCall.text = userData.countryCode+"-"+userData.mobile
            Glide.with(ivProfileImage).load(userData.profileImage).placeholder(R.drawable.ic_user).error(R.drawable.ic_user).transform(CircleCrop()).into(ivProfileImage)
            if(userData.status=="1"){
                ivSwitch.setImageResource(R.drawable.switch_on)
            }
            else{
                ivSwitch.setImageResource(R.drawable.switch_off)
            }
            btnAccept.setOnClickListener {
                affiliateUserClickk.onAffiliateUserClick(position,1)
            }
            btnReject.setOnClickListener {
                affiliateUserClickk.onAffiliateUserClick(position,2)
            }
        }
    }

    fun setData(newList: ArrayList<GetUserResponse.Data.Data>) {
        userList = newList
        notifyDataSetChanged()
    }

    fun setType(typeValue:String) {
        userType = typeValue
        notifyDataSetChanged()
    }

}
