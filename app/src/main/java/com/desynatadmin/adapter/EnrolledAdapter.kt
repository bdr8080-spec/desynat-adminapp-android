package com.desynatadmin.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.desynatadmin.R
import com.desynatadmin.databinding.ItemEnrolledSellerBinding
import com.desynatadmin.ui.home.dealsManagement.response.GetDealDetailResponse

class EnrolledAdapter(var context:Context, var enrolledList: ArrayList<GetDealDetailResponse.Data.SellerDealrecord>, var clickOnItem:ClickOnItem) : RecyclerView.Adapter<EnrolledAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemEnrolledSellerBinding) :
        RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int) = with(binding){
            val enrolledData = enrolledList[position]
            tvSellerName.text = enrolledData?.sellerDetails?.firstName+" "+enrolledData?.sellerDetails?.lastName
            tvCountry.text =  enrolledData?.sellerDetails?.countryDetails?.name
            val count = enrolledData.productCount ?: 0
            tvTotalProductAdded.text = if (count == 1) {
                "$count ${context.getString(R.string.product)}"
            } else {
                "$count ${context.getString(R.string.products)}"
            }
            tvTotalSoldAdded.text = "0 ${context.getString(R.string.products)}"
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
            ItemEnrolledSellerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return enrolledList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

}
