package com.desynatadmin.ui.home.bannerRequestManagement

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.desynatadmin.R
import com.desynatadmin.base.BaseFragment
import com.desynatadmin.data.view_model.HomeViewModel
import com.desynatadmin.databinding.FragmentSellerStoryDetailBinding
import com.desynatadmin.databinding.RejectBannerReasonDialogBinding
import com.desynatadmin.ui.home.bannerRequestManagement.response.GetSellerStoryResponse
import com.desynatadmin.utils.CommonMethod
import com.desynatadmin.utils.Constants
import com.desynatadmin.utils.ProgressDialogUtils
import com.desynatadmin.utils.SharedPreference
import com.desynatadmin.utils.showToast
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class SellerStoryDetailFragment : BaseFragment() {
    private var binding: FragmentSellerStoryDetailBinding?=null
    private val viewModel: HomeViewModel by viewModels()
    private var isObserver = true


    var bannerData: GetSellerStoryResponse.Data.DataX?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSellerStoryDetailBinding.inflate(layoutInflater,container,false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initControl()
        observe()
    }

    override fun initView() {
        ProgressDialogUtils.getInstance().hideProgress()

        val itemJson = arguments?.getString("StoryData")
        if (!itemJson.isNullOrEmpty()) {
            bannerData = Gson().fromJson(itemJson, GetSellerStoryResponse.Data.DataX::class.java)
            setData()
        }

        Log.e("TAG", "detalData:$bannerData ")
        binding?.apply {

            val userInfo = SharedPreference.get(requireActivity()).getUser()
            if (userInfo?.type=="2"){
                val subAdminInfo = SharedPreference.get(requireActivity()).getSubAdmin()
                val module = subAdminInfo?.permissionDetails
                    ?.firstOrNull {
                        it.moduleDetails.name.equals("banner_request_management", true)
                    }

                val actionAccess = module?.canAction == 1

                btnAccept.isVisible = actionAccess
                btnReject.isVisible = actionAccess
            }

            when(bannerData?.adminVerify){
                "0"->{
                    btnAccept.isVisible = true
                    btnReject.isVisible = true
                    tvReason.isVisible = false
                    tvReasonValue.isVisible = false
                    tvStatus.text = getString(R.string.new_text)
                    tvStatus.setTextColor(Color.parseColor("#68CA44"))
                }
                "1"->{
                    btnAccept.isVisible = false
                    btnReject.isVisible = false
                    tvReason.isVisible = false
                    tvReasonValue.isVisible = false
                    if (bannerData?.paymentStatus=="0"){
                        tvStatus.text = getString(R.string.upcoming)
                    }
                    else{
                        tvStatus.text = getString(R.string.active)
                    }

                    tvStatus.setTextColor(Color.parseColor("#68CA44"))
                }
                "2"->{
                    btnAccept.isVisible = false
                    btnReject.isVisible = false
                    tvReason.isVisible = true
                    tvReasonValue.isVisible = true
                    tvStatus.text =  getString(R.string.rejected)
                    tvStatus.setTextColor(Color.parseColor("#D53B36"))
                }
            }


        }
    }


    private fun setData(rejectReason: String?=null) {
        binding?.apply {
            updateStatus()

            Glide.with(requireActivity()).load(bannerData?.file).placeholder(R.drawable.ic_placeholder).into(ivBanner)
            tvName.text = bannerData?.sellerDetails?.firstName+" "+bannerData?.sellerDetails?.lastName
            val countryInfo = bannerData?.sellerDetails?.storeInfo?.countryInfo
            tvCountry.text = if (Constants.language == "ar") countryInfo?.nameAr ?: "" else countryInfo?.name ?: ""
//            tvCountry.text = bannerData?.sellerDetails?.storeInfo?.country
            tvRequestId.text = "#${bannerData?.id}"
            tvRequestOnValue.text = CommonMethod.customizeChangeDateFormat(bannerData?.createdAt.toString(), outputFormat = "dd/MM/yyyy '${getString(R.string.at)}' hh:mm a")
//            when(bannerData?.bannerPosition){
//                "1" ->{
//                    tvBannerPositionValue.text = getString(R.string.home_top)
//                }
//                "2"->{
//                    tvBannerPositionValue.text = getString(R.string.home_bottom)
//                }
//                "3"->{
//                    tvBannerPositionValue.text =  getString(R.string.sub_category)
//                    if (bannerData?.categoryDetails!=null){
//                        tvSubCategoryNameValue.isVisible = true
//                        tvSubCategoryName.isVisible = true
//                        tvSubCategoryNameValue.text = bannerData?.subcategoryDetails?.nameEnglish
//                    }
//                    else{
//                        tvSubCategoryNameValue.isVisible = false
//                        tvSubCategoryName.isVisible = false
//                    }
//                }
//            }

            when(bannerData?.adminVerify){
                "0"->{
                    tvStatus.text =  getString(R.string.new_text)
                    tvStatus.setTextColor(Color.parseColor("#68CA44"))
                    btnReject.isVisible = true
                    btnAccept.isVisible = true
                    tvReason.isVisible = false
                    tvReasonValue.isVisible = false
                }
                "1"->{
                    ivSwitch.isVisible = false
                    btnReject.isVisible = false
                    btnAccept.isVisible = false
                    tvReason.isVisible = false
                    tvReasonValue.isVisible = false
                    tvAmountPaid.isVisible = true
                    tvAmountPaidStatus.isVisible = true
                    tvStatus.setTextColor(Color.parseColor("#68CA44"))
                    if (bannerData?.paymentStatus=="1"){
                        tvTransactionId.isVisible = true
                        tvTransactionIdValue.isVisible = true
                        tvStatus.text =  getString(R.string.active)
                        tvAmountPaidStatus.text = getString(R.string.yes)
                        tvAmountPaidStatus.setTextColor(Color.parseColor("#68CA44"))
                        tvTransactionIdValue.text = bannerData?.transactionId
                    }
                    else{
                        tvStatus.text =  getString(R.string.upcoming)
                        tvAmountPaidStatus.text = getString(R.string.no)
                        tvAmountPaidStatus.setTextColor(Color.parseColor("#D53B36"))
                    }
                }
                "2"->{
                    btnReject.isVisible = false
                    btnAccept.isVisible = false
                    tvStatus.text =  getString(R.string.rejected)
                    tvStatus.setTextColor(Color.parseColor("#D53B36"))
                    tvReason.isVisible = true
                    tvReasonValue.isVisible = true
                    Log.e("TAG", "setData: ${bannerData?.sellerDetails?.rejectReason}")
                    tvReasonValue.text =
                        if (bannerData?.rejectReason.isNullOrBlank()) {
                            rejectReason
                        } else {
                            bannerData?.rejectReason
                        }
                }
            }

            tvTotalDaysValue.text = bannerData?.totalDays
            tvFromDate.text =  CommonMethod.customizeChangeDateFormat(bannerData?.startDate.toString(), inputFormat = "yyyy-MM-dd","dd/MM/yyyy")
            tvToDate.text =  CommonMethod.customizeChangeDateFormat(bannerData?.endDate.toString(), inputFormat = "yyyy-MM-dd","dd/MM/yyyy")
            tvTotalPriceValue.text = "${CommonMethod.formatAmount(bannerData?.amount.toString())} KD"
        }
    }

    override fun initControl() {
        binding?.apply {
            ivBack.setOnClickListener {
                findNavController().popBackStack()
            }

            btnReject.setOnClickListener {
                rejectReasonDialog()
            }

            btnAccept.setOnClickListener {
                acceptRequest()
            }

            ivBanner.setOnClickListener {
//                CommonMethod.imagePreviewDialog(requireActivity(),bannerData?.uploadBanner.toString()){
//
//                }
            }

            ivSwitch.setOnClickListener {
                updateStatusApi()
            }
        }
    }

    private fun updateStatus(){
        binding?.apply {
            if(bannerData?.status==1){
                ivSwitch.setImageResource(R.drawable.switch_on)
            }
            else{
                ivSwitch.setImageResource(R.drawable.switch_off)
            }
        }
    }

    private fun updateStatusApi(){
        val map = HashMap<String,Any>()
        map["id"]=bannerData?.id.toString()
        viewModel.updateBannerStatus(map)
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

    private fun acceptRequest(){
        val map = HashMap<String,Any>()
        map["story_id"] = bannerData?.id.toString()
        viewModel.approveStoryRequest(map)
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

    private fun rejectRequest(reason:String){
        val map = HashMap<String,Any>()
        map["story_id"] = bannerData?.id.toString()
        map["reason"] = reason
        viewModel.rejectStoryRequest(map)
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }


    override fun observe() {
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                isObserver = false
            }
        }

        viewModel.approveBannerRequestResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                CommonMethod.showSuccessDialog(
                    requireActivity(),
                    getString(R.string.story_request_accepted_successfully)
                ) {
                    bannerData?.adminVerify = "1"
                    setData()
                }
            }
        }

        viewModel.rejectStoryRequestResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                CommonMethod.showSuccessDialog(requireActivity(),getString(R.string.story_request_rejected_successfully)){
                    bannerData?.adminVerify = "2"
////                    bannerData?.rejectReason = response.data?.rejectReason
                    binding?.tvReasonValue?.text = response.data?.rejectReason
                    setData(response.data?.rejectReason)
                }
            }
        }

//
//        viewModel.updateBannerStatusResult.observe(viewLifecycleOwner) { response ->
//            if (isObserver) {
//                ProgressDialogUtils.getInstance().hideProgress()
//                bannerData = response.data
//                if (bannerData?.status==1){
//                    showToast(getString(R.string.banner_unblock_successfully))
//                }
//                else{
//                    showToast(getString(R.string.banner_block_successfully))
//                }
//                updateStatus()
//                isObserver = false
//
//            }
//        }

    }

    private fun rejectReasonDialog() {
        val dialog = Dialog(requireActivity(), R.style.CustomDialog)
        val dialogBinding = RejectBannerReasonDialogBinding.inflate(LayoutInflater.from(context))
        dialogBinding.root.let { dialog.setContentView(it) }

        val window: Window = dialog.window!!
        window.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        window.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(false)

        val params: WindowManager.LayoutParams = window.attributes
        params.gravity = Gravity.BOTTOM
        window.attributes = params

        dialogBinding.apply {


            tvToolbar.setOnClickListener {
                dialog.dismiss()
            }

            btnSubmit.setOnClickListener {
                if(etReason.text?.trim().toString().isEmpty()) {
                    showToast(getString(R.string.please_enter_reason))
                }
                else{
                    rejectRequest(etReason.text?.trim().toString())
                    dialog.dismiss()
                }
            }

        }

        dialog.show()
    }

}