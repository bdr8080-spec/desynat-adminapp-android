package com.desynatadmin.ui.m2

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.desynatadmin.R
import com.desynatadmin.base.BaseActivity
import com.desynatadmin.data.view_model.HomeViewModel
import com.desynatadmin.databinding.ActivityNewDetailBinding
import com.desynatadmin.utils.SharedPreference
import com.desynatadmin.utils.formatUtcDateToDdMmYyyy
import com.desynatadmin.utils.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NewDetailActivity : BaseActivity() {
    private lateinit var binding : ActivityNewDetailBinding
    private val viewModel : HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
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

        binding.btnAccept.setOnClickListener {
            val map = HashMap<String, Any>()
            map["seller_id"] = intent.getStringExtra("id") ?: ""
            viewModel.hitAcceptSeller(
                SharedPreference.get(this).accessToken,
                map
            )
        }

    }

    override fun observer() {

        viewModel.getAcceptSeller.observe(this){
            showToast("Seller accept successfully")
            finish()
        }

        viewModel.getSellerDetailData.observe(this){
            binding.tvName.text = it.data.first_name
            binding.tvLocation.text = it.data.store_info.store_address
            binding.tvCall.text = it.data.country_code + "-" + it.data.mobile
            binding.tvEmail.text = it.data.email
            binding.tvAddress.text = it.data.store_info.address_line1
            binding.tvPhone.text = it.data.store_info.store_phone
            binding.tvMail.text = it.data.store_info.store_mail_id
            binding.tvBio.text = it.data.store_info.bio
            binding.tvAccountType.text = it.data.seller_bank_details.account_type
            binding.tvIban.text = it.data.seller_bank_details.iban
            binding.tvswiftbic.text = it.data.seller_bank_details.swift
            binding.tvBankName.text = it.data.seller_bank_details.bank_name
            binding.tvAccountHolderName.text = it.data.seller_bank_details.account_holder_name
            binding.tvJoinedOn.text = "Joined at ${formatUtcDateToDdMmYyyy(it.data.updated_at)}"

            Glide.with(this).load(it.data.owner_document.get(0).document_path).into(binding.doc1)
            Glide.with(this).load(it.data.owner_document.get(1).document_path).into(binding.doc2)
            Glide.with(this).load(it.data.store_document.get(0).document_path).into(binding.doc3)
            Glide.with(this).load(it.data.store_document.get(1).document_path).into(binding.doc4)
            Glide.with(this).load(it.data.seller_bank_details.upload_proof).into(binding.imAccountProof)

        }
    }
}