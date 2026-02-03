package com.desynatadmin.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.desynatadmin.databinding.ItemProductBinding
import com.desynatadmin.R
import com.desynatadmin.utils.CommonMethod

class ProductAdapter(var context:Context, var productList: ArrayList<String>, var productClick:ProductClick) : RecyclerView.Adapter<ProductAdapter.ViewHolder>() {

    interface ProductClick{
        fun onProductClick(position: Int,type:Int)
    }
    inner class ViewHolder(val binding: ItemProductBinding) :
        RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int) = with(binding){
            val productData = productList[position]

//                tvBrand.text = if(productData?.brand?.nameEnglish=="Other") productData?.otherEn else productData?.brand?.nameEnglish
//                tvName.text = productData.productNameEn
//
//                ivProduct.load(productData?.productVariants?.get(0)?.variantImage?.get(0)) {
//                    crossfade(true)
//                    placeholder(R.drawable.ic_placeholder)
//                    error(R.drawable.ic_placeholder)
//                }
//
//                if(productData.productActiveStatus==1){
//                    tvSwitch.text = "Active"
//                    tvSwitch.setTextColor(Color.parseColor("#1A900D"))
//                    tvSwitch.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.switch_on,0, 0 )
//                }
//                else{
//                    tvSwitch.text = "Inactive"
//                    tvSwitch.setTextColor(Color.parseColor("#875C3A"))
//                    tvSwitch.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.switch_off,0,0 )
//
//                }
//
//                tvSwitch.setOnClickListener {
//                    productClick.onProductClick(position,2)
//                }
//
//                /*  val rating = productData?.reviewRating?.toFloatOrNull() ?: 0f
//                  ratingBar.rating = rating
//                  tvRating.text = "(${productData?.reviewRating})"*/
//
//                tvPrice.text = "${(productData?.productVariants?.get(0)?.actualPrice?:0)} KD"
//                tvDiscountedPrice.text = "${(productData?.productVariants?.get(0)?.finalPrice?:0)} KD"
//
//                val variant = productData?.productVariants?.getOrNull(0)
//                val discountValue = variant?.discountValue ?: 0
//                val discountType = if (variant?.discountType == "1")"% off" else " KD off"
//                tvOffer.text = "$discountValue$discountType"
//
//
//                rvColor.apply {
//                    layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
//
//                    val uniqueColors = productList[position].productVariants
//                        ?.distinctBy { it.colorId }
//
//                    adapter = uniqueColors?.let {
//                        ProductColorAdapter(
//                            context = context,
//                            productColorList = it
//                        )
//                    }
//
//                    setHasFixedSize(true)
//                    isNestedScrollingEnabled = false
//                }

            root.setOnClickListener {
                productClick.onProductClick(position,1)
            }
        }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemProductBinding.inflate(
                LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }


}
