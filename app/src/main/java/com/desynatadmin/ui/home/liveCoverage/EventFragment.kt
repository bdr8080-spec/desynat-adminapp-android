package com.desynatadmin.ui.home.liveCoverage

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.desynatadmin.R
import com.desynatadmin.adapter.EventAdapter
import com.desynatadmin.base.BaseFragment
import com.desynatadmin.data.view_model.HomeViewModel
import com.desynatadmin.databinding.FragmentEventBinding
import com.desynatadmin.ui.home.HomeActivity
import com.desynatadmin.ui.home.SubAdminHomeActivity
import com.desynatadmin.ui.home.liveCoverage.response.GetLiveStreamingRoomResponse
import com.desynatadmin.ui.home.subscriptionPlan.response.GetCountryResponse
import com.desynatadmin.utils.CommonMethod
import com.desynatadmin.utils.ProgressDialogUtils
import com.desynatadmin.utils.SharedPreference
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue
import kotlin.toString

@AndroidEntryPoint
class EventFragment : BaseFragment() {
    private var binding: FragmentEventBinding?=null
    private val viewModel: HomeViewModel by viewModels()
    private var isObserver = true
    private var isCountryObserver = true
    private var eventAdapter : EventAdapter?=null
    private var eventList = ArrayList<GetLiveStreamingRoomResponse.Data>()
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
            if (SharedPreference.get(requireActivity()).getUser()?.type=="2"){
                ivBack.isVisible = true
            }
            else{
                ivBack.isVisible = false
            }
            eventList.clear()
            eventAdapter = EventAdapter(requireActivity(),eventList,object : EventAdapter.OnCLick{
                override fun onClick(position: Int) {
                    val bundle = Bundle().apply {
                        putString("eventData", Gson().toJson(eventList[position]))
                    }
                    findNavController().navigate(R.id.action_eventFragment_to_liveEventFragment,bundle)
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

        Handler(Looper.getMainLooper()).postDelayed({
            checkData()
        },500)
    }

    override fun initControl() {
        binding?.apply {
            ivBack.setOnClickListener {
                when (val activity = requireActivity()) {
                    is HomeActivity -> activity.onBackPressed()
                    is SubAdminHomeActivity -> activity.onBackPressed()
                    else -> activity.onBackPressed()
                }
            }

            etCountry.setOnClickListener {
                CommonMethod.showCountryPicker(
                    requireContext(),
                    it,
                    countryList
                ) { selectedCountry ->
                    etCountry.setText(selectedCountry.name)
                    selectedCountryId = selectedCountry
                    getLiveStreaming()
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
                SharedPreference.get(requireActivity()).saveCountryList(countryList)
                isCountryObserver = false
            }
        }

        viewModel.getLiveStreamingRoomResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                eventList.clear()
                eventList.addAll(response.data)
                eventAdapter?.notifyDataSetChanged()
                checkData()

                isObserver = false
            }
        }
    }

    private fun getCountry() {
        viewModel.getCountry()
        isCountryObserver = true
    }

    private fun getLiveStreaming(){
        val map = HashMap<String, Any>()
        map["country_id"] = selectedCountryId?.id.toString()
        viewModel.getLiveStreamingRoom(map)
        ProgressDialogUtils.getInstance().showProgress(requireActivity(),false)
        isObserver = true
    }

    fun setDefaultCountry(){
        for (i in 0 until countryList.size) {
            if (countryList[i].name == "Kuwait") {
                selectedCountryId = countryList[i]
                selectedCountryId = countryList[i]
                binding?.etCountry?.setText(countryList[i].name)
                getLiveStreaming()
            }
        }
    }

    private fun  checkData(){
        binding?.tvNoDataFound?.isVisible = eventList.isEmpty()
    }
}