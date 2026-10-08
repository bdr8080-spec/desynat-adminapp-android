package com.desynatadmin.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.desynatadmin.databinding.ItemCountryBinding

class StatusPickerAdapter(
    var context: Context,
    var statusList: List<String>,
    var onClick: ClickOnItem
) : RecyclerView.Adapter<StatusPickerAdapter.ViewHolder>() {

    interface ClickOnItem {
        fun onClickItem(position: Int)
    }

    inner class ViewHolder(val binding: ItemCountryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) = with(binding) {
            tvCountry.text = statusList[position]
            ivFlag.isVisible = false

            root.setOnClickListener {
                onClick.onClickItem(position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemCountryBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return statusList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }
}
