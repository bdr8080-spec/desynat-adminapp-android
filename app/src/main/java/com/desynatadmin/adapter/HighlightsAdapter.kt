package com.desynatadmin.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.desynatadmin.databinding.CityListItemBinding
import com.desynatadmin.databinding.HighlightsItemBinding

class HighlightsAdapter : RecyclerView.Adapter<HighlightsAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: HighlightsItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HighlightsAdapter.ViewHolder {
        val binding = HighlightsItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

    }

    override fun getItemCount(): Int {
        return 5
    }

}
