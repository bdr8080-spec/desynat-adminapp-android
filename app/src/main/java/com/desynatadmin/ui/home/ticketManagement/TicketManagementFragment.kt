package com.desynatadmin.ui.home.ticketManagement

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.desynatadmin.ui.home.HomeActivity
import com.desynatadmin.ui.home.ticketManagement.response.GetNewTicketResponse
import com.desynatadmin.utils.ProgressDialogUtils
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

            when(type){
                1-> new()
                2-> ongoing()
                3-> past()
            }
        }
    }

    override fun initControl() {
        binding?.apply {
            toolbar.tvToolbar.setOnClickListener {
                (requireActivity() as? HomeActivity)?.onBackPressed()
            }

            ivFilter.setOnClickListener {

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

                binding?.tvCount?.text = "$currentStr of $lastStr"

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

                binding?.tvCount?.text = "$currentStr of $lastStr"

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

                binding?.tvCount?.text = "$currentStr of $lastStr"

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

        map["from_date"] = ""
        map["to_date"] = ""

       when(type){
           1->{
               viewModel.getNewTicket(map)
           }
           2->{
               viewModel.getOngoingTicket(map)
           }
           3->{
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
}