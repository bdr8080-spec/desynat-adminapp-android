package com.desynatadmin.ui.home.bannerRequestManagement

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import androidx.fragment.app.Fragment
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
import com.desynatadmin.adapter.BannerRequestAdapter
import com.desynatadmin.base.BaseFragment
import com.desynatadmin.data.view_model.HomeViewModel
import com.desynatadmin.databinding.FragmentBannerRequestManagementBinding
import com.desynatadmin.databinding.RejectBannerReasonDialogBinding
import com.desynatadmin.databinding.UserFilterDialogBinding
import com.desynatadmin.ui.home.HomeActivity
import com.desynatadmin.ui.home.SubAdminHomeActivity
import com.desynatadmin.ui.home.bannerRequestManagement.response.GetBannerRequestResponse
import com.desynatadmin.ui.home.subscriptionPlan.response.GetCountryResponse
import com.desynatadmin.utils.CommonMethod
import com.desynatadmin.utils.Constants
import com.desynatadmin.utils.ProgressDialogUtils
import com.desynatadmin.utils.SharedPreference
import com.desynatadmin.utils.openCustomDatePicker
import com.desynatadmin.utils.showToast
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.getValue

@AndroidEntryPoint
class BannerRequestManagement1Fragment : BaseFragment() {
    private var binding: FragmentBannerRequestManagementBinding?=null
    private val viewModel: HomeViewModel by viewModels()
    private var isObserver = true
    private var type = 0 //0-new,1-approved,2-rejected

    private var bannerRequestAdapter: BannerRequestAdapter?=null
    private var bannerRequestList = ArrayList<GetBannerRequestResponse.Data.Data>()

    private var search: Job? = null
    private var lastQuery: String? = null

    private var currentPage = 1
    private var lastPage = 1
    private var totalPage = 0
    private var totalItem=0
    private var pageLimit=10
    private var isFromSwipe=false
    private var pagination=false
    private val itemsPerPage = 10

    var startDate:String?=null
    var endDate:String?=null
    var fromTimestamp = 0L
    var toTimestamp = 0L
    var countryId:String?=null
    private var countryList = ArrayList<GetCountryResponse.Data>()

    private var selectedPos = -1
    private var selectedType:String?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBannerRequestManagementBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initControl()
        observe()
    }

    override fun initView() {
        ProgressDialogUtils.getInstance().hideProgress()
        countryList.clear()
        SharedPreference.get(requireActivity()).getCountryList()?.let { countryList.addAll(it) }

        binding?.apply {
            toolbar.tvToolbar.text = getString(R.string.banner_requests)

            CommonMethod.removeSpace(etSearch)

            bannerRequestList.clear()
            bannerRequestAdapter = BannerRequestAdapter(requireActivity(),type,bannerRequestList,object : BannerRequestAdapter.OnClick{
                override fun onItemClick(position: Int,type:Int) {
                    selectedPos = position
                    when(type){
                        1-> acceptRequest()
                        2-> rejectReasonDialog()
                        3->{
                            val bundle = Bundle().apply {
                                putString("bannerData", Gson().toJson(bannerRequestList[position]))
                            }
                            findNavController().navigate(R.id.action_bannerRequest_to_bannerRequestDetailFragment,bundle)
                        }
                    }
                }

            })

            rvBannerRequest.adapter = bannerRequestAdapter

            when(type){
                0->{
                    getBannerRequest()
                    tvNew.setTextColor(requireActivity().getColor(R.color.white))
                    tvApproved.setTextColor(requireActivity().getColor(R.color.black))
                    tvRejected.setTextColor(requireActivity().getColor(R.color.black))
                    tvNew.setBackgroundResource(R.drawable.selected_left_border)
                    tvApproved.background = null
                    tvRejected.background = null
                }
                1->{
                    getBannerRequest()
                    tvApproved.setTextColor(requireActivity().getColor(R.color.white))
                    tvNew.setTextColor(requireActivity().getColor(R.color.black))
                    tvRejected.setTextColor(requireActivity().getColor(R.color.black))
                    tvApproved.setBackgroundResource(R.drawable.selected_mid_border)
                    tvNew.background = null
                    tvRejected.background = null
                }

                2->{
                    getBannerRequest()
                    tvRejected.setTextColor(requireActivity().getColor(R.color.white))
                    tvNew.setTextColor(requireActivity().getColor(R.color.black))
                    tvApproved.setTextColor(requireActivity().getColor(R.color.black))
                    tvRejected.setBackgroundResource(R.drawable.selected_right_border)
                    tvNew.background = null
                    tvApproved.background = null
                }
            }

            swipeRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(requireActivity(), R.color.brown)
            )

            swipeRefreshLayout.setOnRefreshListener {
                isFromSwipe = true
                pagination = false
                currentPage = 1
                getBannerRequest(false)
            }

            lastQuery = etSearch.text.toString().trim()
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
                            getBannerRequest(search =  query.ifEmpty { null })
                    }

                }
            })

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


            tvNew.setOnClickListener {
                type = 0
                getBannerRequest()
                tvNew.setTextColor(requireActivity().getColor(R.color.white))
                tvApproved.setTextColor(requireActivity().getColor(R.color.black))
                tvRejected.setTextColor(requireActivity().getColor(R.color.black))
                tvNew.setBackgroundResource(R.drawable.selected_left_border)
                tvApproved.background = null
                tvRejected.background = null
            }

            tvApproved.setOnClickListener {
                type = 1
                getBannerRequest()
                tvApproved.setTextColor(requireActivity().getColor(R.color.white))
                tvNew.setTextColor(requireActivity().getColor(R.color.black))
                tvRejected.setTextColor(requireActivity().getColor(R.color.black))
                tvApproved.setBackgroundResource(R.drawable.selected_mid_border)
                tvNew.background = null
                tvRejected.background = null
            }

            tvRejected.setOnClickListener {
                type = 2
                getBannerRequest()
                tvRejected.setTextColor(requireActivity().getColor(R.color.white))
                tvNew.setTextColor(requireActivity().getColor(R.color.black))
                tvApproved.setTextColor(requireActivity().getColor(R.color.black))
                tvRejected.setBackgroundResource(R.drawable.selected_right_border)
                tvNew.background = null
                tvApproved.background = null
            }

            ivNextArrow.setOnClickListener {
                if (!isObserver && currentPage < lastPage&&totalItem!=0) {
                    currentPage += 1
                    getBannerRequest(false)
                    binding?.progressBar?.isVisible = true
                }


            }

            ivBackArrow.setOnClickListener {
                if (currentPage > 1) {
                    currentPage--
                    getBannerRequest(false)
                    binding?.progressBar?.isVisible = true
                }
            }

            ivFilter.setOnClickListener {
                filterDialog()
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

        viewModel.approveBannerRequestResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                getBannerRequest()
            }
        }

        viewModel.rejectBannerRequestResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                getBannerRequest()
            }
        }

        viewModel.bannerRequestResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                binding?.progressBar?.isVisible = false
                if (isFromSwipe){
                    binding?.swipeRefreshLayout?.isRefreshing = false
                }

                if (selectedType!=null){
                    CommonMethod.showSuccessDialog(requireActivity(),if (selectedType=="accept") getString(
                        R.string.banner_request_accepted_successfully
                    ) else getString(R.string.banner_request_rejected_successfully)){
                        selectedType = null
                    }
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


                val oldSize = bannerRequestList.size
                if (!pagination){
                    bannerRequestList.clear()
                }
                updatePage()
                val newItems = response.data.data ?: emptyList()

                bannerRequestList.addAll(newItems)
                checkData()

                if (pagination){

                    response.data.data?.size?.let {
                        bannerRequestAdapter?.notifyItemRangeInserted(oldSize,
                            it
                        )
                    }
                }
                else{
                    bannerRequestAdapter?.setData(type,ArrayList(bannerRequestList))
                }

                pagination = false
                isFromSwipe = false
                isObserver = false
            }
        }
    }

    private fun checkData(){
        binding?.tvNoDataFound?.isVisible = bannerRequestList.isEmpty()
    }

    private fun getBannerRequest(loading:Boolean=true,search:String?=null) {
        viewModel.getBannerRequest(type,search = search, fromDate = startDate, toDate = endDate, countryId = countryList.firstOrNull { it.id.toString() == countryId }?.name,currentPage)
        if (loading){
            ProgressDialogUtils.getInstance().showProgress(requireActivity())
        }
        isObserver = true
    }

    private fun updatePage() {
        val start = (currentPage - 1) * itemsPerPage
        val end = minOf(start + itemsPerPage, bannerRequestList.size)
        if (start>=0&&end>=0) {
            if (start < end) {
                val pageData = bannerRequestList.subList(start, end)
                bannerRequestAdapter?.setData(type,ArrayList(pageData))
            }
        }

    }

    private fun acceptRequest(){
        selectedType = "accept"
        val map = HashMap<String,Any>()
        map["banner_id"] = bannerRequestList[selectedPos].id
        viewModel.approveBannerRequest(map)
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

    private fun rejectRequest(reason:String){
        selectedType = "reject"
        val map = HashMap<String,Any>()
        map["banner_id"] = bannerRequestList[selectedPos].id
        map["reason"] = reason
        viewModel.rejectBannerRequest(map)
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
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
                getBannerRequest()
                dialog.dismiss()
            }

            etCountry.setOnClickListener {
                CommonMethod.showCountryPicker(
                    requireContext(),
                    it,
                    countryList
                ) { selectedCountry ->
                    etCountry.text = if (Constants.language=="ar") selectedCountry.nameAr else selectedCountry.name
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
                getBannerRequest()
                dialog.dismiss()
            }

        }

        dialog.show()
    }

    private fun rejectReasonDialog() {
        val dialog = Dialog(requireActivity(), R.style.CustomDialog)
        val dialogBinding = RejectBannerReasonDialogBinding.inflate(LayoutInflater.from(context))
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


            tvToolbar.setOnClickListener {
                dialog.dismiss()
            }

            btnSubmit.setOnClickListener {
                if(etReason.text?.trim().toString().isEmpty()) {
                    showToast(getString(R.string.please_enter_reason))
                }
                else{
                    rejectRequest(etReason.text?.trim().toString())
                    dialog.dismiss()
                }
            }

        }

        dialog.show()
    }

}