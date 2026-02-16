package com.desynatadmin.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.desynatadmin.databinding.ItemColorBinding
import com.desynatadmin.ui.home.productManagement.response.ProductArrangeResponse

class ProductRectangleColorAdapter(var context:Context, var productColorList: ArrayList<ProductArrangeResponse.Product>, var onClick: OnClick) : RecyclerView.Adapter<ProductRectangleColorAdapter.ViewHolder>() {
    var selectedPos = 0
    interface OnClick{
        fun onClickItem(position: Int)
    }
    inner class ViewHolder(val binding: ItemColorBinding) :
        RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int) = with(binding){
                val productColor = productColorList[position]

                ivColor.isVisible = false
                ivRectangleColor.isVisible = true

                ivRectangleColor.setColorFilter(Color.parseColor(productColor.color.hexCode))

                if (selectedPos==position){
                    ivBorderRectangleColor.isVisible = true
                }
                else{
                    ivBorderRectangleColor.isInvisible = true
                }

           root.setOnClickListener {
                selectedPos = position
                onClick.onClickItem(position)
                notifyDataSetChanged()
            }
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
