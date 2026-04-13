package com.desynatadmin.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.desynatadmin.databinding.ItemPaymentBinding
import com.desynatadmin.ui.home.paymentManagement.response.GetPlanPaymentResponse
import com.desynatadmin.utils.CommonMethod

class PlanPaymentAdapter(var context:Context, private var paymentList: ArrayList<GetPlanPaymentResponse.Data.Orders.Data>, var paymentClick: PaymentClick) : RecyclerView.Adapter<PlanPaymentAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemPaymentBinding) :
        RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int) = with(binding){
            val paymentData = paymentList[position]
            tvUser.text = "Seller Name"
            tvUserName.text = paymentData?.sellerDetails?.firstName+" "+paymentData?.sellerDetails?.lastName
            tvOrderId.text = "#${paymentData?.id}"
            tvTransactionId.text = "#${paymentData?.transactionId?:"28673"}"
            tvOrderedOn.text = CommonMethod.customizeChangeDateFormat(paymentData?.createdAt.toString(), outputFormat = "dd/MM/yyyy 'at' hh:mm a")
            tvTotalAmount.text = "${CommonMethod.formatPrice(paymentData?.amount?.toDoubleOrNull()?:0.0)} KD"
            tvPaymentMode.text =  if (paymentData?.paymentMethod=="1") "Card"
            else if (paymentData?.paymentMethod=="1") "Wallet" else "COD"
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

    fun setData(newList: ArrayList<GetPlanPaymentResponse.Data.Orders.Data>) {
        paymentList = newList
        notifyDataSetChanged()
    }

}
