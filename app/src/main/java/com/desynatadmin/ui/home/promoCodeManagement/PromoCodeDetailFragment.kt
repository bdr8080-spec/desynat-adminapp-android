package com.desynatadmin.ui.home.promoCodeManagement

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.desynatadmin.R
import com.desynatadmin.base.BaseFragment
import com.desynatadmin.data.view_model.HomeViewModel
import com.desynatadmin.databinding.FragmentPromoCodeDetailBinding
import com.desynatadmin.ui.home.brandManagement.response.GetBrandResponse
import com.desynatadmin.ui.home.promoCodeManagement.response.GetPromoCodeResponse
import com.desynatadmin.utils.CommonMethod
import com.desynatadmin.utils.ProgressDialogUtils
import com.desynatadmin.utils.showToast
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PromoCodeDetailFragment : BaseFragment() {
    private var binding: FragmentPromoCodeDetailBinding?=null
    private val viewModel: HomeViewModel by viewModels()
    private var isObserver = true
    var promoCodeData: GetPromoCodeResponse.Data?=null

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
        val itemJson = arguments?.getString("promoCodeData")
        if (!itemJson.isNullOrEmpty()) {
            promoCodeData = Gson().fromJson(itemJson, GetPromoCodeResponse.Data::class.java)
            setData()
            updateStatus()
        }
        binding?.apply {
            toolbar.tvToolbar.text = "Detail View"
        }
    }

    override fun initControl() {
        binding?.apply {
            toolbar.tvToolbar.setOnClickListener {
                findNavController().popBackStack()
            }

            tvSwitch.setOnClickListener {
                updatePromoCodeStatus()
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
                tvSwitch.text = "Active"
                tvSwitch.setTextColor(Color.parseColor("#1A900D"))
                tvSwitch.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.switch_on, 0)
            }
            else{
                tvSwitch.text = "Inactive"
                tvSwitch.setTextColor(Color.parseColor("#666666"))
                tvSwitch.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.switch_off, 0)

            }

        }
    }

    private fun setData(){

        binding?.apply {

            tvPromoCodeName.text = promoCodeData?.nameEnglish
            tvFeature.text = promoCodeData?.nameEnglish
            tvDescription.text = promoCodeData?.nameEnglish
            tvTerms.text = promoCodeData?.nameEnglish
            tvCountry.text = promoCodeData?.nameEnglish
            tvApplicable.text = promoCodeData?.nameEnglish
            tvStartDate.text = promoCodeData?.nameEnglish
            tvEndDate.text = promoCodeData?.nameEnglish

            if(promoCodeData?.status==1){
                tvSwitch.text = "Active"
                tvSwitch.setTextColor(Color.parseColor("#1A900D"))
                tvSwitch.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.switch_on, 0)
            }
            else{
                tvSwitch.text = "Inactive"
                tvSwitch.setTextColor(Color.parseColor("#666666"))
                tvSwitch.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.switch_off, 0)

            }
        }

    }

    override fun observe() {
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                errorMessage?.let { showToast(it) }
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