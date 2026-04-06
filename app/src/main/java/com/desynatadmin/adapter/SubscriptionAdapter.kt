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
import com.desynatadmin.utils.CommonMethod

class SubscriptionAdapter(var context:Context, var subscriptionList: ArrayList<GetAllPlanResponse.Data>, var subscriptionClick:SubscriptionClick) : RecyclerView.Adapter<SubscriptionAdapter.ViewHolder>() {

    val backgrounds = listOf(
        R.drawable.pro,
        R.drawable.plus,
        R.drawable.growth,
        R.drawable.color_4,
        R.drawable.color_6,
        R.drawable.color_5,
        R.drawable.color_7,
    )
    inner class ViewHolder(val binding: ItemPlanBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) = with(binding) {
            val subscriptionData = subscriptionList[position]

            tvPackage.text = subscriptionData.planNameEnglish
            if (subscriptionData?.discountedPrice != null && subscriptionData?.discountedPrice != 0.0) {
                tvPrice.text =
                    "KD ${CommonMethod.formatDoubleValue(subscriptionData?.discountedPrice)}"
            } else {
                tvPrice.text = "KD ${CommonMethod.formatDoubleValue(subscriptionData?.price)}"
            }
            when (subscriptionData.durationType) {
                1 -> {
                    tvDuration.text = "for ${subscriptionData.duration} Month"
                }

                2 -> {
                    tvDuration.text = "for ${subscriptionData.duration} Year"
                }

                else -> {
                    tvDuration.text = "for ${subscriptionData.duration} Month"
                }
            }
            /*
                            when(position){
                                0->tvPackage.setBackgroundResource(R.drawable.pro)
                                1->tvPackage.setBackgroundResource(R.drawable.plus)
                                2->tvPackage.setBackgroundResource(R.drawable.growth)
                                else ->tvPackage.setBackgroundResource(R.drawable.pro)
                            }*/


            val backgroundRes = backgrounds[position % backgrounds.size]
            tvPackage.setBackgroundResource(backgroundRes)

            var featureList = ArrayList<FeatureList>()

            featureList.clear()

            subscriptionData.apply {
                if (noOfProducts != 0)
                    featureList.add(FeatureList("${context.getString(R.string.number_of_products)}: ", noOfProducts))
                else  featureList.add(FeatureList("${context.getString(R.string.number_of_products)}: ", 0))

                if (noOfVideos != 0)
                    featureList.add(FeatureList("Number of Videos (Reels): ", noOfVideos))

                if (noOfHighlights != 0)
                    featureList.add(FeatureList("Number of Highlights: ", noOfHighlights))

                featureList.add(FeatureList("Stories: ", stories))

                if (noOfEmployee != 0)
                    featureList.add(FeatureList("$noOfEmployee Store Employees", 0))

                // Handle commission formatting
                val commissionValue = commissionPercentage?.toString()?.toDoubleOrNull()
                if (commissionValue != null && commissionValue != 0.0) {
                    if (commissionValue % 1.0 == 0.0) {
                        val formattedCommission = commissionValue.toInt()
                        featureList.add(FeatureList("Commission: ", formattedCommission))
                    } else {
                        val formattedCommission = commissionValue.toString()
                        featureList.add(FeatureList("Commission: $formattedCommission %", 0))

                    }

                } else {
                    featureList.add(FeatureList("Commission: ", 0))
                }

                if (noOfFreeMonths != null&&noOfFreeMonths != "null"&&noOfFreeMonths != "")
                    featureList.add(FeatureList("Number of Free Month: ", noOfFreeMonths.toIntOrNull()))

            }

            rvFeature.apply {
                layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
                adapter = FeaturePlanAdapter(
                    context = context,
                    featureList, object : FeaturePlanAdapter.SubscriptionFeatureClick {
                        override fun onSubscriptionClick(featurePosition: Int) {
                            val backgroundRes = backgrounds[position % backgrounds.size]
                            subscriptionClick.onSubscriptionClick(position, backgroundRes)
                        }

                    })
                setHasFixedSize(true)
                isNestedScrollingEnabled = false
                isClickable = false
                isFocusable = false
                isFocusableInTouchMode = false

            }
            root.setOnClickListener {
                val backgroundRes = backgrounds[position % backgrounds.size]
                subscriptionClick.onSubscriptionClick(position, backgroundRes)
            }
        }
    }

    interface SubscriptionClick{
        fun onSubscriptionClick(position: Int, backgroundRes: Int)    }

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
       holder.bind(position)
    }
}

data class FeatureList(
    var feature:String,
    var count:Int?=0
)
