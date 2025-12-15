package com.desynatadmin.ui.home.orderManagement

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
import com.desynatadmin.R
import com.desynatadmin.adapter.AffiliateCommissionAdapter
import com.desynatadmin.adapter.OrderAdapter
import com.desynatadmin.base.BaseFragment
import com.desynatadmin.data.view_model.HomeViewModel
import com.desynatadmin.databinding.FragmentOrderManagementBinding
import com.desynatadmin.databinding.UserFilterDialogBinding
import com.desynatadmin.ui.home.HomeActivity
import com.desynatadmin.ui.home.subscriptionPlan.response.GetCountryResponse
import com.desynatadmin.utils.CommonMethod
import com.desynatadmin.utils.ProgressDialogUtils
import com.desynatadmin.utils.openCustomDatePicker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class OrderManagementFragment : BaseFragment() {
    private var binding: FragmentOrderManagementBinding?=null
    private val viewModel: HomeViewModel by viewModels()
    private var isObserver = true
    private var orderList = ArrayList<String>()
    private var orderAdapter : OrderAdapter?=null

    private var type = 1 //1-active,2-exchange,3-complete,4-cancelled

    private var currentPage = 1
    private var lastPage = 1
    private var totalPage = 0
    private var totalItem=0
    private var pageLimit=10
    private var isFromSwipe=false
    private var pagination=false
    private val itemsPerPage = 10

    private var search: Job? = null
    private var lastQuery: String? = null

    var startDate:String?=null
    var endDate:String?=null
    var fromTimestamp = 0L
    var toTimestamp = 0L
    var countryId:String?=null
    private var countryList = ArrayList<GetCountryResponse.Data>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOrderManagementBinding.inflate(layoutInflater,container,false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initControl()
        observe()
    }

    override fun initView() {
        ProgressDialogUtils.getInstance().hideProgress()
        binding?.apply {
            toolbar.tvToolbar.text = "Orders"

            orderList.clear()
            orderAdapter = OrderAdapter(requireActivity(),orderList,object : OrderAdapter.OnClick{
                override fun onItemClick(position: Int,type:Int) {
                }

            })

            rvOrder.adapter = orderAdapter

            swipeRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(requireActivity(), R.color.brown)
            )

            swipeRefreshLayout.setOnRefreshListener {
                isFromSwipe = true
                pagination = false
                currentPage = 1
                getOrder(false)
            }

            etSearch.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    val query = s.toString().trim()
                    if (query == lastQuery) return
                    lastQuery = query
                    search?.cancel()
                    search = lifecycleScope.launch {
                        delay(600)
                        isFromSwipe = false
                        if (!isObserver)
                            getOrder(search =  query.ifEmpty { null })
                    }

                }
            })

            when(type){
                1->{
                    getOrder()
                    tvActive.setTextColor(requireActivity().getColor(R.color.white))
                    tvReturnExchange.setTextColor(requireActivity().getColor(R.color.black))
                    tvCompleted.setTextColor(requireActivity().getColor(R.color.black))
                    tvCancelled.setTextColor(requireActivity().getColor(R.color.black))
                    tvActive.setBackgroundResource(R.drawable.selected_left_gradient)
                    tvReturnExchange.background = null
                    tvCompleted.background = null
                    tvCancelled.background = null
                }
                2->{
                    getOrder()
                    tvReturnExchange.setTextColor(requireActivity().getColor(R.color.white))
                    tvActive.setTextColor(requireActivity().getColor(R.color.black))
                    tvCompleted.setTextColor(requireActivity().getColor(R.color.black))
                    tvCancelled.setTextColor(requireActivity().getColor(R.color.black))
                    tvReturnExchange.setBackgroundResource(R.drawable.selected_mid_gradient)
                    tvActive.background = null
                    tvCompleted.background = null
                    tvCancelled.background = null
                }

                3->{
                    getOrder()
                    tvCompleted.setTextColor(requireActivity().getColor(R.color.white))
                    tvActive.setTextColor(requireActivity().getColor(R.color.black))
                    tvReturnExchange.setTextColor(requireActivity().getColor(R.color.black))
                    tvCancelled.setTextColor(requireActivity().getColor(R.color.black))
                    tvCompleted.setBackgroundResource(R.drawable.selected_mid_gradient)
                    tvActive.background = null
                    tvReturnExchange.background = null
                    tvCancelled.background = null
                }
                4->{
                getOrder()
                tvCancelled.setTextColor(requireActivity().getColor(R.color.white))
                tvActive.setTextColor(requireActivity().getColor(R.color.black))
                tvCompleted.setTextColor(requireActivity().getColor(R.color.black))
                tvReturnExchange.setTextColor(requireActivity().getColor(R.color.black))
                tvCancelled.setBackgroundResource(R.drawable.selected_right_gradient)
                tvActive.background = null
                tvCompleted.background = null
                tvReturnExchange.background = null
                }
            }

        }

        getOrder()
    }

    override fun initControl() {
        binding?.apply {
            toolbar.tvToolbar.setOnClickListener {
                (requireActivity() as? HomeActivity)?.onBackPressed()
            }

            ivNextArrow.setOnClickListener {
                if (!isObserver && currentPage < lastPage&&totalItem!=0) {
                    currentPage += 1
                    getOrder(false)
                    binding?.progressBar?.isVisible = true
                }


            }

            ivBackArrow.setOnClickListener {
                if (currentPage > 1) {
                    currentPage--
                    getOrder(false)
                    binding?.progressBar?.isVisible = true
                }
            }

            ivFilter.setOnClickListener {
                filterDialog()
            }

            tvActive.setOnClickListener {
                type = 1
                getOrder()
                tvActive.setTextColor(requireActivity().getColor(R.color.white))
                tvReturnExchange.setTextColor(requireActivity().getColor(R.color.black))
                tvCompleted.setTextColor(requireActivity().getColor(R.color.black))
                tvCancelled.setTextColor(requireActivity().getColor(R.color.black))
                tvActive.setBackgroundResource(R.drawable.selected_left_gradient)
                tvReturnExchange.background = null
                tvCompleted.background = null
                tvCancelled.background = null
            }

            tvReturnExchange.setOnClickListener {
                type = 2
                getOrder()
                tvReturnExchange.setTextColor(requireActivity().getColor(R.color.white))
                tvActive.setTextColor(requireActivity().getColor(R.color.black))
                tvCompleted.setTextColor(requireActivity().getColor(R.color.black))
                tvCancelled.setTextColor(requireActivity().getColor(R.color.black))
                tvReturnExchange.setBackgroundResource(R.drawable.selected_mid_gradient)
                tvActive.background = null
                tvCompleted.background = null
                tvCancelled.background = null
            }

            tvCompleted.setOnClickListener {
                type = 3
                getOrder()
                tvCompleted.setTextColor(requireActivity().getColor(R.color.white))
                tvActive.setTextColor(requireActivity().getColor(R.color.black))
                tvReturnExchange.setTextColor(requireActivity().getColor(R.color.black))
                tvCancelled.setTextColor(requireActivity().getColor(R.color.black))
                tvCompleted.setBackgroundResource(R.drawable.selected_mid_gradient)
                tvActive.background = null
                tvReturnExchange.background = null
                tvCancelled.background = null
            }

            tvCancelled.setOnClickListener {
                type = 4
                getOrder()
                tvCancelled.setTextColor(requireActivity().getColor(R.color.white))
                tvActive.setTextColor(requireActivity().getColor(R.color.black))
                tvCompleted.setTextColor(requireActivity().getColor(R.color.black))
                tvReturnExchange.setTextColor(requireActivity().getColor(R.color.black))
                tvCancelled.setBackgroundResource(R.drawable.selected_right_gradient)
                tvActive.background = null
                tvCompleted.background = null
                tvReturnExchange.background = null
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

        viewModel.getOrderListResult.observe(viewLifecycleOwner) { response ->
//            if (isObserver) {
//                ProgressDialogUtils.getInstance().hideProgress()
//                binding?.progressBar?.isVisible = false
//                if (isFromSwipe){
//                    binding?.swipeRefreshLayout?.isRefreshing = false
//                }
//
//                currentPage = response.data.currentPage
//                totalItem = response.data.total
//
//                lastPage = response.data.lastPage
//
//                val currentStr = if (currentPage < 10) "0$currentPage" else "$currentPage"
//                val lastStr = if (lastPage < 10) "0$lastPage" else "$lastPage"
//
//                binding?.tvCount?.text = "$currentStr of $lastStr"
//
//                if (currentPage<lastPage){
//                    binding?.ivNextArrow?.setImageResource(R.drawable.ic_next_brand)
//                }
//                else{
//                    binding?.ivNextArrow?.setImageResource(R.drawable.ic_next_brand_disable)
//                }
//
//                if (currentPage>1){
//                    binding?.ivBackArrow?.setImageResource(R.drawable.ic_back_brand)
//                }
//                else{
//                    binding?.ivBackArrow?.setImageResource(R.drawable.ic_back_brand_disable)
//                }
//
//
//                val oldSize = bannerRequestList.size
//                if (!pagination){
//                    bannerRequestList.clear()
//                }
//                updatePage()
//                val newItems = response.data.data ?: emptyList()
//
//                bannerRequestList.addAll(newItems)
//                checkData()
//
//                if (pagination){
//
//                    response.data.data?.size?.let {
//                        bannerRequestAdapter?.notifyItemRangeInserted(oldSize,
//                            it
//                        )
//                    }
//                }
//                else{
//                    bannerRequestAdapter?.notifyDataSetChanged()
//                }
//
//                pagination = false
//                isFromSwipe = false
//                isObserver = false
//            }
        }
    }


    private fun checkData(){
        binding?.tvNoDataFound?.isVisible = orderList.isEmpty()
    }


    private fun updatePage() {
        val start = (currentPage - 1) * itemsPerPage
        val end = minOf(start + itemsPerPage, orderList.size)
        if (start>=0&&end>=0) {
            if (start < end) {
                val pageData = orderList.subList(start, end)
                orderAdapter?.setData(ArrayList(pageData))
            }
        }

    }

    private fun getOrder(loading:Boolean=true,search:String?=null) {
        viewModel.getOrderList(type,search = search, fromDate = startDate, toDate = endDate, countryId = countryId,currentPage)
        if (loading){
            ProgressDialogUtils.getInstance().showProgress(requireActivity())
        }
        isObserver = true
    }

    private fun filterDialog() {
        var selectedCountryId:String?=null
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
            if(countryId!=null){
                etCountry.text = countryList.firstOrNull { it.id.toString() == countryId }?.name
            }
            if (startDate!=null){
                etFrom.text= CommonMethod.formatDate(startDate.toString(),"yyyy-MM-dd","dd/MM/yyyy")
            }
            if (endDate!=null){
                etTo.text= CommonMethod.formatDate(endDate.toString(),"yyyy-MM-dd","dd/MM/yyyy")
            }
            tvToolbar.setOnClickListener {
                dialog.dismiss()
            }

            etFrom.setOnClickListener {
                openCustomDatePicker(requireActivity()){date,timestamp->
                    etFrom.text=date
                    fromTimestamp=timestamp
                }

            }
            etTo.setOnClickListener {
                openCustomDatePicker(requireActivity(), minDate = fromTimestamp){date,timestamp->
                    etTo.text=date
                    toTimestamp=timestamp
                }

            }

            tvClear.setOnClickListener {
                startDate = null
                endDate = null
                countryId = null
                toTimestamp = 0L
                fromTimestamp = 0L
                getOrder()
                dialog.dismiss()
            }

            etCountry.setOnClickListener {
                CommonMethod.showCountryPicker(
                    requireContext(),
                    it,
                    countryList
                ) { selectedCountry ->
                    etCountry.text = selectedCountry.name
                    selectedCountryId = selectedCountry.id.toString()
                }
            }

            tvApply.setOnClickListener{
                if (selectedCountryId!=null) {
                    countryId = selectedCountryId
                }
                if (etFrom.text.isNotEmpty()){
                    startDate = CommonMethod.formatDate(etFrom.text.toString(),"dd/MM/yyyy","yyyy-MM-dd")
                }

                if (etTo.text.isNotEmpty()){
                    endDate = CommonMethod.formatDate(etTo.text.toString(),"dd/MM/yyyy","yyyy-MM-dd")
                }
                getOrder()
                dialog.dismiss()
            }

        }

        dialog.show()
    }


}