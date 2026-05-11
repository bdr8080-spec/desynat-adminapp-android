package com.desynatadmin.ui.home.promoCodeManagement

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.desynatadmin.R
import com.desynatadmin.base.BaseFragment
import com.desynatadmin.data.view_model.HomeViewModel
import com.desynatadmin.databinding.FragmentPromoCodeDetailBinding
import com.desynatadmin.ui.home.brandManagement.response.GetBrandResponse
import com.desynatadmin.ui.home.giftCardManagement.response.GetAllGiftCardResponse
import com.desynatadmin.ui.home.promoCodeManagement.response.GetPromoCodeResponse
import com.desynatadmin.ui.home.subscriptionPlan.response.GetCountryResponse
import com.desynatadmin.utils.CommonMethod
import com.desynatadmin.utils.Constants
import com.desynatadmin.utils.ProgressDialogUtils
import com.desynatadmin.utils.SharedPreference
import com.desynatadmin.utils.showToast
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PromoCodeDetailFragment : BaseFragment() {
    private var binding: FragmentPromoCodeDetailBinding?=null
    private val viewModel: HomeViewModel by viewModels()
    private var isObserver = true
    var promoCodeData: GetPromoCodeResponse.Data.Data?=null
    private var countryList = ArrayList<GetCountryResponse.Data>()
    var actionAccess : Boolean = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPromoCodeDetailBinding.inflate(layoutInflater,container,false)
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
        countryList.clear()
        SharedPreference.get(requireActivity()).getCountryList()?.let { countryList.addAll(it) }
        findNavController().currentBackStackEntry
            ?.savedStateHandle
            ?.getLiveData<String>("updatedPromoCode")
            ?.observe(viewLifecycleOwner) { json ->
                json?.let {
                    promoCodeData = Gson().fromJson(json, GetPromoCodeResponse.Data.Data::class.java)
                    setData()
                    updateStatus()
                }
            }

        ProgressDialogUtils.getInstance().hideProgress()
        val itemJson = arguments?.getString("promoCodeData")
        if (!itemJson.isNullOrEmpty()) {
            promoCodeData = Gson().fromJson(itemJson, GetPromoCodeResponse.Data.Data::class.java)
            setData()
            updateStatus()
        }
        binding?.apply {
            toolbar.tvToolbar.text = getString(R.string.detail_view)

            val userInfo = SharedPreference.get(requireActivity()).getUser()
            if (userInfo?.type=="2"){
                val subAdminInfo = SharedPreference.get(requireActivity()).getSubAdmin()
                val module = subAdminInfo?.permissionDetails
                    ?.firstOrNull {
                        it.moduleDetails.name.equals("promo_code_management", true)
                    }

                actionAccess = module?.canAction == 1

                btnDelete.isVisible = actionAccess
                btnEdit.isVisible = actionAccess
                tvSwitch.isClickable = actionAccess
            }
        }
    }

    override fun initControl() {
        binding?.apply {
            toolbar.ivBack.setOnClickListener {
                findNavController().popBackStack()
            }

            tvSwitch.setOnClickListener {
                if (actionAccess) {
                    updatePromoCodeStatus()
                }
            }

            btnDelete.setOnClickListener {
                deletePromoCode()
            }

            btnEdit.setOnClickListener {
                val bundle = Bundle().apply {
                    putString("promoCodeData", Gson().toJson(promoCodeData))
                }
                findNavController().navigate(R.id.action_promoCodeDetail_to_addPromoCodeFragment,bundle)
            }

        }
    }

    private fun deletePromoCode(){
        val map = HashMap<String,Any>()
        map["promocode_id"]=promoCodeData?.id.toString()
        viewModel.deletePromoCode(map)
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

    private fun updatePromoCodeStatus(){
        val map = HashMap<String,Any>()
        map["promocode_id"]=promoCodeData?.id.toString()
        viewModel.updatePromoCodeStatus(map)
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

    private fun updateStatus(){
        binding?.apply {
            if(promoCodeData?.status==1){
                tvSwitch.text = getString(R.string.active)
                tvSwitch.setTextColor(Color.parseColor("#1A900D"))
                tvSwitch.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.switch_on, 0)
            }
            else{
                tvSwitch.text = getString(R.string.inactive)
                tvSwitch.setTextColor(Color.parseColor("#666666"))
                tvSwitch.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.switch_off, 0)

            }

        }
    }

    private fun setData(){

        binding?.apply {
            tvFeature.text = "${getString(R.string.flat)} ${promoCodeData?.discountPercent}% ${getString(R.string.off_on_minimum_order)} ${promoCodeData?.minOrderValue} KD"
            tvPromoCodeName.text = if(Constants.language=="ar") promoCodeData?.nameArabic else promoCodeData?.nameEnglish
            tvTerms.text = if(Constants.language=="ar") promoCodeData?.descriptionArabic else promoCodeData?.descriptionEnglish
            tvCountry.text =  if (Constants.language=="ar") countryList.firstOrNull { it.id.toString() == promoCodeData?.countryId.toString() }?.nameAr else countryList.firstOrNull { it.id.toString() == promoCodeData?.countryId.toString() }?.name
            tvApplicable.text = if(promoCodeData?.applicableFor==1) getString(R.string.first_order) else getString(
                R.string.every_order
            )
            tvStartDate.text = CommonMethod.formatDate(promoCodeData?.startDate.toString(),"yyyy-MM-dd","dd/MM/yyyy")
            tvEndDate.text = CommonMethod.formatDate(promoCodeData?.endDate.toString(),"yyyy-MM-dd","dd/MM/yyyy")

            if(promoCodeData?.status==1){
                tvSwitch.text = getString(R.string.active)
                tvSwitch.setTextColor(Color.parseColor("#1A900D"))
                tvSwitch.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.switch_on, 0)
            }
            else{
                tvSwitch.text = getString(R.string.inactive)
                tvSwitch.setTextColor(Color.parseColor("#666666"))
                tvSwitch.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.switch_off, 0)

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

        viewModel.updatePromoCodeStatusResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                promoCodeData = response.data
                showToast(response.message)
                updateStatus()
                isObserver = false

            }
        }

        viewModel.deletePromoCodeResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                CommonMethod.showSuccessDialog(requireContext(), "Promocode deleted successfully"){
                    findNavController().popBackStack()
                }
                isObserver = false
            }
        }
    }
}