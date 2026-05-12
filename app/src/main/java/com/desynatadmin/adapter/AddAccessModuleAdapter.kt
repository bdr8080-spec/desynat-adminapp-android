package com.desynatadmin.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.desynatadmin.R
import com.desynatadmin.databinding.ItemAccessModuleBinding
import com.desynatadmin.ui.home.subAdminManagement.response.AddSubAdminRequest
import com.desynatadmin.ui.home.subAdminManagement.response.GetSubAdminDetailResponse
import com.desynatadmin.utils.Constants

class AddAccessModuleAdapter(var context:Context, var moduleList: ArrayList<AddSubAdminRequest.Module>, var onClick:OnClick) : RecyclerView.Adapter<AddAccessModuleAdapter.ViewHolder>() {

    interface OnClick{
        fun ClickOnItem(position: Int,type: String)
    }

    inner class ViewHolder(val binding: ItemAccessModuleBinding) :
        RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int) = with(binding) {
            val moduleData = moduleList[position]

            tvModule.text = if (Constants.language=="ar") moduleData?.nameAr?:moduleData?.name else moduleData?.name


            tvModule.setCompoundDrawablesWithIntrinsicBounds(
                if (moduleData.isSelected) R.drawable.ic_radio_selected
                else R.drawable.ic_radio_unselected,
                0, 0, 0
            )

            rbView.setCompoundDrawablesWithIntrinsicBounds(
                if (moduleData.canView==1) R.drawable.ic_radio_selected
                else R.drawable.ic_radio_unselected,
                0, 0, 0
            )

            rbAction.setCompoundDrawablesWithIntrinsicBounds(
                if (moduleData.canAction==1) R.drawable.ic_radio_selected
                else R.drawable.ic_radio_unselected,
                0, 0, 0
            )

            tvModule.setOnClickListener {
                onClick.ClickOnItem(position, "module")
            }

            rbView.setOnClickListener {
                onClick.ClickOnItem(position, "view")
            }

            rbAction.setOnClickListener {
                onClick.ClickOnItem(position, "action")
            }
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
