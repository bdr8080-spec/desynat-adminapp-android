package com.desynatadmin.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.desynatadmin.databinding.ItemSpecificationBinding

class SpecificationAdapter(var context:Context, var specificationList: ArrayList<String>) : RecyclerView.Adapter<SpecificationAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemSpecificationBinding) :
        RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int) = with(binding){
          //  tvDescription.text = "${specificationList[position].nameEn} - ${specificationList[position].featureEn}"
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
