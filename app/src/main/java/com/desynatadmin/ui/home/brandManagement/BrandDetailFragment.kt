package com.desynatadmin.ui.home.brandManagement

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import coil.load
import com.desynatadmin.R
import com.desynatadmin.base.BaseFragment
import com.desynatadmin.data.view_model.HomeViewModel
import com.desynatadmin.databinding.FragmentAddBrandBinding
import com.desynatadmin.databinding.FragmentBrandDetailBinding
import com.desynatadmin.ui.home.brandManagement.response.GetBrandResponse
import com.desynatadmin.utils.CommonMethod
import com.desynatadmin.utils.ProgressDialogUtils
import com.desynatadmin.utils.SharedPreference
import com.desynatadmin.utils.showToast
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BrandDetailFragment : BaseFragment() {
    private var binding: FragmentBrandDetailBinding?=null
    private var isObserver = true
    private val viewModel: HomeViewModel by viewModels()
    var brandData: GetBrandResponse.Data.Data?=null
    var actionAccess : Boolean = true
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBrandDetailBinding.inflate(layoutInflater,container,false)
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
        val itemJson = arguments?.getString("brandData")
        if (!itemJson.isNullOrEmpty()) {
            brandData = Gson().fromJson(itemJson, GetBrandResponse.Data.Data::class.java)
            setData()
            updateStatus()
        }
        binding?.apply {

            val userInfo = SharedPreference.get(requireActivity()).getUser()
            if (userInfo?.type=="2"){
                val subAdminInfo = SharedPreference.get(requireActivity()).getSubAdmin()
                val module = subAdminInfo?.permissionDetails
                    ?.firstOrNull {
                        it.moduleDetails.name.equals("brand_management", true)
                    }

                actionAccess = module?.canAction == 1
                btnDelete.isVisible = actionAccess
                btnEdit.isVisible = actionAccess
            }

            toolbar.tvToolbar.text = "Brand Management Detail"
        }
    }

    override fun initControl() {
        binding?.apply {
            toolbar.ivBack.setOnClickListener {
                findNavController().popBackStack()
            }

            btnDelete.setOnClickListener {
                CommonMethod.deleteAlertDialog(
                    requireActivity(),
                    "Are you sure you want to delete the brand?"
                ) { action ->
                    when (action) {
                        CommonMethod.DialogAction.DELETE -> {
                            deleteBrand()
                        }

                        CommonMethod.DialogAction.DISCARD -> {

                        }
                    }
                }

            }

            btnEdit.setOnClickListener {
                 val bundle = Bundle().apply {
                     putString("brandData", Gson().toJson(brandData))
                 }
                 findNavController().navigate(R.id.action_brandDetail_to_addBrandFragment,bundle)
            }

            tvSwitch.setOnClickListener {
                if (actionAccess){
                    updateBrandStatus()
                }
            }

            ivBrand.setOnClickListener {
                CommonMethod.imagePreviewDialog(requireActivity(),brandData?.image.toString()){

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


        viewModel.updateBrandStatusResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                brandData = response.data
                if (response.data.status==1){
                    showToast("Brand Activated Successfully")
                }
                else{
                    showToast("Brand Deactivated Successfully")

                }
                updateStatus()
                isObserver = false

            }
        }

        viewModel.deleteBrandResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                CommonMethod.showSuccessDialog(requireContext(), "Brand delete successfully"){
                    findNavController().popBackStack()
                }
                isObserver = false
            }
        }
    }

    private fun deleteBrand(){
        val map = HashMap<String,Any>()
        map["brand_id"]=brandData?.id.toString()
        viewModel.deleteBrand(map)
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

    private fun updateBrandStatus(){
        val map = HashMap<String,Any>()
        map["brand_id"]=brandData?.id.toString()
        viewModel.updateBrandStatus(map)
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

    private fun updateStatus(){
        binding?.apply {
            if(brandData?.status==1){
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

            tvBrandName.text = brandData?.nameEnglish
            if(brandData?.status==1){
                tvSwitch.text = "Active"
                tvSwitch.setTextColor(Color.parseColor("#1A900D"))
                tvSwitch.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.switch_on, 0)
            }
            else{
                tvSwitch.text = "Inactive"
                tvSwitch.setTextColor(Color.parseColor("#666666"))
                tvSwitch.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.switch_off, 0)

            }

            ivBrand.load(brandData?.image) {
                crossfade(true)
                placeholder(R.drawable.ic_placeholder)
                error(R.drawable.ic_placeholder)
            }
        }

    }

}