package com.desynatadmin.ui.m2

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.desynatadmin.R
import com.desynatadmin.adapter.HighlightsAdapter
import com.desynatadmin.adapter.ReelAdapter
import com.desynatadmin.base.BaseActivity
import com.desynatadmin.data.view_model.HomeViewModel
import com.desynatadmin.databinding.ActivityApprovedDetailsBinding
import com.desynatadmin.utils.SharedPreference
import dagger.hilt.android.AndroidEntryPoint

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
        binding.reelRecyclerView.adapter = ReelAdapter()
        binding?.btnSellerInfo?.setOnClickListener {
            binding?.btnSellerInfo?.setBackgroundResource(R.drawable.btn_seller_selected)
            binding?.btnSellerInfo?.setTextColor(Color.WHITE)
            binding?.btnOtherInfo?.setBackgroundResource(R.drawable.btn_other_unselected)
            binding?.btnOtherInfo?.setTextColor(Color.BLACK)
            binding?.sellerInfoLayout?.visibility = View.VISIBLE
            binding?.otherInfoLayout?.visibility = View.GONE
        }

        binding?.btnOtherInfo?.setOnClickListener {
            binding?.btnOtherInfo?.setBackgroundResource(R.drawable.btn_seller_selected)
            binding?.btnOtherInfo?.setTextColor(Color.WHITE)
            binding?.btnSellerInfo?.setBackgroundResource(R.drawable.btn_other_unselected)
            binding?.btnSellerInfo?.setTextColor(Color.BLACK)
            binding?.sellerInfoLayout?.visibility = View.GONE
            binding?.otherInfoLayout?.visibility = View.VISIBLE
        }
        binding?.highlightsRecyclerView?.adapter = HighlightsAdapter()

    }


    override fun observer() {

        viewModel.getSellerDetailData.observe(this){
            Glide.with(this).load(it.data.profile_pic).into(binding.ivProfileImage)
            binding.tvName.text = it.data.first_name
            binding.txt.text = it.data.store_info.bio
            binding.tvLocation.text = it.data.store_info.store_address
            binding.tvCall.text  = it.data.country_code + "-" + it.data.mobile
            binding.tvAddress.text  = it.data.store_info.address_line1
            binding.tvProPlan.text  = it.data.subcription_plan ?: ""
            Glide.with(this).load(it.data.owner_document.get(0).document_path).into(binding.doc1)
            Glide.with(this).load(it.data.owner_document.get(1).document_path).into(binding.doc2)
            Glide.with(this).load(it.data.store_document.get(0).document_path).into(binding.doc3)
            Glide.with(this).load(it.data.store_document.get(1).document_path).into(binding.doc4)

            binding.tvAccountType.text = it.data.seller_bank_details.account_type
            binding.tvIban.text = it.data.seller_bank_details.iban
            binding.tvswiftbic.text = it.data.seller_bank_details.swift
            binding.tvBankName.text = it.data.seller_bank_details.bank_name
            binding.tvAccountHolderName.text = it.data.seller_bank_details.account_holder_name
            Glide.with(this).load(it.data.seller_bank_details.upload_proof).into(binding.imAccountProof)



        }
    }
}