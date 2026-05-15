package com.desynatadmin.ui.home.fullScreenBannerManagement

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.desynatadmin.R
import com.desynatadmin.base.BaseFragment
import com.desynatadmin.data.view_model.HomeViewModel
import com.desynatadmin.databinding.FragmentPromotionalBannerDetailBinding
import com.desynatadmin.ui.home.fullScreenBannerManagement.response.GetPromotionalBannerResponse
import com.desynatadmin.ui.home.subscriptionPlan.response.GetCountryResponse
import com.desynatadmin.utils.CommonMethod
import com.desynatadmin.utils.Constants
import com.desynatadmin.utils.ProgressDialogUtils
import com.desynatadmin.utils.SharedPreference
import com.desynatadmin.utils.showToast
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class PromotionalBannerDetailFragment : BaseFragment() {
    private var binding: FragmentPromotionalBannerDetailBinding? = null
    private val viewModel: HomeViewModel by viewModels()
    private var isObserver = true
    var bannerData: GetPromotionalBannerResponse.Data.Data? = null
    private var countryList = ArrayList<GetCountryResponse.Data>()
    var actionAccess: Boolean = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPromotionalBannerDetailBinding.inflate(layoutInflater, container, false)
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
            bannerData =
                Gson().fromJson(itemJson, GetPromotionalBannerResponse.Data.Data::class.java)
            setData()
        }

        findNavController().currentBackStackEntry
            ?.savedStateHandle
            ?.getLiveData<String>("updatedPromotionalBannerData")
            ?.observe(viewLifecycleOwner) { json ->
                json?.let {
                    bannerData =
                        Gson().fromJson(json, GetPromotionalBannerResponse.Data.Data::class.java)
                    setData()
                }
            }

        binding?.apply {
            val userInfo = SharedPreference.get(requireActivity()).getUser()
            if (userInfo?.type == "2") {
                val subAdminInfo = SharedPreference.get(requireActivity()).getSubAdmin()
                val module = subAdminInfo?.permissionDetails
                    ?.firstOrNull {
                        it.moduleDetails.name.equals("full_screen_banner_management", true)
                    }

                actionAccess = module?.canAction == 1
                btnEdit.isVisible = actionAccess
                btnDelete.isVisible = actionAccess
                ivSwitch.isClickable = actionAccess

            }
        }
    }

    private fun setData() {
        updateStatus()
        binding?.apply {
            /*SharedPreference.get(requireActivity()).getUserCountryList()?.let { countryList.addAll(it) }
            if(bannerData?.countryId!=null){
                tvCountry.text = countryList.firstOrNull { it.id == bannerData?.countryId }?.name

            }*/
            Glide.with(requireActivity()).load(bannerData?.uploadBanner)
                .placeholder(R.drawable.ic_placeholder).into(ivBanner)
            when (bannerData?.bannerPosition) {
                "1" -> {
                    tvBannerPositionValue.text = getString(R.string.home_top)
                }

                "2" -> {
                    tvBannerPositionValue.text = getString(R.string.home_bottom)
                }

                "3" -> {
                    tvBannerPositionValue.text = getString(R.string.category)
                    if (bannerData?.categoryDetails != null) {
                        tvCategoryNameValue.isVisible = true
                        tvCategoryName.isVisible = true
                        tvSubCategoryNameValue.isVisible = true
                        tvSubCategoryName.isVisible = true
                        tvSubCategoryNameValue.text =
                            if (Constants.language == "ar") bannerData?.subcategoryDetails?.nameArabic else bannerData?.subcategoryDetails?.nameEnglish
                        tvCategoryNameValue.text =
                            if (Constants.language == "ar") bannerData?.categoryDetails?.nameAr else bannerData?.categoryDetails?.name
                    } else {
                        tvCategoryNameValue.isVisible = false
                        tvCategoryName.isVisible = false
                        tvSubCategoryNameValue.isVisible = false
                        tvSubCategoryName.isVisible = false
                    }
                }
            }

            tvTotalDaysValue.text = bannerData?.totalDays
            tvFromDate.text = CommonMethod.customizeChangeDateFormat(
                bannerData?.startDate.toString(),
                inputFormat = "yyyy-MM-dd",
                "dd/MM/yyyy"
            )
            tvToDate.text = CommonMethod.customizeChangeDateFormat(
                bannerData?.endDate.toString(),
                inputFormat = "yyyy-MM-dd",
                "dd/MM/yyyy"
            )
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
                findNavController().navigate(
                    R.id.action_promotionalBannerDetail_to_addPromotionalBannerFragment,
                    bundle
                )
            }

            ivBanner.setOnClickListener {
                CommonMethod.imagePreviewDialog(
                    requireActivity(),
                    bannerData?.uploadBanner.toString()
                ) {

                }
            }

            ivSwitch.setOnClickListener {
                if (actionAccess) {
                    updateStatusApi()
                }
            }

        }
    }

    private fun deletePromotionalBanner() {
        val map = HashMap<String, Any>()
        map["banner_id"] = bannerData?.id.toString()
        viewModel.deletePromotionalBanner(map)
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

    private fun updateStatus() {
        binding?.apply {
            if (bannerData?.status == "1") {
                ivSwitch.setImageResource(R.drawable.switch_on)
            } else {
                ivSwitch.setImageResource(R.drawable.switch_off)
            }
        }
    }

    private fun updateStatusApi() {
        val map = HashMap<String, Any>()
        map["banner_id"] = bannerData?.id.toString()
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
                CommonMethod.showSuccessDialog(
                    requireContext(),
                    getString(R.string.banner_deleted_successfully)
                ) {
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