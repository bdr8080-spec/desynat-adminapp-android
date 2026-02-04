package com.desynatadmin.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.desynatadmin.R
import com.desynatadmin.databinding.ItemDealProductBinding
import com.desynatadmin.ui.home.categorySubCategoryManagement.response.GetSubCategoryResponse
import com.desynatadmin.ui.home.dealsManagement.response.GetDealProductResponse
import com.desynatadmin.utils.CommonMethod

class EnrolledSellerProductAdapter(var context:Context, var dealType:Int,var enrolledProductList: ArrayList<GetDealProductResponse.Data.SellerDealrecord.Product>, var clickOnItem:ClickOnItem) : RecyclerView.Adapter<EnrolledSellerProductAdapter.ViewHolder>() {

    var category : GetDealProductResponse.Data.CategoryDetails?=null
    private val subCategoryList = mutableListOf<GetSubCategoryResponse.Data.Data>()

    inner class ViewHolder(val binding: ItemDealProductBinding) :
        RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int) = with(binding){
            val productData = enrolledProductList[position]

            if (dealType==2){
                ivSwitch.isVisible = false
            }
            else{
                ivSwitch.isVisible = false
            }

            if (productData?.productDetails?.productActiveStatus=="1"){
                ivSwitch.setImageResource(R.drawable.switch_on)
            }
            else{
                ivSwitch.setImageResource(R.drawable.switch_off)
            }

            tvCategory.text = "${category?.name} (${subCategoryList.firstOrNull { it.id == productData?.productDetails?.subcategoryId }})"
            tvProductName.text =  productData?.productDetails?.productNameEn
            tvBrand.text =  productData?.productDetails?.brand?.nameEnglish
            Glide.with(context).load(productData?.productDetails?.productVariants?.get(0)?.variantImage?.get(0)).placeholder(
                R.drawable.ic_placeholder).into(ivStore)
            tvPrice.text = "${CommonMethod.formatPrice(productData?.productDetails?.productVariants?.get(0)?.actualPrice?:0.0)} KD"
            tvDiscountPrice.text = "${CommonMethod.formatPrice(productData?.productDetails?.productVariants?.get(0)?.finalPrice?:0.0)} KD"

            val variant = productData?.productDetails?.productVariants?.getOrNull(0)
            val discountValue = CommonMethod.formatPrice(variant?.discountValue?:0.0)
            val discountType = if (variant?.discountType == "1")"% off" else " KD off"
            tvDiscount.text = "$discountValue$discountType"
            root.setOnClickListener {
                clickOnItem.onClickItem(position)
            }
        }
        }

    interface ClickOnItem{
        fun onClickItem(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemDealProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return enrolledProductList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    fun updateCategorySubCategory(
        categoryData: GetDealProductResponse.Data.CategoryDetails?,
        subCategoryData: List<GetSubCategoryResponse.Data.Data>?
    ) {
        category = categoryData
        subCategoryList.clear()
        subCategoryData?.let { subCategoryList.addAll(it) }
        notifyDataSetChanged()
    }

}
