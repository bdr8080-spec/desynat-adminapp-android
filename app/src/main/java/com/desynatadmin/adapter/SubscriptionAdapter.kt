package com.desynatadmin.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.desynatadmin.R
import com.desynatadmin.databinding.ItemBrandBinding
import com.desynatadmin.databinding.ItemPlanBinding
import com.desynatadmin.ui.home.brandManagement.response.GetBrandResponse
import com.desynatadmin.ui.home.categorySubCategoryManagement.response.GetSubCategoryResponse
import com.desynatadmin.ui.home.subscriptionPlan.response.GetAllPlanResponse

class SubscriptionAdapter(var context:Context, var subscriptionList: ArrayList<GetAllPlanResponse.Data>, var subscriptionClick:SubscriptionClick) : RecyclerView.Adapter<SubscriptionAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemPlanBinding) :
        RecyclerView.ViewHolder(binding.root)

    interface SubscriptionClick{
        fun onSubscriptionClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemPlanBinding.inflate(
               LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return subscriptionList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val subscriptionData = subscriptionList[position]

        holder.binding.apply {
            tvPackage.text = subscriptionData.planNameEnglish
            tvPrice.text = "KD ${subscriptionData.price}"
            when(subscriptionData.durationType){
                1->{
                    tvDuration.text = "for ${subscriptionData.duration} Month"
                }
                else ->{
                    tvDuration.text = "for ${subscriptionData.duration} Days"
                }
            }

            when(position){
                0->tvPackage.setBackgroundResource(R.drawable.pro)
                1->tvPackage.setBackgroundResource(R.drawable.plus)
                2->tvPackage.setBackgroundResource(R.drawable.growth)
                else ->tvPackage.setBackgroundResource(R.drawable.pro)
            }
        }
        var featureList = ArrayList<FeatureList>()

        featureList.clear()

        subscriptionData.apply {
            if (noOfProducts != 0)
                featureList.add(FeatureList("Number of Products: ", noOfProducts))

            if (noOfVideos != 0)
                featureList.add(FeatureList("Number of Videos (Reels): ", noOfVideos))

            if (noOfHighlights != 0)
                featureList.add(FeatureList("Number of Highlights: ", noOfHighlights))

            if (stories != 0)
                featureList.add(FeatureList("Stories: ", stories))

            if (noOfEmployee != 0)
                featureList.add(FeatureList("$noOfEmployee Store Employees", 0))

            // Handle commission formatting
            val commissionValue = commissionPercentage?.toString()?.toDoubleOrNull()
            if (commissionValue != null && commissionValue != 0.0) {
                if (commissionValue % 1.0 == 0.0){
                    val formattedCommission =  commissionValue.toInt()
                    featureList.add(FeatureList("Commission: ", formattedCommission))}
                else {
                    val formattedCommission =  commissionValue.toString()
                    featureList.add(FeatureList("Commission: $formattedCommission", 0))

                }

            }
        }

        holder.binding.rvFeature.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = FeaturePlanAdapter(
                context = context,
                featureList,object :FeaturePlanAdapter.SubscriptionFeatureClick{
                    override fun onSubscriptionClick(featurePosition: Int) {
                        subscriptionClick.onSubscriptionClick(position)
                    }

                })
            setHasFixedSize(true)
            isNestedScrollingEnabled = false
        }

        holder.itemView.setOnClickListener {
            subscriptionClick.onSubscriptionClick(position)

        }
    }
}

data class FeatureList(
    var feature:String,
    var count:Int?=0
)
