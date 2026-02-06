package com.desynatadmin.ui.home.fullScreenBannerManagement

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
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
import com.desynatadmin.databinding.FragmentBannerRequestDetailBinding
import com.desynatadmin.databinding.FragmentPromotionalBannerDetailBinding
import com.desynatadmin.databinding.RejectBannerReasonDialogBinding
import com.desynatadmin.ui.home.bannerRequestManagement.response.GetBannerRequestResponse
import com.desynatadmin.ui.home.fullScreenBannerManagement.response.GetFullScreenBannerResponse
import com.desynatadmin.ui.home.fullScreenBannerManagement.response.GetPromotionalBannerResponse
import com.desynatadmin.utils.CommonMethod
import com.desynatadmin.utils.ProgressDialogUtils
import com.desynatadmin.utils.showToast
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class PromotionalBannerDetailFragment : BaseFragment() {
    private var binding: FragmentPromotionalBannerDetailBinding?=null
    private val viewModel: HomeViewModel by viewModels()
    private var isObserver = true
    var bannerData: GetPromotionalBannerResponse.Data.Data?=null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPromotionalBannerDetailBinding.inflate(layoutInflater,container,false)
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

        val itemJson = arguments?.getString("bannerData")
        if (!itemJson.isNullOrEmpty()) {
            bannerData = Gson().fromJson(itemJson, GetPromotionalBannerResponse.Data.Data::class.java)
            setData()
        }

        findNavController().currentBackStackEntry
            ?.savedStateHandle
            ?.getLiveData<String>("updatedPromotionalBannerData")
            ?.observe(viewLifecycleOwner) { json ->
                json?.let {
                    bannerData = Gson().fromJson(json, GetPromotionalBannerResponse.Data.Data::class.java)
                    setData()
                }
            }

        binding?.apply {

        }
    }

    private fun setData() {
        updateStatus()
        binding?.apply {
            Glide.with(requireActivity()).load(bannerData?.uploadBanner).placeholder(R.drawable.ic_placeholder).into(ivBanner)
            when(bannerData?.bannerPosition){
                "1" ->{
                    tvBannerPositionValue.text = "Home Top"
                }
                "2"->{
                    tvBannerPositionValue.text = "Home Bottom"
                }
                "3"->{
                    tvBannerPositionValue.text = "Category"
                    if (bannerData?.categoryDetails!=null){
                        tvSubCategoryNameValue.isVisible = true
                        tvSubCategoryName.isVisible = true
                        tvSubCategoryNameValue.text = bannerData?.subcategoryDetails?.nameEnglish
                    }
                    else{
                        tvSubCategoryNameValue.isVisible = false
                        tvSubCategoryName.isVisible = false
                    }
                }
            }

            tvTotalDaysValue.text = bannerData?.totalDays
            tvFromDate.text =  CommonMethod.customizeChangeDateFormat(bannerData?.startDate.toString(), inputFormat = "yyyy-MM-dd","dd/MM/yyyy")
            tvToDate.text =  CommonMethod.customizeChangeDateFormat(bannerData?.endDate.toString(), inputFormat = "yyyy-MM-dd","dd/MM/yyyy")
        }
    }

    override fun initControl() {
        binding?.apply {
            ivBack.setOnClickListener {
                findNavController().popBackStack()
            }

            btnDelete.setOnClickListener {
                deletePromotionalBanner()
            }

            btnEdit.setOnClickListener {
                val bundle = Bundle().apply {
                    putString("bannerData", Gson().toJson(bannerData))
                }
                findNavController().navigate(R.id.action_promotionalBannerDetail_to_addPromotionalBannerFragment,bundle)
            }

            ivBanner.setOnClickListener {
                CommonMethod.imagePreviewDialog(requireActivity(),bannerData?.uploadBanner.toString()){

                }
            }

            ivSwitch.setOnClickListener {
                updateStatusApi()
            }

        }
    }

    private fun deletePromotionalBanner(){
        val map = HashMap<String,Any>()
        map["banner_id"]=bannerData?.id.toString()
        viewModel.deletePromotionalBanner(map)
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

    private fun updateStatus(){
        binding?.apply {
            if(bannerData?.status=="1"){
                ivSwitch.setImageResource(R.drawable.switch_on)
            }
            else{
                ivSwitch.setImageResource(R.drawable.switch_off)
            }
        }
    }

    private fun updateStatusApi(){
        val map = HashMap<String,Any>()
        map["banner_id"]=bannerData?.id.toString()
        viewModel.updatePromotionalBannerStatus(map)
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

        viewModel.deletePromotionalBannerResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                CommonMethod.showSuccessDialog(requireContext(), "Banner deleted successfully"){
                    findNavController().popBackStack()
                }
                isObserver = false
            }
        }

        viewModel.updatePromotionalBannerStatusResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                bannerData?.status = response.data.status
                showToast(response.message)
                updateStatus()
                isObserver = false
            }
        }

    }
}