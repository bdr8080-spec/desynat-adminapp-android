package com.desynatadmin.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.desynatadmin.R
import com.desynatadmin.databinding.ItemPlanFeatureBinding

class FeaturePlanAdapter(var context:Context, var featureList: ArrayList<FeatureList>,var onClick: SubscriptionFeatureClick) : RecyclerView.Adapter<FeaturePlanAdapter.ViewHolder>() {
    interface SubscriptionFeatureClick{
        fun onSubscriptionClick(featurePosition: Int)
    }
    inner class ViewHolder(val binding: ItemPlanFeatureBinding) :
        RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int) = with(binding){
                tvFeature.text = featureList[position].feature
                if (featureList[position].feature=="${context.getString(R.string.commission)}: "){
                    if (featureList[position].count!=0) {
                        tvCount.text = "${featureList[position].count.toString()}%"
                    }
                    else{
                        tvCount.text = context.getString(R.string.no)
                    }
                }
                else if (featureList[position].feature=="${context.getString(R.string.stories)}: "){
                    tvCount.text = if(featureList[position].count==1) context.getString(R.string.open) else context.getString(
                        R.string.close
                    )
                }
                else if (featureList[position].feature=="${context.getString(R.string.show_in_recommended_stores)}: "){
                    tvCount.text = if(featureList[position].count==1) context.getString(R.string.yes) else context.getString(
                        R.string.no
                    )
                }
                else{
                    if (featureList[position].count!=0) {
                        tvCount.text = featureList[position].count.toString()
                    }
                    else if (featureList[position].feature=="${context.getString(R.string.number_of_products)}: "&&featureList[position].count==0){
                        tvCount.text = context.getString(R.string.unlimited)
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
