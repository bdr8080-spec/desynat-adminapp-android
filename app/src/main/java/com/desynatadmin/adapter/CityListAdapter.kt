package com.desynatadmin.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.desynatadmin.databinding.CityListItemBinding

class CityListAdapter(private val onItemClick: () -> Unit) : RecyclerView.Adapter<CityListAdapter.ViewHolder>() {

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
            tvViewZone.setOnClickListener {
                onItemClick()
            }
        }
    }

    override fun getItemCount(): Int = 5
}
