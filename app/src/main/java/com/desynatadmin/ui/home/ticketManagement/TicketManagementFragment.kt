package com.desynatadmin.ui.home.ticketManagement

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.desynatadmin.R
import com.desynatadmin.adapter.TicketAdapter
import com.desynatadmin.base.BaseFragment
import com.desynatadmin.data.view_model.HomeViewModel
import com.desynatadmin.databinding.FragmentTicketManagementBinding
import com.desynatadmin.databinding.UserFilterDialogBinding
import com.desynatadmin.ui.home.HomeActivity
import com.desynatadmin.ui.home.ticketManagement.response.GetNewTicketResponse
import com.desynatadmin.utils.CommonMethod
import com.desynatadmin.utils.ProgressDialogUtils
import com.desynatadmin.ui.home.SubAdminHomeActivity
import com.desynatadmin.utils.openCustomizeDatePicker
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.getValue

@AndroidEntryPoint
class TicketManagementFragment : BaseFragment() {
    private var binding: FragmentTicketManagementBinding? = null
    private val viewModel: HomeViewModel by viewModels()
    private var isObserver = true
    private var ticketAdapter: TicketAdapter?=null
    private var ticketList = ArrayList<GetNewTicketResponse.Data.Data>()
    private var type = 1

    private var currentPage = 1
    private var lastPage = 1
    private var totalPage = 0
    private var totalItem=0
    private var pageLimit=10
    private var isFromSwipe=false
    private var pagination=false
    private var searchJob: Job? = null
    private var lastQuery: String? = null
    private val itemsPerPage = 10

    var startDateNew:String?=null
    var endDateNew:String?=null
    var fromTimestampNew=0L
    var toTimestampNew=0L

    var startDateOngoing:String?=null
    var endDateOngoing:String?=null
    var fromTimestampOngoing=0L
    var toTimestampOngoing=0L

    var startDatePast:String?=null
    var endDatePast:String?=null
    var fromTimestampPast=0L
    var toTimestampPast=0L
    var isHandled = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            FragmentTicketManagementBinding.inflate(layoutInflater, container, false)
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


            val savedStateHandle = findNavController().currentBackStackEntry?.savedStateHandle

            val openPast = savedStateHandle?.get<Boolean>("openPastTab")

            if (openPast == true) {
                type = 3
                past()
            } else {
                when(type){
                    1->new()
                    2->ongoing()
                    3->past()
                }
            }

           savedStateHandle?.getLiveData<Boolean>("openPastTab")
                ?.observe(viewLifecycleOwner) { shouldOpenPast ->
                    if (shouldOpenPast == true) {
                        past()

                        savedStateHandle.remove<Boolean>("openPastTab")
                    }
                }

            toolbar.tvToolbar.text = getString(R.string.ticket_management)

            ticketAdapter =
                TicketAdapter(requireActivity(), ticketList, object : TicketAdapter.TicketClick {
                    override fun onTicketClick(position: Int,type: Int) {
                        val bundle = Bundle().apply {
                            putString("ticketData", Gson().toJson(ticketList[position]))
                        }
                        findNavController().navigate(R.id.action_ticketManagement_to_ticketDetailFragment,bundle)
                    }

                })
            rvTicket.adapter = ticketAdapter

            swipeRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(requireActivity(), R.color.brown)
            )

            swipeRefreshLayout.setOnRefreshListener {
                isFromSwipe = true
                pagination = false
                currentPage = 1
                getTicket(false)
            }

            etSearch.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    val query = s.toString().trim()
                    if (query == lastQuery) return
                    lastQuery = query
                    searchJob?.cancel()
                    searchJob = lifecycleScope.launch {
                        delay(600)
                        currentPage = 1
                        pagination = false
                        isFromSwipe = false
                        if (!isObserver)
                            getTicket(search =  query.ifEmpty { null })
                    }

                }
            })


        }
    }

    override fun initControl() {
        binding?.apply {
            toolbar.tvToolbar.setOnClickListener {
                 when (val activity = requireActivity()) {
        is HomeActivity -> activity.onBackPressed()
        is SubAdminHomeActivity -> activity.onBackPressed()
        else -> activity.onBackPressed()
    }
            }

            ivFilter.setOnClickListener {
                ticketFilterDialog()
            }

            tvNew.setOnClickListener {
                new()
            }

            tvOngoing.setOnClickListener {
                ongoing()
            }

            tvPast.setOnClickListener {
                past()
            }

            ivNextArrow.setOnClickListener {
                if (!isObserver && currentPage < lastPage&&totalItem!=0) {
                    currentPage += 1
                    getTicket(false)
                    binding?.progressBar?.isVisible = true
                }


            }

            ivBackArrow.setOnClickListener {
                if (currentPage > 1) {
                    currentPage--
                    getTicket(false)
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
                if (isFromSwipe){
                    binding?.swipeRefreshLayout?.isRefreshing = false
                }

                pagination = false
                isFromSwipe = false
                isObserver = false
            }
        }

        viewModel.getNewTicketResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                binding?.progressBar?.isVisible = false
                if (isFromSwipe){
                    binding?.swipeRefreshLayout?.isRefreshing = false
                }

                currentPage = response.data.currentPage
                totalItem = response.data.total

                lastPage = response.data.lastPage

                val currentStr = if (currentPage < 10) "0$currentPage" else "$currentPage"
                val lastStr = if (lastPage < 10) "0$lastPage" else "$lastPage"

                binding?.tvCount?.text = "$currentStr ${getString(R.string.of)} $lastStr"

                if (currentPage<lastPage){
                    binding?.ivNextArrow?.setImageResource(R.drawable.ic_next_brand)
                }
                else{
                    binding?.ivNextArrow?.setImageResource(R.drawable.ic_next_brand_disable)
                }

                if (currentPage>1){
                    binding?.ivBackArrow?.setImageResource(R.drawable.ic_back_brand)
                }
                else{
                    binding?.ivBackArrow?.setImageResource(R.drawable.ic_back_brand_disable)
                }


                val oldSize = ticketList.size
                if (!pagination){
                    ticketList.clear()
                }
                updatePage()
                val newItems = response.data.data ?: emptyList()

                ticketList.addAll(newItems)
                checkData()

                if (pagination){

                    response.data.data?.size?.let {
                        ticketAdapter?.notifyItemRangeInserted(oldSize,
                            it
                        )
                    }
                }
                else{
                    ticketAdapter?.notifyDataSetChanged()
                }

                pagination = false
                isFromSwipe = false
                isObserver = false
            }
        }

        viewModel.getOngoingTicketResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                binding?.progressBar?.isVisible = false
                if (isFromSwipe){
                    binding?.swipeRefreshLayout?.isRefreshing = false
                }

                currentPage = response.data.currentPage
                totalItem = response.data.total

                lastPage = response.data.lastPage

                val currentStr = if (currentPage < 10) "0$currentPage" else "$currentPage"
                val lastStr = if (lastPage < 10) "0$lastPage" else "$lastPage"

                binding?.tvCount?.text = "$currentStr ${getString(R.string.of)} $lastStr"

                if (currentPage<lastPage){
                    binding?.ivNextArrow?.setImageResource(R.drawable.ic_next_brand)
                }
                else{
                    binding?.ivNextArrow?.setImageResource(R.drawable.ic_next_brand_disable)
                }

                if (currentPage>1){
                    binding?.ivBackArrow?.setImageResource(R.drawable.ic_back_brand)
                }
                else{
                    binding?.ivBackArrow?.setImageResource(R.drawable.ic_back_brand_disable)
                }


                val oldSize = ticketList.size
                if (!pagination){
                    ticketList.clear()
                }
                updatePage()
                val newItems = response.data.data ?: emptyList()

                ticketList.addAll(newItems)
                checkData()

                if (pagination){

                    response.data.data?.size?.let {
                        ticketAdapter?.notifyItemRangeInserted(oldSize,
                            it
                        )
                    }
                }
                else{
                    ticketAdapter?.notifyDataSetChanged()
                }

                pagination = false
                isFromSwipe = false
                isObserver = false
            }
        }

        viewModel.getResolvedTicketResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                binding?.progressBar?.isVisible = false
                if (isFromSwipe){
                    binding?.swipeRefreshLayout?.isRefreshing = false
                }

                currentPage = response.data.currentPage
                totalItem = response.data.total

                lastPage = response.data.lastPage

                val currentStr = if (currentPage < 10) "0$currentPage" else "$currentPage"
                val lastStr = if (lastPage < 10) "0$lastPage" else "$lastPage"

                binding?.tvCount?.text = "$currentStr ${getString(R.string.of)} $lastStr"

                if (currentPage<lastPage){
                    binding?.ivNextArrow?.setImageResource(R.drawable.ic_next_brand)
                }
                else{
                    binding?.ivNextArrow?.setImageResource(R.drawable.ic_next_brand_disable)
                }

                if (currentPage>1){
                    binding?.ivBackArrow?.setImageResource(R.drawable.ic_back_brand)
                }
                else{
                    binding?.ivBackArrow?.setImageResource(R.drawable.ic_back_brand_disable)
                }


                val oldSize = ticketList.size
                if (!pagination){
                    ticketList.clear()
                }
                updatePage()
                val newItems = response.data.data ?: emptyList()

                ticketList.addAll(newItems)
                checkData()

                if (pagination){

                    response.data.data?.size?.let {
                        ticketAdapter?.notifyItemRangeInserted(oldSize,
                            it
                        )
                    }
                }
                else{
                    ticketAdapter?.notifyDataSetChanged()
                }

                pagination = false
                isFromSwipe = false
                isObserver = false
            }
        }

    }

    private fun new(){
        binding?.apply {
            type = 1
            currentPage = 1
            getTicket()
            tvNew.setTextColor(requireActivity().getColor(R.color.white))
            tvOngoing.setTextColor(requireActivity().getColor(R.color.black))
            tvPast.setTextColor(requireActivity().getColor(R.color.black))
            tvNew.setBackgroundResource(R.drawable.selected_left_border)
            tvOngoing.background = null
            tvPast.background = null
        }
    }

    private fun ongoing(){
        binding?.apply {
            type = 2
            currentPage = 1
            getTicket()
            tvOngoing.setTextColor(requireActivity().getColor(R.color.white))
            tvNew.setTextColor(requireActivity().getColor(R.color.black))
            tvPast.setTextColor(requireActivity().getColor(R.color.black))
            tvOngoing.setBackgroundResource(R.drawable.selected_mid_border)
            tvNew.background = null
            tvPast.background = null
        }
    }

    private fun past(){
        binding?.apply {
            type = 3
            currentPage = 1
            getTicket()
            tvPast.setTextColor(requireActivity().getColor(R.color.white))
            tvNew.setTextColor(requireActivity().getColor(R.color.black))
            tvOngoing.setTextColor(requireActivity().getColor(R.color.black))
            tvPast.setBackgroundResource(R.drawable.selected_right_border)
            tvNew.background = null
            tvOngoing.background = null

        }
    }

    private fun getTicket(loading:Boolean=true,search:String?=lastQuery){
        val map = HashMap<String, Any>()
        map["limit"] = 10
        map["page"] = currentPage
        if (search!=null){
            map["search"] = search
        }


       when(type){
           1->{
               if (startDateNew!=null){
                   map["from_date"] = startDateNew.toString()
               }
               if (endDateNew!=null){
                   map["to_date"] = endDateNew.toString()
               }
               viewModel.getNewTicket(map)
           }
           2->{
               if (startDateOngoing!=null){
                   map["from_date"] = startDateOngoing.toString()
               }
               if (endDateOngoing!=null){
                   map["to_date"] = endDateOngoing.toString()
               }
               viewModel.getOngoingTicket(map)
           }
           3->{
               if (startDatePast!=null){
                   map["from_date"] = startDatePast.toString()
               }
               if (endDatePast!=null){
                   map["to_date"] = endDatePast.toString()
               }
               viewModel.getResolvedTicket(map)
           }
       }
        isObserver = true
        if (loading){
            ProgressDialogUtils.getInstance().showProgress(requireActivity())
        }
    }

    private fun checkData(){
        binding?.tvNoDataFound?.isVisible = ticketList.isEmpty()
    }

    private fun updatePage() {
        val start = (currentPage - 1) * itemsPerPage
        val end = minOf(start + itemsPerPage, ticketList.size)
        if (start>=0&&end>=0) {
            if (start < end) {
                val pageData = ticketList.subList(start, end)
                ticketAdapter?.setData(ArrayList(pageData))
            }
        }

    }

    private fun ticketFilterDialog() {
        var selectedStartDate:String?=null
        var selectedEndDate:String?=null
        var selectedStartDateTimeStamp = 0L
        var selectedEndDateTimeStamp = 0L
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
            etCountry.isVisible = false
            tvCountry.isVisible = false

            when(type){
                1->{
                    selectedStartDate = startDateNew
                    selectedStartDateTimeStamp = fromTimestampNew
                    selectedEndDate = endDateNew
                    selectedEndDateTimeStamp = toTimestampNew
                }
                2->{
                    selectedStartDate = startDateOngoing
                    selectedStartDateTimeStamp = fromTimestampOngoing
                    selectedEndDate = endDateOngoing
                    selectedEndDateTimeStamp = toTimestampOngoing
                }
                3->{
                    selectedStartDate = startDatePast
                    selectedStartDateTimeStamp = fromTimestampPast
                    selectedEndDate = endDatePast
                    selectedEndDateTimeStamp = toTimestampPast
                }
            }
            if (selectedStartDate!=null){
                etFrom.text=CommonMethod.formatDate(selectedStartDate.toString(),"yyyy-MM-dd","dd/MM/yyyy")
            }
            if (selectedEndDate!=null){
                etTo.text=CommonMethod.formatDate(selectedEndDate.toString(),"yyyy-MM-dd","dd/MM/yyyy")
            }


            tvToolbar.setOnClickListener {
                dialog.dismiss()
            }

            etFrom.setOnClickListener {
                openCustomizeDatePicker(requireActivity(),maxDate = System.currentTimeMillis()){date,timestamp->
                    etFrom.text=date
                    selectedStartDateTimeStamp=timestamp
                }

            }
            etTo.setOnClickListener {
                openCustomizeDatePicker(requireActivity(), minDate = selectedStartDateTimeStamp,maxDate = System.currentTimeMillis()){date,timestamp->
                    etTo.text=date
                    selectedEndDateTimeStamp=timestamp
                }

            }

            tvClear.setOnClickListener {
                when(type) {
                    1 -> {
                        startDateNew = null
                        endDateNew = null
                        fromTimestampNew = 0L
                        toTimestampNew = 0L
                    }
                    2 -> {
                        startDateOngoing = null
                        endDateOngoing = null
                        fromTimestampOngoing = 0L
                        toTimestampOngoing = 0L
                    }
                    3 -> {
                        startDatePast = null
                        endDatePast = null
                        fromTimestampPast = 0L
                        toTimestampPast = 0L
                    }

                }
                getTicket()
                dialog.dismiss()
            }

            tvApply.setOnClickListener{
                if (etFrom.text.isNotEmpty()){
                    val startDate = CommonMethod.formatDate(etFrom.text.toString(),"dd/MM/yyyy","yyyy-MM-dd")
                    when(type) {
                        1 -> {
                            startDateNew = startDate
                        }
                        2 -> {
                            startDateOngoing = startDate
                        }
                        3 -> {
                            startDatePast = startDate
                        }

                    }
                }

                if (etTo.text.isNotEmpty()){
                    val endDate = CommonMethod.formatDate(etTo.text.toString(),"dd/MM/yyyy","yyyy-MM-dd")
                    when(type) {
                        1 -> {
                            endDateNew = endDate
                        }
                        2 -> {
                            endDateOngoing = endDate
                        }
                        3 -> {
                            endDatePast = endDate
                        }

                    }
                }

                getTicket()
                dialog.dismiss()
            }

        }

        dialog.show()
    }

}