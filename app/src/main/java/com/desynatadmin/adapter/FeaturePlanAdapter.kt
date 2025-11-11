package com.desynatadmin.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.desynatadmin.databinding.ItemPlanFeatureBinding

class FeaturePlanAdapter(var context:Context, var featureList: ArrayList<FeatureList>,var onClick: SubscriptionFeatureClick) : RecyclerView.Adapter<FeaturePlanAdapter.ViewHolder>() {
    interface SubscriptionFeatureClick{
        fun onSubscriptionClick(featurePosition: Int)
    }
    inner class ViewHolder(val binding: ItemPlanFeatureBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemPlanFeatureBinding.inflate(
                LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return featureList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.apply {
            tvFeature.text = featureList[position].feature
            if (featureList[position].count!=0) {
                tvCount.text = featureList[position].count.toString()
            }
        }

        holder.itemView.setOnClickListener {
            onClick.onSubscriptionClick(position)

        }

    }


}
