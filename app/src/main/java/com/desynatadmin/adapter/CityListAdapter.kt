package com.desynatadmin.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.desynatadmin.R
import com.desynatadmin.databinding.CityListItemBinding
import com.desynatadmin.ui.home.brandManagement.response.ServiceCityListResponse
import com.desynatadmin.utils.SharedPreference

class CityListAdapter(var context: Context,
                      val list: ArrayList<ServiceCityListResponse.Data.Data>,
                      private val onItemClick: OnClick
) : RecyclerView.Adapter<CityListAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: CityListItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = CityListItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.apply {
            tvCountryName.text = list[position].name
            var actionAccess: Boolean=true
            val userInfo = SharedPreference.get(context).getUser()
            if (userInfo?.type=="2"){
                val subAdminInfo = SharedPreference.get(context).getSubAdmin()
                val module = subAdminInfo?.permissionDetails
                    ?.firstOrNull {
                        it.moduleDetails.name.equals("country_management", true)
                    }

                actionAccess = module?.canAction == 1
                imDelete.isVisible = actionAccess
                toggle.isClickable = actionAccess
            }
            tvViewZone.setOnClickListener {
                onItemClick.onViewCLick(position)
            }
            imDelete.setOnClickListener {
                onItemClick.onDeleteClick(position)
            }
            toggle.setOnClickListener {
                if (actionAccess) {
                    onItemClick.onStatusClick(position)
                }
            }

            if(list[position].status == 0){
                toggle.setImageResource(R.drawable.toggle_off)
            }
            else{
                toggle.setImageResource(R.drawable.toggle_ic)
            }

        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    interface OnClick {
        fun onViewCLick(position: Int)
        fun onEditClick(position: Int)
        fun onDeleteClick(position: Int)
        fun onStatusClick(position: Int)
    }
}
