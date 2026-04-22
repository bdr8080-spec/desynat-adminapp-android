package com.desynatadmin.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.desynatadmin.R
import com.desynatadmin.databinding.ItemAccessModuleBinding
import com.desynatadmin.ui.home.subAdminManagement.response.GetSubAdminDetailResponse

class AccessModuleAdapter(var context:Context, var moduleList: ArrayList<GetSubAdminDetailResponse.Data.PermissionDetail>, var onClick:OnClick) : RecyclerView.Adapter<AccessModuleAdapter.ViewHolder>() {

    interface OnClick{
        fun ClickOnItem(position: Int,type: String)
    }

    inner class ViewHolder(val binding: ItemAccessModuleBinding) :
        RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int) = with(binding){
            var moduleData = moduleList[position]
            tvModule.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_radio_selected,0,0,0)
            tvModule.text = moduleData?.moduleDetails?.name
            rbView.isChecked = moduleData?.canView==1
            rbAction.isChecked = moduleData?.canAction==1
        }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemAccessModuleBinding.inflate(
                LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return moduleList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

}
