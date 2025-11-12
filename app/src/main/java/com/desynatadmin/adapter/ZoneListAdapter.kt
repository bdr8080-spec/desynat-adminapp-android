package com.desynatadmin.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.desynatadmin.R
import com.desynatadmin.databinding.ZoneListItemBinding
import com.desynatadmin.ui.home.brandManagement.response.ZoneListResponse

class ZoneListAdapter(val list : ArrayList<ZoneListResponse.Data.Data>, val click : OnClick): RecyclerView.Adapter<ZoneListAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ZoneListItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ZoneListAdapter.ViewHolder {
        val binding = ZoneListItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ZoneListAdapter.ViewHolder, position: Int) {

        holder.binding.apply {
            tvZoneName.text = list[position].name_english
            tvViewZone.setOnClickListener {
                click.onClick(position)
            }
            if(list[position].status == 0){
                toggle.setImageResource(R.drawable.toggle_off)
            }
            else{
                toggle.setImageResource(R.drawable.toggle_ic)
            }

            imDelete.setOnClickListener {
                click.deleteClick(position)
            }
            toggle.setOnClickListener {
                click.updateStatus(position)
            }
            imEdit.setOnClickListener {
                click.editClick(position)
            }

        }

    }

    override fun getItemCount(): Int {
        return list.size
    }

    interface  OnClick{
        fun onClick(position: Int)
        fun editClick(position: Int)
        fun deleteClick(position: Int)
        fun updateStatus(position: Int)
    }

}
