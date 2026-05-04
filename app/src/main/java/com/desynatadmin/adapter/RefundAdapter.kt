package com.desynatadmin.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.desynatadmin.R
import com.desynatadmin.databinding.ItemRefundBinding
import com.desynatadmin.ui.home.refundManagement.response.GetRefundOrderResponse
import com.desynatadmin.utils.CommonMethod

class RefundAdapter(var context:Context, var refundList: ArrayList<GetRefundOrderResponse.Data.Data>, var onClick:OnClick) : RecyclerView.Adapter<RefundAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemRefundBinding) :
        RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int) = with(binding){
           var refundData = refundList[position]
                tvName.text = refundData.userDetails?.fullName
            tvSellerName.text = refundData.productDetails?.sellerDetails?.firstName+" "+refundData.productDetails?.sellerDetails?.lastName
            tvCountryName.text = refundData?.productDetails?.store?.countryInfo?.name
            tvOrderIdValue.text = "#${refundData?.order?.id}"
            tvTransactionId.text = "#${refundData?.order?.transactionId}"
            tvOrderedOn.text = CommonMethod.customizeChangeDateFormatKuwaitFromUtc(refundData?.order?.createdAt.toString(), outputFormat = "dd/MM/yyyy 'at' hh:mm a")
            tvCancelledOn.text = CommonMethod.customizeChangeDateFormatKuwaitFromUtc(refundData?.updatedAt.toString(), outputFormat = "dd/MM/yyyy 'at' hh:mm a")
            tvTotalAmount.text = "${CommonMethod.formatPrice(refundData?.order?.totalAmount?.toDoubleOrNull()?:0.0)} KD"

            if (refundData?.status=="2"){
                tvStatusValue.text = context.getString(R.string.completed)
                tvStatusValue.setTextColor(Color.parseColor("#1A900D"))
            }
            else{
                tvStatusValue.text = context.getString(R.string.pending)
                tvStatusValue.setTextColor(Color.parseColor("#FFA41B"))
            }

            tvViewMore.setOnClickListener {
                if (tvViewMore.text == context.getString(R.string.less_view)){
                    tvViewMore.text = context.getString(R.string.more_info)
                    tvCountry.isVisible = false
                    tvCountryName.isVisible = false
                    tvSellerName.isVisible = false
                    tvSeller.isVisible = false
                    view2.isVisible = false
                    tvReason.isVisible = false
                    tvReasonValue.isVisible = false
                }
                else{
                    tvViewMore.text = context.getString(R.string.less_view)
                    tvCountry.isVisible = true
                    tvCountryName.isVisible = true
                    tvSellerName.isVisible = true
                    tvSeller.isVisible = true
                    if (refundData?.returnReason!=null){
                        view2.isVisible = true
                        tvReason.isVisible = true
                        tvReasonValue.isVisible = true
                        tvReasonValue.text = refundData?.returnReason.toString()
                    }
                    else if (refundData?.exchangeReason!=null){
                        view2.isVisible = true
                        tvReason.isVisible = true
                        tvReasonValue.isVisible = true
                        tvReasonValue.text = refundData?.exchangeReason.toString()
                    }
                    else if (refundData?.cancelReason!=null){
                        view2.isVisible = true
                        tvReason.isVisible = true
                        tvReasonValue.isVisible = true
                        tvReasonValue.text = refundData?.cancelReason.toString()
                    }
                    else{
                        view2.isVisible = false
                        tvReason.isVisible = false
                        tvReasonValue.isVisible = false
                    }
                }

            }

        }
        }

    interface OnClick{
        fun OnItemClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemRefundBinding.inflate(
                LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return refundList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
       holder.bind(position)
    }

    fun setData(newList: ArrayList<GetRefundOrderResponse.Data.Data>) {
        refundList = newList
        notifyDataSetChanged()
    }

}
