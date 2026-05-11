package com.desynatadmin.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.desynatadmin.R
import com.desynatadmin.databinding.ItemOrderProductBinding
import com.desynatadmin.ui.home.orderManagement.response.GetActiveOrderResponse
import com.desynatadmin.utils.CommonMethod
import com.desynatadmin.utils.Constants

class OrderProductAdapter(var context:Context, var productList: ArrayList<GetActiveOrderResponse.Data.Data>) : RecyclerView.Adapter<OrderProductAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemOrderProductBinding) :
        RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int) = with(binding){
            val productData = productList[position]
                tvCount.text = if((position+1)<10) "0${(position+1)}" else (position+1).toString()
                tvProduct.text = if (Constants.language=="ar") productData?.productDetails?.productNameAr else productData?.productDetails?.productNameEn
                tvPrice.text = "${CommonMethod.formatPrice(productData?.amount?:0.0)} KD"
                tvFabric.text = if (Constants.language=="ar") productData?.productDetails?.brand?.nameArabic else productData?.productDetails?.brand?.nameEnglish
                tvSizeValue.text = if (Constants.language=="ar") productData?.variationDetails?.size?.nameAr else productData?.variationDetails?.size?.name
                tvColorValue.text = if (Constants.language=="ar") productData?.variationDetails?.color?.nameAr else productData?.variationDetails?.color?.name
                tvCategoryValue.text = if (Constants.language=="ar") productData?.productDetails?.category?.nameAr else productData?.productDetails?.category?.name
                tvQuantityValue.text = productData?.quantity?.toString()

                ivProduct.load(productData?.variationDetails?.variantImage?.get(0)) {
                    crossfade(true)
                    placeholder(R.drawable.ic_placeholder)
                    error(R.drawable.ic_placeholder)
                }
            }

        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemOrderProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
       holder.bind(position)
    }

}
