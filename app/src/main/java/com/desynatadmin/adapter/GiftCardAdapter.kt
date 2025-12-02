package com.desynatadmin.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.desynatadmin.R
import com.desynatadmin.databinding.ItemBrandBinding
import com.desynatadmin.databinding.ItemCardBinding
import com.desynatadmin.ui.home.brandManagement.response.GetBrandResponse
import com.desynatadmin.ui.home.categorySubCategoryManagement.response.GetSubCategoryResponse

class GiftCardAdapter(var context:Context, var giftCardList: ArrayList<GetBrandResponse.Data.Data>, var giftCardClick:GiftCardClick) : RecyclerView.Adapter<GiftCardAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemCardBinding) :
        RecyclerView.ViewHolder(binding.root)

    interface GiftCardClick{
        fun onGiftCardClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return giftCardList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var giftData = giftCardList[position]

        holder.binding.apply {
            tvBrandName.text = giftData.nameEnglish
            ivBrand.load(giftData.image) {
                crossfade(true)
                placeholder(R.drawable.ic_placeholder)
                error(R.drawable.ic_placeholder)
            }
        }

        holder.itemView.setOnClickListener {
            giftCardClick.onGiftCardClick(position)

        }
    }

    fun setData(newList: ArrayList<GetBrandResponse.Data.Data>) {
        giftCardList = newList
        notifyDataSetChanged()
    }

}
