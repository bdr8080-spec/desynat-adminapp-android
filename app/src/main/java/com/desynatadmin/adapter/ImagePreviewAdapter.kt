package com.desynatadmin.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.desynatadmin.R
import com.desynatadmin.databinding.ItemPreviewImageBinding

class ImagePreviewAdapter(var context:Context, var productList: ArrayList<String>) : RecyclerView.Adapter<ImagePreviewAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemPreviewImageBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemPreviewImageBinding.inflate(
                LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val productData = productList[position]
        holder.binding.apply {
//            ivProduct.load(productData) {
//                crossfade(true)
//                placeholder(R.drawable.ic_placeholder)
//                error(R.drawable.ic_placeholder)
//            }
            Glide.with(context).load(productData).placeholder(R.drawable.ic_placeholder).into(ivProduct)
        }

    }


}
