package com.desynatadmin.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.desynatadmin.databinding.ApprovedItemBinding
import com.desynatadmin.databinding.ReelItemBinding

class ReelAdapter() :
    RecyclerView.Adapter<ReelAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ReelItemBinding) :
        RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int) = with(binding){
            root.setOnClickListener {
            }
        }
        }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ReelAdapter.ViewHolder {
        val binding = ReelItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return 10
    }

}