package com.desynatadmin.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.desynatadmin.R
import com.desynatadmin.databinding.ItemPaymentBinding
import com.desynatadmin.ui.home.paymentManagement.response.GetOrderPaymentResponse
import com.desynatadmin.utils.CommonMethod

class OrderPaymentAdapter(var context:Context, private var paymentList: ArrayList<GetOrderPaymentResponse.Data.Orders.Data>, var paymentClick: PaymentClick) : RecyclerView.Adapter<OrderPaymentAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemPaymentBinding) :
        RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int) = with(binding){
            val paymentData = paymentList[position]
            tvViewMore.isVisible = true
            tvViewMore.setOnClickListener {
                if (tvViewMore.text == context.getString(R.string.view_more)){
                    tvViewMore.text = context.getString(R.string.view_less)
                    view2.isVisible = true
                    tvDiscount.isVisible = true
                    tvDiscountValue.isVisible = true
                    tvCommission.isVisible = true
                    tvCommissionEarned.isVisible = true

                    tvSeller.isVisible = true
                    tvSellerName.isVisible = true
                    if (paymentData?.order?.promocodeDetails!=null){
                        tvApplied.isVisible = true
                        tvAppliedPromocode.isVisible = true
                    }
                    else{
                        tvApplied.isVisible = false
                        tvAppliedPromocode.isVisible = false
                    }
                }
                else{
                    tvViewMore.text = context.getString(R.string.view_more)
                    view2.isVisible = false

                    tvDiscount.isVisible = false
                    tvDiscountValue.isVisible = false
                    tvCommission.isVisible = false
                    tvCommissionEarned.isVisible = false
                    tvApplied.isVisible = false
                    tvAppliedPromocode.isVisible = false
                    tvSeller.isVisible = false
                    tvSellerName.isVisible = false
                }
            }

            tvSellerName.text = paymentData?.productDetails?.sellerDetails?.firstName+" "+ paymentData?.productDetails?.sellerDetails?.lastName
            tvAppliedPromocode.text = paymentData?.order?.promocodeDetails?.nameEnglish
            tvCommissionEarned.text = "${CommonMethod.formatPrice(paymentData?.adminCommission?.toDoubleOrNull()?:0.0)} KD"
            tvDiscountValue.text = "${CommonMethod.formatPrice(paymentData?.totalDiscount?.toDoubleOrNull()?:0.0)} KD"

            tvUserName.text = paymentData?.userDetails?.fullName
                tvOrderId.text = "#${paymentData?.order?.id}"
            tvTransactionId.text = "#${paymentData?.order?.transactionId?:"28673"}"
            tvOrderedOn.text = CommonMethod.customizeChangeDateFormat(paymentData?.order?.createdAt.toString(), outputFormat = "dd/MM/yyyy 'at' hh:mm a")
            tvTotalAmount.text = "${CommonMethod.formatPrice(paymentData?.order?.totalAmount?.toDoubleOrNull()?:0.0)} KD"
            tvPaymentMode.text =  if (paymentData?.order?.paymentMethod=="1") context.getString(R.string.card)
            else if (paymentData?.order?.paymentMethod=="1") context.getString(R.string.wallet) else context.getString(R.string.cod)

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

    fun setData(newList: ArrayList<GetOrderPaymentResponse.Data.Orders.Data>) {
        paymentList = newList
        notifyDataSetChanged()
    }

}
