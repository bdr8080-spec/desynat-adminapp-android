package com.desynatadmin.ui.m2

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.desynatadmin.R
import com.desynatadmin.adapter.HighlightsAdapter
import com.desynatadmin.adapter.ReelAdapter
import com.desynatadmin.base.BaseActivity
import com.desynatadmin.data.view_model.HomeViewModel
import com.desynatadmin.databinding.ActivityApprovedDetailsBinding
import com.desynatadmin.utils.SharedPreference
import com.desynatadmin.utils.formatUtcDateToDdMmYyyy
import com.desynatadmin.utils.formatUtcDateToLocal
import com.desynatadmin.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.roundToInt

@AndroidEntryPoint
class ApprovedDetailsActivity : BaseActivity() {
    private lateinit var binding: ActivityApprovedDetailsBinding
    private val viewModel : HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inflate *after* onCreate is called
        binding = ActivityApprovedDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
        initView()
        initControl()
        observer()
    }

    override fun initView() {
        viewModel.hitSellerDetail(
            SharedPreference.get(this).accessToken,
            intent.getStringExtra("id") ?: ""
        )
    }

    override fun initControl() {

        binding.ivBack.setOnClickListener {
            finish()
        }

        binding.ivToggle.setOnClickListener {
            val map = HashMap<String, Any>()
            map["seller_id"] = intent.getStringExtra("id") ?: ""
            viewModel.hitUpdateSellerStaus(
                SharedPreference.get(this).accessToken,
                map
            )
        }

        binding.reelRecyclerView.adapter = ReelAdapter()
        binding?.btnSellerInfo?.setOnClickListener {
            binding?.btnSellerInfo?.setBackgroundResource(R.drawable.selected_left_border)
            binding?.btnSellerInfo?.setTextColor(Color.WHITE)
            binding?.btnOtherInfo?.setBackgroundResource(R.drawable.btn_other_unselected)
            binding?.btnOtherInfo?.setTextColor(Color.BLACK)
            binding?.sellerInfoLayout?.visibility = View.VISIBLE
            binding?.otherInfoLayout?.visibility = View.GONE
        }

        binding?.btnOtherInfo?.setOnClickListener {
            binding?.btnOtherInfo?.setBackgroundResource(R.drawable.selected_right_border)
            binding?.btnOtherInfo?.setTextColor(Color.WHITE)
            binding?.btnSellerInfo?.background = null
            binding?.btnSellerInfo?.setTextColor(Color.BLACK)
            binding?.sellerInfoLayout?.visibility = View.GONE
            binding?.otherInfoLayout?.visibility = View.VISIBLE
        }
        binding?.highlightsRecyclerView?.adapter = HighlightsAdapter()

    }


    override fun observer() {

        viewModel.getUpdateSellerStatus.observe(this){
            if(it.data.is_block == 0){
                showToast("Seller UnBlock Successfully")
                binding.ivToggle.setImageResource(R.drawable.toggle_ic)
            }
            else{
                showToast("Seller Block Successfully")
                binding.ivToggle.setImageResource(R.drawable.toggle_off)
            }
        }

        viewModel.getSellerDetailData.observe(this){
            if(it.data.is_block == 0){
                binding.ivToggle.setImageResource(R.drawable.toggle_ic)
            }
            else{
                binding.ivToggle.setImageResource(R.drawable.toggle_off)
            }
            Glide.with(this).load(it.data.profile_pic).transform(CircleCrop()).into(binding.ivProfileImage)
            Glide.with(this).load(it.data.store_info.store_image).into(binding.imPuma)
            binding.tvName.text = it.data.first_name
            binding.tvEmail.text = it.data.email
            binding.txt.text = it.data.store_info.bio
            binding.tvLocation.text = it.data.store_info.country_info.name
            binding.tvCall.text  = it.data.country_code + "-" + it.data.mobile
            binding.tvAddress.text  = "${it.data.store_info.store_name}\n${it.data.store_info.store_address}"
           // binding.tvProPlan.text  = it.data.subcription_plan ?: ""
            Glide.with(this).load(it.data.owner_document.get(0).document_path).into(binding.doc1)
            Glide.with(this).load(it.data.owner_document.get(1).document_path).into(binding.doc2)
            Glide.with(this).load(it.data.store_document.get(0).document_path).into(binding.doc3)
            Glide.with(this).load(it.data.store_document.get(1).document_path).into(binding.doc4)
            binding.tvAccountType.text = when(it.data.seller_bank_details.account_type){
                "0" -> "Personal Account"
                else -> "Business Account"
            }
            binding.cl1.isVisible = true
            binding.clOrder.isVisible = true
            if(it?.data?.subcription_plan.isNullOrEmpty()){
                binding.tvPurchasePlan.text = "No Active Plans"
                binding.purchasePlanLayout.isVisible = false
            }
            else{
                binding.tvPurchasePlan.text = "Purchased Plan"
                binding.purchasePlanLayout.isVisible = true
                binding.tvStatus.text = if(it.data?.subcription_plan?.get(0)?.plan_info?.status == 0) " Active" else "Expire"
                binding.tvProPlanLabel.text = it.data?.subcription_plan?.get(0)?.plan_info?.plan_name_english ?: ""
                if(it.data?.subcription_plan?.get(0)?.plan_info?.duration_type == 1){
                    binding.tvProPlan.text = it.data?.subcription_plan?.get(0)?.plan_info?.duration + "Months"
                }
                else{
                    binding.tvProPlan.text = it.data?.subcription_plan?.get(0)?.plan_info?.duration + "Years"
                }
                binding.tvPurchasedOn.text = formatUtcDateToLocal( it.data?.subcription_plan?.get(0)?.plan_info?.created_at ?: "")
                binding.tvTotalAmount.text = "${it.data?.subcription_plan?.get(0)?.plan_info?.price?.roundToInt()} KD"


            }
            binding.tvIban.text = it.data.seller_bank_details.iban
            binding.tvswiftbic.text = it.data.seller_bank_details.swift
            binding.tvBankName.text = it.data.seller_bank_details.bank_name
            binding.tvAccountHolderName.text = it.data.seller_bank_details.account_holder_name
            Glide.with(this).load(it.data.seller_bank_details.upload_proof).into(binding.imAccountProof)
        }
    }
}