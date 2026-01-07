package com.desynatadmin.ui.home.liveRequestAndTimeSlotManagement

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.desynatadmin.R
import com.desynatadmin.adapter.LiveRequestAdapter
import com.desynatadmin.base.BaseFragment
import com.desynatadmin.data.view_model.HomeViewModel
import com.desynatadmin.databinding.FragmentLiveRequestAndTimeslotManagementBinding
import com.desynatadmin.ui.home.HomeActivity
import com.desynatadmin.ui.home.liveRequestAndTimeSlotManagement.response.GetLiveRequestResponse
import com.desynatadmin.ui.home.subscriptionPlan.response.GetCountryResponse
import com.desynatadmin.utils.CommonMethod
import com.desynatadmin.utils.ProgressDialogUtils
import com.desynatadmin.utils.SharedPreference
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LiveRequestAndTimeslotManagementFragment : BaseFragment() {
    private var binding: FragmentLiveRequestAndTimeslotManagementBinding?=null
    private val viewModel: HomeViewModel by viewModels()
    private var isObserver = true
    private var type = 1
    private var liveRequestType = 0 //0-new,1-accepted,2-rejected
    private var liveRequestList = ArrayList<GetLiveRequestResponse.Data.Data>()
    private var liveRequestAdapter: LiveRequestAdapter?=null
    private var selectedPos = -1

    private var currentPage = 1
    private var lastPage = 1
    private var totalPage = 0
    private var totalItem = 0
    private var pageLimit = 10
    private var pagination = false
    private var searchBanner: Job? = null
    private var lastQuery: String? = null
    private val itemsPerPage = 10
    var countryId: String? = null
    private var countryList = ArrayList<GetCountryResponse.Data>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLiveRequestAndTimeslotManagementBinding.inflate(layoutInflater,container,false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initControl()
        observe()
    }

    override fun initView() {
        countryList.clear()
        SharedPreference.get(requireActivity()).getCountryList()?.let { countryList.addAll(it) }
        binding?.apply {
            ProgressDialogUtils.getInstance().hideProgress()
            toolbar.tvToolbar.text = "Live Request & Timeslot Management"

            countryId = countryList[0].id.toString()
            etCountry.setText(countryList[0].name)

            if (type==1){
                type = 1
                clSearch.isVisible = false
                llLiveRequest.isVisible = false
                btnSubmit.isVisible = true
                llTimeSlot.isVisible = true
                ivBackArrow.isVisible = false
                tvCount.isVisible = false
                ivNextArrow.isVisible = false
                tvLiveRequestTimeslot.setTextColor(requireActivity().getColor(R.color.white))
                tvLiveRequest.setTextColor(requireActivity().getColor(R.color.black))
                tvLiveRequestTimeslot.setBackgroundResource(R.drawable.selected_left_border)
                tvLiveRequest.background = null
            }
            else{
                type = 2
                clSearch.isVisible = true
                llLiveRequest.isVisible = true
                btnSubmit.isVisible = false
                llTimeSlot.isVisible = false
                ivBackArrow.isVisible = true
                tvCount.isVisible = true
                ivNextArrow.isVisible = true
                tvLiveRequestTimeslot.setTextColor(requireActivity().getColor(R.color.black))
                tvLiveRequest.setTextColor(requireActivity().getColor(R.color.white))
                tvLiveRequest.setBackgroundResource(R.drawable.selected_right_border)
                tvLiveRequestTimeslot.background = null
            }

            when(liveRequestType){
                0-> {
                    liveRequestType = 0
                    getLiveRequest()
                    tvNew.setTextColor(Color.parseColor("#875C3A"))
                    viewNew.setBackgroundColor(Color.parseColor("#875C3A"))
                    tvApproved.setTextColor(Color.parseColor("#C0BBBB"))
                    viewApproved.setBackgroundColor(Color.parseColor("#C0BBBB"))
                    tvRejected.setTextColor(Color.parseColor("#C0BBBB"))
                    viewRejected.setBackgroundColor(Color.parseColor("#C0BBBB"))
                }

                1-> {
                    liveRequestType = 1
                    getLiveRequest()
                    tvNew.setTextColor(Color.parseColor("#C0BBBB"))
                    viewNew.setBackgroundColor(Color.parseColor("#C0BBBB"))
                    tvApproved.setTextColor(Color.parseColor("#875C3A"))
                    viewApproved.setBackgroundColor(Color.parseColor("#875C3A"))
                    tvRejected.setTextColor(Color.parseColor("#C0BBBB"))
                    viewRejected.setBackgroundColor(Color.parseColor("#C0BBBB"))
                }

                2-> {
                    liveRequestType = 1
                    getLiveRequest()
                    tvNew.setTextColor(Color.parseColor("#C0BBBB"))
                    viewNew.setBackgroundColor(Color.parseColor("#C0BBBB"))
                    tvApproved.setTextColor(Color.parseColor("#C0BBBB"))
                    viewApproved.setBackgroundColor(Color.parseColor("#C0BBBB"))
                    tvRejected.setTextColor(Color.parseColor("#875C3A"))
                    viewRejected.setBackgroundColor(Color.parseColor("#875C3A"))
                }
            }

            liveRequestAdapter = LiveRequestAdapter(requireActivity(),liveRequestList,object :LiveRequestAdapter.OnClick{
                override fun click(position: Int, clickType: Int) {
                    selectedPos = position
                    when(clickType){
                        1->{
                            acceptLiveRequest()
                        }
                        2->{
                            rejectLiveRequest("")
                        }
                    }
                }

            })
            rvLiveRequest.adapter = liveRequestAdapter

            etSearch.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    val query = s.toString().trim()
                    if (query == lastQuery) return
                    lastQuery = query
                    searchBanner?.cancel()
                    searchBanner = lifecycleScope.launch {
                        delay(600)
                        currentPage = 1
                        pagination = false
                        if (!isObserver)
                            getLiveRequest(search = query.ifEmpty { null })
                    }

                }
            })
        }
    }

    override fun initControl() {
        binding?.apply {
            toolbar.tvToolbar.setOnClickListener {
                (requireActivity() as? HomeActivity)?.onBackPressed()
            }

            etCountry.setOnClickListener{
                CommonMethod.showCountryPicker(
                    context = requireContext(),
                    anchor = it,
                    countryList = countryList,
                    isDialCode = false
                ) { selectedCountry ->
                    etCountry.setText(selectedCountry.name)
                    countryId = selectedCountry.id.toString()
                }
            }

            tvLiveRequestTimeslot.setOnClickListener {
                type = 1
                clSearch.isVisible = false
                llLiveRequest.isVisible = false
                llTimeSlot.isVisible = true
                tvLiveRequestTimeslot.setTextColor(requireActivity().getColor(R.color.white))
                tvLiveRequest.setTextColor(requireActivity().getColor(R.color.black))
                tvLiveRequestTimeslot.setBackgroundResource(R.drawable.selected_left_border)
                tvLiveRequest.background = null
            }

            tvLiveRequest.setOnClickListener {
                type = 2
                clSearch.isVisible = true
                llLiveRequest.isVisible = true
                llTimeSlot.isVisible = false
                tvLiveRequestTimeslot.setTextColor(requireActivity().getColor(R.color.black))
                tvLiveRequest.setTextColor(requireActivity().getColor(R.color.white))
                tvLiveRequest.setBackgroundResource(R.drawable.selected_right_border)
                tvLiveRequestTimeslot.background = null
            }

            tvNew.setOnClickListener {
                liveRequestType = 0
                getLiveRequest()
                tvNew.setTextColor(Color.parseColor("#875C3A"))
                viewNew.setBackgroundColor(Color.parseColor("#875C3A"))
                tvApproved.setTextColor(Color.parseColor("#C0BBBB"))
                viewApproved.setBackgroundColor(Color.parseColor("#C0BBBB"))
                tvRejected.setTextColor(Color.parseColor("#C0BBBB"))
                viewRejected.setBackgroundColor(Color.parseColor("#C0BBBB"))
            }

            tvApproved.setOnClickListener {
                liveRequestType = 1
                getLiveRequest()
                tvNew.setTextColor(Color.parseColor("#C0BBBB"))
                viewNew.setBackgroundColor(Color.parseColor("#C0BBBB"))
                tvApproved.setTextColor(Color.parseColor("#875C3A"))
                viewApproved.setBackgroundColor(Color.parseColor("#875C3A"))
                tvRejected.setTextColor(Color.parseColor("#C0BBBB"))
                viewRejected.setBackgroundColor(Color.parseColor("#C0BBBB"))
            }

            tvRejected.setOnClickListener {
                liveRequestType = 2
                getLiveRequest()
                tvNew.setTextColor(Color.parseColor("#C0BBBB"))
                viewNew.setBackgroundColor(Color.parseColor("#C0BBBB"))
                tvApproved.setTextColor(Color.parseColor("#C0BBBB"))
                viewApproved.setBackgroundColor(Color.parseColor("#C0BBBB"))
                tvRejected.setTextColor(Color.parseColor("#875C3A"))
                viewRejected.setBackgroundColor(Color.parseColor("#875C3A"))
            }

            ivNextArrow.setOnClickListener {
                if (!isObserver && currentPage < lastPage && totalItem != 0) {
                    currentPage += 1
                    getLiveRequest(false)
                    binding?.progressBar?.isVisible = true
                }


            }

            ivBackArrow.setOnClickListener {
                if (currentPage > 1) {
                    currentPage--
                    getLiveRequest(false)
                    binding?.progressBar?.isVisible = true
                }
            }

        }
    }

    override fun observe() {
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                binding?.progressBar?.isVisible = false
                pagination = false
                isObserver = false
            }
        }

        viewModel.getLiveRequestResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                binding?.progressBar?.isVisible = false
                currentPage = response.data.currentPage
                totalItem = response.data.total

                lastPage = response.data.lastPage

                val currentStr = if (currentPage < 10) "0$currentPage" else "$currentPage"
                val lastStr = if (lastPage < 10) "0$lastPage" else "$lastPage"

                binding?.tvCount?.text = "$currentStr of $lastStr"

                if (currentPage < lastPage) {
                    binding?.ivNextArrow?.setImageResource(R.drawable.ic_next_brand)
                } else {
                    binding?.ivNextArrow?.setImageResource(R.drawable.ic_next_brand_disable)
                }

                if (currentPage > 1) {
                    binding?.ivBackArrow?.setImageResource(R.drawable.ic_back_brand)
                } else {
                    binding?.ivBackArrow?.setImageResource(R.drawable.ic_back_brand_disable)
                }


                val oldSize = liveRequestList.size
                if (!pagination) {
                    liveRequestList.clear()
                }
                updatePage()
                val newItems = response.data.data ?: emptyList()

                liveRequestList.addAll(newItems)
                checkData()

                if (pagination) {

                    response.data.data?.size?.let {
                        liveRequestAdapter?.notifyItemRangeInserted(
                            oldSize,
                            it
                        )
                    }
                } else {
                    liveRequestAdapter?.notifyDataSetChanged()
                }

                pagination = false
                isObserver = false
            }
        }

    }

    private fun getLiveRequest(loading: Boolean = true, search: String? = lastQuery) {
        viewModel.getLiveRequest(
            liveRequestType,
            search = search,
            countryId,
            page = currentPage
        )
        if (loading) {
            ProgressDialogUtils.getInstance().showProgress(requireActivity())
        }
        isObserver = true
    }

    private fun acceptLiveRequest(){
        val map = HashMap<String, Any>()
        map["request_id"] = liveRequestList[selectedPos].id
        viewModel.acceptLiveRequest(map)
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

    private fun rejectLiveRequest(reason:String){
        val map = HashMap<String, Any>()
        map["request_id"] = liveRequestList[selectedPos].id
        map["reject_reason"] = reason
        viewModel.rejectLiveRequest(map)
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

    private fun checkData() {
        binding?.tvNoDataFound?.isVisible = liveRequestList.isEmpty()
    }

    private fun updatePage() {
        val start = (currentPage - 1) * itemsPerPage
        val end = minOf(start + itemsPerPage, liveRequestList.size)
        if (start >= 0 && end >= 0) {
            if (start < end) {
                val pageData = liveRequestList.subList(start, end)
                liveRequestAdapter?.setData(ArrayList(pageData))
            }
        }
    }


}