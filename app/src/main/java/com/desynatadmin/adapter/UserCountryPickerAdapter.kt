package com.desynatadmin.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.desynatadmin.databinding.ItemCountryBinding
import com.desynatadmin.ui.home.subscriptionPlan.response.GetCountryResponse

class UserCountryPickerAdapter(var context:Context, var countryList: ArrayList<GetCountryResponse.Data>, var onClick:ClickOnItem) : RecyclerView.Adapter<UserCountryPickerAdapter.ViewHolder>() {

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
            tvCountry.text = "${countryList[position].flag1}  ${countryList[position].name}"
            ivFlag.isVisible = false
          //  ivFlag.load(countryList[position].flag)
        }

        holder.itemView.setOnClickListener {
            onClick.onClickItem(position)
        }

    }


}
