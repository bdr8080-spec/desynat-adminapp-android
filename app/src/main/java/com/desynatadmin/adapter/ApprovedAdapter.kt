package com.desynatadmin.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.desynatadmin.databinding.ApprovedItemBinding

class ApprovedAdapter(val onClick: () -> Unit) :
    RecyclerView.Adapter<ApprovedAdapter.ViewHolder>() {
    class ViewHolder(val binding: ApprovedItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ApprovedAdapter.ViewHolder {
        val binding = ApprovedItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.apply {
            root.setOnClickListener {
                onClick()
            }
        }
    }

    override fun getItemCount(): Int {
        return 5
    }

}


