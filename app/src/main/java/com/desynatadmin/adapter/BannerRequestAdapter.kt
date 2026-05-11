package com.desynatadmin.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.desynatadmin.R
import com.desynatadmin.databinding.ItemBannerRequestBinding
import com.desynatadmin.ui.home.bannerRequestManagement.response.GetBannerRequestResponse
import com.desynatadmin.utils.CommonMethod
import com.desynatadmin.utils.Constants
import com.desynatadmin.utils.SharedPreference

class BannerRequestAdapter(var context:Context, var type:Int,var bannerList: ArrayList<GetBannerRequestResponse.Data.Data>, var onClick:OnClick) : RecyclerView.Adapter<BannerRequestAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemBannerRequestBinding) :
        RecyclerView.ViewHolder(binding.root){

        fun bind(position: Int) = with(binding){
            val bannerRequestData = bannerList[position]

            var actionAccess : Boolean = true
            val userInfo = SharedPreference.get(context).getUser()
            if (userInfo?.type=="2"){
                val subAdminInfo = SharedPreference.get(context).getSubAdmin()
                val module = subAdminInfo?.permissionDetails
                    ?.firstOrNull {
                        it.moduleDetails.name.equals("banner_request_management", true)
                    }

                actionAccess = module?.canAction == 1

                btnAccept.isVisible = actionAccess
                btnReject.isVisible = actionAccess
            }

                tvName.text = bannerRequestData.sellerDetails?.firstName+" "+bannerRequestData.sellerDetails?.lastName
                tvCountry.text = if (Constants.language=="ar") bannerRequestData.sellerDetails?.countryDetails?.nameAr else bannerRequestData.sellerDetails?.countryDetails?.name
                tvRequestId.text = "#${bannerRequestData?.orderId}"
                tvRequestOnValue.text = CommonMethod.customizeChangeDateFormat(bannerRequestData?.createdAt.toString(), outputFormat = "dd/MM/yyyy '${context.getString(R.string.at)}' hh:mm a")
                tvDurationValue.text = bannerRequestData.totalDays
                tvTotalAmountValue.text = "${CommonMethod.formatAmount(bannerRequestData?.price?:"")} KD"
                when(bannerRequestData?.bannerPosition) {
                    "1" -> {
                        tvBannerPositionValue.text = context.getString(R.string.home_top)
                    }

                    "2" -> {
                        tvBannerPositionValue.text = context.getString(R.string.home_bottom)
                    }

                    "3" -> {
                        tvBannerPositionValue.text = context.getString(R.string.sub_category)
                    }
                }

                when(type){
                    0->{
                        tvStatus.isVisible = false
                        tvStatusValue.isVisible = false
                        btnReject.isVisible = true
                        btnAccept.isVisible = true
                        tvBannerPosition.isVisible = false
                        tvBannerPositionValue.isVisible = false
                        tvPaymentStatus.isVisible = false
                        tvPaymentStatusValue.isVisible = false

                        tvReason.isVisible = false
                        tvReasonValue.isVisible = false
                    }
                    1->{
                        tvStatus.isVisible = true
                        tvStatusValue.isVisible = true
                        btnReject.isVisible = false
                        btnAccept.isVisible = false
                        tvBannerPosition.isVisible = true
                        tvBannerPositionValue.isVisible = true
                        tvPaymentStatus.isVisible = true
                        tvPaymentStatusValue.isVisible = true

                        tvReason.isVisible = false
                        tvReasonValue.isVisible = false


                        tvStatusValue.setTextColor(Color.parseColor("#68CA44"))
                        if (bannerRequestData.paymentStatus=="1"){
                            tvStatusValue.text = context.getString(R.string.active)
                            tvPaymentStatusValue.text = context.getString(R.string.paid)
                            tvPaymentStatusValue.setTextColor(Color.parseColor("#68CA44"))
                        }
                        else{
                            tvStatusValue.text = context.getString(R.string.upcoming)
                            tvPaymentStatusValue.text =  context.getString(R.string.pending)
                            tvPaymentStatusValue.setTextColor(Color.parseColor("#D53B36"))
                        }
                    }
                    2->{
                        tvStatus.isVisible = true
                        tvStatusValue.isVisible = true
                        btnReject.isVisible = false
                        btnAccept.isVisible = false
                        tvStatusValue.text =  context.getString(R.string.rejected)
                        tvStatusValue.setTextColor(Color.parseColor("#D53B36"))

                        tvReason.isVisible = true
                        tvReasonValue.isVisible = true

                        tvPaymentStatus.isVisible = false
                        tvPaymentStatusValue.isVisible = false
                        //   tvDuration.text = "Bank Acc no."
                        tvTotalAmount.text = context.getString(R.string.total_amount)
                        tvPaymentStatus.text = context.getString(R.string.requested_amount)
                        tvDurationValue.text = bannerRequestData?.sellerDetails?.bankInformation

                        tvReasonValue.text = bannerRequestData?.cancelledReason
                        tvPaymentStatusValue.text = "${CommonMethod.formatAmount(bannerRequestData.price)} KD"
                    }

                }
                btnAccept.setOnClickListener {
                    onClick.onItemClick(position,1)
                }

                btnReject.setOnClickListener {
                    onClick.onItemClick(position,2)
                }

            root.setOnClickListener {
                onClick.onItemClick(position,3)
            }
        }
        }

    interface OnClick{
        fun onItemClick(position: Int,type:Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemBannerRequestBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return bannerList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    fun setData(typeValue:Int,newList: ArrayList<GetBannerRequestResponse.Data.Data>) {
        bannerList = newList
        type = typeValue
        notifyDataSetChanged()
    }
}
