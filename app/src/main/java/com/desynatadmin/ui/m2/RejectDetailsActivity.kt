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
            binding.tvName.text = it?.data?.firstName+" "+it?.data?.lastName
            binding.tvLocation.text = if (Constants.language=="ar") it?.data?.storeInfo?.countryInfo?.nameAr?:it?.data?.storeInfo?.countryInfo?.name else it?.data?.storeInfo?.countryInfo?.name
            binding.tvCall.text = it?.data?.countryCode + "-" + it?.data?.mobile
            binding.tvEmail.text = it.data.email
            binding.tvAddress.text = it?.data?.storeInfo?.storeAddress
            binding.RejectionDescription.text = it?.data?.rejectReason
            binding.tvStoreName.text = it?.data?.storeInfo?.storeName
            binding.tvMail.text = it?.data?.storeInfo?.storeMailId
            binding.tvBio.text = it?.data?.storeInfo?.bio
            Glide.with(this).load(it?.data?.storeInfo?.storeImage).placeholder(R.drawable.ic_user).transform(CircleCrop()).into(binding.ivStoreImage)
            Glide.with(this).load(it?.data?.profilePic).circleCrop().into(binding.ivProfileImage)
            Glide.with(this).load(it?.data?.ownerDocument?.getOrNull(0)?.documentPath).placeholder(R.drawable.ic_placeholder).into(binding.doc1)
            Glide.with(this).load(it?.data?.ownerDocument?.getOrNull(1)?.documentPath).placeholder(R.drawable.ic_placeholder).into(binding.doc2)
            Glide.with(this).load(it?.data?.storeDocument?.getOrNull(0)?.documentPath).placeholder(R.drawable.ic_placeholder).into(binding.doc3)
            Glide.with(this).load(it?.data?.storeDocument?.getOrNull(1)?.documentPath).placeholder(R.drawable.ic_placeholder).into(binding.doc4)
            binding.tvPhone.text = it?.data?.storeInfo?.countryCode + "-" + it?.data?.storeInfo?.storePhone
            binding.tvAccountType.text = when(it.data.sellerBankDetails.accountType){
                "0" -> getString(R.string.personal_account)
                else -> getString(R.string.business_account)
            }
            binding.tvIban.text = it?.data?.sellerBankDetails?.iban
            binding.tvswiftbic.text = it?.data?.sellerBankDetails?.swift
            binding.tvBankName.text = it?.data?.sellerBankDetails?.bankName
            binding.tvAccountHolderName.text = it?.data?.sellerBankDetails?.accountHolderName
            Glide.with(this).load(it?.data?.sellerBankDetails?.uploadProof).placeholder(R.drawable.ic_placeholder).into(binding.imAccountProof)
            binding.tvJoinedOn.text = "${getString(R.string.joined_on)} ${formatUtcDateToDdMmYyyy(it?.data?.createdAt.toString())}"

        }

    }
}