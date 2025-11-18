package com.desynatadmin.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.desynatadmin.databinding.ItemCountryBinding
import com.desynatadmin.ui.home.deliveryBoyManagement.response.GetKuwaitZoneResponse

class ZonePickerAdapter(var context:Context, var zoneList: ArrayList<GetKuwaitZoneResponse.Data>, var onClick:ClickOnItem) : RecyclerView.Adapter<ZonePickerAdapter.ViewHolder>() {

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
        return zoneList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.apply {
            tvCountry.text = "${zoneList[position].nameEnglish}"
        }

        holder.itemView.setOnClickListener {
            onClick.onClickItem(position)
        }

    }


}
