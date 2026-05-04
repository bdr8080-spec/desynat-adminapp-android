package com.desynatadmin.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.desynatadmin.R
import com.desynatadmin.databinding.ItemReasonBinding
import com.desynatadmin.databinding.ItemUserBinding
import com.desynatadmin.ui.home.reasonManagement.response.GetReasonResponse
import com.desynatadmin.ui.home.userManagement.response.GetUserResponse
import com.desynatadmin.utils.Constants
import com.desynatadmin.utils.SharedPreference
import com.desynatadmin.utils.formatUtcDateToDdMmYyyy

class ReasonAdapter(var context:Context, var reasonList: ArrayList<GetReasonResponse.Data.Data>, var reasonClick:ReasonClick) : RecyclerView.Adapter<ReasonAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemReasonBinding) :
        RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int) = with(binding){
            val reasonData = reasonList[position]
            var actionAccess : Boolean = true
            val userInfo = SharedPreference.get(context).getUser()
            if (userInfo?.type=="2"){
                val subAdminInfo = SharedPreference.get(context).getSubAdmin()
                val module = subAdminInfo?.permissionDetails
                    ?.firstOrNull {
                        it.moduleDetails.name.equals("reason_management", true)
                    }

                actionAccess = module?.canAction == 1

                ivEdit.isVisible = actionAccess
                ivDelete.isVisible = actionAccess
                ivSwitch.isClickable = actionAccess
            }
                tvReason.text = if (Constants.language=="ar") reasonData.reasonArabic else reasonData.reasonEnglish
                if(reasonData.status==1){
                    ivSwitch.setImageResource(R.drawable.switch_on)
                }
                else{
                    ivSwitch.setImageResource(R.drawable.switch_off)
                }

                ivEdit.setOnClickListener {
                    reasonClick.onReasonClick(position,1)
                }

                ivSwitch.setOnClickListener {
                    if (actionAccess) {
                        reasonClick.onReasonClick(position, 2)
                    }
                }

                ivDelete.setOnClickListener {
                    reasonClick.onReasonClick(position,3)
                }
            }

        }

    interface ReasonClick{
        fun onReasonClick(position: Int,type:Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemReasonBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return reasonList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
       holder.bind(position)
    }

    fun setData(newList: ArrayList<GetReasonResponse.Data.Data>) {
        reasonList = newList
        notifyDataSetChanged()
    }

}
