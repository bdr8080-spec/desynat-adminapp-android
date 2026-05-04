package com.desynatadmin.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.desynatadmin.R
import com.desynatadmin.databinding.ItemPaymentBinding
import com.desynatadmin.ui.home.paymentManagement.response.GetBannerPaymentResponse
import com.desynatadmin.utils.CommonMethod

class BannerPaymentAdapter(var context:Context, private var paymentList: ArrayList<GetBannerPaymentResponse.Data.Orders.Data>, var paymentClick: PaymentClick) : RecyclerView.Adapter<BannerPaymentAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemPaymentBinding) :
        RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int) = with(binding){
            val paymentData = paymentList[position]
            tvUser.text = context.getString(R.string.purchased_by)
            tvCountry.isVisible = true
            tvCountryValue.isVisible = true
            tvCountryValue.text = paymentData?.sellerDetails?.storeInfo?.countryInfo?.name
            tvUserName.text = paymentData?.sellerDetails?.firstName+" "+paymentData?.sellerDetails?.lastName
            tvOrderId.text = "#${paymentData?.id}"
            tvTransactionId.text = "#${paymentData?.transactionId?:"28673"}"
            tvOrderedOn.text = CommonMethod.customizeChangeDateFormat(paymentData?.createdAt.toString(), outputFormat = "dd/MM/yyyy 'at' hh:mm a")
            tvTotalAmount.text = "${CommonMethod.formatPrice(paymentData?.price?.toDoubleOrNull()?:0.0)} KD"
            tvPaymentMode.text =  if (paymentData?.paymentMethod=="3") context.getString(R.string.cod)
            else if (paymentData?.paymentMethod=="1") context.getString(R.string.wallet) else context.getString(R.string.card)
            root.setOnClickListener {
                paymentClick.onPaymentClick(position,0)

            }
        }
        }

    interface PaymentClick{
        fun onPaymentClick(position: Int,type:Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemPaymentBinding.inflate(
                LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return paymentList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
       holder.bind(position)
    }


    fun setData(newList: ArrayList<GetBannerPaymentResponse.Data.Orders.Data>) {
        paymentList = newList
        notifyDataSetChanged()
    }
}
