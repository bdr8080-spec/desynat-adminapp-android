package com.desynatadmin.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.desynatadmin.databinding.ApprovedItemBinding
import com.desynatadmin.databinding.ReelItemBinding


class ReelAdapter() :
    RecyclerView.Adapter<ReelAdapter.ViewHolder>() {
    class ViewHolder(val binding: ReelItemBinding) :
        RecyclerView.ViewHolder(binding.root)

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
        holder.binding.apply {
            root.setOnClickListener {
            }
        }
    }

    override fun getItemCount(): Int {
        return 10
    }

}