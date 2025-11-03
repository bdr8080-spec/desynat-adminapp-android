package com.desynatadmin.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.desynatadmin.R
import com.desynatadmin.databinding.ItemCategoryBinding
import com.desynatadmin.ui.home.categorySubCategoryManagement.response.GetCategoryResponse

class CategoryAdapter(var context:Context, private var categoryList: ArrayList<GetCategoryResponse.Data>, var categoryClick:CategoryClick) : RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemCategoryBinding) :
        RecyclerView.ViewHolder(binding.root)

    interface CategoryClick{
        fun onCategoryClick(position: Int,type:Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemCategoryBinding.inflate(
                android.view.LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var categoryData = categoryList[position]

        holder.binding.apply {
            tvCategoryName.text = categoryData.name
            tvDescription.text = categoryData.description
            ivCategory.load(categoryData.image) {
                crossfade(true)
                placeholder(R.drawable.ic_placeholder)
                error(R.drawable.ic_placeholder)
            }

            relCategory.backgroundTintList = ColorStateList.valueOf(Color.parseColor(categoryData.colorCode))

            if(categoryData.status==1){
                tvSwitch.text = "Active"
                tvSwitch.setTextColor(Color.parseColor("#1A900D"))
                tvSwitch.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.switch_on, 0)
            }
            else{
                tvSwitch.text = "Inactive"
                tvSwitch.setTextColor(Color.parseColor("#666666"))
                tvSwitch.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.switch_off, 0)

            }

            tvSwitch.setOnClickListener {
                categoryClick.onCategoryClick(position,1)

            }

        }

        holder.itemView.setOnClickListener {
            categoryClick.onCategoryClick(position,0)

        }
    }

}
