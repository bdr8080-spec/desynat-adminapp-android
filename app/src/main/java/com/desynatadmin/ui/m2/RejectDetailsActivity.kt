package com.desynatadmin.ui.m2

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.desynatadmin.R
import com.desynatadmin.base.BaseActivity
import com.desynatadmin.data.view_model.HomeViewModel
import com.desynatadmin.databinding.ActivityRejectDetailsBinding
import com.desynatadmin.utils.Constants
import com.desynatadmin.utils.SharedPreference
import com.desynatadmin.utils.formatUtcDateToDdMmYyyy
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RejectDetailsActivity : BaseActivity() {
    private lateinit var binding : ActivityRejectDetailsBinding
    private val viewmodel: HomeViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRejectDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
        initControl()
        observer()

    }

    override fun initView() {

    }

    override fun onResume() {
        super.onResume()
        viewmodel.hitSellerDetail(
            SharedPreference.get(this).accessToken,
            intent.getStringExtra("id") ?: ""
        )
    }

    override fun initControl() {
        binding.tvToolbar.setOnClickListener {
            finish()
        }
    }

    override fun observer() {
        viewmodel.getSellerDetailData.observe(this){
            binding.tvName.text = it?.data?.first_name+" "+it?.data?.last_name
            binding.tvLocation.text = if (Constants.language=="ar") it?.data?.store_info?.country_info?.name_ar?:it?.data?.store_info?.country_info?.name else it?.data?.store_info?.country_info?.name
            binding.tvCall.text = it?.data?.country_code + "-" + it?.data?.mobile
            binding.tvEmail.text = it.data.email
            binding.tvAddress.text = it?.data?.store_info?.store_address
            binding.RejectionDescription.text = it?.data?.reject_reason
            binding.tvStoreName.text = it?.data?.store_info?.store_name
            binding.tvMail.text = it?.data?.store_info?.store_mail_id
            binding.tvBio.text = it?.data?.store_info?.bio
            Glide.with(this).load(it?.data?.store_info?.store_image).placeholder(R.drawable.ic_user).transform(CircleCrop()).into(binding.ivStoreImage)
            Glide.with(this).load(it?.data?.profile_pic).circleCrop().into(binding.ivProfileImage)
            Glide.with(this).load(it?.data?.owner_document?.getOrNull(0)?.document_path).placeholder(R.drawable.ic_placeholder).into(binding.doc1)
            Glide.with(this).load(it?.data?.owner_document?.getOrNull(1)?.document_path).placeholder(R.drawable.ic_placeholder).into(binding.doc2)
            Glide.with(this).load(it?.data?.store_document?.getOrNull(0)?.document_path).placeholder(R.drawable.ic_placeholder).into(binding.doc3)
            Glide.with(this).load(it?.data?.store_document?.getOrNull(1)?.document_path).placeholder(R.drawable.ic_placeholder).into(binding.doc4)
            binding.tvPhone.text = it?.data?.store_info?.country_code + "-" + it?.data?.store_info?.store_phone
            binding.tvAccountType.text = when(it.data.seller_bank_details.account_type){
                "0" -> getString(R.string.personal_account)
                else -> getString(R.string.business_account)
            }
            binding.tvIban.text = it?.data?.seller_bank_details?.iban
            binding.tvswiftbic.text = it?.data?.seller_bank_details?.swift
            binding.tvBankName.text = it?.data?.seller_bank_details?.bank_name
            binding.tvAccountHolderName.text = it?.data?.seller_bank_details?.account_holder_name
            Glide.with(this).load(it?.data?.seller_bank_details?.upload_proof).placeholder(R.drawable.ic_placeholder).into(binding.imAccountProof)
            binding.tvJoinedOn.text = "${getString(R.string.joined_on)} ${formatUtcDateToDdMmYyyy(it?.data?.created_at.toString())}"

        }

    }
}