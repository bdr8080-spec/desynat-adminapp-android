package com.desynatadmin.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.desynatadmin.databinding.SimpleItemLayoutBinding
import com.desynatadmin.ui.home.brandManagement.response.CityResponse
import com.desynatadmin.ui.home.brandManagement.response.CountryREsponse

class ListAdapter(val list: ArrayList<out Any>, val click: OnCLick) :
    RecyclerView.Adapter<ListAdapter.ListViewHolder>() {
    inner class ListViewHolder(val binding: SimpleItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        return ListViewHolder(
            SimpleItemLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.binding.apply {
            val item = list[position]

            val displayName = when (item) {
                is CountryREsponse.Data -> item.name
                is CityResponse.Data -> item.name
                else -> "Unknown"
            }

            ivName.text = displayName

            root.setOnClickListener {
                click.onClick(
                    position
                )
            }
        }
    }

    interface OnCLick {
        fun onClick(position: Int)
    }
}