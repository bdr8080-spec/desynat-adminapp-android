package com.desynatadmin.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.desynatadmin.R
import com.desynatadmin.databinding.ItemProductBinding
import com.desynatadmin.ui.home.orderManagement.response.GetActiveOrderResponse
import com.desynatadmin.utils.CommonMethod

class ProductAdapter(var context:Context, var productList: ArrayList<GetActiveOrderResponse.Data.Data>) : RecyclerView.Adapter<ProductAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemProductBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var productData = productList[position]
        holder.binding.apply {
            tvCount.text = if((position+1)<10) "0${(position+1)}" else (position+1).toString()
            tvProduct.text = productData?.productDetails?.productNameEn
            tvPrice.text = "${CommonMethod.formatPrice(productData?.order?.totalAmount?:0.0)} KD"
            tvFabric.text = productData?.productDetails?.brand?.nameEnglish
            tvSizeValue.text = productData?.variationDetails?.size?.name
            tvColorValue.text = productData?.variationDetails?.color?.name
            tvCategoryValue.text = productData?.productDetails?.category?.name
            tvQuantityValue.text = productData?.quantity?.toString()

            ivProduct.load(productData?.variationDetails?.variantImage?.get(0)) {
                crossfade(true)
                placeholder(R.drawable.ic_placeholder)
                error(R.drawable.ic_placeholder)
            }
        }


    }

}
