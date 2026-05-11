package com.desynatadmin.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.desynatadmin.databinding.ItemCountryBinding
import com.desynatadmin.ui.home.subscriptionPlan.response.GetCountryResponse
import com.desynatadmin.utils.Constants

class CountryPickerAdapter(var context:Context, var countryList: ArrayList<GetCountryResponse.Data>,var isDialCode:Boolean=false,var onClick:ClickOnItem) : RecyclerView.Adapter<CountryPickerAdapter.ViewHolder>() {

    interface ClickOnItem{
        fun onClickItem(position: Int)
    }
    inner class ViewHolder(val binding: ItemCountryBinding) :
        RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int) = with(binding){
                if (isDialCode){
                    tvCountry.text = "${countryList[position].flag}  ${if (Constants.language=="ar") countryList[position].nameAr else countryList[position].name} (${countryList[position].dialCode})"
                }
                else{
                    tvCountry.text = "${countryList[position].flag}  ${if (Constants.language=="ar") countryList[position].nameAr else countryList[position].name}"
                }
                ivFlag.isVisible = false
                // ivFlag.load(countryList[position].flag1)

            root.setOnClickListener {
                onClick.onClickItem(position)
            }
        }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemCountryBinding.inflate(
                LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return countryList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }
}
