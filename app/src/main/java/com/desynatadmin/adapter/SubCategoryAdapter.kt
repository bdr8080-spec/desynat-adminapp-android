package com.desynatadmin.adapter

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.desynatadmin.R
import com.desynatadmin.databinding.ItemSubCategoryBinding
import com.desynatadmin.ui.home.categorySubCategoryManagement.response.GetSubCategoryResponse
import com.desynatadmin.utils.Constants

class SubCategoryAdapter(var context:Context, private var subCategoryList: ArrayList<GetSubCategoryResponse.Data.Data>, var subCategoryClick:SubCategoryClick) : RecyclerView.Adapter<SubCategoryAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemSubCategoryBinding) :
        RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int) = with(binding){
            val subCategoryData = subCategoryList[position]

                tvBrandName.text = if (Constants.language=="ar") subCategoryData.nameArabic else subCategoryData.nameEnglish
                ivSubCategory.load(subCategoryData.image) {
                    crossfade(true)
                    placeholder(R.drawable.ic_placeholder)
                    error(R.drawable.ic_placeholder)
                }

            root.setOnClickListener {
                subCategoryClick.onSubCategoryClick(position)

            }
        }
        }

    interface SubCategoryClick{
        fun onSubCategoryClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemSubCategoryBinding.inflate(
                android.view.LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return subCategoryList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
      holder.bind(position)
    }

    fun setData(newList: ArrayList<GetSubCategoryResponse.Data.Data>) {
        subCategoryList = newList
        notifyDataSetChanged()
    }

}
