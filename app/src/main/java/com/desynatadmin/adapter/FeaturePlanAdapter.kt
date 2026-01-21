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
        RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int) = with(binding){
                tvFeature.text = featureList[position].feature
                if (featureList[position].feature=="Commission: "){
                    if (featureList[position].count!=0) {
                        tvCount.text = "${featureList[position].count.toString()}%"
                    }
                    else{
                        tvCount.text = "No"
                    }
                }
                else if (featureList[position].feature=="Stories: "){
                    tvCount.text = if(featureList[position].count==1) "Open" else "Close"
                }
                else{
                    if (featureList[position].count!=0) {
                        tvCount.text = featureList[position].count.toString()
                    }
                }
            root.setOnClickListener {
                onClick.onSubscriptionClick(position)

            }
        }
        }

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
        holder.bind(position)
    }


}
