package com.desynatadmin.ui.m2

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.view.isInvisible
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
import com.desynatadmin.ui.home.storyManagement.ViewStoryActivity
import com.desynatadmin.ui.home.storyManagement.response.GetStoryResponse
import com.desynatadmin.utils.CommonMethod
import com.desynatadmin.utils.Constants
import com.desynatadmin.utils.ProgressDialogUtils
import com.desynatadmin.utils.SharedPreference
import com.desynatadmin.utils.formatUtcDateToLocal
import com.desynatadmin.utils.showToast
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ApprovedDetailsActivity : BaseActivity() {
    private lateinit var binding: ActivityApprovedDetailsBinding
    private val viewModel : HomeViewModel by viewModels()
    private var sellerDetailData: SellerDetailResponse.Data?=null
    var highlightList = ArrayList<GetStoryResponse.Data.Data>()
    var reelList = ArrayList<GetStoryResponse.Data.Data>()
    var storyList = ArrayList<GetStoryResponse.Data.Data>()
    var highlightsAdapter : HighlightsAdapter?=null
    var reelAdapter : ReelAdapter?=null
    var reelPos = -1

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

    override fun onResume() {
        getSellerDetail()
        super.onResume()
    }

    override fun initView() {

    }

    fun getSellerDetail(){
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
        highlightsAdapter = HighlightsAdapter(this, highlightList,object : HighlightsAdapter.OnItemClick{
            override fun clickOnItem(position: Int) {
                            val bundle = Bundle().apply {
                                putString("from", "highlight")
                                putString("storyData", Gson().toJson(highlightList[position]))
                            }
                val intent = Intent(this@ApprovedDetailsActivity, ViewReelActivity::class.java)
                intent.putExtras(bundle)
                startActivity(intent)
            }

        })

        binding?.highlightsRecyclerView?.adapter = highlightsAdapter


        reelAdapter = ReelAdapter(this,reelList,object : ReelAdapter.OnItemClick{
            override fun clickOnItem(position: Int,type:Int) {
                when(type){
                    0->{
                        val bundle = Bundle().apply {
                            putString("from", "reel")
                            putString("storyData", Gson().toJson(reelList[position]))
                        }
                        val intent = Intent(this@ApprovedDetailsActivity, ViewReelActivity::class.java)
                        intent.putExtras(bundle)
                        startActivity(intent)
                    }
                    1->{
                       reelPos = position
                        deleteReel()
                    }
                }

            }

        })
        binding.reelRecyclerView.adapter = reelAdapter

        binding?.apply {
            doc1.setOnClickListener {
                imagePreviewDialog(sellerDetailData?.ownerDocument?.get(0)?.documentPath?:"")
            }

            doc2.setOnClickListener {
                imagePreviewDialog(sellerDetailData?.ownerDocument?.get(1)?.documentPath?:"")
            }

            doc3.setOnClickListener {
                imagePreviewDialog(sellerDetailData?.storeDocument?.get(0)?.documentPath?:"")
            }

            doc4.setOnClickListener {
                imagePreviewDialog(sellerDetailData?.storeDocument?.get(1)?.documentPath?:"")
            }


            ivProfileImage.setOnClickListener {
                if (storyList.isNotEmpty()){
                    val bundle = Bundle().apply {
                        putString("from", "admin")
                        putString("storyData", Gson().toJson(storyList))
                    }
                    val intent = Intent(this@ApprovedDetailsActivity, ViewStoryActivity::class.java)
                    intent.putExtras(bundle)
                    startActivity(intent)
                }
            }
        }

    }


    override fun observer() {

        viewModel.getUpdateSellerStatus.observe(this){
            if(it.data.is_block == 0){
                showToast(getString(R.string.seller_unblock_successfully))
                binding.ivToggle.setImageResource(R.drawable.switch_off)
            }
            else{
                showToast(getString(R.string.seller_block_successfully))
                binding.ivToggle.setImageResource(R.drawable.switch_on)
            }
        }

        viewModel.getSellerDetailData.observe(this){
            ProgressDialogUtils.getInstance().hideProgress()

            sellerDetailData = it.data
            if(it.data.isBlock == 0){
                binding.ivToggle.setImageResource(R.drawable.switch_off)
            }
            else{
                binding.ivToggle.setImageResource(R.drawable.switch_on)
            }
            Glide.with(this).load(it?.data?.profilePic).placeholder(R.drawable.ic_user).transform(CircleCrop()).into(binding.ivProfileImage)
            Glide.with(this).load(it?.data?.storeInfo?.storeImage).placeholder(R.drawable.ic_placeholder).into(binding.imPuma)
            binding.tvName.text = it?.data?.firstName+" "+it?.data?.lastName
            binding.tvEmail.text = it?.data?.email
            binding.txt.text = it?.data?.storeInfo?.bio
            binding.tvLocation.text = if (Constants.language=="ar") it?.data?.storeInfo?.countryInfo?.nameAr?:it?.data?.storeInfo?.countryInfo?.name else it?.data?.storeInfo?.countryInfo?.name
            binding.tvCall.text  = it?.data?.countryCode + "-" + it?.data?.mobile
            binding.tvAddress.text  = "${it?.data?.storeInfo?.storeName}\n${it?.data?.storeInfo?.storeAddress}"
           // binding.tvProPlan.text  = it.data.subcription_plan ?: ""
            Glide.with(this).load(it?.data?.ownerDocument?.getOrNull(0)?.documentPath).placeholder(R.drawable.ic_placeholder).into(binding.doc1)
            Glide.with(this).load(it?.data?.ownerDocument?.getOrNull(1)?.documentPath).placeholder(R.drawable.ic_placeholder).into(binding.doc2)
            Glide.with(this).load(it?.data?.storeDocument?.getOrNull(0)?.documentPath).placeholder(R.drawable.ic_placeholder).into(binding.doc3)
            Glide.with(this).load(it?.data?.storeDocument?.getOrNull(1)?.documentPath).placeholder(R.drawable.ic_placeholder).into(binding.doc4)
            binding.tvAccountType.text = when(it.data?.sellerBankDetails?.accountType){
                "0" -> getString(R.string.personal_account)
                else -> getString(R.string.business_account)
            }
            binding.cl1.isVisible = true
            binding.clOrder.isVisible = true
            if(it?.data?.subcriptionPlan.isNullOrEmpty()){
                binding.tvPurchasePlan.text = getString(R.string.no_active_plans)
                binding.purchasePlanLayout.isVisible = false
            }
            else{
                binding.tvPurchasePlan.text = getString(R.string.purchased_plan)
                binding.purchasePlanLayout.isVisible = true
                binding.tvStatus.text = if(it.data?.subcriptionPlan?.get(0)?.planInfo?.status == 0) getString(R.string.active) else getString(R.string.expired)
                binding.tvProPlanLabel.text =if (Constants.language=="ar") it.data?.subcriptionPlan?.get(0)?.planInfo?.planNameArabic  else it.data?.subcriptionPlan?.get(0)?.planInfo?.planNameEnglish ?: ""
                if(it.data?.subcriptionPlan?.get(0)?.planInfo?.durationType == 1){
                    binding.tvProPlan.text = it.data?.subcriptionPlan?.get(0)?.planInfo?.duration + getString(
                        R.string.months
                    )
                }
                else{
                    binding.tvProPlan.text = it.data?.subcriptionPlan?.get(0)?.planInfo?.duration + getString(
                        R.string.years
                    )
                }
                binding.tvPurchasedOn.text = formatUtcDateToLocal( it.data?.subcriptionPlan?.get(0)?.planInfo?.createdAt ?: "")
                binding.tvTotalAmount.text = "${it.data?.subcriptionPlan?.get(0)?.planInfo?.price?:0.0} KD"

            }

            storyList.clear()
            it?.data?.stories?.let { c -> storyList.addAll(c) }

            if (storyList.size!=0){
                binding?.ivStoryHighlight?.isVisible = true
            }
            else{
                binding?.ivStoryHighlight?.isInvisible = true
            }

            highlightList.clear()
            it?.data?.highlights?.let { c -> highlightList.addAll(c) }
            highlightsAdapter?.notifyDataSetChanged()

            reelList.clear()
            it?.data?.reels?.let { c -> reelList.addAll(c) }
            reelAdapter?.notifyDataSetChanged()

            binding?.tvNoHighlightsFound?.isVisible = highlightList.isEmpty()
            binding?.tvNoReelFound?.isVisible = reelList.isEmpty()


            binding?.rating?.rating = it?.data?.averageStoreRating?.toFloatOrNull()?:0f
            binding?.tvRating?.text = "(${it?.data?.averageStoreRating?:0})"

            binding.tvCompletedOrderValue.text =
                (it?.data?.totalCompletedOrders ?: 0).toString()

            binding.tvCancelledOrderValue.text =
                (it?.data?.totalCancelledOrders ?: 0).toString()

            binding.tvMyEarningValue.text =
                (it?.data?.totalEarning ?: 0).toString()
            binding.tvCommission.text = it?.data?.affilateCommission
            binding.tvIban.text = it?.data?.sellerBankDetails?.iban
            binding.tvswiftbic.text = it?.data?.sellerBankDetails?.swift
            binding.tvBankName.text = it?.data?.sellerBankDetails?.bankName
            binding.tvAccountHolderName.text = it.data?.sellerBankDetails?.accountHolderName
            Glide.with(this).load(it?.data?.sellerBankDetails?.uploadProof).placeholder(R.drawable.ic_placeholder).into(binding.imAccountProof)
        }

        viewModel.deleteSellerReelData.observe(this){
            reelList.removeAt(reelPos)
            reelAdapter?.notifyDataSetChanged()
            reelPos = -1
            CommonMethod.showSuccessDialog(this,getString(R.string.reel_deleted_successfully))

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

    fun deleteReel(){
        viewModel.deleteSellerReel(reelList[reelPos].id.toString())
    }
}