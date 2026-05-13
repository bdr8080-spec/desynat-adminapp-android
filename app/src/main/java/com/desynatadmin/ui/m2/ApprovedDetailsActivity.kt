package com.desynatadmin.ui.m2

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.view.isVisible
import coil.load
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.desynatadmin.R
import com.desynatadmin.adapter.HighlightsAdapter
import com.desynatadmin.adapter.ReelAdapter
import com.desynatadmin.base.BaseActivity
import com.desynatadmin.data.view_model.HomeViewModel
import com.desynatadmin.databinding.ActivityApprovedDetailsBinding
import com.desynatadmin.databinding.ImagePreviewDialogBinding
import com.desynatadmin.ui.home.brandManagement.response.SellerDetailResponse
import com.desynatadmin.utils.Constants
import com.desynatadmin.utils.ProgressDialogUtils
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
    private var sellerDetailData: SellerDetailResponse.Data?=null

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
        ProgressDialogUtils.getInstance().showProgress(this)
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
            binding?.btnOtherInfo?.background = null
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

        binding?.apply {
            doc1.setOnClickListener {
                imagePreviewDialog(sellerDetailData?.owner_document?.get(0)?.document_path?:"")
            }

            doc2.setOnClickListener {
                imagePreviewDialog(sellerDetailData?.owner_document?.get(1)?.document_path?:"")
            }

            doc3.setOnClickListener {
                imagePreviewDialog(sellerDetailData?.store_document?.get(0)?.document_path?:"")
            }

            doc4.setOnClickListener {
                imagePreviewDialog(sellerDetailData?.store_document?.get(1)?.document_path?:"")
            }
        }

    }


    override fun observer() {

        viewModel.getUpdateSellerStatus.observe(this){
            if(it.data.is_block == 0){
                showToast(getString(R.string.seller_unblock_successfully))
                binding.ivToggle.setImageResource(R.drawable.toggle_ic)
            }
            else{
                showToast(getString(R.string.seller_block_successfully))
                binding.ivToggle.setImageResource(R.drawable.toggle_off)
            }
        }

        viewModel.getSellerDetailData.observe(this){
            ProgressDialogUtils.getInstance().hideProgress()

            sellerDetailData = it.data
            if(it.data.is_block == 0){
                binding.ivToggle.setImageResource(R.drawable.toggle_ic)
            }
            else{
                binding.ivToggle.setImageResource(R.drawable.toggle_off)
            }
            Glide.with(this).load(it?.data?.profile_pic).placeholder(R.drawable.ic_user).transform(CircleCrop()).into(binding.ivProfileImage)
            Glide.with(this).load(it?.data?.store_info?.store_image).placeholder(R.drawable.ic_placeholder).into(binding.imPuma)
            binding.tvName.text = it?.data?.first_name+" "+it?.data?.last_name
            binding.tvEmail.text = it?.data?.email
            binding.txt.text = it?.data?.store_info?.bio
            binding.tvLocation.text = if (Constants.language=="ar") it?.data?.store_info?.country_info?.name_ar?:it?.data?.store_info?.country_info?.name else it?.data?.store_info?.country_info?.name
            binding.tvCall.text  = it?.data?.country_code + "-" + it?.data?.mobile
            binding.tvAddress.text  = "${it?.data?.store_info?.store_name}\n${it?.data?.store_info?.store_address}"
           // binding.tvProPlan.text  = it.data.subcription_plan ?: ""
            Glide.with(this).load(it?.data?.owner_document?.getOrNull(0)?.document_path).placeholder(R.drawable.ic_placeholder).into(binding.doc1)
            Glide.with(this).load(it?.data?.owner_document?.getOrNull(1)?.document_path).placeholder(R.drawable.ic_placeholder).into(binding.doc2)
            Glide.with(this).load(it?.data?.store_document?.getOrNull(0)?.document_path).placeholder(R.drawable.ic_placeholder).into(binding.doc3)
            Glide.with(this).load(it?.data?.store_document?.getOrNull(1)?.document_path).placeholder(R.drawable.ic_placeholder).into(binding.doc4)
            binding.tvAccountType.text = when(it.data?.seller_bank_details?.account_type){
                "0" -> getString(R.string.personal_account)
                else -> getString(R.string.business_account)
            }
            binding.cl1.isVisible = true
            binding.clOrder.isVisible = true
            if(it?.data?.subcription_plan.isNullOrEmpty()){
                binding.tvPurchasePlan.text = getString(R.string.no_active_plans)
                binding.purchasePlanLayout.isVisible = false
            }
            else{
                binding.tvPurchasePlan.text = getString(R.string.purchased_plan)
                binding.purchasePlanLayout.isVisible = true
                binding.tvStatus.text = if(it.data?.subcription_plan?.get(0)?.plan_info?.status == 0) getString(R.string.active) else getString(R.string.expired)
                binding.tvProPlanLabel.text =if (Constants.language=="ar") it.data?.subcription_plan?.get(0)?.plan_info?.plan_name_arabic  else it.data?.subcription_plan?.get(0)?.plan_info?.plan_name_english ?: ""
                if(it.data?.subcription_plan?.get(0)?.plan_info?.duration_type == 1){
                    binding.tvProPlan.text = it.data?.subcription_plan?.get(0)?.plan_info?.duration + getString(
                        R.string.months
                    )
                }
                else{
                    binding.tvProPlan.text = it.data?.subcription_plan?.get(0)?.plan_info?.duration + getString(
                        R.string.years
                    )
                }
                binding.tvPurchasedOn.text = formatUtcDateToLocal( it.data?.subcription_plan?.get(0)?.plan_info?.created_at ?: "")
                binding.tvTotalAmount.text = "${it.data?.subcription_plan?.get(0)?.plan_info?.price?.roundToInt()} KD"


            }
            binding.tvIban.text = it?.data?.seller_bank_details?.iban
            binding.tvswiftbic.text = it?.data?.seller_bank_details?.swift
            binding.tvBankName.text = it?.data?.seller_bank_details?.bank_name
            binding.tvAccountHolderName.text = it.data?.seller_bank_details?.account_holder_name
            Glide.with(this).load(it?.data?.seller_bank_details?.upload_proof).placeholder(R.drawable.ic_placeholder).into(binding.imAccountProof)
        }
    }

    private fun imagePreviewDialog(image:String) {
        val dialog = Dialog(this, R.style.CustomDialog)
        val dialogBinding = ImagePreviewDialogBinding.inflate(LayoutInflater.from(this))
        dialogBinding.root.let { dialog?.setContentView(it) }

        val window: Window = dialog?.window!!
        window.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        window.setBackgroundDrawableResource(android.R.color.transparent)
        dialog?.setCancelable(false)

        val params: WindowManager.LayoutParams = window.attributes
        params.gravity = Gravity.CENTER
        window.attributes = params

        dialogBinding.apply {

            tvToolbar.setOnClickListener{
                dialog?.dismiss()
            }

            phptoView.load(image) {
                crossfade(true)
                placeholder(R.drawable.ic_placeholder)
                error(R.drawable.ic_placeholder)
            }
        }

        dialog?.show()
    }

}