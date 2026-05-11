package com.desynatadmin.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.desynatadmin.databinding.ItemBrandFilterBinding
import com.desynatadmin.ui.home.brandManagement.response.GetBrandResponse
import com.desynatadmin.utils.Constants

class DealBrandFilterAdapter(var context: Context, var list:ArrayList<GetBrandResponse.Data.Data>, var onClick:OnClick): RecyclerView.Adapter<DealBrandFilterAdapter.ViewHolder>() {

    interface OnClick{
        fun onClick(position: Int)
    }

    inner class ViewHolder(val binding: ItemBrandFilterBinding) :
        RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int) = with(binding){
                tvCountry.text = if (Constants.language=="ar") list[position].nameArabic else list[position].nameEnglish
                root.setOnClickListener {
                    onClick.onClick(position)
                }
        }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DealBrandFilterAdapter.ViewHolder {
        val binding = ItemBrandFilterBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DealBrandFilterAdapter.ViewHolder, position: Int) {
       holder.bind(position)
    }

    override fun getItemCount(): Int {
        return list.size
    }

}