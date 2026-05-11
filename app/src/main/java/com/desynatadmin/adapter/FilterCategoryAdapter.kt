package com.desynatadmin.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.desynatadmin.databinding.ItemCheckboxBrandBinding
import com.desynatadmin.ui.home.categorySubCategoryManagement.response.GetCategoryResponse
import com.desynatadmin.R
import com.desynatadmin.utils.Constants

class FilterCategoryAdapter(var context:Context, var categoryList: ArrayList<GetCategoryResponse.Data>, var onClick: OnClick) : RecyclerView.Adapter<FilterCategoryAdapter.ViewHolder>() {

    var selectedPos = -1
    interface OnClick{
        fun onClickItem(position: Int)
    }
    inner class ViewHolder(val binding: ItemCheckboxBrandBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemCheckboxBrandBinding.inflate(
                LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.apply {

            tvBrand.text = if (Constants.language=="ar") categoryList[position].nameAr else categoryList[position].name

            if (selectedPos==position){
                ivImage.setImageResource(R.drawable.ic_radio_selected)
            }
            else{
                ivImage.setImageResource(R.drawable.ic_radio_unselected)
            }
        }

        holder.itemView.setOnClickListener {
            if (selectedPos==position){
                selectedPos = -1
            }
            else{
                selectedPos = position
            }
            onClick.onClickItem(selectedPos)
            notifyDataSetChanged()
        }

    }

    fun setSelectedCategoryId(id: Int?) {
        selectedPos = categoryList.indexOfFirst { it.id == id }
        notifyDataSetChanged()
    }

}
