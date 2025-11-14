package com.desynatadmin.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.desynatadmin.databinding.ItemCountryBinding
import com.desynatadmin.ui.home.brandManagement.response.CountryREsponse
import com.desynatadmin.ui.home.subscriptionPlan.response.GetCountryResponse

class CountryPickerAdapter(var context:Context, var countryList: ArrayList<GetCountryResponse.Data>,var onClick:ClickOnItem) : RecyclerView.Adapter<CountryPickerAdapter.ViewHolder>() {

    interface ClickOnItem{
        fun onClickItem(position: Int)
    }
    inner class ViewHolder(val binding: ItemCountryBinding) :
        RecyclerView.ViewHolder(binding.root)

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
        holder.binding.apply {
            tvCountry.text = "${countryList[position].name} (${countryList[position].dialCode})"
            ivFlag.load(countryList[position].flag1)
        }

        holder.itemView.setOnClickListener {
            onClick.onClickItem(position)
        }

    }


}
