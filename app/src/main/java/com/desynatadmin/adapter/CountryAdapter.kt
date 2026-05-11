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
import com.desynatadmin.utils.Constants

class CountryAdapter(var list:ArrayList<GetCountryResponse.Data>,var context: Context,var onClick:(String,String)->Unit): RecyclerView.Adapter<CountryAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: CountryRecylerBinding) :
        RecyclerView.ViewHolder(binding.root)
    var selectedIndex=-1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CountryAdapter.ViewHolder {
        val binding = CountryRecylerBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CountryAdapter.ViewHolder, position: Int) {
        holder.binding.apply {
            if (selectedIndex==position){
                name.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0, R.drawable.ic_radio_selected,0)
            }
            else{
                name.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0, R.drawable.ic_radio_unselected,0)

            }
            name.text="${list[position].flag}  ${if (Constants.language=="ar") list[position].nameAr else list[position].name}"
            root.setOnClickListener {
                selectedIndex=position
                notifyDataSetChanged()
                onClick(list[position].name,list[position].id.toString())
            }

        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

}