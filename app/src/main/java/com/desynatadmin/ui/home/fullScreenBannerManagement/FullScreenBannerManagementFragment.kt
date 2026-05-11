package com.desynatadmin.ui.home.fullScreenBannerManagement

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
import com.desynatadmin.adapter.FullScreenBannerAdapter
import com.desynatadmin.ui.home.SubAdminHomeActivity
import com.desynatadmin.adapter.PromotionalBannerAdapter
import com.desynatadmin.base.BaseFragment
import com.desynatadmin.data.view_model.HomeViewModel
import com.desynatadmin.databinding.BannerFilterDialogBinding
import com.desynatadmin.databinding.FragmentFullScreenBannerManagementBinding
import com.desynatadmin.ui.home.HomeActivity
import com.desynatadmin.ui.home.fullScreenBannerManagement.response.GetFullScreenBannerResponse
import com.desynatadmin.ui.home.fullScreenBannerManagement.response.GetPromotionalBannerResponse
import com.desynatadmin.ui.home.subscriptionPlan.response.GetCountryResponse
import com.desynatadmin.utils.CommonMethod
import com.desynatadmin.utils.Constants
import com.desynatadmin.utils.ProgressDialogUtils
import com.desynatadmin.utils.SharedPreference
import com.desynatadmin.utils.openCustomDatePicker
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FullScreenBannerManagementFragment : BaseFragment() {
    private var binding: FragmentFullScreenBannerManagementBinding? = null
    private val viewModel: HomeViewModel by viewModels()
    private var isObserver = true
    private var isCountryObserver = true
    private var bannerList = ArrayList<GetFullScreenBannerResponse.Data.Data>()
    private var promotionBannerList = ArrayList<GetPromotionalBannerResponse.Data.Data>()
    private var bannerAdapter: FullScreenBannerAdapter? = null
    private var promotionalBannerAdapter: PromotionalBannerAdapter? = null

    private var currentPage = 1
    private var lastPage = 1
    private var totalPage = 0
    private var totalItem = 0
    private var pageLimit = 10
    private var isFromSwipe = false
    private var pagination = false
    private var searchBanner: Job? = null
    private var lastQuery: String? = null
    private val itemsPerPage = 10
    var startDate: String? = null
    var endDate: String? = null
    var fromTimestamp = 0L
    var toTimestamp = 0L
    var countryId: String? = null

    var startDate1: String? = null
    var endDate1: String? = null
    var fromTimestamp1 = 0L
    var toTimestamp1 = 0L
    private var selectedBannerType: String? = null
    private var selectedBannerType1: String? = null
    private var type = 1
    private var countryList = ArrayList<GetCountryResponse.Data>()
    var actionAccess: Boolean = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            FragmentFullScreenBannerManagementBinding.inflate(layoutInflater, container, false)
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
            ProgressDialogUtils.getInstance().hideProgress()
            CommonMethod.removeSpace(etSearch)
            toolbar.tvToolbar.text = getString(R.string.banner_management)

            val userInfo = SharedPreference.get(requireActivity()).getUser()
            if (userInfo?.type=="2"){
                val subAdminInfo = SharedPreference.get(requireActivity()).getSubAdmin()
                val module = subAdminInfo?.permissionDetails
                    ?.firstOrNull {
                        it.moduleDetails.name.equals("full_screen_banner_management", true)
                    }

                actionAccess = module?.canAction == 1
                ivAddBanner.isVisible = actionAccess

            }

            bannerList.clear()
            bannerAdapter = FullScreenBannerAdapter(requireActivity(), bannerList, object :
                FullScreenBannerAdapter.BannerClick {
                override fun onBannerClick(position: Int) {
                    val bundle = Bundle().apply {
                        putString("bannerData", Gson().toJson(bannerList[position]))
                    }
                    findNavController().navigate(
                        R.id.action_fullScreenBanner_to_fullScreenBannerDetailFragment,
                        bundle
                    )
                }

            })

            rvBanner.adapter = bannerAdapter

            promotionBannerList.clear()
            promotionalBannerAdapter = PromotionalBannerAdapter(requireActivity(), promotionBannerList, object :
                PromotionalBannerAdapter.BannerClick {
                override fun onBannerClick(position: Int) {
                    val bundle = Bundle().apply {
                        putString("bannerData", Gson().toJson(promotionBannerList[position]))
                    }
                    findNavController().navigate(
                        R.id.action_fullScreenBanner_to_promotionalBannerDetailFragment,
                        bundle
                    )
                }

            })

            rvPromotionalBanner.adapter = promotionalBannerAdapter

            swipeRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(requireActivity(), R.color.brown)
            )

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
                        isFromSwipe = false
                        if (!isObserver)
                            if (type==2){
                                getPromotionalBanner(loading = false, search = query.ifEmpty { null })

                            }
                        else{
                                getAllBanner(loading = false, search = query.ifEmpty { null })

                            }
                    }

                }
            })

            swipeRefreshLayout.setOnRefreshListener {
                isFromSwipe = true
                pagination = false
                currentPage = 1
                if (type==2){
                    getPromotionalBanner(false)
                }
                else{
                    getAllBanner(false)
                }
            }

            if (type==1){
                currentPage = 1
                type = 1
                getAllBanner()
                tvFullScreen.setTextColor(requireActivity().getColor(R.color.white))
                tvPromotional.setTextColor(requireActivity().getColor(R.color.black))
                tvFullScreen.setBackgroundResource(R.drawable.selected_left_border)
                tvPromotional.background = null
            }
            else{
                currentPage = 1
                type = 2
                getPromotionalBanner()
                tvFullScreen.setTextColor(requireActivity().getColor(R.color.black))
                tvPromotional.setTextColor(requireActivity().getColor(R.color.white))
                tvPromotional.setBackgroundResource(R.drawable.selected_right_border)
                tvFullScreen.background = null
            }
        }

        if (SharedPreference.get(requireActivity()).getUserCountryList() == null) {
            getCountry()
        } else {
            countryList.clear()
            SharedPreference.get(requireActivity()).getUserCountryList()
                ?.let { countryList.addAll(it) }
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


            tvFullScreen.setOnClickListener {
                currentPage = 1
                type = 1
                getAllBanner()
                tvFullScreen.setTextColor(requireActivity().getColor(R.color.white))
                tvPromotional.setTextColor(requireActivity().getColor(R.color.black))
                tvFullScreen.setBackgroundResource(R.drawable.selected_left_border)
                tvPromotional.background = null
            }

            tvPromotional.setOnClickListener {
                currentPage = 1
                type = 2
                getPromotionalBanner()
                tvFullScreen.setTextColor(requireActivity().getColor(R.color.black))
                tvPromotional.setTextColor(requireActivity().getColor(R.color.white))
                tvPromotional.setBackgroundResource(R.drawable.selected_right_border)
                tvFullScreen.background = null
            }

            ivFilter.setOnClickListener {
                filterDialog()
            }

            ivAddBanner.setOnClickListener {
                if (type==2){
                    findNavController().navigate(R.id.action_fullScreenBanner_to_addPromotionalBannerFragment)
                }
                else {
                    findNavController().navigate(R.id.action_fullScreenBanner_to_addFullScreenBannerFragment)
                }
            }

            ivNextArrow.setOnClickListener {
                if (!isObserver && currentPage < lastPage && totalItem != 0) {
                    currentPage += 1
                    if (type==2){
                        getPromotionalBanner(false)
                    }
                    else{
                        getAllBanner(false)
                    }
                    binding?.progressBar?.isVisible = true
                }


            }

            ivBackArrow.setOnClickListener {
                if (currentPage > 1) {
                    currentPage--
                    if (type==2){
                        getPromotionalBanner(false)
                    }
                    else{
                        getAllBanner(false)
                    }
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
                if (isFromSwipe) {
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

        viewModel.getFullScreenBannerResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                binding?.progressBar?.isVisible = false
                if (isFromSwipe) {
                    binding?.swipeRefreshLayout?.isRefreshing = false
                }

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


                val oldSize = bannerList.size
                if (!pagination) {
                    bannerList.clear()
                }
                updatePage()
                val newItems = response.data.data ?: emptyList()

                bannerList.addAll(newItems)
                checkData()

                if (pagination) {

                    response.data.data?.size?.let {
                        bannerAdapter?.notifyItemRangeInserted(
                            oldSize,
                            it
                        )
                    }
                } else {
                    bannerAdapter?.notifyDataSetChanged()
                }

                pagination = false
                isFromSwipe = false
                isObserver = false
            }
        }

        viewModel.getPromotionalBannerResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                binding?.progressBar?.isVisible = false
                if (isFromSwipe) {
                    binding?.swipeRefreshLayout?.isRefreshing = false
                }

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


                val oldSize = promotionBannerList.size
                if (!pagination) {
                    promotionBannerList.clear()
                }
                updatePage()
                val newItems = response.data.data ?: emptyList()

                promotionBannerList.addAll(newItems)
                checkPromotionalData()

                if (pagination) {

                    response.data.data?.size?.let {
                        promotionalBannerAdapter?.notifyItemRangeInserted(
                            oldSize,
                            it
                        )
                    }
                } else {
                    promotionalBannerAdapter?.notifyDataSetChanged()
                }

                pagination = false
                isFromSwipe = false
                isObserver = false
            }
        }

    }

    private fun getAllBanner(loading: Boolean = true, search: String? = lastQuery) {
        viewModel.getFullScreenBanner(
            search = search,
            countryId,
            when (selectedBannerType){
                "app"-> "1"
                "web"-> "2"
                else -> null
            },
            startDate,
            endDate,
            page = currentPage
        )
        if (loading) {
            ProgressDialogUtils.getInstance().showProgress(requireActivity())
        }
        isObserver = true
    }
    private fun getPromotionalBanner(loading: Boolean = true, search: String? = lastQuery) {
        val map = HashMap<String, Any>()
        if (search!=null) {
            map["search"] = search
        }
        if (startDate1!=null) {
            map["start_date"] = startDate1.toString()
        }
        if (endDate1!=null) {
            map["end_date"] = endDate1.toString()
        }
        if (selectedBannerType1!=null) {
            map["banner_position"] =
                when (selectedBannerType1) {
                    "top" -> "1"
                    "bottom" -> "2"
                    "category" -> "3"
                    else -> "1"
                }
        }
        map["page"] = currentPage
        map["limit"] = 10
        viewModel.getPromotionalBanner(map)
        if (loading) {
            ProgressDialogUtils.getInstance().showProgress(requireActivity())
        }
        isObserver = true
    }

    private fun checkData() {
        binding?.tvNoDataFound?.isVisible = bannerList.isEmpty()
        binding?.rvBanner?.isVisible = bannerList.isNotEmpty()
        binding?.rvPromotionalBanner?.isVisible = false
    }

 private fun checkPromotionalData() {
        binding?.tvNoDataFound?.isVisible = promotionBannerList.isEmpty()
        binding?.rvPromotionalBanner?.isVisible = promotionBannerList.isNotEmpty()
     binding?.rvBanner?.isVisible = false
    }

    private fun updatePage() {
        val start = (currentPage - 1) * itemsPerPage
        val end = minOf(start + itemsPerPage, bannerList.size)
        if (start >= 0 && end >= 0) {
            if (start < end) {
                val pageData = bannerList.subList(start, end)
                bannerAdapter?.setData(ArrayList(pageData))
            }
        }
    }

    private fun filterDialog() {
        var selectedCountryId: String? = null
        var selectedBannerTypeTemp: String? = null
        val dialog = Dialog(requireActivity(), R.style.CustomDialog)
        val dialogBinding = BannerFilterDialogBinding.inflate(LayoutInflater.from(context))
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

            if (type==2){
                tvType.text = getString(R.string.banner_position)
                etCountry.isVisible = false
                tvCountry.isVisible = false
                rbBoth.text = getString(R.string.home_top)
                rbApp.text = getString(R.string.bottom)
                rbWeb.text = getString(R.string.category)

                when (selectedBannerType1) {
                    "top" -> rbBoth.isChecked = true
                    "bottom" -> rbApp.isChecked = true
                    "category" -> rbWeb.isChecked = true
                }
                if (startDate1 != null) {
                    etFrom.text =
                        CommonMethod.formatDate(startDate1.toString(), "yyyy-MM-dd", "dd/MM/yyyy")
                }
                if (endDate1 != null) {
                    etTo.text = CommonMethod.formatDate(endDate1.toString(), "yyyy-MM-dd", "dd/MM/yyyy")
                }
            }
            else{
                tvType.text = getString(R.string.type)
                rbBoth.text = getString(R.string.both)
                rbApp.text = getString(R.string.app_banner)
                rbWeb.text = getString(R.string.web_banner)
                when (selectedBannerType) {
                    "both" -> rbBoth.isChecked = true
                    "app" -> rbApp.isChecked = true
                    "web" -> rbWeb.isChecked = true
                }

                if (countryId != null) {
                    etCountry.text = countryList.firstOrNull { it.id.toString() == countryId }?.name
                }
                if (startDate != null) {
                    etFrom.text =
                        CommonMethod.formatDate(startDate.toString(), "yyyy-MM-dd", "dd/MM/yyyy")
                }
                if (endDate != null) {
                    etTo.text = CommonMethod.formatDate(endDate.toString(), "yyyy-MM-dd", "dd/MM/yyyy")
                }
            }

            tvToolbar.setOnClickListener {
                dialog.dismiss()
            }

            rgType.setOnCheckedChangeListener { _, checkedId ->
                selectedBannerTypeTemp = when (checkedId) {
                    R.id.rbBoth -> if (type==2) "top" else "both"
                    R.id.rbApp ->  if (type==2) "bottom" else "app"
                    R.id.rbWeb ->  if (type==2) "category" else "web"
                    else -> null
                }
            }

            etFrom.setOnClickListener {
                openCustomDatePicker(requireActivity()) { date, timestamp ->
                    etFrom.text = date
                    if (type==2){
                        fromTimestamp1 = timestamp
                    }
                    else{
                        fromTimestamp = timestamp
                    }
                }

            }
            etTo.setOnClickListener {
                openCustomDatePicker(
                    requireActivity(),
                    minDate = if (type==2) fromTimestamp1 else fromTimestamp
                ) { date, timestamp ->
                    etTo.text = date
                    if (type==2){
                        toTimestamp1 = timestamp
                    }
                    else{
                        toTimestamp = timestamp
                    }
                }

            }

            tvClear.setOnClickListener {
                if (type==2){
                    startDate1 = null
                    endDate1 = null
                    selectedBannerType1 = null
                    toTimestamp1 = 0L
                    fromTimestamp1 = 0L
                    getPromotionalBanner()
                }
                else{
                    startDate = null
                    endDate = null
                    countryId = null
                    selectedBannerType = null
                    toTimestamp = 0L
                    fromTimestamp = 0L
                    getAllBanner()
                }

                dialog.dismiss()
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

            tvApply.setOnClickListener {
                if (type==2){
                    if (selectedBannerTypeTemp != null) {
                        selectedBannerType1 = selectedBannerTypeTemp
                    }
                    if (etFrom.text.isNotEmpty()) {
                        startDate1 =
                            CommonMethod.formatDate(
                                etFrom.text.toString(),
                                "dd/MM/yyyy",
                                "yyyy-MM-dd"
                            )
                    }

                    if (etTo.text.isNotEmpty()) {
                        endDate1 =
                            CommonMethod.formatDate(
                                etTo.text.toString(),
                                "dd/MM/yyyy",
                                "yyyy-MM-dd"
                            )
                    }
                    getPromotionalBanner()
                }
                else {

                    if (selectedCountryId != null) {
                        countryId =
                            countryList.firstOrNull { it.id.toString() == selectedCountryId }?.name
                    }
                    if (selectedBannerTypeTemp != null) {
                        selectedBannerType = selectedBannerTypeTemp
                    }
                    if (etFrom.text.isNotEmpty()) {
                        startDate =
                            CommonMethod.formatDate(
                                etFrom.text.toString(),
                                "dd/MM/yyyy",
                                "yyyy-MM-dd"
                            )
                    }

                    if (etTo.text.isNotEmpty()) {
                        endDate =
                            CommonMethod.formatDate(
                                etTo.text.toString(),
                                "dd/MM/yyyy",
                                "yyyy-MM-dd"
                            )
                    }
                    getAllBanner()
                }
                dialog.dismiss()
            }

        }

        dialog.show()
    }


    private fun getCountry() {
        viewModel.getUserCountry()
        isCountryObserver = true
    }


}