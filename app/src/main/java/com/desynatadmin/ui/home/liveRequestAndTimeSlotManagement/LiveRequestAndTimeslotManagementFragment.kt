package com.desynatadmin.ui.home.liveRequestAndTimeSlotManagement

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.PopupWindow
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.desynatadmin.R
import com.desynatadmin.adapter.LiveRequestAdapter
import com.desynatadmin.adapter.LiveRequestCountryAdapter
import com.desynatadmin.adapter.TimeSlotAdapter
import com.desynatadmin.base.BaseFragment
import com.desynatadmin.data.view_model.HomeViewModel
import com.desynatadmin.databinding.AddTimeSlotDialogBinding
import com.desynatadmin.databinding.FragmentLiveRequestAndTimeslotManagementBinding
import com.desynatadmin.databinding.RadioCustomCountryPickerBinding
import com.desynatadmin.databinding.UserFilterDialogBinding
import com.desynatadmin.ui.home.HomeActivity
import com.desynatadmin.ui.home.liveRequestAndTimeSlotManagement.response.AddTimeSlotRequest
import com.desynatadmin.ui.home.liveRequestAndTimeSlotManagement.response.GetLiveRequestResponse
import com.desynatadmin.ui.home.liveRequestAndTimeSlotManagement.response.GetTimeSlotResponse
import com.desynatadmin.ui.home.subscriptionPlan.response.GetCountryResponse
import com.desynatadmin.utils.CommonMethod
import com.desynatadmin.utils.ProgressDialogUtils
import com.desynatadmin.utils.SharedPreference
import com.desynatadmin.utils.customCalendar.CalendarAdapter
import com.desynatadmin.utils.customCalendar.CalendarDay
import com.desynatadmin.utils.openCustomDatePicker
import com.desynatadmin.utils.showToast
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class LiveRequestAndTimeslotManagementFragment : BaseFragment() {
    private var binding: FragmentLiveRequestAndTimeslotManagementBinding?=null
    private val viewModel: HomeViewModel by viewModels()
    private var isObserver = true
    private var isLiveRequestObserver = true
    private var type = 1

    private var timeSlotList = ArrayList<GetTimeSlotResponse.Data>()
    private var timeSlotAdapter: TimeSlotAdapter?=null

    private var liveRequestType = 0 //0-new,1-accepted,2-rejected
    private var liveRequestList = ArrayList<GetLiveRequestResponse.Data.Data>()
    private var liveRequestAdapter: LiveRequestAdapter?=null
    private var liveRequestCountryAdapter: LiveRequestCountryAdapter?=null
    private var selectedPos = -1
    private var selectedTimeSlotPos = -1

    private var currentPage = 1
    private var lastPage = 1
    private var totalPage = 0
    private var totalItem = 0
    private var pageLimit = 10
    private var pagination = false
    private var searchBanner: Job? = null
    private var lastQuery: String? = null
    private val itemsPerPage = 10
    var selectedCountryIdSlotTime: GetCountryResponse.Data? = null
    var selectedCountryId: GetCountryResponse.Data? = null
    var timeSlotDate: String? = null
    private var selectedCalendar: Calendar? = null

    private var countryList = ArrayList<GetCountryResponse.Data>()

    var filterStartDate:String?=null
    var filterEndDate:String?=null
    var filterFromTimestamp = 0L
    var filterToTimestamp = 0L
    var filterCountryId:String?=null

    private val calendar = Calendar.getInstance()
    private lateinit var calendarAdapter: CalendarAdapter
    private val daysList = mutableListOf<CalendarDay>()

    private val months = listOf(
        "January", "February", "March", "April",
        "May", "June", "July", "August",
        "September", "October", "November", "December"
    )
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
                for (i in 0 until countryList.size) {
                    if (countryList[i].name == "Kuwait") {
                        selectedCountryId = countryList[i]
                        selectedCountryIdSlotTime = countryList[i]
                        etCountry.setText(countryList[i].name)
                    }
                }

            calendarAdapter = CalendarAdapter(daysList,calendar.get(Calendar.MONTH),
                calendar.get(Calendar.YEAR)) { selectedDay ->
                val selectedCal = calendar.clone() as Calendar
                selectedCal.set(Calendar.DAY_OF_MONTH, selectedDay.day)

                val format = SimpleDateFormat("EEE, MMM dd", Locale.getDefault())

                tvCalendarDate.text = format.format(selectedCal.time)
                val format1 = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
                tvSelectedDateToSlot.text = formatDateWithToday(selectedCal)
            }

            rvCalendar.layoutManager = GridLayoutManager(requireContext(), 7)
            rvCalendar.adapter = calendarAdapter

            setupCalendarAdapter()
            loadCalendar()

            val todayFormat = SimpleDateFormat("EEE, MMM dd", Locale.getDefault())
            binding?.tvCalendarDate?.text = todayFormat.format(Calendar.getInstance().time)
            val todayFormat1 = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
            binding?.tvSelectedDateToSlot?.text = "Today, "+todayFormat1.format(Calendar.getInstance().time)


            if (type==1){
                type = 1
                liveRequestType = 0
                binding?.etCountry?.setText(selectedCountryIdSlotTime?.name)
                getTimeSlot()
                clSearch.isVisible = false
                llLiveRequest.isVisible = false
                btnAddTimeSlot.isVisible = true
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
                binding?.etCountry?.setText(selectedCountryId?.name)
                clSearch.isVisible = true
                llLiveRequest.isVisible = true
                btnAddTimeSlot.isVisible = false
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

            timeSlotAdapter = TimeSlotAdapter(requireActivity(),timeSlotList,object :TimeSlotAdapter.OnClick{
                override fun click(position: Int, clickType: Int) {
                    selectedTimeSlotPos = position
                    when(clickType){
                        1->{
                            addTimeSlotDialog(true)

                        }
                        2->{
                            deleteTimeSlot()
                        }
                        3->{
                            updateTimeSlotStatus()
                        }
                    }
                }

            })
            rvTimeSlot.adapter = timeSlotAdapter

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
            toolbar.setOnClickListener {
                (requireActivity() as? HomeActivity)?.onBackPressed()
            }

            ivFilter.setOnClickListener {
                filterDialog()
            }

            etCountry.setOnClickListener{
                showCustomCountryPicker(tvCountry)
//                if (rvCountry.isVisible){
//                    rvCountry.isVisible = false
//                    viewCountry.isVisible = false
//                    etCountry.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_drop_down, 0)
//                }
//                else{
//                    rvCountry.isVisible = true
//                    viewCountry.isVisible = true
//                    etCountry.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.right_arrow, 0)
//                }

            }

            tvLiveRequestTimeslot.setOnClickListener {
                type = 1
                liveRequestType = 0
                getTimeSlot()
                binding?.etCountry?.setText(selectedCountryIdSlotTime?.name)
                clSearch.isVisible = false
                llLiveRequest.isVisible = false
                llTimeSlot.isVisible = true
                btnAddTimeSlot.isVisible = true
                tvLiveRequestTimeslot.setTextColor(requireActivity().getColor(R.color.white))
                tvLiveRequest.setTextColor(requireActivity().getColor(R.color.black))
                tvLiveRequestTimeslot.setBackgroundResource(R.drawable.selected_left_border)
                tvLiveRequest.background = null
            }

            tvLiveRequest.setOnClickListener {
                type = 2
                liveRequestType = 0
                getLiveRequest()
                binding?.etCountry?.setText(selectedCountryId?.name)
                clSearch.isVisible = true
                llLiveRequest.isVisible = true
                llTimeSlot.isVisible = false
                btnAddTimeSlot.isVisible = false
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

            btnAddTimeSlot.setOnClickListener {
                addTimeSlotDialog()
            }

            ivNext.setOnClickListener {
                calendar.add(Calendar.MONTH, 1)
                loadCalendar()
                setupCalendarAdapter()
            }

            ivPrevious.setOnClickListener {
                calendar.add(Calendar.MONTH, -1)
                loadCalendar()
                setupCalendarAdapter()
            }

            tvMonthYear.setOnClickListener {
                openMonthPopup(tvMonthYear)
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

        viewModel.getTimeSlotResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                timeSlotList.clear()
                timeSlotList.addAll(response.data)
                timeSlotAdapter?.notifyDataSetChanged()
                checkTimeSlotData()
                isObserver = false
            }
            }

        viewModel.addTimeSlotLiveDataResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                CommonMethod.showSuccessDialog(requireActivity(),response.message)
                var data = response.data
                data.status = 1
                timeSlotList.add(data)
                timeSlotAdapter?.notifyDataSetChanged()
                checkTimeSlotData()
                isObserver = false
            }
        }

        viewModel.updateTimeSlotLiveDataResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                CommonMethod.showSuccessDialog(requireActivity(),response.message)
                timeSlotList[selectedTimeSlotPos].from = response.data.from
                timeSlotList[selectedTimeSlotPos].to = response.data.to
                timeSlotAdapter?.notifyItemChanged(selectedTimeSlotPos)
                selectedTimeSlotPos = -1
                isObserver = false
            }
        }

        viewModel.updateTimeSlotStatusResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                CommonMethod.showSuccessDialog(requireActivity(),response.message)
                timeSlotList[selectedTimeSlotPos].status = response.data.status
                timeSlotAdapter?.notifyItemChanged(selectedTimeSlotPos)
                selectedTimeSlotPos = -1
                isObserver = false
            }
        }

        viewModel.deleteTimeSlotResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                CommonMethod.showSuccessDialog(requireActivity(),response.message)
                timeSlotList.removeAt(selectedTimeSlotPos)
                timeSlotAdapter?.notifyItemRemoved(selectedPos)
                timeSlotAdapter?.notifyItemRangeChanged(selectedPos, timeSlotList.size)
                checkTimeSlotData()
                selectedTimeSlotPos = -1
                isObserver = false
            }
        }

        viewModel.getLiveRequestResult.observe(viewLifecycleOwner) { response ->
            if (isLiveRequestObserver) {
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
                isLiveRequestObserver = false
            }
        }

        viewModel.acceptLiveRequestResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                CommonMethod.showSuccessDialog(requireActivity(),"Request accepted successfully!"){
                    getLiveRequest()
                }
                isObserver = false
            }
        }

        viewModel.rejectLiveRequestResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                CommonMethod.showSuccessDialog(requireActivity(),"Request rejected successfully!"){
                    getLiveRequest()
                }
                isObserver = false
            }
        }

    }

    private fun getLiveRequest(loading: Boolean = true, search: String? = lastQuery) {
        val map = HashMap<String,Any>()
        map["type"] = liveRequestType
        if (search!=null&&search!="") {
            map["search"] = search
        }

        if (filterStartDate!=null) {
            map["from_date"] = filterStartDate.toString()
        }
        if (filterEndDate!=null) {
            map["to_date"] = filterEndDate.toString()
        }
        if (filterCountryId!=null) {
            map["country"] = countryList.firstOrNull { it.id.toString() == filterCountryId }?.name.toString()
        }
      else{
            map["country"] = selectedCountryId?.name.toString()
        }
        map["page"] = currentPage
        map["limit"] = 10
        viewModel.getLiveRequest(
            map
        )
        if (loading) {
            ProgressDialogUtils.getInstance().showProgress(requireActivity())
        }
        isLiveRequestObserver = true
    }

    private fun getTimeSlot() {
        viewModel.getTimeSlot(
            selectedCountryIdSlotTime?.id.toString(),
            timeSlotDate.toString()
        )
            ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

    private fun addTimeSlot(fromTime: String, toTime: String){
        val request = AddTimeSlotRequest(
            countryId = selectedCountryIdSlotTime?.id.toString(),
            date = timeSlotDate.toString(),
            from = fromTime,
            to = toTime
        )
        viewModel.addTimeSlot(request)
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

    private fun editTimeSlot(fromTime: String, toTime: String) {
        val request = AddTimeSlotRequest(
            countryId = selectedCountryIdSlotTime?.id.toString(),
            date = timeSlotDate.toString(),
            from = fromTime,
            to = toTime,
            timeslotId = timeSlotList[selectedTimeSlotPos].id.toString()
        )
        viewModel.updateTimeSlot(request)
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

    private fun deleteTimeSlot(){
        val map = HashMap<String, Any>()
        map["timeslot_id"] = timeSlotList[selectedTimeSlotPos].id
        viewModel.deleteTimeSlot(map)
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

    private fun updateTimeSlotStatus(){
        val map = HashMap<String, Any>()
        map["timeslot_id"] = timeSlotList[selectedTimeSlotPos].id
        viewModel.updateTimeSlotStatus(map)
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
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

    private fun checkTimeSlotData() {
        binding?.tvNoSlots?.isVisible = timeSlotList.isEmpty()

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

    private fun addTimeSlotDialog(isEdit:Boolean = false) {
        val dialog = Dialog(requireActivity(), R.style.CustomDialog)
        val dialogBinding = AddTimeSlotDialogBinding.inflate(LayoutInflater.from(context))
        dialogBinding.root.let { dialog.setContentView(it) }

        val window: Window = dialog.window!!
        window.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        window.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(false)

        val params: WindowManager.LayoutParams = window.attributes
        params.gravity = Gravity.BOTTOM
        window.attributes = params

        dialogBinding.apply {

            if (isEdit){
                val from = parseTime(timeSlotList[selectedTimeSlotPos].from)
                val to = parseTime(timeSlotList[selectedTimeSlotPos].to)

                etFromHour.setText(from.hour)
                etFromMin.setText(from.minute)
                etFromType.setText(from.type)

                etToHour.setText(to.hour)
                etToMin.setText(to.minute)
                etToType.setText(to.type)
            }

            tvToolbar.setOnClickListener {
                dialog.dismiss()
            }

            etFromHour.setOnClickListener {
                showHourDropdown(it) { value ->
                    etFromHour.setText(value)
                }
            }

            etFromMin.setOnClickListener {
                showMinuteDropdown(it) { value ->
                    etFromMin.setText(value)
                }
            }

            etFromType.setOnClickListener {
                showTypeDropdown(it) { value ->
                    etFromType.setText(value)
                }
            }

            etToHour.setOnClickListener {
                showHourDropdown(it) { value ->
                    etToHour.setText(value)
                }
            }

            etToMin.setOnClickListener {
                showMinuteDropdown(it) { value ->
                    etToMin.setText(value)
                }
            }

            etToType.setOnClickListener {
                showTypeDropdown(it) { value ->
                    etToType.setText(value)
                }
            }


            btnSubmit.setOnClickListener {
                val fromHour = etFromHour.text.toString()
                val fromMin = etFromMin.text.toString()
                val fromType = etFromType.text.toString()

                val toHour = etToHour.text.toString()
                val toMin = etToMin.text.toString()
                val toType = etToType.text.toString()


                if (!isTimeValid(fromHour, fromMin, fromType, toHour, toMin, toType)) {
                    return@setOnClickListener
                }

                val fromTime = mergeTime(fromHour, fromMin, fromType)
                val toTime = mergeTime(toHour, toMin, toType)

                if (isEdit){
                    editTimeSlot(fromTime,toTime)
                }
                else{
                    addTimeSlot(fromTime,toTime)
                }
                dialog.dismiss()
            }

        }

        dialog.show()
    }

    private fun isTimeValid(
        fromHour: String,
        fromMin: String,
        fromType: String,
        toHour: String,
        toMin: String,
        toType: String
    ): Boolean {

        if (fromHour.isBlank()) {
            showToast("Please select From hour")
            return false
        }
        if (fromMin.isBlank()) {
            showToast("Please select From minutes")
            return false
        }
        if (fromType.isBlank()) {
            showToast("Please select From AM/PM")
            return false
        }

        if (toHour.isBlank()) {
            showToast("Please select To hour")
            return false
        }
        if (toMin.isBlank()) {
            showToast("Please select To minutes")
            return false
        }
        if (toType.isBlank()) {
            showToast("Please select To AM/PM")
            return false
        }

        val fromTime = convertToMinutes(fromHour, fromMin, fromType)
        val toTime = convertToMinutes(toHour, toMin, toType)

        if (fromTime >= toTime) {
            showToast("From time must be earlier than To time")
            return false
        }

        return true
    }

    private fun convertToMinutes(hour: String, min: String, type: String): Int {
        var h = hour.toInt()
        val m = min.toInt()

        if (type == "PM" && h != 12) h += 12
        if (type == "AM" && h == 12) h = 0

        return h * 60 + m
    }

    private fun mergeTime(hour: String, minute: String, type: String): String {
        return "${hour}:${minute} ${type}"
    }

    private fun parseTime(time: String): ParsedTime {
        // "05:00 AM"
        val parts = time.split(" ")
        val timePart = parts[0]        // 05:00
        val type = parts[1]            // AM

        val hm = timePart.split(":")
        val hour = hm[0]               // 05
        val minute = hm[1]             // 00

        return ParsedTime(hour, minute, type)
    }

    private fun updateMonthTitle() {
        val monthYearFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        binding?.tvMonthYear?.text = monthYearFormat.format(calendar.time)
    }

    private fun loadCalendar() {
        daysList.clear()

        val tempCalendar = calendar.clone() as Calendar
        tempCalendar.set(Calendar.DAY_OF_MONTH, 1)

        val today = Calendar.getInstance()
        val firstDayOfWeek = tempCalendar.get(Calendar.DAY_OF_WEEK) - 1
        val maxDays = tempCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)

        repeat(firstDayOfWeek) {
            daysList.add(CalendarDay(0, false))
        }

        for (day in 1..maxDays) {

            val dateCal = calendar.clone() as Calendar
            dateCal.set(Calendar.DAY_OF_MONTH, day)
            dateCal.set(Calendar.HOUR_OF_DAY, 0)
            dateCal.set(Calendar.MINUTE, 0)
            dateCal.set(Calendar.SECOND, 0)
            dateCal.set(Calendar.MILLISECOND, 0)

            val todayCal = today.clone() as Calendar
            todayCal.set(Calendar.HOUR_OF_DAY, 0)
            todayCal.set(Calendar.MINUTE, 0)
            todayCal.set(Calendar.SECOND, 0)
            todayCal.set(Calendar.MILLISECOND, 0)

            val isPastDate = dateCal.before(todayCal)

            val isSelected =
                selectedCalendar?.let {
                    it.get(Calendar.YEAR) == calendar.get(Calendar.YEAR) &&
                            it.get(Calendar.MONTH) == calendar.get(Calendar.MONTH) &&
                            it.get(Calendar.DAY_OF_MONTH) == day
                } ?: false

            val isToday =
                selectedCalendar == null &&
                        day == today.get(Calendar.DAY_OF_MONTH) &&
                        calendar.get(Calendar.MONTH) == today.get(Calendar.MONTH) &&
                        calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR)

            daysList.add(
                CalendarDay(
                    day = day,
                    isCurrentMonth = true,
                    isSelected = isSelected || isToday,
                    isDisabled = isPastDate
                )
            )
        }

        calendarAdapter.notifyDataSetChanged()
        updateMonthTitle()
    }

    private fun setupCalendarAdapter() {
        calendarAdapter = CalendarAdapter(
            daysList,
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.YEAR)
        ) { selectedDay ->

            selectedCalendar = calendar.clone() as Calendar
            selectedCalendar?.set(Calendar.DAY_OF_MONTH, selectedDay.day)

            val format = SimpleDateFormat("EEE, MMM dd", Locale.getDefault())
            binding?.tvCalendarDate?.text = format.format(selectedCalendar?.time)
            val format1 = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())

            binding?.tvSelectedDateToSlot?.text = format1.format(selectedCalendar?.time)

            timeSlotDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                .format(selectedCalendar?.time)
            getTimeSlot()
        }

        binding?.rvCalendar?.adapter = calendarAdapter
    }

    private fun openMonthYearPicker() {

        val constraints = CalendarConstraints.Builder()
            .setOpenAt(calendar.timeInMillis)
            .build()

        val picker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select Month & Year")
            .setTheme(R.style.MaterialCalendarTheme)
            .setCalendarConstraints(constraints)
            .build()

        picker.show(parentFragmentManager, "MONTH_YEAR_PICKER")

        picker.addOnPositiveButtonClickListener { selectedMillis ->

            val selectedCalendar = Calendar.getInstance()
            selectedCalendar.timeInMillis = selectedMillis

            calendar.set(Calendar.MONTH, selectedCalendar.get(Calendar.MONTH))
            calendar.set(Calendar.YEAR, selectedCalendar.get(Calendar.YEAR))

            setupCalendarAdapter()
            loadCalendar()
        }
    }

    private fun formatDateWithToday(calendar: Calendar): String {
        val today = Calendar.getInstance()

        val isToday =
            calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                    calendar.get(Calendar.MONTH) == today.get(Calendar.MONTH) &&
                    calendar.get(Calendar.DAY_OF_MONTH) == today.get(Calendar.DAY_OF_MONTH)

        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val monthYear = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
            .format(calendar.time)

        val suffix = getDaySuffix(day)

        return if (isToday) {
            "Today, $day$suffix $monthYear"
        } else {
            "$day$suffix $monthYear"
        }
    }

    private fun getDaySuffix(day: Int): String {
        return if (day in 11..13) {
            "th"
        } else {
            when (day % 10) {
                1 -> "st"
                2 -> "nd"
                3 -> "rd"
                else -> "th"
            }
        }
    }

    private fun openMonthPopup(anchor: View) {
        val popup = PopupMenu(requireContext(), anchor)

        months.forEachIndexed { index, month ->
            popup.menu.add(0, index, index, month)
        }

        popup.setOnMenuItemClickListener { item ->
            calendar.set(Calendar.MONTH, item.itemId)

            loadCalendar()
            true
        }

        popup.show()
    }


    data class ParsedTime(
        val hour: String,
        val minute: String,
        val type: String
    )

    private fun showHourDropdown(view: View, onSelect: (String) -> Unit) {
        val popup = PopupMenu(requireContext(), view)
        (1..12).forEach {
            popup.menu.add(String.format("%02d", it))
        }
        popup.setOnMenuItemClickListener {
            onSelect(it.title.toString())
            true
        }
        popup.show()
    }

    private fun showMinuteDropdown(view: View, onSelect: (String) -> Unit) {
        val popup = PopupMenu(requireContext(), view)
        (0..59).forEach {
            popup.menu.add(String.format("%02d", it))
        }
        popup.setOnMenuItemClickListener {
            onSelect(it.title.toString())
            true
        }
        popup.show()
    }

    private fun showTypeDropdown(view: View, onSelect: (String) -> Unit) {
        val popup = PopupMenu(requireContext(), view)
        popup.menu.add("AM")
        popup.menu.add("PM")
        popup.setOnMenuItemClickListener {
            onSelect(it.title.toString())
            true
        }
        popup.show()
    }

    private fun filterDialog() {
        var selectedFilterCountryId:String?=null
        val dialog = Dialog(requireActivity(), R.style.CustomDialog)
        val dialogBinding = UserFilterDialogBinding.inflate(LayoutInflater.from(context))
        dialogBinding.root.let { dialog.setContentView(it) }

        val window: Window = dialog.window!!
        window.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        window.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(false)

        val params: WindowManager.LayoutParams = window.attributes
        params.gravity = Gravity.BOTTOM
        window.attributes = params

        dialogBinding.apply {
            if(filterCountryId!=null){
                etCountry.text = countryList.firstOrNull { it.id.toString() == filterCountryId }?.name
            }
            if (filterStartDate!=null){
                etFrom.text= CommonMethod.formatDate(filterStartDate.toString(),"yyyy-MM-dd","dd/MM/yyyy")
            }
            if (filterEndDate!=null){
                etTo.text= CommonMethod.formatDate(filterEndDate.toString(),"yyyy-MM-dd","dd/MM/yyyy")
            }
            tvToolbar.setOnClickListener {
                dialog.dismiss()
            }

            etFrom.setOnClickListener {
                openCustomDatePicker(requireActivity()){date,timestamp->
                    etFrom.text=date
                    filterFromTimestamp=timestamp
                }

            }
            etTo.setOnClickListener {
                openCustomDatePicker(requireActivity(), minDate = filterFromTimestamp){date,timestamp->
                    etTo.text=date
                    filterToTimestamp=timestamp
                }

            }

            tvClear.setOnClickListener {
                filterStartDate = null
                filterEndDate = null
                filterCountryId = null
                filterToTimestamp = 0L
                filterFromTimestamp = 0L
                getLiveRequest()
                dialog.dismiss()
            }

            etCountry.setOnClickListener {
                CommonMethod.showCountryPicker(
                    requireContext(),
                    it,
                    countryList
                ) { selectedCountry ->
                    etCountry.text = selectedCountry.name
                    selectedFilterCountryId = selectedCountry.id.toString()
                }
            }

            tvApply.setOnClickListener{
                if (selectedCountryId!=null) {
                    filterCountryId = selectedFilterCountryId
                }
                if (etFrom.text.isNotEmpty()){
                    filterStartDate = CommonMethod.formatDate(etFrom.text.toString(),"dd/MM/yyyy","yyyy-MM-dd")
                }

                if (etTo.text.isNotEmpty()){
                    filterEndDate = CommonMethod.formatDate(etTo.text.toString(),"dd/MM/yyyy","yyyy-MM-dd")
                }
                getLiveRequest()
                dialog.dismiss()
            }

        }

        dialog.show()
    }

    private fun showCustomCountryPicker(anchor:View){
       val previousCountry:GetCountryResponse.Data? = if (type==1){
               selectedCountryIdSlotTime
           }
           else{
               selectedCountryId
           }

        val customBinding = RadioCustomCountryPickerBinding.inflate(LayoutInflater.from(context))

        val popupWindow = PopupWindow(
            customBinding.root,
            anchor.width,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            true
        )

        popupWindow.isOutsideTouchable = true
        popupWindow.elevation = 20f
        popupWindow.showAsDropDown(anchor)

        customBinding.etCountry.setText(selectedCountryId?.name)
        customBinding.etCountry.setOnClickListener{
            popupWindow.dismiss()
        }

        liveRequestCountryAdapter = LiveRequestCountryAdapter(requireActivity(),countryList,previousCountry,object :LiveRequestCountryAdapter.OnClick{
            override fun onClick(position: Int) {

                if (type==1){
                    selectedCountryIdSlotTime = countryList[position]
                    binding?.etCountry?.setText(selectedCountryIdSlotTime?.name)
                    getTimeSlot()
                }
                else{
                    selectedCountryId = countryList[position]
                    binding?.etCountry?.setText(selectedCountryId?.name)
                    getLiveRequest()
                }
                popupWindow.dismiss()

            }

        })
        customBinding.rvCountry.adapter = liveRequestCountryAdapter

    }

}