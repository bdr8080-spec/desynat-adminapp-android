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
import com.desynatadmin.ui.home.affiliateUserManagement.response.GetNewAffiliateUserResponse
import com.desynatadmin.ui.home.userManagement.response.GetUserResponse
import com.desynatadmin.utils.SharedPreference
import com.desynatadmin.utils.formatUtcDateToDdMmYyyy

class AffiliateUserAdapter(var context:Context, var userList: ArrayList<GetNewAffiliateUserResponse.Data.Data>, var affiliateUserClickk:AffiliateUserClick) : RecyclerView.Adapter<AffiliateUserAdapter.ViewHolder>() {

   var userType = "1"
    inner class ViewHolder(val binding: ItemAffiliateUserBinding) :
        RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int) = with(binding){
            var actionAccess : Boolean = true
            val userData = userList[position]

            val userInfo = SharedPreference.get(context).getUser()
            if (userInfo?.type=="2"){
                val subAdminInfo = SharedPreference.get(context).getSubAdmin()
                val module = subAdminInfo?.permissionDetails
                    ?.firstOrNull {
                        it.moduleDetails.name.equals("affiliate_user_management", true)
                    }

                actionAccess = module?.canAction == 1
                btnAccept.isVisible = actionAccess
                btnReject.isVisible = actionAccess
                ivSwitch.isClickable = actionAccess
            }

                when(userType){
                    "1" ->{
                        btnAccept.isVisible = actionAccess
                        btnReject.isVisible = actionAccess
                        clEarning.isVisible = actionAccess
                        clRejected.isVisible = actionAccess
                    }
                    "2" ->{
                        btnAccept.isVisible = false
                        btnReject.isVisible = false
                        clEarning.isVisible = true
                        clRejected.isVisible = false
                        tvTotalEarningsValue.text = "0 KD"
                        tvTotalOrdersValue.text = "0"
                    }
                    "3" ->{
                        btnAccept.isVisible = false
                        btnReject.isVisible = false
                        clEarning.isVisible = false
                        clRejected.isVisible = true
                        tvRejectionDescription.text = userData?.rejectReason
                    }
                }


                tvName.text = userData.fullName
                tvLocation.text = userData.countryDetails?.name
                tvJoinedOn.text = "${context.getString(R.string.joined_at)} ${formatUtcDateToDdMmYyyy(userData.createdAt)}"
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

            root.setOnClickListener {
                affiliateUserClickk.onAffiliateUserClick(position,3)
            }
            }

        }

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
       holder.bind(position)
    }

    fun setData(newList: ArrayList<GetNewAffiliateUserResponse.Data.Data>) {
        userList = newList
        notifyDataSetChanged()
    }

    fun setType(typeValue:String) {
        userType = typeValue
        notifyDataSetChanged()
    }

}
