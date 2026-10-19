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
import com.desynatadmin.ui.home.HomeActivity
import com.desynatadmin.ui.home.SubAdminHomeActivity
import com.desynatadmin.ui.home.subscriptionPlan.response.GetAllPlanResponse
import com.desynatadmin.ui.home.subscriptionPlan.response.GetCountryResponse
import com.desynatadmin.utils.CommonMethod
import com.desynatadmin.utils.Constants
import com.desynatadmin.utils.ProgressDialogUtils
import com.desynatadmin.utils.SharedPreference
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
            val userInfo = SharedPreference.get(requireActivity()).getUser()
            if (userInfo?.type=="2"){
                val subAdminInfo = SharedPreference.get(requireActivity()).getSubAdmin()
                val module = subAdminInfo?.permissionDetails
                    ?.firstOrNull {
                        it.moduleDetails.name.equals("plan_management", true)
                    }

                val actionAccess = module?.canAction == 1
                ivAddPlan.isVisible = actionAccess
            }
            if (SharedPreference.get(requireActivity()).getUser()?.type=="2"){
                ivBack.isVisible = true
            }
            else{
                ivBack.isVisible = false
            }
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
        if(SharedPreference.get(requireActivity()).getCountryList() == null) {
            getCountryApi()
        }
        else{
            countryList.clear()
            SharedPreference.get(requireActivity()).getCountryList()?.let { countryList.addAll(it) }
            if (selectedCountryCode==null){
                setCountry()
            }
            else{
                getSubscriptionPlan()
            }
        }
    }

    override fun initControl() {
        binding?.apply {
            ivAddPlan.setOnClickListener {
                findNavController().navigate(R.id.action_planList_to_addPlanFragment)
            }

            ivBack.setOnClickListener {
                when (val activity = requireActivity()) {
                    is HomeActivity -> activity.onBackPressed()
                    is SubAdminHomeActivity -> activity.onBackPressed()
                    else -> activity.onBackPressed()
                }
            }

            etCountry.setOnClickListener {
                CommonMethod.showCountryPicker(
                    context = requireContext(),
                    anchor = it,
                    countryList = countryList,
                    isDialCode = false
                ) { selectedCountry ->
                    etCountry.setText(if (Constants.language=="ar") selectedCountry.nameAr else selectedCountry.name)
                    selectedCountryCode = selectedCountry.id.toString()
                    getSubscriptionPlan(true)
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

        viewModel.getCountryResult.observe(viewLifecycleOwner) { response ->
            if (isCountryObserver) {
                countryList.clear()
                countryList.addAll(response.data)
                setCountry()

                isCountryObserver = false
            }
        }

        viewModel.subscriptionPlanResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                binding?.mainProgressBar?.isVisible = false
                ProgressDialogUtils.getInstance().hideProgress()
                subscriptionList.clear()
                subscriptionList.addAll(response.data)
                binding?.tvNoDataFound?.isVisible = subscriptionList.isEmpty()
                    subscriptionAdapter?.notifyDataSetChanged()
                isObserver = false
            }
        }

    }

    private fun getSubscriptionPlan(isLoading:Boolean=false){
        if (selectedCountryCode!=null){
            viewModel.getSubscriptionPlan(selectedCountryCode.toString())
        }
        else{
            viewModel.getSubscriptionPlan()
        }
        if (isLoading) {
            binding?.mainProgressBar?.isVisible = true
        }
        isObserver = true
        }


    private fun getCountryApi(){
        viewModel.getCountry()
        isCountryObserver = true
    }

    private fun setCountry() {
        if (!countryList.isNullOrEmpty()) {
            SharedPreference.get(requireActivity()).saveCountryList(countryList)
            var foundKuwait = false
            for (i in 0 until countryList.size) {
                if (countryList[i].name.equals("Kuwait", ignoreCase = true) || countryList[i].nameAr == "الكويت") {
                    selectedCountryCode = countryList[i].id.toString()
                    binding?.etCountry?.setText(if (Constants.language == "ar") countryList[i].nameAr else countryList[i].name)
                    foundKuwait = true
                    break
                }
            }
            if (!foundKuwait) {
                selectedCountryCode = countryList[0].id.toString()
                binding?.etCountry?.setText(if (Constants.language == "ar") countryList[0].nameAr else countryList[0].name)
            }
            getSubscriptionPlan()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        ProgressDialogUtils.getInstance().hideProgress()
    }

}