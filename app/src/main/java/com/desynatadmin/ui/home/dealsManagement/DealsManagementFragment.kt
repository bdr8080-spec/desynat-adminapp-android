package com.desynatadmin.ui.home.dealsManagement

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.desynatadmin.R
import com.desynatadmin.adapter.DealsAdapter
import com.desynatadmin.base.BaseFragment
import com.desynatadmin.data.view_model.HomeViewModel
import com.desynatadmin.databinding.FragmentDealsManagementBinding
import com.desynatadmin.ui.home.HomeActivity
import com.desynatadmin.ui.home.dealsManagement.response.GetActiveDealResponse
import com.desynatadmin.utils.ProgressDialogUtils
import com.google.gson.Gson
import com.desynatadmin.ui.home.SubAdminHomeActivity
import com.desynatadmin.utils.SharedPreference
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job

@AndroidEntryPoint
class DealsManagementFragment : BaseFragment() {
    private var binding: FragmentDealsManagementBinding?=null
    private val viewModel: HomeViewModel by viewModels()
    private var isObserver = true

    private var dealsList = ArrayList<GetActiveDealResponse.Data.Data>()
    private var dealsAdapter : DealsAdapter?=null
    private var currentPage = 1
    private var lastPage = 1
    private var totalPage = 0
    private var totalItem=0
    private var pageLimit=10
    private var isFromSwipe=false
    private var pagination=false
    private var search: Job? = null
    private var lastQuery: String? = null
    private val itemsPerPage = 10

    private var type = 1
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDealsManagementBinding.inflate(layoutInflater,container,false)
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
            val userInfo = SharedPreference.get(requireActivity()).getUser()
            if (userInfo?.type=="2"){
                val subAdminInfo = SharedPreference.get(requireActivity()).getSubAdmin()
                val actionAccess = subAdminInfo?.permissionDetails
                    ?.any {
                        it.moduleDetails.name.equals("deal_management", ignoreCase = true)&&it.canAction==1
                    } == true

                ivAddDeals.isVisible = actionAccess
            }
            toolbar.tvToolbar.text = "Deals Management"

            dealsList.clear()
            dealsAdapter = DealsAdapter(requireActivity(),dealsList,object :DealsAdapter.DealsClick{
                override fun onDealsClick(position: Int) {
                    val bundle = Bundle().apply {
                        putString("dealsData", Gson().toJson(dealsList[position]))
                        putInt("dealType", type)
                    }
                    findNavController().navigate(R.id.action_dealManagement_to_dealDetailFragment,bundle)
                }

            })

            rvDeals.adapter = dealsAdapter

            swipeRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(requireActivity(), R.color.brown)
            )

            swipeRefreshLayout.setOnRefreshListener {
                isFromSwipe = true
                pagination = false
                currentPage = 1
                if (type==1){
                    getActiveDeals(false)
                }
                else{
                    getExpiredDeals(false)
                }
            }

            if (type==1){
                currentPage = 1
                type = 1
                getActiveDeals()
                tvActive.setTextColor(requireActivity().getColor(R.color.white))
                tvExpired.setTextColor(requireActivity().getColor(R.color.black))
                tvActive.setBackgroundResource(R.drawable.selected_left_border)
                tvExpired.background = null
            }
            else{
                currentPage = 1
                type = 2
                getExpiredDeals()
                tvActive.setTextColor(requireActivity().getColor(R.color.black))
                tvExpired.setTextColor(requireActivity().getColor(R.color.white))
                tvExpired.setBackgroundResource(R.drawable.selected_right_border)
                tvActive.background = null
            }
        }
    }

    override fun initControl() {
        binding?.apply {

            toolbar.tvToolbar.setOnClickListener{
                 when (val activity = requireActivity()) {
        is HomeActivity -> activity.onBackPressed()
        is SubAdminHomeActivity -> activity.onBackPressed()
        else -> activity.onBackPressed()
    }
            }

            tvActive.setOnClickListener {
                currentPage = 1
                type = 1
                getActiveDeals()
                tvActive.setTextColor(requireActivity().getColor(R.color.white))
                tvExpired.setTextColor(requireActivity().getColor(R.color.black))
                tvActive.setBackgroundResource(R.drawable.selected_left_border)
                tvExpired.background = null
            }

            tvExpired.setOnClickListener {
                currentPage = 1
                type = 2
                getExpiredDeals()
                tvActive.setTextColor(requireActivity().getColor(R.color.black))
                tvExpired.setTextColor(requireActivity().getColor(R.color.white))
                tvExpired.setBackgroundResource(R.drawable.selected_right_border)
                tvActive.background = null
            }

            ivAddDeals.setOnClickListener {
                findNavController().navigate(R.id.action_dealManagement_to_addDealFragment)
            }

            ivNextArrow.setOnClickListener {
                if (!isObserver && currentPage < lastPage&&totalItem!=0) {
                    currentPage += 1
                    if (type==1){
                        getActiveDeals(false)
                    }
                    else{
                        getExpiredDeals(false)
                    }

                    binding?.progressBar?.isVisible = true
                }
            }

            ivBackArrow.setOnClickListener {
                if (currentPage > 1) {
                    currentPage--
                    if (type==1){
                        getActiveDeals(false)
                    }
                    else{
                        getExpiredDeals(false)
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
                if (isFromSwipe){
                    binding?.swipeRefreshLayout?.isRefreshing = false
                }

                pagination = false
                isFromSwipe = false
                isObserver = false
            }
        }

        viewModel.getActiveDealsResult.observe(viewLifecycleOwner) { response ->
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

                val oldSize = dealsList.size
                if (!pagination){
                    dealsList.clear()
                }
                updatePage()
                val newItems = response.data.data ?: emptyList()

                dealsList.addAll(newItems)
                checkData()

                if (pagination){

                    response.data.data?.size?.let {
                        dealsAdapter?.notifyItemRangeInserted(oldSize,
                            it
                        )
                    }
                }
                else{
                    dealsAdapter?.notifyDataSetChanged()
                }

                pagination = false
                isFromSwipe = false
                isObserver = false
            }
        }

        viewModel.getExpiredDealsResult.observe(viewLifecycleOwner) { response ->
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

                val oldSize = dealsList.size
                if (!pagination){
                    dealsList.clear()
                }
                updatePage()
                val newItems = response.data.data ?: emptyList()

                dealsList.addAll(newItems)
                checkData()

                if (pagination){

                    response.data.data?.size?.let {
                        dealsAdapter?.notifyItemRangeInserted(oldSize,
                            it
                        )
                    }
                }
                else{
                    dealsAdapter?.notifyDataSetChanged()
                }

                pagination = false
                isFromSwipe = false
                isObserver = false
            }
        }


    }

    private fun getActiveDeals(loading:Boolean=true){
        viewModel.getActiveDeals(search = null, page = currentPage)
        if (loading){
            ProgressDialogUtils.getInstance().showProgress(requireActivity())
        }
        isObserver = true
    }

    private fun getExpiredDeals(loading:Boolean=true){
        viewModel.getExpiredDeals(search = null, page = currentPage)
        if (loading){
            ProgressDialogUtils.getInstance().showProgress(requireActivity())
        }
        isObserver = true
    }

    private fun checkData(){
        binding?.tvNoDataFound?.isVisible = dealsList.isEmpty()
    }

    private fun updatePage() {
        val start = (currentPage - 1) * itemsPerPage
        val end = minOf(start + itemsPerPage, dealsList.size)
        if (start>=0&&end>=0) {
            if (start < end) {
                val pageData = dealsList.subList(start, end)
                dealsAdapter?.setData(ArrayList(pageData))
            }
        }
    }

}