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
import com.desynatadmin.adapter.FullScreenBannerAdapter.BannerClick
import com.desynatadmin.base.BaseFragment
import com.desynatadmin.data.view_model.HomeViewModel
import com.desynatadmin.databinding.FragmentFullScreenBannerManagementBinding
import com.desynatadmin.databinding.UserFilterDialogBinding
import com.desynatadmin.ui.home.HomeActivity
import com.desynatadmin.ui.home.fullScreenBannerManagement.response.GetFullScreenBannerResponse
import com.desynatadmin.ui.home.subscriptionPlan.response.GetCountryResponse
import com.desynatadmin.utils.CommonMethod
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
    private var bannerAdapter: FullScreenBannerAdapter? = null

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
    private var countryList = ArrayList<GetCountryResponse.Data>()
    private var isLoad = false

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
            toolbar.tvToolbar.text = "Full Screen Banner Management"

            bannerList.clear()
            bannerAdapter = FullScreenBannerAdapter(requireActivity(), bannerList, object :
                BannerClick {
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
                            getAllBanner(search = query.ifEmpty { null })
                    }

                }
            })

            swipeRefreshLayout.setOnRefreshListener {
                isFromSwipe = true
                pagination = false
                currentPage = 1
                getAllBanner(false)
            }
        }

        if (!isLoad) {
            isLoad = true
            getAllBanner()
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
            toolbar.tvToolbar.setOnClickListener {
                (requireActivity() as? HomeActivity)?.onBackPressed()
            }

            ivFilter.setOnClickListener {
                filterDialog()
            }

            ivAddBanner.setOnClickListener {
                findNavController().navigate(R.id.action_fullScreenBanner_to_addFullScreenBannerFragment)
            }

            ivNextArrow.setOnClickListener {
                if (!isObserver && currentPage < lastPage && totalItem != 0) {
                    currentPage += 1
                    getAllBanner(false)
                    binding?.progressBar?.isVisible = true
                }


            }

            ivBackArrow.setOnClickListener {
                if (currentPage > 1) {
                    currentPage--
                    getAllBanner(false)
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

    }

    private fun getAllBanner(loading: Boolean = true, search: String? = lastQuery) {
        viewModel.getFullScreenBanner(
            search = search,
            countryId,
            startDate,
            endDate,
            page = currentPage
        )
        if (loading) {
            ProgressDialogUtils.getInstance().showProgress(requireActivity())
        }
        isObserver = true
    }

    private fun checkData() {
        binding?.tvNoDataFound?.isVisible = bannerList.isEmpty()
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
                etTo.text = CommonMethod.formatDate(endDate.toString(), "yyyy-MM-dd", "dd/MM/yyyy")
            }
            tvToolbar.setOnClickListener {
                dialog.dismiss()
            }

            etFrom.setOnClickListener {
                openCustomDatePicker(requireActivity()) { date, timestamp ->
                    etFrom.text = date
                    fromTimestamp = timestamp
                }

            }
            etTo.setOnClickListener {
                openCustomDatePicker(
                    requireActivity(),
                    minDate = fromTimestamp
                ) { date, timestamp ->
                    etTo.text = date
                    toTimestamp = timestamp
                }

            }

            tvClear.setOnClickListener {
                startDate = null
                endDate = null
                countryId = null
                toTimestamp = 0L
                fromTimestamp = 0L
                getAllBanner()
                dialog.dismiss()
            }

            etCountry.setOnClickListener {
                CommonMethod.showAllCountryPicker(
                    requireContext(),
                    it,
                    countryList
                ) { selectedCountry ->
                    etCountry.text = selectedCountry.name
                    selectedCountryId = selectedCountry.id.toString()
                }
            }

            tvApply.setOnClickListener {
                if (selectedCountryId != null) {
                    countryId =
                        countryList.firstOrNull { it.id.toString() == selectedCountryId }?.name
                }
                if (etFrom.text.isNotEmpty()) {
                    startDate =
                        CommonMethod.formatDate(etFrom.text.toString(), "dd/MM/yyyy", "yyyy-MM-dd")
                }

                if (etTo.text.isNotEmpty()) {
                    endDate =
                        CommonMethod.formatDate(etTo.text.toString(), "dd/MM/yyyy", "yyyy-MM-dd")
                }
                getAllBanner()
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