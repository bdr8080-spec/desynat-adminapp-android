package com.desynatadmin.ui.home.fullScreenBannerManagement

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.desynatadmin.R
import com.desynatadmin.base.BaseFragment
import com.desynatadmin.data.view_model.HomeViewModel
import com.desynatadmin.databinding.FragmentFullScreenBannerDetailBinding
import com.desynatadmin.ui.home.fullScreenBannerManagement.response.GetFullScreenBannerResponse
import com.desynatadmin.ui.home.subscriptionPlan.response.GetCountryResponse
import com.desynatadmin.utils.CommonMethod
import com.desynatadmin.utils.Constants
import com.desynatadmin.utils.ProgressDialogUtils
import com.desynatadmin.utils.SharedPreference
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class FullScreenBannerDetailFragment : BaseFragment() {
    private var binding: FragmentFullScreenBannerDetailBinding?=null
    private val viewModel: HomeViewModel by viewModels()
    private var isObserver = true
    var bannerData: GetFullScreenBannerResponse.Data.Data?=null
    private var countryList = ArrayList<GetCountryResponse.Data>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFullScreenBannerDetailBinding.inflate(layoutInflater,container,false)
        return binding?.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initControl()
        observe()
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun initView() {
        ProgressDialogUtils.getInstance().hideProgress()
        countryList.clear()
        SharedPreference.get(requireActivity()).getUserCountryList()?.let { countryList.addAll(it) }
        findNavController().currentBackStackEntry
            ?.savedStateHandle
            ?.getLiveData<String>("updatedBannerData")
            ?.observe(viewLifecycleOwner) { json ->
                json?.let {
                    bannerData = Gson().fromJson(json, GetFullScreenBannerResponse.Data.Data::class.java)
                    setData()
                }
            }

        ProgressDialogUtils.getInstance().hideProgress()
        val itemJson = arguments?.getString("bannerData")
        if (!itemJson.isNullOrEmpty()) {
            bannerData = Gson().fromJson(itemJson, GetFullScreenBannerResponse.Data.Data::class.java)
            setData()
        }
        binding?.apply {
            toolbar.tvToolbar.text = getString(R.string.detail)

            val userInfo = SharedPreference.get(requireActivity()).getUser()
            if (userInfo?.type=="2"){
                val subAdminInfo = SharedPreference.get(requireActivity()).getSubAdmin()
                val module = subAdminInfo?.permissionDetails
                    ?.firstOrNull {
                        it.moduleDetails.name.equals("full_screen_banner_management", true)
                    }

                val actionAccess = module?.canAction == 1
                btnEdit.isVisible = actionAccess
                btnDelete.isVisible = actionAccess

            }
        }
    }

    override fun initControl() {
        binding?.apply {
            toolbar.ivBack.setOnClickListener {
                findNavController().popBackStack()
            }

            ivBanner.setOnClickListener {
                CommonMethod.imagePreviewDialog(requireActivity(),bannerData?.image?:"")
            }

            btnDelete.setOnClickListener {
                deleteFullScreenBanner()
            }

            btnEdit.setOnClickListener {
                val bundle = Bundle().apply {
                    putString("bannerData", Gson().toJson(bannerData))
                }
                findNavController().navigate(R.id.action_fullScreenBannerDetail_to_addFullScreenBannerFragment,bundle)
            }

        }
    }

    private fun deleteFullScreenBanner(){
        val map = HashMap<String,Any>()
        map["banner_id"]=bannerData?.id.toString()
        viewModel.deleteFullScreenBanner(map)
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setData(){

        binding?.apply {

            Glide.with(requireActivity()).load(bannerData?.image).placeholder(R.drawable.ic_placeholder).into(ivBanner)

            tvTitle.text = bannerData?.title

            tvCountry.text =  if (Constants.language=="ar") countryList.firstOrNull { it.id.toString() == bannerData?.countryId.toString() }?.nameAr else countryList.firstOrNull { it.id.toString() == bannerData?.countryId.toString() }?.name
            tvStartDate.text = CommonMethod.formatDate(bannerData?.startDate.toString(),"yyyy-MM-dd","dd/MM/yyyy")
            tvEndDate.text = CommonMethod.formatDate(bannerData?.endDate.toString(),"yyyy-MM-dd","dd/MM/yyyy")
            tvTypeValue.text = if (bannerData?.type=="2") getString(R.string.web_banner) else getString(
                R.string.app_banner
            )
            when(getBannerStatus(bannerData?.startDate.toString(),bannerData?.endDate.toString())){
                "Upcoming"->{
                    tvStatus.text = getString(R.string.upcoming)
                    tvStatus.setTextColor(Color.parseColor("#1A900D"))
                }
                "Active" ->{
                    tvStatus.text = getString(R.string.active)
                    tvStatus.setTextColor(Color.parseColor("#1A900D"))
                }
                "Expired"->{
                    tvStatus.text = getString(R.string.expired)
                    tvStatus.setTextColor(Color.parseColor("#D53B36"))
                }
            }
        }

    }

    override fun observe() {
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()

                isObserver = false
            }
        }

        viewModel.deleteFullScreenBannerResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                CommonMethod.showSuccessDialog(requireContext(),
                    getString(R.string.banner_deleted_successfully)){
                    findNavController().popBackStack()
                }
                isObserver = false
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getBannerStatus(startDate: String, endDate: String): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        val start = LocalDate.parse(startDate, formatter)
        val end = LocalDate.parse(endDate, formatter)
        val today = LocalDate.now()

        return when {
            today.isBefore(start) -> getString(R.string.upcoming)
            (today.isEqual(start) || today.isAfter(start)) &&
                    (today.isEqual(end) || today.isBefore(end)) -> getString(R.string.active)
            else -> getString(R.string.expired)
        }
    }

}