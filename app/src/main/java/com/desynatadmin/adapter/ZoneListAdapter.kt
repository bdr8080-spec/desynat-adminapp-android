package com.desynatadmin.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.desynatadmin.databinding.ZoneListItemBinding

class ZoneListAdapter(): RecyclerView.Adapter<ZoneListAdapter.ViewHolder>() {
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
            tvViewZone.setOnClickListener {
            }
        }

    }

    override fun getItemCount(): Int {
        return 5
    }

}
