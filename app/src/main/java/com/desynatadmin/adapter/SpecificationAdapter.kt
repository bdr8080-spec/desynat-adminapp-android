package com.desynatadmin.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.desynatadmin.databinding.ItemSpecificationBinding
import com.desynatadmin.ui.home.productManagement.response.GetProductDetailResponse
import com.desynatadmin.utils.Constants

class SpecificationAdapter(var context:Context, var specificationList: ArrayList<GetProductDetailResponse.Data.ProductSpecification>) : RecyclerView.Adapter<SpecificationAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemSpecificationBinding) :
        RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int) = with(binding){
            tvDescription.text = if (Constants.language=="ar") "${specificationList[position].nameAr} - ${specificationList[position].featureAr}"
            else "${specificationList[position].nameEn} - ${specificationList[position].featureEn}"
        }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemSpecificationBinding.inflate(
                LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return specificationList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

}
