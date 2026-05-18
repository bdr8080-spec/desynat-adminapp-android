package com.desynatadmin.ui.m2

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.desynatadmin.R
import com.desynatadmin.base.BaseActivity
import com.desynatadmin.data.view_model.HomeViewModel
import com.desynatadmin.databinding.ActivityNewDetailBinding
import com.desynatadmin.databinding.RejectReasonLayoutBinding
import com.desynatadmin.utils.Constants
import com.desynatadmin.utils.SharedPreference
import com.desynatadmin.utils.formatUtcDateToDdMmYyyy
import com.desynatadmin.utils.showToast
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlin.plus

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
        binding?.apply {
            var actionAccess: Boolean=true
            val userInfo = SharedPreference.get(this@NewDetailActivity).getUser()
            if (userInfo?.type=="2"){
                val subAdminInfo = SharedPreference.get(this@NewDetailActivity).getSubAdmin()
                val module = subAdminInfo?.permissionDetails
                    ?.firstOrNull {
                        it.moduleDetails.name.equals("seller_management", true)
                    }

                actionAccess = module?.canAction == 1
                btnAccept.isVisible = actionAccess
                btnReject.isVisible = actionAccess
            }
        }
        viewModel.hitSellerDetail(
            SharedPreference.get(this).accessToken,
            intent.getStringExtra("id") ?: ""
        )
    }

    override fun initControl() {

        binding.tvToolbar.setOnClickListener {
            finish()
        }

        binding.btnAccept.setOnClickListener {
            val map = HashMap<String, Any>()
            map["seller_id"] = intent.getStringExtra("id") ?: ""
            viewModel.hitAcceptSeller(
                SharedPreference.get(this).accessToken,
                map
            )
        }

        binding.btnReject.setOnClickListener {
            deleteBottomSheet()
        }
    }

    var deleteBottomDialog: BottomSheetDialog? = null
    private fun deleteBottomSheet() {
        deleteBottomDialog = BottomSheetDialog(this, R.style.BottomSheetDialog)
        val deleteBinding = RejectReasonLayoutBinding.inflate(layoutInflater)
        var type = ""

        deleteBottomDialog?.apply {
            setContentView(deleteBinding.root)
            setCancelable(true)
        }

        deleteBinding.rbComplete.setOnClickListener {
            type = "complete"
            deleteBinding.rbComplete.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_radio_selected,0,0,0)
            deleteBinding.rbReject.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_radio_unselected,0,0,0)
        }
        deleteBinding.rbReject.setOnClickListener {
            type = "document"
            deleteBinding.rbComplete.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_radio_unselected,0,0,0)
            deleteBinding.rbReject.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_radio_selected,0,0,0)
        }

        deleteBinding.btnAccept.setOnClickListener {
            if(!deleteBinding.etReason.text.isNullOrEmpty()){
                val map = HashMap<String, Any>()
                map["seller_id"] = intent.getStringExtra("id") ?: ""
                map["reject_type"] = type
                map["reject_reason"] = deleteBinding.etReason.text.toString()
                viewModel.hitRejectSeller(
                    SharedPreference.get(this).accessToken,
                    map
                )
            }
            else{
                showToast(getString(R.string.please_enter_reason))
            }

        }

        deleteBinding.closeBtn.setOnClickListener {
            deleteBottomDialog?.dismiss()
        }


        deleteBottomDialog?.show()
    }


    override fun observer() {

        viewModel.getAcceptSeller.observe(this){
            showToast(getString(R.string.seller_accept_successfully))
            finish()
        }


        viewModel.getRejectSeller.observe(this){
            showToast(getString(R.string.seller_reject_successfully))
            deleteBottomDialog?.dismiss()
            finish()
        }

        viewModel.getSellerDetailData.observe(this){
            binding.tvName.text = it.data.firstName+" "+it?.data?.lastName
            binding.tvStoreName.text = it.data.storeInfo.storeName
            binding.tvLocation.text = if (Constants.language=="ar") it?.data?.storeInfo?.countryInfo?.nameAr?:it?.data?.storeInfo?.countryInfo?.name else it?.data?.storeInfo?.countryInfo?.name
            binding.tvCall.text = it.data.countryCode + "-" + it.data.mobile
            binding.tvEmail.text = it.data.email
            binding.tvAddress.text = it.data.storeInfo.storeAddress
            binding.tvPhone.text = it.data.storeInfo.countryCode + "-" + it.data.storeInfo.storePhone
            binding.tvMail.text = it.data.storeInfo.storeMailId
            binding.tvBio.text = it.data.storeInfo.bio
            binding.tvAccountType.text = when(it?.data?.sellerBankDetails?.accountType){
                "0" -> getString(R.string.personal_account)
                else -> getString(R.string.business_account)
            }
            binding.tvIban.text = it?.data?.sellerBankDetails?.iban
            binding.tvswiftbic.text = it?.data?.sellerBankDetails?.swift
            binding.tvBankName.text = it?.data?.sellerBankDetails?.bankName
            binding.tvAccountHolderName.text = it?.data?.sellerBankDetails?.accountHolderName
            binding.tvJoinedOn.text = "${getString(R.string.joined_at)} ${formatUtcDateToDdMmYyyy(it?.data?.updatedAt.toString())}"

            Glide.with(this).load(it?.data?.storeInfo?.storeImage).placeholder(R.drawable.ic_user).transform(CircleCrop()).into(binding.ivStoreImage)
            Glide.with(this).load(it?.data?.profilePic).transform(CircleCrop()).into(binding.ivProfileImage)
            Glide.with(this).load(it?.data?.ownerDocument?.getOrNull(0)?.documentPath).placeholder(R.drawable.ic_placeholder).into(binding.doc1)
            Glide.with(this).load(it?.data?.ownerDocument?.getOrNull(1)?.documentPath).placeholder(R.drawable.ic_placeholder).into(binding.doc2)
            Glide.with(this).load(it?.data?.storeDocument?.getOrNull(0)?.documentPath).placeholder(R.drawable.ic_placeholder).into(binding.doc3)
            Glide.with(this).load(it?.data?.storeDocument?.getOrNull(1)?.documentPath).placeholder(R.drawable.ic_placeholder).into(binding.doc4)
            Glide.with(this).load(it?.data?.sellerBankDetails?.uploadProof).placeholder(R.drawable.ic_placeholder).into(binding.imAccountProof)
        }
    }
}