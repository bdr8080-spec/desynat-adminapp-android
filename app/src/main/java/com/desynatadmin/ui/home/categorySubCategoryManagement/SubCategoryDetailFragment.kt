package com.desynatadmin.ui.home.categorySubCategoryManagement

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import coil.load
import com.desynatadmin.R
import com.desynatadmin.base.BaseFragment
import com.desynatadmin.data.view_model.HomeViewModel
import com.desynatadmin.databinding.FragmentSubCategoryDetailBinding
import com.desynatadmin.ui.home.brandManagement.response.GetBrandResponse
import com.desynatadmin.ui.home.categorySubCategoryManagement.response.GetSubCategoryResponse
import com.desynatadmin.utils.CommonMethod
import com.desynatadmin.utils.ProgressDialogUtils
import com.desynatadmin.utils.showToast
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SubCategoryDetailFragment : BaseFragment() {
    private var binding: FragmentSubCategoryDetailBinding?=null
        private val viewModel: HomeViewModel by viewModels()
    private var isObserver = true
    private var subCategorydata: GetSubCategoryResponse.Data.Data?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSubCategoryDetailBinding.inflate(layoutInflater,container,false)
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
        val itemJson = arguments?.getString("subCategoryData")
        if (!itemJson.isNullOrEmpty()) {
            subCategorydata = Gson().fromJson(itemJson, GetSubCategoryResponse.Data.Data::class.java)
            setData()
            updateStatus()
        }
        binding?.apply {
            toolbar.tvToolbar.text = "Sub-Category Detail"
        }
    }

    override fun initControl() {
        binding?.apply {
            toolbar.ivBack.setOnClickListener {
                findNavController().popBackStack()
            }

            btnDelete.setOnClickListener {
                deleteSubCategory()
            }

            btnEdit.setOnClickListener {
                val bundle = Bundle().apply {
                    putString("subCategoryData", Gson().toJson(subCategorydata))
                    putString("categoryId", subCategorydata?.categoryId.toString())
                }
                findNavController().navigate(R.id.action_subCategoryDetail_to_addSubCategoryFragment,bundle)
            }

            tvSwitch.setOnClickListener {
                updateSubCategoryStatus()
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


        viewModel.updateSubCategoryStatusResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                subCategorydata = response.data
                if (response.data.status==1){
                    showToast("Sub-Category Activated Successfully")
                }
                else{
                    showToast("Sub-Category Deactivated Successfully")

                }
                updateStatus()
                isObserver = false

            }
        }

        viewModel.deleteSubCategoryResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                CommonMethod.showSuccessDialog(requireContext(), "Sub-Category delete successfully"){
                    findNavController().popBackStack()
                }
                isObserver = false
            }
        }
    }

    private fun deleteSubCategory(){
        val map = HashMap<String,Any>()
        map["subcategory_id"]=subCategorydata?.id.toString()
        viewModel.deleteSubCategory(map)
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

    private fun updateSubCategoryStatus(){
        val map = HashMap<String,Any>()
        map["subcategory_id"]=subCategorydata?.id.toString()
        viewModel.updateSubCategoryStatus(map)
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }


    private fun updateStatus(){
        binding?.apply {

            if (subCategorydata?.status == 1) {
                tvSwitch.text = "Active"
                tvSwitch.setTextColor(Color.parseColor("#1A900D"))
                tvSwitch.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.switch_on, 0)
            } else {
                tvSwitch.text = "Inactive"
                tvSwitch.setTextColor(Color.parseColor("#666666"))
                tvSwitch.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.switch_off, 0)

            }
        }
    }


    private fun setData(){

        binding?.apply {

            tvSubcategoryName.text = subCategorydata?.nameEnglish
            ivSubCategory.load(subCategorydata?.image) {
                crossfade(true)
                placeholder(R.drawable.ic_placeholder)
                error(R.drawable.ic_placeholder)
            }
        }

    }



}