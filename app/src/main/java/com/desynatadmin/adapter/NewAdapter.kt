package com.desynatadmin.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.desynatadmin.databinding.NewItemBinding

class NewAdapter(val onClick: () -> Unit) : RecyclerView.Adapter<NewAdapter.ViewHolder>() {
     class ViewHolder(val binding: NewItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NewAdapter.ViewHolder {
        val binding = NewItemBinding.inflate(
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

