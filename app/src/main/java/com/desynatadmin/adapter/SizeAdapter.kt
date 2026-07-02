package com.desynatadmin.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.desynatadmin.databinding.ItemSizeBinding
import com.desynatadmin.ui.home.productManagement.response.ProductArrangeResponse
import com.desynatadmin.utils.Constants

class SizeAdapter(var context:Context, var productSizeList: ArrayList<ProductArrangeResponse.Product.ProductVariation>, var onClick:OnClick) : RecyclerView.Adapter<SizeAdapter.ViewHolder>() {
    var selectedPos = 0
    interface OnClick{
        fun onClickItem(position: Int)
    }
    inner class ViewHolder(val binding: ItemSizeBinding) :
        RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int) = with(binding){
                val productSize = productSizeList[position]

                tvSize.text = if (Constants.language=="ar") productSize?.size?.name else productSize?.size?.name

                if (selectedPos==position){
                    tvSize.setTextColor(Color.parseColor("#000000"))
                }
                else{
                    tvSize.setTextColor(Color.parseColor("#E6E6E6"))
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
            ItemSizeBinding.inflate(
                LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return productSizeList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    fun updateData(pos:Int){
        selectedPos = pos
        notifyDataSetChanged()
    }


}
