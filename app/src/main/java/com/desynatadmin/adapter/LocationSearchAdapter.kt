package com.desynatadmin.adapter

import com.desynatadmin.databinding.LayoutItemBinding
import android.view.LayoutInflater

import android.view.ViewGroup

import androidx.recyclerview.widget.RecyclerView



import com.desynatadmin.ui.home.response.PlacePrediction

class LocationSearchAdapter(var list:ArrayList<PlacePrediction>,

                            private val onItemClick: (PlacePrediction) -> Unit):RecyclerView.Adapter<LocationSearchAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: LayoutItemBinding) :

        RecyclerView.ViewHolder(binding.root)


    override fun onCreateViewHolder(

        parent: ViewGroup,

        viewType: Int,

        ): LocationSearchAdapter.ViewHolder {

        val binding = LayoutItemBinding.inflate(

            LayoutInflater.from(parent.context),

            parent,

            false

        )

        return ViewHolder(binding)

    }

    override fun onBindViewHolder(holder: LocationSearchAdapter.ViewHolder, position: Int) {

        holder.binding.apply {

            name.text=list[position].description

            root.setOnClickListener { onItemClick(list[position]) }

        }

    }


    override fun getItemCount(): Int {

        return list.size

    }

    fun updateData(newList: List<PlacePrediction>) {

        list.clear()

        list.addAll(newList)

        notifyDataSetChanged()

    }

}
