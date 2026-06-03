package com.desynatadmin.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.bumptech.glide.Glide
import com.desynatadmin.databinding.ItemProductBinding
import com.desynatadmin.R
import com.desynatadmin.ui.home.productManagement.response.GetAllProductListResponse
import com.desynatadmin.utils.Constants
import com.desynatadmin.utils.SharedPreference

class ProductAdapter(var context:Context, var productList: ArrayList<GetAllProductListResponse.Data.Data>, var productClick:ProductClick) : RecyclerView.Adapter<ProductAdapter.ViewHolder>() {

    interface ProductClick{
        fun onProductClick(position: Int,type:Int)
    }
    inner class ViewHolder(val binding: ItemProductBinding) :
        RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int) = with(binding){
            var actionAccess: Boolean=true
            val productData = productList[position]
            val userInfo = SharedPreference.get(context).getUser()
            if (userInfo?.type=="2"){
                val subAdminInfo = SharedPreference.get(context).getSubAdmin()
                val module = subAdminInfo?.permissionDetails
                    ?.firstOrNull {
                        it.moduleDetails.name.equals("product_management", true)
                    }

                actionAccess = module?.canAction == 1

                ivSwitch.isClickable = actionAccess
            }
                tvBrand.text = if(productData?.brand?.nameEnglish.toString()=="Other") {
                    if (Constants.language=="ar") productData?.otherAr.toString() else productData?.otherEn.toString()
                } else {
                    if (Constants.language=="ar") productData?.brand?.nameArabic.toString() else productData?.brand?.nameEnglish.toString()
                }
                tvName.text =  if (Constants.language=="ar") productData.productNameAr else productData.productNameEn
                tvCountry.text =  if (Constants.language=="ar") productData.sellerDetails?.countryDetails?.nameAr else productData.sellerDetails?.countryDetails?.name

            Glide.with(context).load(productData?.productVariants?.get(0)?.variantImage?.get(0)).placeholder(R.drawable.ic_placeholder).into(ivProduct)



                if(productData.productActiveStatus==1){
                    ivSwitch.setImageResource(R.drawable.switch_on)
                }
                else{
                    ivSwitch.setImageResource(R.drawable.switch_off)
                }

            ivSwitch.setOnClickListener {
                if (actionAccess) {
                    productClick.onProductClick(position, 2)
                }
                }

                  val rating = productData?.averageRating?.product?.toFloatOrNull() ?: 0f
                  ratingBar.rating = rating
                  tvRating.text = "(${productData?.averageRating?.product})"

                tvPrice.text = "${(productData?.productVariants?.get(0)?.actualPrice?:0)} KD"
                tvDiscountedPrice.text = "${(productData?.productVariants?.get(0)?.finalPrice?:0)} KD"

                val variant = productData?.productVariants?.getOrNull(0)
                val discountValue = variant?.discountValue ?: 0
                val discountType = if (variant?.discountType == "1")"% ${context.getString(R.string.off)}" else " KD ${
                    context.getString(
                        R.string.off
                    )
                }"
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
