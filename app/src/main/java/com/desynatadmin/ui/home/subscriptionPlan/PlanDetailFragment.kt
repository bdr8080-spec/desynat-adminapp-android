package com.desynatadmin.ui.home.subscriptionPlan

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
import com.desynatadmin.databinding.FragmentPlanDetailBinding
import com.desynatadmin.ui.home.subscriptionPlan.response.GetAllPlanResponse
import com.desynatadmin.utils.CommonMethod
import com.desynatadmin.utils.ProgressDialogUtils
import com.desynatadmin.utils.showToast
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlanDetailFragment : BaseFragment() {
    private var binding: FragmentPlanDetailBinding?=null
    private val viewModel: HomeViewModel by viewModels()
    private var isObserver = true
    private var subscriptionData: GetAllPlanResponse.Data?=null
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
        if (!itemJson.isNullOrEmpty()) {
            subscriptionData = Gson().fromJson(itemJson, GetAllPlanResponse.Data::class.java)
            setData()
            updateStatus()
        }
        binding?.apply {
            toolbar.tvToolbar.text = "Plan Details"
        }
    }

    override fun initControl() {
        binding?.apply {
            toolbar.tvToolbar.setOnClickListener {
                findNavController().popBackStack()
            }

            btnDelete.setOnClickListener {
                deletePlan()
            }

            btnEdit.setOnClickListener {
                val bundle = Bundle().apply {
                    putString("subscriptionData", Gson().toJson(subscriptionData))
                }
                findNavController().navigate(R.id.action_planDetail_to_addPlanFragment,bundle)
            }

            tvSwitch.setOnClickListener {
                updatePlanStatus()
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

        viewModel.deleteSubscriptionResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                CommonMethod.showSuccessDialog(requireContext(), "Plan deleted successfully!"){
                    findNavController().popBackStack()
                }
                isObserver = false
            }
        }

        viewModel.updateSubscriptionPlanStatusResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                subscriptionData = response.data
                updateStatus()
                CommonMethod.showSuccessDialog(requireContext(), "Plan status updated successfully!")
                isObserver = false
            }
        }
    }

    private fun updatePlanStatus(){
        val map = HashMap<String,Any>()
        map["plan_id"] = subscriptionData?.id.toString()
        viewModel.updateSubscriptionPlanStatus(map)

        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

    private fun deletePlan(){
        val map = HashMap<String,Any>()
        map["plan_id"] = subscriptionData?.id.toString()
        viewModel.deleteSubscriptionPlan(map)

        ProgressDialogUtils.getInstance().showProgress(requireActivity())
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

            tvPackage.text = subscriptionData?.planNameEnglish
            tvPrice.text = "KD ${subscriptionData?.planNameEnglish}"
            when(subscriptionData?.durationType){
                1->{
                    tvValidTime.text = "for ${subscriptionData?.duration} Month"
                }
                else ->{
                    tvValidTime.text = "for ${subscriptionData?.duration} Days"
                }
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

}