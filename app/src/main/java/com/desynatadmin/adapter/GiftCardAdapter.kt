package com.desynatadmin.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.desynatadmin.R
import com.desynatadmin.databinding.ItemCardBinding
import com.desynatadmin.ui.home.brandManagement.response.GetBrandResponse
import com.desynatadmin.ui.home.giftCardManagement.response.GetAllGiftCardResponse
import com.desynatadmin.utils.Constants

class GiftCardAdapter(var context:Context, var giftCardList: ArrayList<GetAllGiftCardResponse.Data.Data>, var giftCardClick:GiftCardClick) : RecyclerView.Adapter<GiftCardAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemCardBinding) :
        RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int) = with(binding){
            val giftData = giftCardList[position]

                tvGiftCardName.text = if (Constants.language=="ar") giftData?.nameArabic else giftData?.nameEnglish
                ivGift.load(giftData.image) {
                    crossfade(true)
                    placeholder(R.drawable.ic_placeholder)
                    error(R.drawable.ic_placeholder)
                }

            root.setOnClickListener {
                giftCardClick.onGiftCardClick(position)
            }
        }
        }

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
      holder.bind(position)
    }

    fun setData(newList: ArrayList<GetAllGiftCardResponse.Data.Data>) {
        giftCardList = newList
        notifyDataSetChanged()
    }

}
