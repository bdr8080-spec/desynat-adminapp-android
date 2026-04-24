package com.desynatadmin.ui.home.subscriptionPlan

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.desynatadmin.R
import com.desynatadmin.adapter.FeatureList
import com.desynatadmin.base.BaseFragment
import com.desynatadmin.data.view_model.HomeViewModel
import com.desynatadmin.databinding.FragmentPlanDetailBinding
import com.desynatadmin.ui.home.subscriptionPlan.response.GetAllPlanResponse
import com.desynatadmin.utils.CommonMethod
import com.desynatadmin.utils.ProgressDialogUtils
import com.desynatadmin.utils.SharedPreference
import com.desynatadmin.utils.showToast
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlanDetailFragment : BaseFragment() {
    private var binding: FragmentPlanDetailBinding?=null
    private val viewModel: HomeViewModel by viewModels()
    private var isObserver = true
    private var subscriptionData: GetAllPlanResponse.Data?=null
    private var background: Int?=R.drawable.plus
    var actionAccess: Boolean=true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPlanDetailBinding.inflate(layoutInflater,container,false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initControl()
        observe()
    }

    override fun initView() {
        val itemJson = arguments?.getString("subscriptionData")
        background = arguments?.getInt("background")
        if (!itemJson.isNullOrEmpty()) {
            subscriptionData = Gson().fromJson(itemJson, GetAllPlanResponse.Data::class.java)
            setData()
            updateStatus()
            getSubscriptionPlan()
        }
        binding?.apply {
            toolbar.tvToolbar.text = "Plan Details"
            val userInfo = SharedPreference.get(requireActivity()).getUser()
            if (userInfo?.type=="2"){
                val subAdminInfo = SharedPreference.get(requireActivity()).getSubAdmin()
                val module = subAdminInfo?.permissionDetails
                    ?.firstOrNull {
                        it.moduleDetails.name.equals("plan_management", true)
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

            btnDelete.setOnClickListener {
                CommonMethod.deleteAlertDialog(
                    requireActivity(),
                    "Are you sure you want to delete the plan?"
                ) { action ->
                    when (action) {
                        CommonMethod.DialogAction.DELETE -> {
                            deletePlan()
                        }

                        CommonMethod.DialogAction.DISCARD -> {

                        }
                    }
                }

            }

            btnEdit.setOnClickListener {
                val bundle = Bundle().apply {
                    putString("subscriptionData", Gson().toJson(subscriptionData))
                }
                findNavController().navigate(R.id.action_planDetail_to_addPlanFragment,bundle)
            }

            tvSwitch.setOnClickListener {
                if (actionAccess) {
                    updatePlanStatus()
                }
            }
        }
    }

    override fun observe() {
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                binding?.mainProgressBar?.isVisible = false
                isObserver = false
            }
        }

        viewModel.deleteSubscriptionResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                binding?.mainProgressBar?.isVisible = false
                CommonMethod.showSuccessDialog(requireContext(), "Plan deleted successfully!"){
                    findNavController().popBackStack()
                }
                isObserver = false
            }
        }

        viewModel.subscriptionPlanDetailResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                binding?.mainProgressBar?.isVisible = false
                subscriptionData = response.data
                setData()
                isObserver = false
            }
        }

        viewModel.updateSubscriptionPlanStatusResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                binding?.mainProgressBar?.isVisible = false
                binding?.mainProgressBar?.isVisible = false
                subscriptionData = response.data
                if (response.data.status==1){
//                    CommonMethod.showSuccessDialog(requireContext(), "Plan active successfully")
                   showToast("Plan active successfully")
                }
                else{
                    showToast("Plan inactive successfully")
                }
                updateStatus()

                isObserver = false
            }
        }
    }

    private fun updatePlanStatus(){
        val map = HashMap<String,Any>()
        map["plan_id"] = subscriptionData?.id.toString()
        viewModel.updateSubscriptionPlanStatus(map)

        binding?.mainProgressBar?.isVisible = true
        isObserver = true
    }

    private fun deletePlan(){
        val map = HashMap<String,Any>()
        map["plan_id"] = subscriptionData?.id.toString()
        viewModel.deleteSubscriptionPlan(map)

        binding?.mainProgressBar?.isVisible = true
        isObserver = true
    }

    private fun updateStatus(){
        binding?.apply {
            if(subscriptionData?.status==1){
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
            background?.let { tvPackage.setBackgroundResource(it) }
            tvPackage.text = subscriptionData?.planNameEnglish
            if(subscriptionData?.discountedPrice!=null&&subscriptionData?.discountedPrice!=0.0){
                tvPrice.text = "KD ${CommonMethod.formatDoubleValue(subscriptionData?.discountedPrice)}"
            }
            else{
                tvPrice.text = "KD ${CommonMethod.formatDoubleValue(subscriptionData?.price)}"
            }
            when(subscriptionData?.durationType){
                1->{
                    tvValidTime.text = "for ${subscriptionData?.duration} Month"
                }
                2->{
                    tvValidTime.text = "for ${subscriptionData?.duration} Year"
                }
                else ->{
                    tvValidTime.text = "for ${subscriptionData?.duration} Month"
                }
            }

           // setVisibilityIfZero(llProduct, subscriptionData?.noOfProducts)
            setVisibilityIfZero(llVideos, subscriptionData?.noOfVideos)
            setVisibilityIfZero(llHighlights, subscriptionData?.noOfHighlights)
            setVisibilityIfZero(llEmployees, subscriptionData?.noOfEmployee)
            if (subscriptionData?.productType==1){
                tvProduct.text = "Unlimited"
            }
            else{
                tvProduct.text = subscriptionData?.noOfProducts.toString()
            }

            tvReel.text = subscriptionData?.noOfVideos.toString()
            tvHighlights.text = subscriptionData?.noOfHighlights.toString()
            tvStories.text = if(subscriptionData?.stories==1)"Open" else "Close"
            tvEmployee.text = subscriptionData?.noOfEmployee.toString()+" Store Employees"

            val commissionValue = subscriptionData?.commissionPercentage?.toString()?.toDoubleOrNull()
            if (commissionValue != null && commissionValue != 0.0) {
                tvCommission.text =  "${CommonMethod.formatDoubleValue(subscriptionData?.commissionPercentage)}%"
            }
            else{
                tvCommission.text = "No"
            }

            val freeMonth = subscriptionData?.noOfFreeMonths
            if (freeMonth != null && freeMonth != "null"&& freeMonth != "") {
                llFreeMonth.isVisible = true
                tvNoOfFreeMonth.text =  subscriptionData?.noOfFreeMonths
            }
            else{
                llFreeMonth.isVisible = false
            }

            if(subscriptionData?.status==1){
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

    private fun setVisibilityIfZero(view: View, value: Any?) {
        val numericValue = when (value) {
            is Int -> value
            is Double -> value.toInt()
            is Float -> value.toInt()
            is String -> value.toIntOrNull() ?: 0
            else -> 0
        }
        view.visibility = if (numericValue == 0) View.GONE else View.VISIBLE
    }


    private fun getSubscriptionPlan(){

            viewModel.getSubscriptionPlanDetail(subscriptionData?.id.toString())
        binding?.mainProgressBar?.isVisible = true
        isObserver = true
    }
}