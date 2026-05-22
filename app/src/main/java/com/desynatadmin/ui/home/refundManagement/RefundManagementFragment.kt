package com.desynatadmin.ui.home.refundManagement

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
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.desynatadmin.R
import com.desynatadmin.adapter.RefundAdapter
import com.desynatadmin.base.BaseFragment
import com.desynatadmin.data.view_model.HomeViewModel
import com.desynatadmin.databinding.FragmentRefundManagementBinding
import com.desynatadmin.databinding.RefundFilterDialogBinding
import com.desynatadmin.ui.home.HomeActivity
import com.desynatadmin.ui.home.SubAdminHomeActivity
import com.desynatadmin.ui.home.refundManagement.response.GetRefundOrderResponse
import com.desynatadmin.ui.home.subscriptionPlan.response.GetCountryResponse
import com.desynatadmin.utils.CommonMethod
import com.desynatadmin.utils.Constants
import com.desynatadmin.utils.ProgressDialogUtils
import com.desynatadmin.utils.SharedPreference
import com.desynatadmin.utils.openCustomDatePicker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.getValue

@AndroidEntryPoint
class RefundManagementFragment : BaseFragment() {
    private var binding: FragmentRefundManagementBinding?=null
    private val viewModel: HomeViewModel by viewModels()
    private var isObserver = true

    private var isCountryObserver = true
    private var refundList = ArrayList<GetRefundOrderResponse.Data.Data>()
    private var refundAdapter : RefundAdapter?=null
    private var searchJob: Job? = null
    private var lastQuery: String? = null

    private var currentPage = 1
    private var lastPage = 1
    private var totalPage = 0
    private var totalItem=0
    private var pageLimit=10
    private var isFromSwipe=false
    private var pagination=false
    private val itemsPerPage = 10
    var selectedPos = -1
    private var countryList = ArrayList<GetCountryResponse.Data>()
    var countryId:String?=null
    var startDate:String?=null
    var endDate:String?=null
    var status:String?=null
    var fromTimestamp=0L
    var toTimestamp=0L

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRefundManagementBinding.inflate(layoutInflater,container,false)
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
            toolbar.tvToolbar.text = getString(R.string.refund_management)

            refundAdapter = RefundAdapter(requireActivity(),refundList,object : RefundAdapter.OnClick{
                override fun OnItemClick(position: Int) {

                }

            })
            rvRefund.adapter = refundAdapter

            lastQuery = etSearch.text.toString().trim()
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
                            getRefundList(search =  query.ifEmpty { null }, loading = false)
                    }

                }
            })

            swipeRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(requireActivity(), R.color.brown)
            )

            swipeRefreshLayout.setOnRefreshListener {
                isFromSwipe = true
                pagination = false
                currentPage = 1
                getRefundList(false)
            }
        }

        if (SharedPreference.get(requireActivity()).getUserCountryList()!=null){
            countryList.clear()
            SharedPreference.get(requireActivity()).getUserCountryList()?.let { countryList.addAll(it) }
        }
        else{
            getCountryApi()
        }

        getRefundList()

    }

    override fun initControl() {
        binding?.apply {

    toolbar.ivBack.setOnClickListener {
    when (val activity = requireActivity()) {
        is HomeActivity -> activity.onBackPressed()
        is SubAdminHomeActivity -> activity.onBackPressed()
        else -> activity.onBackPressed()
    }
}


            ivFilter.setOnClickListener {
                filterDialog()
            }

            btnExport.setOnClickListener {
                val csv = CommonMethod.listToCsv(refundList)
                CommonMethod.saveCsvFile(requireContext(), "refund_${System.currentTimeMillis()}", csv)
            }

            ivNextArrow.setOnClickListener {
                if (!isObserver && currentPage < lastPage&&totalItem!=0) {
                    currentPage += 1
                    getRefundList(false)
                    binding?.progressBar?.isVisible = true
                }
            }

            ivBackArrow.setOnClickListener {
                if (currentPage > 1) {
                    currentPage--
                    getRefundList(false)
                    binding?.progressBar?.isVisible = true
                }
            }
        }
    }

    private fun getCountryApi(){
        viewModel.getUserCountry()
        isCountryObserver = true
    }


    private fun getRefundList(loading:Boolean=true,search:String?=null){
        val map = HashMap<String,Any>()
        map["page"] = currentPage
        map["limit"] = 10
        if (search!=null) {
            map["search"] = search
        }
        if (status!=null) {
            map["status"] = status.toString()
        }
        if (startDate!=null) {
            map["from_date"] = startDate.toString()
        }
        if (endDate!=null) {
            map["to_date"] = endDate.toString()
        }
        if (countryId!=null) {
            map["country_name"] = countryList.firstOrNull { it.id.toString() == countryId }?.name.toString()
        }
        viewModel.getRefundOrder(map)
        if (loading){
            ProgressDialogUtils.getInstance().showProgress(requireActivity())
        }
        isObserver = true
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

        viewModel.getUserCountryResult.observe(viewLifecycleOwner) { response ->
            if (isCountryObserver) {
                countryList.clear()
                countryList.addAll(response.data)
                SharedPreference.get(requireActivity()).saveUserCountryList(countryList)
                isCountryObserver = false
            }
        }

                viewModel.getRefundOrderResult.observe(viewLifecycleOwner) { response ->
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


                val oldSize = refundList.size
                if (!pagination){
                    refundList.clear()
                }
                updatePage()
                val newItems = response.data.data ?: emptyList()

                refundList.addAll(newItems)
                checkData()

                if (pagination){

                    response.data.data?.size?.let {
                        refundAdapter?.notifyItemRangeInserted(oldSize,
                            it
                        )
                    }
                }
                else{
                    refundAdapter?.notifyDataSetChanged()
                }

                pagination = false
                isFromSwipe = false
                isObserver = false
            }
        }
    }

    private fun checkData() {
        binding?.tvNoDataFound?.isVisible = refundList.isEmpty()
    }

    private fun updatePage() {
        val start = (currentPage - 1) * itemsPerPage
        val end = minOf(start + itemsPerPage, refundList.size)
        if (start>=0&&end>=0) {
            if (start < end) {
                val pageData = refundList.subList(start, end)
                refundAdapter?.setData(ArrayList(pageData))
            }
        }

    }

    private fun filterDialog() {
        var selectedCountryId:String?=null
        var tempStatus:String?=null
        val dialog = Dialog(requireActivity(), R.style.CustomDialog)
        val dialogBinding = RefundFilterDialogBinding.inflate(LayoutInflater.from(context))
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
            if (countryId != null) {
                etCountry.text = countryList.firstOrNull { it.id.toString() == countryId }?.name
            }
            if (status != null) {
                tvStatusValue.text = status
                tempStatus = status
            }
            if (startDate != null) {
                etFrom.text =
                    CommonMethod.formatDate(startDate.toString(), "yyyy-MM-dd", "dd/MM/yyyy")
            }
            if (endDate != null) {
                etTo.text =
                    CommonMethod.formatDate(endDate.toString(), "yyyy-MM-dd", "dd/MM/yyyy")
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

            etCountry.setOnClickListener {
                CommonMethod.showAllCountryPicker(
                    requireContext(),
                    it,
                    countryList
                ) { selectedCountry ->
                    etCountry.text =if (Constants.language=="ar") selectedCountry.nameAr else   selectedCountry.name
                    selectedCountryId = selectedCountry.id.toString()
                }
            }

            tvStatusValue.setOnClickListener {
                showStatusDropdown(tvStatusValue){selectedStatus ->
                    tempStatus = selectedStatus
                }
            }

            tvClear.setOnClickListener {
                countryId = null
                startDate = null
                endDate = null
                status = null
                toTimestamp = 0L
                fromTimestamp = 0L
                getRefundList()
                dialog.dismiss()
            }

            tvApply.setOnClickListener{
                if (tempStatus!=null){
                    status = tempStatus
                }
                if (selectedCountryId!=null) {
                    countryId = selectedCountryId
                }
                if (etFrom.text.isNotEmpty()){
                    startDate = CommonMethod.formatDate(etFrom.text.toString(),"dd/MM/yyyy","yyyy-MM-dd")
                }

                if (etTo.text.isNotEmpty()){
                    endDate = CommonMethod.formatDate(etTo.text.toString(),"dd/MM/yyyy","yyyy-MM-dd")
                }

                getRefundList()
                dialog.dismiss()
            }

        }

        dialog.show()
    }

    private fun showStatusDropdown(view: TextView, onItemSelected: (String) -> Unit) {
        val statusList = arrayListOf("Pending", "Completed", "Rejected")

        val popup = androidx.appcompat.widget.ListPopupWindow(requireActivity())
        popup.anchorView = view
        popup.setAdapter(
            ArrayAdapter(requireActivity(), android.R.layout.simple_list_item_1, statusList)
        )

        popup.setOnItemClickListener { _, _, pos, _ ->
            val selectedStatus = statusList[pos]
            view.text = selectedStatus
            onItemSelected(selectedStatus)
            popup.dismiss()
        }

        popup.show()
    }

}