package com.desynatadmin.ui.home.liveCoverage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.desynatadmin.R
import com.desynatadmin.adapter.EventAdapter
import com.desynatadmin.base.BaseFragment
import com.desynatadmin.data.view_model.HomeViewModel
import com.desynatadmin.databinding.FragmentEventBinding
import com.desynatadmin.ui.home.subscriptionPlan.response.GetCountryResponse
import com.desynatadmin.utils.CommonMethod
import com.desynatadmin.utils.ProgressDialogUtils
import com.desynatadmin.utils.SharedPreference
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue
@AndroidEntryPoint
class EventFragment : BaseFragment() {
    private var binding: FragmentEventBinding?=null
    private val viewModel: HomeViewModel by viewModels()
    private var isObserver = true
    private var isCountryObserver = true
    private var eventAdapter : EventAdapter?=null
    private var eventList = ArrayList<String>()
    private var countryList = ArrayList<GetCountryResponse.Data>()
    private var selectedCountryId : GetCountryResponse.Data?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEventBinding.inflate(layoutInflater,container,false)
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

            eventAdapter = EventAdapter(requireActivity(),eventList,object : EventAdapter.OnCLick{
                override fun onClick(position: Int) {
                    findNavController().navigate(R.id.action_eventFragment_to_liveEventFragment)
                }

            })
            rvEvents.adapter = eventAdapter

            if (SharedPreference.get(requireActivity()).getCountryList() == null) {
                getCountry()
            } else {
                countryList.clear()
                SharedPreference.get(requireActivity()).getCountryList()
                    ?.let { countryList.addAll(it) }
                setDefaultCountry()
            }

        }

    }

    override fun initControl() {
        binding?.apply {
            etCountry.setOnClickListener {
                CommonMethod.showCountryPicker(
                    requireContext(),
                    it,
                    countryList
                ) { selectedCountry ->
                    etCountry.setText(selectedCountry.name)
                    selectedCountryId = selectedCountry
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

        viewModel.getCountryListResult.observe(viewLifecycleOwner) { response ->
            if (isCountryObserver) {
                countryList.clear()
                countryList.addAll(response.data)
                setDefaultCountry()
                SharedPreference.get(requireActivity()).saveUserCountryList(countryList)
                isCountryObserver = false
            }
        }
    }

    private fun getCountry() {
        viewModel.getCountry()
        isCountryObserver = true
    }

    fun setDefaultCountry(){
        for (i in 0 until countryList.size) {
            if (countryList[i].name == "Kuwait") {
                selectedCountryId = countryList[i]
                selectedCountryId = countryList[i]
                binding?.etCountry?.setText(countryList[i].name)
            }
        }
    }
}