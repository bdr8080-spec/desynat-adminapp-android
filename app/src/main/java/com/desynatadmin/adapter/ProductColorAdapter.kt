package com.desynatadmin.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.desynatadmin.databinding.ItemColorBinding

class ProductColorAdapter(var context:Context, var productColorList: List<String>) : RecyclerView.Adapter<ProductColorAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemColorBinding) :
        RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int) = with(binding){
            val productColor = productColorList[position]
                ivColor.isVisible = true
                ivRectangleColor.isVisible = false
          //  ivColor.setColorFilter(Color.parseColor(colorCode))
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
