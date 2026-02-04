package com.desynatadmin.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.desynatadmin.R
import com.desynatadmin.databinding.ItemSubCategorySelectionBinding
import com.desynatadmin.ui.home.categorySubCategoryManagement.response.GetSubCategoryResponse

class MultipleSubCategorySelectionAdapter(
    private val list: MutableList<GetSubCategoryResponse.Data.Data>,
    private val onSelectionChanged: () -> Unit
) : RecyclerView.Adapter<MultipleSubCategorySelectionAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemSubCategorySelectionBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemSubCategorySelectionBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]

        holder.binding.apply {
            tvName.text = item.nameEnglish
            ivImage.setImageResource(
                if (item.isSelected)
                    R.drawable.ic_checkbox
                else
                    R.drawable.ic_checkbox_unselected
            )
        }

        holder.itemView.setOnClickListener {
            item.isSelected = !item.isSelected
            notifyItemChanged(position)
            onSelectionChanged()
        }
    }
}
