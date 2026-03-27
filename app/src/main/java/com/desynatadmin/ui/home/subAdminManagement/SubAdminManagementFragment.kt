package com.desynatadmin.ui.home.subAdminManagement

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
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.desynatadmin.R
import com.desynatadmin.adapter.SubAdminAdapter
import com.desynatadmin.base.BaseFragment
import com.desynatadmin.data.view_model.HomeViewModel
import com.desynatadmin.databinding.FragmentSubAdminManagementBinding
import com.desynatadmin.databinding.UserFilterDialogBinding
import com.desynatadmin.ui.home.HomeActivity
import com.desynatadmin.ui.home.subscriptionPlan.response.GetCountryResponse
import com.desynatadmin.ui.home.userManagement.response.GetUserResponse
import com.desynatadmin.utils.CommonMethod
import com.desynatadmin.utils.ProgressDialogUtils
import com.desynatadmin.utils.SharedPreference
import com.desynatadmin.utils.openCustomDatePicker
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.getValue

@AndroidEntryPoint
class SubAdminManagementFragment : BaseFragment() {
    private var binding: FragmentSubAdminManagementBinding? = null
    private val viewModel: HomeViewModel by viewModels()
    private var isObserver = true

    private var isCountryObserver = true
    private var subAdminList = ArrayList<GetUserResponse.Data.Data>()
    private var subAdminAdapter : SubAdminAdapter?=null
    private var type = 1
    private var currentPage = 1
    private var lastPage = 1
    private var totalPage = 0
    private var totalItem=0
    private var pageLimit=10
    private var isFromSwipe=false
    private var pagination=false
    private var searchUser: Job? = null
    private var lastQuery: String? = null
    private val itemsPerPage = 10
    var selectedPos = -1
    private var countryList = ArrayList<GetCountryResponse.Data>()
    var countryId:String?=null
    var startDate:String?=null
    var endDate:String?=null
    var fromTimestamp=0L
    var toTimestamp=0L
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSubAdminManagementBinding.inflate(layoutInflater, container, false)
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

            subAdminList.clear()
            subAdminAdapter = SubAdminAdapter(requireActivity(),subAdminList,object : SubAdminAdapter.SubAdminClick{
                override fun onSubAdminClick(position: Int,type:Int) {
                    when(type){
                        1->{
                            val bundle = Bundle().apply {
                                putString("subAdminData", Gson().toJson(subAdminList[position]))
                            }
                            findNavController().navigate(R.id.action_subAdminManagement_to_subAdminDetailFragment,bundle)
                        }

                    }

                }

            })

            rvSubAdmin.adapter = subAdminAdapter

            etSearch.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

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
                            getSubAdmin(search =  query.ifEmpty { null }, loading = false)
                    }

                }
            })

            swipeRefreshLayout.setOnRefreshListener {
                isFromSwipe = true
                pagination = false
                currentPage = 1
                getSubAdmin(false)
            }

        }

        getSubAdmin()
    }

    override fun initControl() {
        binding?.apply {
            toolbar.ivBack.setOnClickListener {
                (requireActivity() as? HomeActivity)?.onBackPressed()
            }

            tvAll.setOnClickListener {
                allSubAdmin()
            }

            tvActive.setOnClickListener {
                activeSubAdmin()
            }

            tvInactive.setOnClickListener {
                inactiveSubAdmin()
            }

            ivFilter.setOnClickListener {
                subAdminFilterDialog()
            }

            btnExport.setOnClickListener {

            }

            ivNextArrow.setOnClickListener {
                if (!isObserver && currentPage < lastPage&&totalItem!=0) {
                    currentPage += 1
                    getSubAdmin(false)
                    binding?.progressBar?.isVisible = true
                }
            }

            ivBackArrow.setOnClickListener {
                if (currentPage > 1) {
                    currentPage--
                    getSubAdmin(false)
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
    }

    private fun getSubAdmin(loading:Boolean=true,search:String?=null){
      //  viewModel.getSubAdmin(search = search, page = currentPage.toString(),fromDate=startDate, toDate = endDate, countryId = countryId)
        if (loading){
            ProgressDialogUtils.getInstance().showProgress(requireActivity())
        }
        isObserver = true
    }

    private fun getUserCountryApi(){
        viewModel.getUserCountry()
        isCountryObserver = true
    }

    private fun updatePage() {
        val start = (currentPage - 1) * itemsPerPage
        val end = minOf(start + itemsPerPage, subAdminList.size)
        if (start>=0&&end>=0) {
            if (start < end) {
                val pageData = subAdminList.subList(start, end)
                subAdminAdapter?.setData(ArrayList(pageData))
            }
        }

    }

    private fun subAdminFilterDialog() {
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
                getSubAdmin()
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

                getSubAdmin()
                dialog.dismiss()
            }

        }

        dialog.show()
    }

    private fun allSubAdmin(){
        binding?.apply {
            type = 1
            getSubAdmin()
            tvAll.setTextColor(Color.parseColor("#875C3A"))
            viewAll.setBackgroundColor(Color.parseColor("#875C3A"))
            tvActive.setTextColor(Color.parseColor("#C0BBBB"))
            viewActive.setBackgroundColor(Color.parseColor("#C0BBBB"))
            tvInactive.setTextColor(Color.parseColor("#C0BBBB"))
            viewInactive.setBackgroundColor(Color.parseColor("#C0BBBB"))
        }
    }

    private fun activeSubAdmin(){
        binding?.apply {
            type = 1
            getSubAdmin()
            tvActive.setTextColor(Color.parseColor("#875C3A"))
            viewActive.setBackgroundColor(Color.parseColor("#875C3A"))
            tvAll.setTextColor(Color.parseColor("#C0BBBB"))
            viewAll.setBackgroundColor(Color.parseColor("#C0BBBB"))
            tvInactive.setTextColor(Color.parseColor("#C0BBBB"))
            viewInactive.setBackgroundColor(Color.parseColor("#C0BBBB"))
        }
    }

    private fun inactiveSubAdmin(){
        binding?.apply {
            type = 1
            getSubAdmin()
            tvInactive.setTextColor(Color.parseColor("#875C3A"))
            viewInactive.setBackgroundColor(Color.parseColor("#875C3A"))
            tvActive.setTextColor(Color.parseColor("#C0BBBB"))
            viewActive.setBackgroundColor(Color.parseColor("#C0BBBB"))
            tvAll.setTextColor(Color.parseColor("#C0BBBB"))
            viewAll.setBackgroundColor(Color.parseColor("#C0BBBB"))
        }
    }


}