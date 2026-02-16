package com.desynatadmin.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.desynatadmin.databinding.ItemProductBinding
import com.desynatadmin.R
import com.desynatadmin.ui.home.productManagement.response.GetAllProductListResponse

class ProductAdapter(var context:Context, var productList: ArrayList<GetAllProductListResponse.Data.Data>, var productClick:ProductClick) : RecyclerView.Adapter<ProductAdapter.ViewHolder>() {

    interface ProductClick{
        fun onProductClick(position: Int,type:Int)
    }
    inner class ViewHolder(val binding: ItemProductBinding) :
        RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int) = with(binding){
            val productData = productList[position]

                tvBrand.text = if(productData?.brand?.nameEnglish.toString()=="Other") productData?.otherEn.toString() else productData?.brand?.nameEnglish.toString()
                tvName.text = productData.productNameEn

                ivProduct.load(productData?.productVariants?.get(0)?.variantImage?.get(0)) {
                    crossfade(true)
                    placeholder(R.drawable.ic_placeholder)
                    error(R.drawable.ic_placeholder)
                }

                if(productData.productActiveStatus==1){
                    ivSwitch.setImageResource(R.drawable.switch_on)
                }
                else{
                    ivSwitch.setImageResource(R.drawable.switch_off)
                }

            ivSwitch.setOnClickListener {
                    productClick.onProductClick(position,2)
                }

                  val rating = productData?.averageRating?.product?.toFloatOrNull() ?: 0f
                  ratingBar.rating = rating
                  tvRating.text = "(${productData?.averageRating?.product})"

                tvPrice.text = "${(productData?.productVariants?.get(0)?.actualPrice?:0)} KD"
                tvDiscountedPrice.text = "${(productData?.productVariants?.get(0)?.finalPrice?:0)} KD"

                val variant = productData?.productVariants?.getOrNull(0)
                val discountValue = variant?.discountValue ?: 0
                val discountType = if (variant?.discountType == "1")"% off" else " KD off"
                tvOffer.text = "$discountValue$discountType"

                rvColor.apply {
                    layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)

                    val uniqueColors = productList[position].productVariants
                        ?.distinctBy { it.colorId }

                    adapter = uniqueColors?.let {
                        ProductColorAdapter(
                            context = context,
                            productColorList = it
                        )
                    }

                    setHasFixedSize(true)
                    isNestedScrollingEnabled = false
                }

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

    fun setData(newList: ArrayList<GetAllProductListResponse.Data.Data>) {
        productList = newList
        notifyDataSetChanged()
    }

}
