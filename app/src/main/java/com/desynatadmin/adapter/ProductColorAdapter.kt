package com.desynatadmin.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.desynatadmin.databinding.ItemColorBinding
import com.desynatadmin.ui.home.productManagement.response.GetAllProductListResponse

class ProductColorAdapter(var context:Context, var productColorList: List<GetAllProductListResponse.Data.Data.ProductVariant>) : RecyclerView.Adapter<ProductColorAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemColorBinding) :
        RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int) = with(binding){
            val productColor = productColorList[position]
                ivColor.isVisible = true
                ivRectangleColor.isVisible = false
            ivColor.setColorFilter(Color.parseColor(productColor?.color?.hexCode))
        }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemColorBinding.inflate(
                LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return productColorList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

}
