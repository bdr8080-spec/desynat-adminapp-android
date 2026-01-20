package com.desynatadmin.ui.home.dealsManagement

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import coil.load
import com.bumptech.glide.Glide
import com.desynatadmin.R
import com.desynatadmin.adapter.EnrolledAdapter
import com.desynatadmin.base.BaseFragment
import com.desynatadmin.data.view_model.HomeViewModel
import com.desynatadmin.databinding.FragmentDealsDetailBinding
import com.desynatadmin.ui.home.dealsManagement.response.GetActiveDealResponse
import com.desynatadmin.utils.CommonMethod
import com.desynatadmin.utils.ProgressDialogUtils
import com.desynatadmin.utils.showToast
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DealsDetailFragment : BaseFragment() {
    private var binding: FragmentDealsDetailBinding? = null
    private val viewModel: HomeViewModel by viewModels()
    private var isObserver = true
    private var dealData: GetActiveDealResponse.Data.Data?=null
    private var enrolledList = ArrayList<String>()
    private var enrolledAdapter: EnrolledAdapter?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDealsDetailBinding.inflate(layoutInflater, container, false)
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
        findNavController().currentBackStackEntry
            ?.savedStateHandle
            ?.getLiveData<String>("updatedDealData")
            ?.observe(viewLifecycleOwner) { json ->
                json?.let {
                    dealData = Gson().fromJson(json, GetActiveDealResponse.Data.Data::class.java)
                    setData()
                }
            }

        val itemJson = arguments?.getString("dealsData")

        binding?.apply {
            toolbar.tvToolbar.text = "Details"

            enrolledAdapter = EnrolledAdapter(requireActivity(),enrolledList,object :EnrolledAdapter.ClickOnItem{
                override fun onClickItem(position: Int) {

                }

            })
            rvEnrolled.adapter = enrolledAdapter

            if (!itemJson.isNullOrEmpty()) {
                dealData = Gson().fromJson(itemJson, GetActiveDealResponse.Data.Data::class.java)
                setData()
            }


        }
    }

    override fun initControl() {
        binding?.apply {
            toolbar.ivBack.setOnClickListener {
                findNavController().popBackStack()
            }

            btnEdit.setOnClickListener {
                val bundle = Bundle().apply {
                    putString("dealsData", Gson().toJson(dealData))
                }
                findNavController().navigate(R.id.action_dealDetail_to_addDealFragment,bundle)
            }

            tvSwitch.setOnClickListener {
                updateDealStatus()
            }

            ivDeal.setOnClickListener {
                CommonMethod.imagePreviewDialog(requireActivity(),dealData?.image.toString()){

                }
            }

            btnDelete.setOnClickListener {
                deleteDeal()
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

        viewModel.updateDealStatusResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                dealData?.status = response.data.status
                if (response.data.status==1){
                    showToast("Deal Activated Successfully")
                }
                else{
                    showToast("Deal Deactivated Successfully")

                }
                updateStatus()
                isObserver = false

            }
        }

        viewModel.deleteDealResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                CommonMethod.showSuccessDialog(requireContext(), "Deal delete successfully"){
                    findNavController().popBackStack()
                }
                isObserver = false
            }
        }
    }

    private fun updateStatus() {
        binding?.apply {
                if(dealData?.status==1){
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
            updateStatus()
            tvDealName.text = dealData?.nameEnglish
            tvCategoryValue.text = dealData?.categoryDetails?.name
            tvSubCategoryValue.text = dealData?.subCategoryDetails?.nameEnglish
            tvDurationValue.text = "${CommonMethod.customizeChangeDateFormat(dealData?.from.toString(), inputFormat = "yyyy-MM-dd", outputFormat = "dd/MM/yyyy")}  - ${CommonMethod.customizeChangeDateFormat(dealData?.to.toString(), inputFormat = "yyyy-MM-dd", outputFormat = "dd/MM/yyyy")}"
            // tvTotalEnrolled.text = "${dealData }Sellers"
            tvDiscountValue.text = dealData?.discount+"%"
            Glide.with(requireActivity()).load(dealData?.image).placeholder(R.drawable.ic_placeholder).into(ivDeal)
        }
    }

    private fun deleteDeal(){
        val map = HashMap<String,Any>()
        map["deal_id"]=dealData?.id.toString()
        viewModel.deleteDeal(map)
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

    private fun updateDealStatus(){
        val map = HashMap<String,Any>()
        map["deal_id"]=dealData?.id.toString()
        viewModel.updateDealStatus(map)
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

}