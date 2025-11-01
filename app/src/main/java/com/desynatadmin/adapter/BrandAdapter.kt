package com.desynatadmin.adapter

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.desynatadmin.R
import com.desynatadmin.databinding.ItemBrandBinding
import com.desynatadmin.ui.home.brandManagement.response.GetBrandResponse
import com.desynatadmin.ui.home.categorySubCategoryManagement.response.GetSubCategoryResponse

class BrandAdapter(var context:Context, var brandList: ArrayList<GetBrandResponse.Data.Data>, var brandClick:BrandClick) : RecyclerView.Adapter<BrandAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemBrandBinding) :
        RecyclerView.ViewHolder(binding.root)

    interface BrandClick{
        fun onBrandClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemBrandBinding.inflate(
                android.view.LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return brandList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var brandData = brandList[position]

        holder.binding.apply {
            tvBrandName.text = brandData.nameEnglish
            ivBrand.load(brandData.image) {
                crossfade(true)
                placeholder(R.drawable.ic_placeholder)
                error(R.drawable.ic_placeholder)
            }
        }

        holder.itemView.setOnClickListener {
            brandClick.onBrandClick(position)

        }
    }

    fun setData(newList: ArrayList<GetBrandResponse.Data.Data>) {
        brandList = newList
        notifyDataSetChanged()
    }

}
