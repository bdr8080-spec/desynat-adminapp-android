package com.desynatadmin.ui.home.deliveryBoyManagement

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
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.desynatadmin.R
import com.desynatadmin.adapter.DeliveryBoyAdapter
import com.desynatadmin.base.BaseFragment
import com.desynatadmin.data.view_model.HomeViewModel
import com.desynatadmin.databinding.FragmentDeliveryBoyManagementBinding
import com.desynatadmin.databinding.UserFilterDialogBinding
import com.desynatadmin.ui.home.HomeActivity
import com.desynatadmin.ui.home.deliveryBoyManagement.response.GetAllDeliveryBoyResponse
import com.desynatadmin.utils.CommonMethod
import com.desynatadmin.utils.ProgressDialogUtils
import com.desynatadmin.utils.openCustomDatePicker
import com.desynatadmin.ui.home.SubAdminHomeActivity
import com.desynatadmin.utils.SharedPreference
import com.desynatadmin.utils.showToast
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DeliveryBoyManagementFragment : BaseFragment() {
    private var binding: FragmentDeliveryBoyManagementBinding?=null
    private val viewModel: HomeViewModel by viewModels()
    private var isObserver = true

    private var deliveryBoyList = ArrayList<GetAllDeliveryBoyResponse.Data.Data>()
    private var deliveryBoyAdapter : DeliveryBoyAdapter?=null
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
    var selectedPos = -1

    var startDate:String?=null
    var endDate:String?=null
    var fromTimestamp = 0L
    var toTimestamp = 0L

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDeliveryBoyManagementBinding.inflate(layoutInflater,container,false)
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
                val module = subAdminInfo?.permissionDetails
                    ?.firstOrNull {
                        it.moduleDetails.name.equals("delivery_boy_management", true)
                    }

                val actionAccess = module?.canAction == 1

                ivAdd.isVisible = actionAccess
            }
            CommonMethod.removeSpace(etSearch)
            toolbar.tvToolbar.text = "Delivery Boy Management"
            deliveryBoyList.clear()
            deliveryBoyAdapter = DeliveryBoyAdapter(requireActivity(),deliveryBoyList,object : DeliveryBoyAdapter.DeliveryBoyClick{
                override fun onDeliveryBoyClick(position: Int,type:Int) {
                    when(type){
                        1->{
                            val bundle = Bundle().apply {
                                putString("deliveryBoyData", Gson().toJson(deliveryBoyList[position]))
                            }
                            findNavController().navigate(R.id.action_deliveryBoyFragment_to_deliveryBoyDetailFragment,bundle)
                        }
                        2->{
                            selectedPos = position
                            updateDeliveryBoyStatus()

                        }
                        3->{
                            CommonMethod.deleteAlertDialog(
                                requireActivity(),
                                "Are you sure you want to delete the delivery boy account?"
                            ) { action ->
                                when (action) {
                                    CommonMethod.DialogAction.DELETE -> {
                                        selectedPos = position
                                        deleteDeliveryBoy()
                                    }

                                    CommonMethod.DialogAction.DISCARD -> {

                                    }
                                }
                            }
                        }
                        4->{
                            val bundle = Bundle().apply {
                                putString("deliveryBoyData", Gson().toJson(deliveryBoyList[position]))
                            }
                            findNavController().navigate(R.id.action_deliveryBoyFragment_to_addDeliveryBoyFragment,bundle)
                        }
                    }

                }

            })

            rvDeliveryBoy.adapter = deliveryBoyAdapter

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
                        currentPage = 1
                        pagination = false
                        isFromSwipe = false
                        if (!isObserver)
                            getAllDeliveryBoy(search =  query.ifEmpty { null })
                    }

                }
            })

            swipeRefreshLayout.setOnRefreshListener {
                isFromSwipe = true
                pagination = false
                currentPage = 1
                getAllDeliveryBoy(false)
            }
        }

        getAllDeliveryBoy()
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

            ivAdd.setOnClickListener {
                findNavController().navigate(R.id.action_deliveryBoyFragment_to_addDeliveryBoyFragment)
            }

            ivFilter.setOnClickListener {
                filterDialog()
            }

            ivNextArrow.setOnClickListener {
                if (!isObserver && currentPage < lastPage&&totalItem!=0) {
                    currentPage += 1
                    getAllDeliveryBoy(false)
                    binding?.progressBar?.isVisible = true
                }


            }

            ivBackArrow.setOnClickListener {
                if (currentPage > 1) {
                    currentPage--
                    getAllDeliveryBoy(false)
                    binding?.progressBar?.isVisible = true
                }
            }
        }
    }

    private fun getAllDeliveryBoy(loading:Boolean=true,search:String?=null){
        viewModel.getDeliveryBoy(search = search, page = currentPage.toString(), fromDate = startDate, toDate = endDate)
        if (loading){
            ProgressDialogUtils.getInstance().showProgress(requireActivity())
        }
        isObserver = true
    }

    private fun updateDeliveryBoyStatus(){
        val map = HashMap<String,Any>()
        map["user_id"] = deliveryBoyList[selectedPos].id
        viewModel.blockUnblockDeliveryBoy(map)
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

    private fun deleteDeliveryBoy(){
        val map = HashMap<String,Any>()
        map["user_id"] = deliveryBoyList[selectedPos].id
        viewModel.deleteDeliveryBoy(map)
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

    private fun checkData(){
        binding?.tvNoDataFound?.isVisible = deliveryBoyList.isEmpty()
    }

    private fun updatePage() {
        val start = (currentPage - 1) * itemsPerPage
        val end = minOf(start + itemsPerPage, deliveryBoyList.size)
        if (start>=0&&end>=0) {
            if (start < end) {
                val pageData = deliveryBoyList.subList(start, end)
                deliveryBoyAdapter?.setData(ArrayList(pageData))
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

        viewModel.updateDeliveryBoyStatusResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                showToast(response.message)
                deliveryBoyList[selectedPos].status = response.data.status
                deliveryBoyAdapter?.notifyItemChanged(selectedPos)
                selectedPos = -1
                isObserver = false
            }
        }

        viewModel.deleteDeliveryBoyResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                showToast(response.message)
                deliveryBoyList.removeAt(selectedPos)
                deliveryBoyAdapter?.notifyItemRemoved(selectedPos)
                deliveryBoyAdapter?.notifyItemRangeChanged(selectedPos, deliveryBoyList.size)
                checkData()
                selectedPos = -1
                isObserver = false
            }
        }

        viewModel.getDeliveryBoyResult.observe(viewLifecycleOwner) { response ->
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


                val oldSize = deliveryBoyList.size
                if (!pagination){
                    deliveryBoyList.clear()
                }
                updatePage()
                val newItems = response.data.data ?: emptyList()

                deliveryBoyList.addAll(newItems)
                checkData()

                if (pagination){

                    response.data.data?.size?.let {
                        deliveryBoyAdapter?.notifyItemRangeInserted(oldSize,
                            it
                        )
                    }
                }
                else{
                    deliveryBoyAdapter?.notifyDataSetChanged()
                }

                pagination = false
                isFromSwipe = false
                isObserver = false
            }
        }

    }

    private fun filterDialog() {
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

            tvCountry.isVisible = false
            etCountry.isVisible = false

            if (startDate!=null){
                etFrom.text=CommonMethod.formatDate(startDate.toString(),"yyyy-MM-dd","dd/MM/yyyy")
            }
            if (endDate!=null){
                etTo.text=CommonMethod.formatDate(endDate.toString(),"yyyy-MM-dd","dd/MM/yyyy")
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
                toTimestamp = 0L
                fromTimestamp = 0L
                getAllDeliveryBoy()
                dialog.dismiss()
            }

            tvApply.setOnClickListener{
                if (etFrom.text.isNotEmpty()){
                    startDate = CommonMethod.formatDate(etFrom.text.toString(),"dd/MM/yyyy","yyyy-MM-dd")
                }

                if (etTo.text.isNotEmpty()){
                    endDate = CommonMethod.formatDate(etTo.text.toString(),"dd/MM/yyyy","yyyy-MM-dd")
                }
                    getAllDeliveryBoy()
                    dialog.dismiss()
            }

        }

        dialog.show()
    }

}