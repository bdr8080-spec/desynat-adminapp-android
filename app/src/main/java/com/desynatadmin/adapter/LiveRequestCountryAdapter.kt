package com.desynatadmin.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.desynatadmin.R
import com.desynatadmin.databinding.CityListItemBinding
import com.desynatadmin.databinding.CountryRecylerBinding
import com.desynatadmin.ui.home.subscriptionPlan.response.GetCountryResponse

class LiveRequestCountryAdapter(var context: Context,var list:ArrayList<GetCountryResponse.Data>,var selectedCountry:GetCountryResponse.Data?, var onClick:OnClick): RecyclerView.Adapter<LiveRequestCountryAdapter.ViewHolder>() {

    interface OnClick{
        fun onClick(position: Int)
    }

    inner class ViewHolder(val binding: CountryRecylerBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LiveRequestCountryAdapter.ViewHolder {
        val binding = CountryRecylerBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LiveRequestCountryAdapter.ViewHolder, position: Int) {
        holder.binding.apply {
            if (selectedCountry?.id==list[position].id){
                name.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0, R.drawable.ic_radio_selected,0)
            }
            else{
                name.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0, R.drawable.ic_radio_unselected,0)

            }
            name.text="${list[position].flag}  ${list[position].name}"
            root.setOnClickListener {
                selectedCountry = list[position]
                notifyDataSetChanged()
                onClick.onClick(position)
            }

        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

}