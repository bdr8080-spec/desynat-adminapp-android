package com.desynatadmin.ui.home.dealsManagement

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.desynatadmin.R
import com.desynatadmin.adapter.EnrolledAdapter
import com.desynatadmin.base.BaseFragment
import com.desynatadmin.data.view_model.HomeViewModel
import com.desynatadmin.databinding.FragmentDealsDetailBinding
import com.desynatadmin.ui.home.dealsManagement.response.GetActiveDealResponse
import com.desynatadmin.ui.home.dealsManagement.response.GetDealDetailResponse
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
    private var dealType = 1
    private var dealData: GetActiveDealResponse.Data.Data?=null
    private var dealDetailData: GetDealDetailResponse.Data?=null
    private var enrolledList = ArrayList<GetDealDetailResponse.Data.SellerDealrecord>()
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

        val itemJson = arguments?.getString("dealsData")
         dealType = arguments?.getInt("dealType")?:1

        if (!itemJson.isNullOrEmpty()) {
            dealData = Gson().fromJson(itemJson, GetActiveDealResponse.Data.Data::class.java)
        }
        binding?.apply {
            toolbar.tvToolbar.text = "Details"

            if (dealType==2){
                relStatus.isVisible = false
             btnEdit.isVisible = false
             btnDelete.isVisible = false
                llRevenue.isVisible = true
            }
            else{
                llRevenue.isVisible = false
                relStatus.isVisible = true
                btnEdit.isVisible = true
                btnDelete.isVisible = true
            }
            enrolledAdapter = EnrolledAdapter(requireActivity(),enrolledList,object :EnrolledAdapter.ClickOnItem{
                override fun onClickItem(position: Int) {
                    val bundle = Bundle().apply {
                        putString("dealsData", Gson().toJson(enrolledList[position]))
                        putInt("dealType", dealType)
                    }
                    findNavController().navigate(R.id.action_dealDetail_to_dealProductFragment,bundle)
                }
            })
            rvEnrolled.adapter = enrolledAdapter

            getDealDetail()

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

        viewModel.getDealDetailResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                dealDetailData = response?.data
                setData()
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
            enrolledList.clear()
            dealDetailData?.sellerDealrecords?.let { enrolledList.addAll(it) }
            enrolledAdapter?.notifyDataSetChanged()
            checkData()
            tvDealName.text = dealDetailData?.nameEnglish
            tvCategoryValue.text = dealDetailData?.categoryDetails?.name
            tvSubCategoryValue.text = dealDetailData?.subCategoryDetails?.nameEnglish
            tvDurationValue.text = "${CommonMethod.customizeChangeDateFormat(dealDetailData?.from.toString(), inputFormat = "yyyy-MM-dd", outputFormat = "dd/MM/yyyy")}  - ${CommonMethod.customizeChangeDateFormat(dealDetailData?.to.toString(), inputFormat = "yyyy-MM-dd", outputFormat = "dd/MM/yyyy")}"
            tvDiscountValue.text = dealDetailData?.discount+"%"
            Glide.with(requireActivity()).load(dealDetailData?.image).placeholder(R.drawable.ic_placeholder).into(ivDeal)
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

    private fun getDealDetail(){
        val map = HashMap<String,Any>()
        map["deal_id"]=dealData?.id.toString()
        viewModel.getDealDetail(map)
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

    private fun checkData(){
        binding?.tvNotData?.isVisible = enrolledList.isEmpty()
    }

}