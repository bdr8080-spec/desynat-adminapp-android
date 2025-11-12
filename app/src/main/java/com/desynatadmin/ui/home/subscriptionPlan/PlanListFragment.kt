package com.desynatadmin.ui.home.subscriptionPlan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.desynatadmin.R
import com.desynatadmin.adapter.SubscriptionAdapter
import com.desynatadmin.base.BaseFragment
import com.desynatadmin.data.view_model.HomeViewModel
import com.desynatadmin.databinding.FragmentPlanListBinding
import com.desynatadmin.ui.home.brandManagement.response.CountryREsponse
import com.desynatadmin.ui.home.subscriptionPlan.response.GetAllPlanResponse
import com.desynatadmin.ui.home.subscriptionPlan.response.GetCountryResponse
import com.desynatadmin.utils.ProgressDialogUtils
import com.desynatadmin.utils.SharedPreference
import com.desynatadmin.utils.showToast
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlanListFragment : BaseFragment() {
    private var binding: FragmentPlanListBinding?=null
    private val viewModel: HomeViewModel by viewModels()
    private var isCountryObserver = true
    private var isObserver = true
    private var subscriptionAdapter: SubscriptionAdapter?=null
    private var subscriptionList = ArrayList<GetAllPlanResponse.Data>()
    private var countryList = ArrayList<GetCountryResponse.Data>()
    private var selectedCountryCode: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPlanListBinding.inflate(layoutInflater,container,false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initControl()
        observe()
    }

    override fun initView() {

        binding?.apply {
            subscriptionAdapter = SubscriptionAdapter(requireActivity(),subscriptionList,object: SubscriptionAdapter.SubscriptionClick{
                override fun onSubscriptionClick(position: Int,backgrounRes:Int) {
                    val bundle = Bundle().apply {
                        putString("subscriptionData", Gson().toJson(subscriptionList[position]))
                        putInt("background", backgrounRes)
                    }
                    findNavController().navigate(R.id.action_planList_to_planDetailFragment,bundle)
                }

            })
            rvPlan.adapter = subscriptionAdapter
        }
        getCountryApi()
      //  getSubscriptionPlan()
    }

    override fun initControl() {
        binding?.apply {
            ivAddPlan.setOnClickListener {
                findNavController().navigate(R.id.action_planList_to_addPlanFragment)
            }

            etCountry.setOnClickListener {
                ccpCountryPicker.launchCountrySelectionDialog()
            }

            ccpCountryPicker.setOnCountryChangeListener {
                val countryName = ccpCountryPicker.selectedCountryName
                selectedCountryCode = ccpCountryPicker.selectedCountryNameCode
                etCountry.setText(countryName)
                getSubscriptionPlan()
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

        viewModel.getCountryResultt.observe(viewLifecycleOwner) { response ->
            if (isCountryObserver) {
                countryList.addAll(response.data)
                setCountry()

                isCountryObserver = false
            }
        }
        viewModel.subscriptionPlanResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                subscriptionList.clear()
                subscriptionList.addAll(response.data)
                binding?.tvNoDataFound?.isVisible = subscriptionList.isEmpty()
                    subscriptionAdapter?.notifyDataSetChanged()
                isObserver = false
            }
        }

    }

    private fun getSubscriptionPlan(){
        if (selectedCountryCode!=null){
            viewModel.getSubscriptionPlan(countryList.find { it.isoCode == selectedCountryCode }?.id.toString())
        }
        else{
            viewModel.getSubscriptionPlan()
        }
            ProgressDialogUtils.getInstance().showProgress(requireActivity())
            isObserver = true
        }


    private fun getCountryApi(){
        viewModel.getCountry()
        isCountryObserver = true
    }

    private fun setCountry() {
        if (!countryList.isNullOrEmpty()) {
            SharedPreference.get(requireActivity()).saveCountryList(countryList)
            val countryCodes = countryList?.map { it.isoCode }
            val countryCodeString = countryCodes?.joinToString(",")
            binding?.ccpCountryPicker?.setCustomMasterCountries(countryCodeString)
            selectedCountryCode = countryList[0].isoCode
            binding?.etCountry?.setText(countryList[0].name)
            getSubscriptionPlan()
        }
    }


}