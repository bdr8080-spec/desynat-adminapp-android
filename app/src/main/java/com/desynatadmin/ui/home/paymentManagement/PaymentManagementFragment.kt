package com.desynatadmin.ui.home.paymentManagement

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
import android.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.desynatadmin.R
import com.desynatadmin.adapter.BannerPaymentAdapter
import com.desynatadmin.adapter.GiftCardPaymentAdapter
import com.desynatadmin.adapter.OrderPaymentAdapter
import com.desynatadmin.adapter.PlanPaymentAdapter
import com.desynatadmin.base.BaseFragment
import com.desynatadmin.data.view_model.HomeViewModel
import com.desynatadmin.databinding.FragmentPaymentManagementBinding
import com.desynatadmin.databinding.PaymentFilterDialogBinding
import com.desynatadmin.databinding.UserFilterDialogBinding
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
class PaymentManagementFragment : BaseFragment() {

    private var binding: FragmentPaymentManagementBinding? = null
    private val viewModel: HomeViewModel by viewModels()
    private var isObserver = true
    private var isCountryObserver = true

    private var orderPaymentAdapter: OrderPaymentAdapter? = null
    private var planPaymentAdapter: PlanPaymentAdapter? = null
    private var giftCardPaymentAdapter: GiftCardPaymentAdapter? = null
    private var bannerPaymentAdapter: BannerPaymentAdapter? = null

    private var orderPaymentList = ArrayList<String>()
    private var planPaymentList = ArrayList<String>()
    private var giftCardPaymentList = ArrayList<String>()
    private var bannerPaymentList = ArrayList<String>()
    private var type = 1

    private var pagination=false
    private val itemsPerPage = 10
    private var currentPage = 1

    private var lastPage = 1
    private var totalPage = 0
    private var totalItem=0
    private var pageLimit=10
    private var search: Job? = null
    private var lastQuery: String? = null

    var startDate:String?=null
    var endDate:String?=null
    var fromTimestamp = 0L
    var toTimestamp = 0L
    var countryName:String?=null
    var countryId:String?=null

    private var countryList = ArrayList<GetCountryResponse.Data>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPaymentManagementBinding.inflate(layoutInflater, container, false)
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
            orderPaymentAdapter = OrderPaymentAdapter(requireActivity(), orderPaymentList, object :
                OrderPaymentAdapter.PaymentClick {
                override fun onPaymentClick(position: Int, type: Int) {

                }

            })
            rvOrder.adapter = orderPaymentAdapter

            planPaymentAdapter = PlanPaymentAdapter(requireActivity(), planPaymentList, object :
                PlanPaymentAdapter.PaymentClick {
                override fun onPaymentClick(position: Int, type: Int) {

                }

            })
            rvPlan.adapter = planPaymentAdapter

            giftCardPaymentAdapter =
                GiftCardPaymentAdapter(requireActivity(), giftCardPaymentList, object :
                    GiftCardPaymentAdapter.PaymentClick {
                    override fun onPaymentClick(position: Int, type: Int) {

                    }

                })
            rvGift.adapter = giftCardPaymentAdapter

            bannerPaymentAdapter =
                BannerPaymentAdapter(requireActivity(), bannerPaymentList, object :
                    BannerPaymentAdapter.PaymentClick {
                    override fun onPaymentClick(position: Int, type: Int) {

                    }

                })
            rvBanner.adapter = bannerPaymentAdapter

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
                    search?.cancel()
                    search = lifecycleScope.launch {
                        delay(600)
                        if (!isObserver)
                            when (type) {
                                1 -> {
                                    getOrderPayment(search = query.ifEmpty { null })
                                }

                                2 -> {
                                    getPlanPayment(search = query.ifEmpty { null })
                                }

                                3 -> {
                                    getGiftCardPayment(search = query.ifEmpty { null })
                                }

                                4 -> {
                                    getBannerPayment(search = query.ifEmpty { null })
                                }
                            }

                    }

                }
            })

        }
    }

    override fun initControl() {
        binding?.apply {
            when (type) {
                1 -> order()
                2 -> plan()
                3 -> giftCard()
                4 -> banner()
            }

            btnExport.setOnClickListener {

            }

            ivFilter.setOnClickListener {
                paymentFilterDialog()
            }

            ivNextArrow.setOnClickListener {
                if (!isObserver && currentPage < lastPage&&totalItem!=0) {
                    currentPage += 1
                    when(type) {
                        1 -> {
                            getOrderPayment(false)
                        }
                        2 -> {
                            getPlanPayment(false)
                        }
                        3 -> {
                            getGiftCardPayment(false)
                        }
                        4 -> {
                            getBannerPayment(false)
                        }
                    }
                    binding?.progressBar?.isVisible = true
                }


            }

            ivBackArrow.setOnClickListener {
                if (currentPage > 1) {
                    currentPage--
                    when(type) {
                        1 -> {
                            getOrderPayment(false)
                        }
                        2 -> {
                            getPlanPayment(false)
                        }
                        3 -> {
                            getGiftCardPayment(false)
                        }
                        4 -> {
                            getBannerPayment(false)
                        }
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
                pagination = false
                isObserver = false
            }
        }

        viewModel.getCountryResult.observe(viewLifecycleOwner) { response ->
            if (isCountryObserver) {
                countryList.clear()
                countryList.addAll(response.data)
                setCountry()

                isCountryObserver = false
            }
        }
    }

    private fun order() {
        binding?.apply {
            type = 1
            getOrderPayment()
            clOrderData.isVisible = true
            clDate.isVisible = false
            rvOrder.isVisible = true
            rvPlan.isVisible = false
            rvGift.isVisible = false
            rvBanner.isVisible = false
            tvOrders.setTextColor(requireActivity().getColor(R.color.white))
            tvPlan.setTextColor(requireActivity().getColor(R.color.black))
            tvGiftCard.setTextColor(requireActivity().getColor(R.color.black))
            tvBanner.setTextColor(requireActivity().getColor(R.color.black))
            tvOrders.setBackgroundResource(R.drawable.selected_left_border)
            tvPlan.background = null
            tvGiftCard.background = null
            tvBanner.background = null
        }
    }

    private fun plan() {
        binding?.apply {
            type = 1
            getPlanPayment()
            clOrderData.isVisible = false
            clDate.isVisible = true
            rvOrder.isVisible = false
            rvPlan.isVisible = true
            rvGift.isVisible = false
            rvBanner.isVisible = false
            tvPlan.setTextColor(requireActivity().getColor(R.color.white))
            tvOrders.setTextColor(requireActivity().getColor(R.color.black))
            tvGiftCard.setTextColor(requireActivity().getColor(R.color.black))
            tvBanner.setTextColor(requireActivity().getColor(R.color.black))
            tvPlan.setBackgroundResource(R.drawable.selected_mid_border)
            tvOrders.background = null
            tvGiftCard.background = null
            tvBanner.background = null
        }
    }

    private fun giftCard() {
        binding?.apply {
            type = 1
            getGiftCardPayment()
            clOrderData.isVisible = false
            clDate.isVisible = true
            rvOrder.isVisible = false
            rvPlan.isVisible = false
            rvGift.isVisible = true
            rvBanner.isVisible = false
            tvGiftCard.setTextColor(requireActivity().getColor(R.color.white))
            tvPlan.setTextColor(requireActivity().getColor(R.color.black))
            tvOrders.setTextColor(requireActivity().getColor(R.color.black))
            tvBanner.setTextColor(requireActivity().getColor(R.color.black))
            tvGiftCard.setBackgroundResource(R.drawable.selected_mid_border)
            tvPlan.background = null
            tvOrders.background = null
            tvBanner.background = null
        }
    }

    private fun banner() {
        binding?.apply {
            type = 1
            getBannerPayment()
            clOrderData.isVisible = false
            clDate.isVisible = true
            rvOrder.isVisible = false
            rvPlan.isVisible = false
            rvGift.isVisible = false
            rvBanner.isVisible = true
            tvBanner.setTextColor(requireActivity().getColor(R.color.white))
            tvPlan.setTextColor(requireActivity().getColor(R.color.black))
            tvGiftCard.setTextColor(requireActivity().getColor(R.color.black))
            tvOrders.setTextColor(requireActivity().getColor(R.color.black))
            tvBanner.setBackgroundResource(R.drawable.selected_right_border)
            tvPlan.background = null
            tvGiftCard.background = null
            tvOrders.background = null
        }
    }

    private fun checkData() {
        when (type) {
            1 -> {
                binding?.tvNoDataFound?.isVisible = orderPaymentList.isEmpty()
            }

            2 -> {
                binding?.tvNoDataFound?.isVisible = planPaymentList.isEmpty()
            }

            3 -> {
                binding?.tvNoDataFound?.isVisible = giftCardPaymentList.isEmpty()
            }

            4 -> {
                binding?.tvNoDataFound?.isVisible = bannerPaymentList.isEmpty()
            }
        }
    }

    private fun getOrderPayment(loading: Boolean = true, search: String? = null) {
        if (loading) {
            ProgressDialogUtils.getInstance().showProgress(requireActivity())
        }
        isObserver = true
    }

    private fun getPlanPayment(loading: Boolean = true, search: String? = null) {
        if (loading) {
            ProgressDialogUtils.getInstance().showProgress(requireActivity())
        }
        isObserver = true
    }

    private fun getGiftCardPayment(loading: Boolean = true, search: String? = null) {
        if (loading) {
            ProgressDialogUtils.getInstance().showProgress(requireActivity())
        }
        isObserver = true
    }

    private fun getBannerPayment(loading: Boolean = true, search: String? = null) {
        if (loading) {
            ProgressDialogUtils.getInstance().showProgress(requireActivity())
        }
        isObserver = true
    }

    private fun getCountryApi(){
        viewModel.getCountry()
        isCountryObserver = true
    }

    private fun setCountry() {
        if (!countryList.isNullOrEmpty()) {
            SharedPreference.get(requireActivity()).saveCountryList(countryList)
        }
    }

    private fun paymentFilterDialog() {
        var selectedCountryId:String?=null
        val dialog = Dialog(requireActivity(), R.style.CustomDialog)
        val dialogBinding = PaymentFilterDialogBinding.inflate(LayoutInflater.from(context))
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

            if(SharedPreference.get(requireActivity()).getCountryList() == null) {
                getCountryApi()
            }
            else{
                countryList.clear()
                SharedPreference.get(requireActivity()).getCountryList()?.let { countryList.addAll(it) }
            }

            if(countryId!=null){
                etCountry.text = countryList.firstOrNull { it.id.toString() == countryId }?.name
            }
            if (startDate!=null){
                etFrom.text= CommonMethod.formatDate(startDate.toString(),"yyyy-MM-dd","dd/MM/yyyy")
            }
            if (endDate!=null){
                etTo.text= CommonMethod.formatDate(endDate.toString(),"yyyy-MM-dd","dd/MM/yyyy")
            }
            tvTitle.setOnClickListener {
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

            etStatus.setOnClickListener {
                openStatusPopup(it) { status ->
                    etStatus.setText(status)
                    when (status) {
                        "Pending" -> {
                        }
                        "Rejected" -> {
                        }
                        "Completed" -> {
                        }
                    }
                }
            }

            etCountry.setOnClickListener {
                CommonMethod.showCountryPicker(
                    requireContext(),
                    etCountry,
                    countryList
                ) { selectedCountry ->
                    etCountry.text = selectedCountry.name
                    selectedCountryId = selectedCountry.id.toString()
                }
            }

            clear.setOnClickListener {
                startDate = null
                endDate = null
                countryId = null
                countryName = null
                toTimestamp = 0L
                fromTimestamp = 0L
                when(type) {
                    1 -> {
                        getOrderPayment()
                    }
                    2 -> {
                        getPlanPayment()
                    }
                    3 -> {
                        getGiftCardPayment()
                    }
                    4 -> {
                        getBannerPayment()
                    }
                }
                dialog.dismiss()
            }

            apply.setOnClickListener{
                if (selectedCountryId!=null) {
                    countryId = selectedCountryId
                    countryName = countryList.firstOrNull { it.id.toString() == countryId }?.name
                }
                if (etFrom.text.isNotEmpty()){
                    startDate = CommonMethod.formatDate(etFrom.text.toString(),"dd/MM/yyyy","yyyy-MM-dd")
                }

                if (etTo.text.isNotEmpty()){
                    endDate = CommonMethod.formatDate(etTo.text.toString(),"dd/MM/yyyy","yyyy-MM-dd")
                }
                when(type) {
                    1 -> {
                        getOrderPayment()
                    }
                    2 -> {
                        getPlanPayment()
                    }
                    3 -> {
                        getGiftCardPayment()
                    }
                    4 -> {
                        getBannerPayment()
                    }
                }
                dialog.dismiss()
            }

        }

        dialog.show()
    }

    private fun openStatusPopup(anchor: View, onStatusSelected: (String) -> Unit) {

        val popup = PopupMenu(requireContext(), anchor)

        val statusList = listOf("Pending", "Rejected", "Completed")

        statusList.forEachIndexed { index, status ->
            popup.menu.add(0, index, index, status)
        }

        popup.setOnMenuItemClickListener { item ->
            val selectedStatus = statusList[item.itemId]
            onStatusSelected(selectedStatus)
            true
        }


        popup.show()
    }

}