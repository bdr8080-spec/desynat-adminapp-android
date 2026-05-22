package com.desynatadmin.ui.home.affiliateUserManagement

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
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.desynatadmin.R
import com.desynatadmin.adapter.AffiliateUserAdapter
import com.desynatadmin.base.BaseFragment
import com.desynatadmin.data.view_model.HomeViewModel
import com.desynatadmin.databinding.FragmentAffiliateUserManagementBinding
import com.desynatadmin.databinding.RejectReasonLayoutBinding
import com.desynatadmin.databinding.UserFilterDialogBinding
import com.desynatadmin.ui.home.HomeActivity
import com.desynatadmin.ui.home.SubAdminHomeActivity
import com.desynatadmin.ui.home.affiliateUserManagement.response.GetNewAffiliateUserResponse
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

@AndroidEntryPoint
class AffiliateUserManagementFragment : BaseFragment() {
    private var binding: FragmentAffiliateUserManagementBinding? = null
    private val viewModel: HomeViewModel by viewModels()
    private var isObserver = true
    private var isCountryObserver = true
    private var userList = ArrayList<GetNewAffiliateUserResponse.Data.Data>()
    private var affiliateUserAdapter: AffiliateUserAdapter? = null
    private var currentPage = 1
    private var lastPage = 1
    private var totalPage = 0
    private var totalItem = 0
    private var pageLimit = 10
    private var isFromSwipe = false
    private var pagination = false
    private var searchUser: Job? = null
    private var lastQuery: String? = null
    private val itemsPerPage = 10
    var selectedPos = -1
    private var countryList = ArrayList<GetCountryResponse.Data>()
    var countryId: String? = null
    var startDate: String? = null
    var endDate: String? = null
    var fromTimestamp = 0L
    var toTimestamp = 0L
    var userType = "1"
    var filterType = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAffiliateUserManagementBinding.inflate(layoutInflater, container, false)
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
            toolbar.tvToolbar.text = getString(R.string.affiliate_user_management)

            userList.clear()
            affiliateUserAdapter = AffiliateUserAdapter(
                requireActivity(),
                userList,
                object : AffiliateUserAdapter.AffiliateUserClick {
                    override fun onAffiliateUserClick(position: Int, type: Int) {
                        when (type) {
                            1 -> {
                                selectedPos = position
                                acceptAffiliateUser()

                            }

                            2 -> {
                                selectedPos = position
                                rejectReasonDialog()

                            }

                            3 -> {
                                val bundle = Bundle().apply {
                                    putString("userData", Gson().toJson(userList[position]))
                                    putString("userType", userType)
                                }
                                findNavController().navigate(
                                    R.id.action_affiliateUserManagement_to_affiliateUserDetailFragment,
                                    bundle
                                )
                            }
                        }

                    }

                })

            rvAffiliateUser.adapter = affiliateUserAdapter

            lastQuery = etSearch.text.toString().trim()
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
                    searchUser?.cancel()
                    searchUser = lifecycleScope.launch {
                        delay(600)
                        currentPage = 1
                        pagination = false
                        isFromSwipe = false
                        if (!isObserver)
                            getAllAffiliateUser(search = query.ifEmpty { null }, loading = false)
                    }

                }
            })

            when (userType) {
                "1" -> {
                    userType = "1"
                    getAllAffiliateUser()
                    affiliateUserAdapter?.setType(userType)
                    tvNew.setTextColor(Color.parseColor("#875C3A"))
                    viewNew.setBackgroundColor(Color.parseColor("#875C3A"))
                    tvApproved.setTextColor(Color.parseColor("#C0BBBB"))
                    viewApproved.setBackgroundColor(Color.parseColor("#C0BBBB"))
                    tvRejected.setTextColor(Color.parseColor("#C0BBBB"))
                    viewRejected.setBackgroundColor(Color.parseColor("#C0BBBB"))
                }

                "2" -> {
                    userType = "2"
                    getAllAffiliateUser()
                    affiliateUserAdapter?.setType(userType)
                    tvNew.setTextColor(Color.parseColor("#C0BBBB"))
                    viewNew.setBackgroundColor(Color.parseColor("#C0BBBB"))
                    tvApproved.setTextColor(Color.parseColor("#875C3A"))
                    viewApproved.setBackgroundColor(Color.parseColor("#875C3A"))
                    tvRejected.setTextColor(Color.parseColor("#C0BBBB"))
                    viewRejected.setBackgroundColor(Color.parseColor("#C0BBBB"))
                }

                "3" -> {
                    userType = "3"
                    getAllAffiliateUser()
                    affiliateUserAdapter?.setType(userType)
                    tvNew.setTextColor(Color.parseColor("#C0BBBB"))
                    viewNew.setBackgroundColor(Color.parseColor("#C0BBBB"))
                    tvApproved.setTextColor(Color.parseColor("#C0BBBB"))
                    viewApproved.setBackgroundColor(Color.parseColor("#C0BBBB"))
                    tvRejected.setTextColor(Color.parseColor("#875C3A"))
                    viewRejected.setBackgroundColor(Color.parseColor("#875C3A"))
                }
            }


            swipeRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(requireActivity(), R.color.brown)
            )

            swipeRefreshLayout.setOnRefreshListener {
                isFromSwipe = true
                pagination = false
                currentPage = 1
                getAllAffiliateUser(false)
            }
        }

        if (SharedPreference.get(requireActivity()).getCountryList() != null) {
            countryList.clear()
            SharedPreference.get(requireActivity()).getCountryList()?.let { countryList.addAll(it) }
        } else {
            getCountryApi()
        }

        getAllAffiliateUser()
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

            btnExport.setOnClickListener {
                val csv = CommonMethod.listToCsv(userList)
                CommonMethod.saveCsvFile(
                    requireContext(),
                    "affiliate_user_${System.currentTimeMillis()}",
                    csv
                )
            }


            ivFilter.setOnClickListener {
                userFilterDialog()
            }

            ivNextArrow.setOnClickListener {
                if (!isObserver && currentPage < lastPage && totalItem != 0) {
                    currentPage += 1
                    getAllAffiliateUser(false)
                    binding?.progressBar?.isVisible = true
                }
            }

            ivBackArrow.setOnClickListener {
                if (currentPage > 1) {
                    currentPage--
                    getAllAffiliateUser(false)
                    binding?.progressBar?.isVisible = true
                }
            }

            tvNew.setOnClickListener {
                userType = "1"
                getAllAffiliateUser()
                tvNew.setTextColor(Color.parseColor("#875C3A"))
                viewNew.setBackgroundColor(Color.parseColor("#875C3A"))
                tvApproved.setTextColor(Color.parseColor("#C0BBBB"))
                viewApproved.setBackgroundColor(Color.parseColor("#C0BBBB"))
                tvRejected.setTextColor(Color.parseColor("#C0BBBB"))
                viewRejected.setBackgroundColor(Color.parseColor("#C0BBBB"))
            }

            tvApproved.setOnClickListener {
                userType = "2"
                getAllAffiliateUser()
                tvNew.setTextColor(Color.parseColor("#C0BBBB"))
                viewNew.setBackgroundColor(Color.parseColor("#C0BBBB"))
                tvApproved.setTextColor(Color.parseColor("#875C3A"))
                viewApproved.setBackgroundColor(Color.parseColor("#875C3A"))
                tvRejected.setTextColor(Color.parseColor("#C0BBBB"))
                viewRejected.setBackgroundColor(Color.parseColor("#C0BBBB"))
            }

            tvRejected.setOnClickListener {
                userType = "3"
                getAllAffiliateUser()
                tvNew.setTextColor(Color.parseColor("#C0BBBB"))
                viewNew.setBackgroundColor(Color.parseColor("#C0BBBB"))
                tvApproved.setTextColor(Color.parseColor("#C0BBBB"))
                viewApproved.setBackgroundColor(Color.parseColor("#C0BBBB"))
                tvRejected.setTextColor(Color.parseColor("#875C3A"))
                viewRejected.setBackgroundColor(Color.parseColor("#875C3A"))
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

        viewModel.getCountryResult.observe(viewLifecycleOwner) { response ->
            if (isCountryObserver) {
                countryList.clear()
                countryList.addAll(response.data)
                SharedPreference.get(requireActivity()).saveUserCountryList(countryList)
                isCountryObserver = false
            }
        }

        viewModel.acceptAffiliateUserResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                userList.removeAt(selectedPos)
                affiliateUserAdapter?.notifyItemRemoved(selectedPos)
                affiliateUserAdapter?.notifyItemRangeChanged(selectedPos, userList.size)
                checkData()
                CommonMethod.showSuccessDialog(
                    requireActivity(),
                    getString(R.string.affiliate_user_approved_successfully)
                ) {

                }
                selectedPos = -1
                isObserver = false
            }
        }

        viewModel.rejectAffiliateUserResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                userList.removeAt(selectedPos)
                affiliateUserAdapter?.notifyItemRemoved(selectedPos)
                affiliateUserAdapter?.notifyItemRangeChanged(selectedPos, userList.size)
                checkData()
                CommonMethod.showSuccessDialog(
                    requireActivity(),
                    getString(R.string.affiliate_user_rejected_successfully)
                ) {

                }
                selectedPos = -1
                isObserver = false
            }
        }

        viewModel.getNewAffiliateUserResult.observe(viewLifecycleOwner) { response ->
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

                binding?.tvCount?.text = "$currentStr ${getString(R.string.of)} $lastStr"

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


                val oldSize = userList.size
                if (!pagination) {
                    userList.clear()
                }
                updatePage()
                val newItems = response.data.data ?: emptyList()

                userList.addAll(newItems)
                checkData()

                if (pagination) {

                    response.data.data?.size?.let {
                        affiliateUserAdapter?.notifyItemRangeInserted(
                            oldSize,
                            it
                        )
                    }
                } else {
                    affiliateUserAdapter?.notifyDataSetChanged()
                }
                affiliateUserAdapter?.setType(userType)
                pagination = false
                isFromSwipe = false
                isObserver = false
            }
        }

        viewModel.getApprovedAffiliateUserResult.observe(viewLifecycleOwner) { response ->
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

                binding?.tvCount?.text = "$currentStr ${getString(R.string.of)} $lastStr"

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


                val oldSize = userList.size
                if (!pagination) {
                    userList.clear()
                }
                updatePage()
                val newItems = response.data.data ?: emptyList()

                userList.addAll(newItems)
                checkData()

                if (pagination) {

                    response.data.data?.size?.let {
                        affiliateUserAdapter?.notifyItemRangeInserted(
                            oldSize,
                            it
                        )
                    }
                } else {
                    affiliateUserAdapter?.notifyDataSetChanged()
                }
                affiliateUserAdapter?.setType(userType)
                pagination = false
                isFromSwipe = false
                isObserver = false
            }
        }

        viewModel.getRejectedAffiliateUserResult.observe(viewLifecycleOwner) { response ->
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

                binding?.tvCount?.text = "$currentStr ${getString(R.string.of)} $lastStr"

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


                val oldSize = userList.size
                if (!pagination) {
                    userList.clear()
                }
                updatePage()
                val newItems = response.data.data ?: emptyList()

                userList.addAll(newItems)
                checkData()

                if (pagination) {

                    response.data.data?.size?.let {
                        affiliateUserAdapter?.notifyItemRangeInserted(
                            oldSize,
                            it
                        )
                    }
                } else {
                    affiliateUserAdapter?.notifyDataSetChanged()
                }
                affiliateUserAdapter?.setType(userType)

                pagination = false
                isFromSwipe = false
                isObserver = false
            }
        }

    }

    private fun getAllAffiliateUser(loading: Boolean = true, search: String? = null) {
        val map = HashMap<String, Any>()
        map["page"] = currentPage
        map["limit"] = 10
        if (search != null) {
            map["search"] = search
        }
        when (userType) {
            "1" -> {
                if (filterType == userType) {
                    if (startDate != null) {
                        map["from_date"] = startDate.toString()
                    }
                    if (endDate != null) {
                        map["to_date"] = endDate.toString()
                    }
                    if (countryId != null) {
                        map["country"] =
                            countryList.firstOrNull { it.id.toString() == countryId }?.name.toString()
                    }
                }

                viewModel.getNewAffiliateUser(map)
            }

            "2" -> {
                if (filterType == userType) {
                    if (startDate != null) {
                        map["from_date1"] = startDate.toString()
                    }
                    if (endDate != null) {
                        map["to_date1"] = endDate.toString()
                    }
                    if (countryId != null) {
                        map["country1"] =
                            countryList.firstOrNull { it.id.toString() == countryId }?.name.toString()
                    }
                }
                viewModel.getApprovedAffiliateUser(map)
            }

            "3" -> {
                if (filterType == userType) {
                    if (startDate != null) {
                        map["from_date2"] = startDate.toString()
                    }
                    if (endDate != null) {
                        map["to_date2"] = endDate.toString()
                    }
                    if (countryId != null) {
                        map["country2"] =
                            countryList.firstOrNull { it.id.toString() == countryId }?.name.toString()
                    }
                }
                viewModel.getRejectedAffiliateUser(map)
            }
        }

        if (loading) {
            ProgressDialogUtils.getInstance().showProgress(requireActivity())
        }
        isObserver = true
    }

    private fun getCountryApi() {
        viewModel.getCountry()
        isCountryObserver = true
    }

    private fun acceptAffiliateUser() {
        val map = HashMap<String, Any>()
        map["user_id"] = userList[selectedPos].id
        viewModel.acceptAffiliateUser(map)
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

    private fun rejectAffiliateUser(reason: String, type: String) {
        val map = HashMap<String, Any>()
        map["user_id"] = userList[selectedPos].id
        map["reject_type"] = type
        map["reject_reason"] = reason
        viewModel.rejectAffiliateUser(map)
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

    private fun checkData() {
        binding?.tvNoDataFound?.isVisible = userList.isEmpty()
        if (lastQuery != null) {
            binding?.tvNoDataFound?.text = getString(R.string.no_result_found)
        } else {
            binding?.tvNoDataFound?.text = getString(R.string.no_data_found)
        }

        /*  if (userList.isNotEmpty()){
              binding?.tvCount?.isVisible = true
              binding?.ivBackArrow?.isVisible = true
              binding?.ivNextArrow?.isVisible = true
              binding?.tvNoDataFound?.isVisible = false
          }
          else{
              binding?.tvCount?.isVisible = false
              binding?.ivBackArrow?.isVisible = false
              binding?.ivNextArrow?.isVisible = false
              binding?.tvNoDataFound?.isVisible = true
              if (lastQuery!=null){
                  binding?.tvNoDataFound?.text = "No result found"
              }else{
                  binding?.tvNoDataFound?.text = "No data found"
              }
          }*/
    }

    private fun updatePage() {
        val start = (currentPage - 1) * itemsPerPage
        val end = minOf(start + itemsPerPage, userList.size)
        if (start >= 0 && end >= 0) {
            if (start < end) {
                val pageData = userList.subList(start, end)
                affiliateUserAdapter?.setData(ArrayList(pageData))
            }
        }

    }

    private fun userFilterDialog() {
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
            if (filterType == userType) {
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

            etCountry.setOnClickListener {
                CommonMethod.showCountryPicker(
                    requireContext(),
                    it,
                    countryList
                ) { selectedCountry ->
                    etCountry.text =
                        if (Constants.language == "ar") selectedCountry.nameAr else selectedCountry.name
                    selectedCountryId = selectedCountry.id.toString()
                }
            }

            tvClear.setOnClickListener {
                countryId = null
                startDate = null
                endDate = null
                toTimestamp = 0L
                fromTimestamp = 0L
                filterType = ""
                getAllAffiliateUser()
                dialog.dismiss()
            }

            tvApply.setOnClickListener {
                if (selectedCountryId != null) {
                    countryId = selectedCountryId
                }
                if (etFrom.text.isNotEmpty()) {
                    startDate =
                        CommonMethod.formatDate(etFrom.text.toString(), "dd/MM/yyyy", "yyyy-MM-dd")
                }

                if (etTo.text.isNotEmpty()) {
                    endDate =
                        CommonMethod.formatDate(etTo.text.toString(), "dd/MM/yyyy", "yyyy-MM-dd")
                }

                filterType = userType
                getAllAffiliateUser()
                dialog.dismiss()
            }

        }

        dialog.show()
    }

    private fun rejectReasonDialog() {
        var rejectionType = ""
        val dialog = Dialog(requireActivity(), R.style.CustomDialog)
        val dialogBinding = RejectReasonLayoutBinding.inflate(LayoutInflater.from(context))
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


            closeBtn.setOnClickListener {
                dialog.dismiss()
            }

            rbComplete.setOnClickListener {
                rejectionType = getString(R.string.complete)
                rbComplete.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    R.drawable.ic_radio_selected,
                    0,
                    0,
                    0
                )
                rbReject.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    R.drawable.ic_radio_unselected,
                    0,
                    0,
                    0
                )
            }

            rbReject.setOnClickListener {
                rejectionType = getString(R.string.document)
                rbComplete.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    R.drawable.ic_radio_unselected,
                    0,
                    0,
                    0
                )
                rbReject.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    R.drawable.ic_radio_selected,
                    0,
                    0,
                    0
                )
            }

            btnAccept.setOnClickListener {
                if (rejectionType == "") {
                    showToast(getString(R.string.please_select_reason_type))
                } else if (etReason.text?.trim().toString().isEmpty()) {
                    showToast(getString(R.string.please_enter_reason))
                } else {
                    rejectAffiliateUser(etReason.text?.trim().toString(), rejectionType)
                    dialog.dismiss()
                }
            }

        }

        dialog.show()
    }

}