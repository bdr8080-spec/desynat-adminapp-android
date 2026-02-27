package com.desynatadmin.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.desynatadmin.R
import com.desynatadmin.databinding.ItemCheckboxBrandBinding
import com.desynatadmin.ui.home.brandManagement.response.GetBrandResponse
import com.desynatadmin.ui.home.subscriptionPlan.response.GetCountryResponse

class FilterCountryAdapter(var context:Context, var countryList: ArrayList<GetCountryResponse.Data>, var onClick: OnClick) : RecyclerView.Adapter<FilterCountryAdapter.ViewHolder>() {
    var selectedPos = -1
    interface OnClick{
        fun onClickItem(position: Int)
    }
    inner class ViewHolder(val binding: ItemCheckboxBrandBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemCheckboxBrandBinding.inflate(
                LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return countryList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.apply {

            tvBrand.text = countryList[position].name

            if (countryList[position].isSelected){
                selectedPos = position
                ivImage.setImageResource(R.drawable.ic_checkbox)
            }
            else{
                ivImage.setImageResource(R.drawable.ic_checkbox_unselected)
            }

        }


        holder.itemView.setOnClickListener {
            if (selectedPos==position){
                selectedPos = -1
                countryList[position].isSelected = !countryList[position].isSelected
            }
            else{
                if (selectedPos!=-1){
                    countryList[selectedPos].isSelected = false
                }
                selectedPos = position
                countryList[position].isSelected = !countryList[position].isSelected
            }

                        notifyDataSetChanged()
        }

    }


}
