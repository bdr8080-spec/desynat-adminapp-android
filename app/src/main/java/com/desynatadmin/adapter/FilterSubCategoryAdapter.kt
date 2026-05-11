package com.desynatadmin.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.desynatadmin.databinding.ItemCheckboxBrandBinding
import com.desynatadmin.R
import com.desynatadmin.ui.home.categorySubCategoryManagement.response.GetSubCategoryResponse
import com.desynatadmin.utils.Constants

class FilterSubCategoryAdapter(var context:Context, var subCategoryList: ArrayList<GetSubCategoryResponse.Data.Data>, var onClick: OnClick) : RecyclerView.Adapter<FilterSubCategoryAdapter.ViewHolder>() {
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
        return subCategoryList.size
    }

    override fun onBindViewHolder(holder: FilterSubCategoryAdapter.ViewHolder, position: Int) {
        holder.binding.apply {

            tvBrand.text =  if (Constants.language=="ar") subCategoryList[position].nameArabic else subCategoryList[position].nameEnglish

            if (subCategoryList[position].isSelected){
                selectedPos = position
                ivImage.setImageResource(R.drawable.ic_checkbox)
            }
            else{
                ivImage.setImageResource(R.drawable.ic_checkbox_unselected)
            }

        }


        holder.itemView.setOnClickListener {
            if (selectedPos==position){
                selectedPos = -1
                subCategoryList[position].isSelected = !subCategoryList[position].isSelected
            }
            else{
                if (selectedPos!=-1){
                    subCategoryList[selectedPos].isSelected = false
                }
                selectedPos = position
                subCategoryList[position].isSelected = !subCategoryList[position].isSelected
            }

            notifyDataSetChanged()
        }

    }
}
