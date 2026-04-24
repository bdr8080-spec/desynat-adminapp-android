package com.desynatadmin.ui.home.sellerPayout

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
import com.desynatadmin.ui.home.SubAdminHomeActivity
import androidx.navigation.fragment.findNavController
import com.desynatadmin.R
import com.desynatadmin.adapter.SellerPayoutAdapter
import com.desynatadmin.base.BaseFragment
import com.desynatadmin.data.view_model.HomeViewModel
import com.desynatadmin.databinding.FragmentSellerPayoutManagementBinding
import com.desynatadmin.databinding.UserFilterDialogBinding
import com.desynatadmin.ui.home.HomeActivity
import com.desynatadmin.ui.home.subscriptionPlan.response.GetCountryResponse
import com.desynatadmin.utils.CommonMethod
import com.desynatadmin.utils.ProgressDialogUtils
import com.desynatadmin.utils.SharedPreference
import com.desynatadmin.utils.openCustomDatePicker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.getValue

@AndroidEntryPoint
class SellerPayoutManagementFragment : BaseFragment() {
    private var binding: FragmentSellerPayoutManagementBinding?=null
    private val viewModel: HomeViewModel by viewModels()
    private var isObserver = true
    private var searchJob: Job? = null
    private var lastQuery: String? = null
    private var sellerPayoutAdapter: SellerPayoutAdapter? = null
    private var payoutList = ArrayList<String>()
    private var isCountryObserver = true
    private var countryList = ArrayList<GetCountryResponse.Data>()

    private var currentPage = 1
    private var lastPage = 1
    private var totalPage = 0
    private var totalItem=0
    private var pageLimit=10
    private var isFromSwipe=false
    private var pagination=false
    private val itemsPerPage = 10

    var countryId:String?=null
    var startDate:String?=null
    var endDate:String?=null
    var fromTimestamp=0L
    var toTimestamp=0L

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSellerPayoutManagementBinding.inflate(layoutInflater,container,false)
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
            CommonMethod.removeSpace(etSearch)
            toolbar.tvToolbar.text = "Seller Payout"

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
                            getSellerPayoutList(search =  query.ifEmpty { null }, loading = false)
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
                getSellerPayoutList(false)
            }

            sellerPayoutAdapter = SellerPayoutAdapter(requireActivity(),payoutList,object :
                SellerPayoutAdapter.OnClick{
                override fun OnItemClick(position: Int) {
                    findNavController().navigate(R.id.action_sellerPayoutManagement_to_managePayoutFragment)
                }

            })
            rvSellerPayout.adapter = sellerPayoutAdapter

            if (SharedPreference.get(requireActivity()).getCountryList()!=null){
                countryList.clear()
                SharedPreference.get(requireActivity()).getCountryList()?.let { countryList.addAll(it) }
            }
            else{
                getCountryApi()
            }
        }
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

            ivNextArrow.setOnClickListener {
                if (!isObserver && currentPage < lastPage&&totalItem!=0) {
                    currentPage += 1
                    getSellerPayoutList(false)
                    binding?.progressBar?.isVisible = true
                }
            }

            ivBackArrow.setOnClickListener {
                if (currentPage > 1) {
                    currentPage--
                    getSellerPayoutList(false)
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

        viewModel.getCountryResult.observe(viewLifecycleOwner) { response ->
            if (isCountryObserver) {
                countryList.clear()
                countryList.addAll(response.data)
                SharedPreference.get(requireActivity()).saveUserCountryList(countryList)
                isCountryObserver = false
            }
        }

//        viewModel.getSellerPayoutListResult.observe(viewLifecycleOwner) { response ->
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
//                val oldSize = payoutList.size
//                if (!pagination){
//                    payoutList.clear()
//                }
//                updatePage()
//                val newItems = response.data.data ?: emptyList()
//
//                payoutList.addAll(newItems)
//                checkData()
//
//                if (pagination){
//
//                    response.data.data?.size?.let {
//                        sellerPayoutAdapter?.notifyItemRangeInserted(oldSize,
//                            it
//                        )
//                    }
//                }
//                else{
//                    sellerPayoutAdapter?.notifyDataSetChanged()
//                }
//
//                pagination = false
//                isFromSwipe = false
//                isObserver = false
//            }
//        }
    }

    private fun getCountryApi(){
        viewModel.getCountry()
        isCountryObserver = true
    }

    private fun getSellerPayoutList(loading:Boolean=true,search:String?=null){
        val map = HashMap<String,Any>()
        map["page"] = currentPage
        map["limit"] = 10
        if (search!=null) {
            map["search"] = search
        }
        if (startDate!=null) {
            map["from_date"] = startDate.toString()
        }
        if (endDate!=null) {
            map["to_date"] = endDate.toString()
        }
        if (countryId!=null) {
            map["country"] = countryList.firstOrNull { it.id.toString() == countryId }?.name.toString()
        }
   //     viewModel.getSellerPayoutList(map)
        if (loading){
            ProgressDialogUtils.getInstance().showProgress(requireActivity())
        }
        isObserver = true
    }


    private fun checkData() {
        binding?.tvNotData?.isVisible = payoutList.isEmpty()

    }

    private fun updatePage() {
        val start = (currentPage - 1) * itemsPerPage
        val end = minOf(start + itemsPerPage, payoutList.size)
        if (start>=0&&end>=0) {
            if (start < end) {
                val pageData = payoutList.subList(start, end)
                sellerPayoutAdapter?.setData(ArrayList(pageData))
            }
        }

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
                if (countryId != null) {
                    etCountry.text = countryList.firstOrNull { it.id.toString() == countryId }?.name
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
                CommonMethod.showCountryPicker(
                    requireContext(),
                    it,
                    countryList
                ) { selectedCountry ->
                    etCountry.text = selectedCountry.name
                    selectedCountryId = selectedCountry.id.toString()
                }
            }

            tvClear.setOnClickListener {
                countryId = null
                startDate = null
                endDate = null
                toTimestamp = 0L
                fromTimestamp = 0L
                getSellerPayoutList()
                dialog.dismiss()
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

                getSellerPayoutList()
                dialog.dismiss()
            }

        }

        dialog.show()
    }

}