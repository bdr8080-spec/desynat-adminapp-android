package com.desynatadmin.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.desynatadmin.R
import com.desynatadmin.databinding.ItemDealsBinding
import com.desynatadmin.ui.home.dealsManagement.response.GetActiveDealResponse
import com.desynatadmin.utils.CommonMethod

class DealsAdapter(var context:Context, var dealsList: ArrayList<GetActiveDealResponse.Data.Data>, var dealsClick:DealsClick) : RecyclerView.Adapter<DealsAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemDealsBinding) :
        RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int) = with(binding){
            val dealData = dealsList[position]

                tvDealNameValue.text = dealData.nameEnglish
                tvDurationValue.text = "${CommonMethod.customizeChangeDateFormat(dealData.from, inputFormat = "yyyy-MM-dd", outputFormat = "dd/MM/yyyy")}  - ${CommonMethod.customizeChangeDateFormat(dealData.to, inputFormat = "yyyy-MM-dd", outputFormat = "dd/MM/yyyy")}"
                // tvTotalEnrolled.text = "${dealData }Sellers"
                tvDiscountValue.text = dealData.discount+"%"
                ivDeal.load(dealData.image) {
                    crossfade(true)
                    placeholder(R.drawable.ic_placeholder)
                    error(R.drawable.ic_placeholder)
                }

            root.setOnClickListener {
                dealsClick.onDealsClick(position)

            }
        }
        }

    interface DealsClick{
        fun onDealsClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemDealsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return dealsList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
       holder.bind(position)
    }

    fun setData(newList: ArrayList<GetActiveDealResponse.Data.Data>) {
        dealsList = newList
        notifyDataSetChanged()
    }

}
