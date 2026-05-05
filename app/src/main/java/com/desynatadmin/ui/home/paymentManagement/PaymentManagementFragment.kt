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
import androidx.core.content.ContextCompat
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
import com.desynatadmin.ui.home.HomeActivity
import com.desynatadmin.ui.home.SubAdminHomeActivity
import com.desynatadmin.ui.home.paymentManagement.response.GetBannerPaymentResponse
import com.desynatadmin.ui.home.paymentManagement.response.GetGiftCardPaymentResponse
import com.desynatadmin.ui.home.paymentManagement.response.GetOrderPaymentResponse
import com.desynatadmin.ui.home.paymentManagement.response.GetPlanPaymentResponse
import com.desynatadmin.ui.home.subscriptionPlan.response.GetCountryResponse
import com.desynatadmin.utils.CommonMethod
import com.desynatadmin.utils.ProgressDialogUtils
import com.desynatadmin.utils.SharedPreference
import com.desynatadmin.utils.openCustomDatePicker
import com.desynatadmin.utils.openCustomizeDatePicker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Calendar
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

    private var orderPaymentList = ArrayList<GetOrderPaymentResponse.Data.Orders.Data>()
    private var planPaymentList = ArrayList<GetPlanPaymentResponse.Data.Orders.Data>()
    private var giftCardPaymentList = ArrayList<GetGiftCardPaymentResponse.Data.Orders.Data>()
    private var bannerPaymentList = ArrayList<GetBannerPaymentResponse.Data.Orders.Data>()
    private var type = 1

    private var pagination=false
    private val itemsPerPage = 10
    private var currentPage = 1
    private var isFromSwipe=false

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

    var startDateGift:String?=null
    var endDateGift:String?=null
    var fromTimestampGift = 0L
    var toTimestampGift = 0L
    var countryNameGift:String?=null
    var countryIdGift:String?=null

    var startDatePlan:String?=null
    var endDatePlan:String?=null
    var fromTimestampPlan = 0L
    var toTimestampPlan = 0L
    var countryNamePlan:String?=null
    var countryIdPlan:String?=null

    var startDateBanner:String?=null
    var endDateBanner:String?=null
    var fromTimestampBanner = 0L
    var toTimestampBanner = 0L
    var countryNameBanner:String?=null
    var countryIdBanner:String?=null

    private var countryList = ArrayList<GetCountryResponse.Data>()

    private var totalRevenue=0.0
    private var orderCommission=0.0
    private var specialCommission=0.0

    private val months = listOf(
        "January", "February", "March", "April",
        "May", "June", "July", "August",
        "September", "October", "November", "December"
    )

    private var selectedMonthIndex: Int = -1
    private var selectedYear: Int = -1

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

            if (SharedPreference.get(requireActivity()).getUser()?.type=="2"){
                ivBack.isVisible = true
            }
            else{
                ivBack.isVisible = false
            }

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
                            currentPage = 1
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

            swipeRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(requireActivity(), R.color.brown)
            )

            swipeRefreshLayout.setOnRefreshListener {
                isFromSwipe = true
                pagination = false
                currentPage = 1
               when(type){
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
            }

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

            etMonth.setOnClickListener {
                openMonthPopup(it) { index, month ->
                    selectedMonthIndex = index
                    binding?.etMonth?.text = month
                    applyMonthYearFilter()
                }
            }

            etYear.setOnClickListener {
                openYearPopup(it) { year ->
                    selectedYear = year
                    binding?.etYear?.text = year.toString()
                    applyMonthYearFilter()
                }
            }

            btnReset.setOnClickListener {
                resetMonthYearFilter()
            }

            btnExport.setOnClickListener {
                when(type) {
                    1 -> {
                        val csv = CommonMethod.listToCsv(orderPaymentList)
                        CommonMethod.saveCsvFile(requireContext(), "order_payment_${System.currentTimeMillis()}", csv)
                    }
                    2 -> {
                        val csv = CommonMethod.listToCsv(planPaymentList)
                        CommonMethod.saveCsvFile(requireContext(), "plan_payment_${System.currentTimeMillis()}", csv)
                    }
                    3 -> {
                        val csv = CommonMethod.listToCsv(giftCardPaymentList)
                        CommonMethod.saveCsvFile(requireContext(), "gift_payment_${System.currentTimeMillis()}", csv)
                    }
                    4 -> {
                        val csv = CommonMethod.listToCsv(bannerPaymentList)
                        CommonMethod.saveCsvFile(requireContext(), "banner_payment_${System.currentTimeMillis()}", csv)
                    }
                }
            }

            tvOrders.setOnClickListener {
                order()
            }

            tvPlan.setOnClickListener {
                plan()
            }

            tvGiftCard.setOnClickListener {
                giftCard()
            }

            tvBanner.setOnClickListener {
                banner()
            }

            ivFilter.setOnClickListener {
                paymentFilterDialog()
            }

            ivBack.setOnClickListener {
                when (val activity = requireActivity()) {
                    is HomeActivity -> activity.onBackPressed()
                    is SubAdminHomeActivity -> activity.onBackPressed()
                    else -> activity.onBackPressed()
                }
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
                binding?.mainProgressBar?.isVisible = false
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

        viewModel.getOrderPaymentResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                binding?.mainProgressBar?.isVisible = false
                ProgressDialogUtils.getInstance().hideProgress()
                binding?.progressBar?.isVisible = false
                if (isFromSwipe){
                    binding?.swipeRefreshLayout?.isRefreshing = false
                }

                orderCommission = response.data.totalCommission
                specialCommission = response.data.totalCustomOrderRevenue
                totalRevenue = response.data.totalRevenue

                setData()

                currentPage = response.data.orders.currentPage
                totalItem = response.data.orders.total

                lastPage = response.data.orders.lastPage

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


                val oldSize = orderPaymentList.size
                if (!pagination){
                    orderPaymentList.clear()
                }
                updatePage()
                val newItems = response.data.orders.data ?: emptyList()

                orderPaymentList.addAll(newItems)
                checkData()

                if (pagination){

                    response.data.orders?.data?.size?.let {
                        orderPaymentAdapter?.notifyItemRangeInserted(oldSize,
                            it
                        )
                    }
                }
                else{
                    orderPaymentAdapter?.notifyDataSetChanged()
                }

                pagination = false
                isFromSwipe = false
                isObserver = false
            }
        }

        viewModel.getPlanPaymentResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                binding?.progressBar?.isVisible = false
                binding?.mainProgressBar?.isVisible = false
                if (isFromSwipe){
                    binding?.swipeRefreshLayout?.isRefreshing = false
                }
                totalRevenue = response.data.totalRevenue
                currentPage = response.data.orders.currentPage
                totalItem = response.data.orders.total
                setData()

                lastPage = response.data.orders.lastPage

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


                val oldSize = planPaymentList.size
                if (!pagination){
                    planPaymentList.clear()
                }
                updatePage()
                val newItems = response.data.orders.data ?: emptyList()

                planPaymentList.addAll(newItems)
                checkData()

                if (pagination){

                    response.data.orders?.data?.size?.let {
                        planPaymentAdapter?.notifyItemRangeInserted(oldSize,
                            it
                        )
                    }
                }
                else{
                    planPaymentAdapter?.notifyDataSetChanged()
                }

                pagination = false
                isFromSwipe = false
                isObserver = false
            }
        }

        viewModel.getBannerPaymentResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                binding?.mainProgressBar?.isVisible = false
                binding?.progressBar?.isVisible = false
                if (isFromSwipe){
                    binding?.swipeRefreshLayout?.isRefreshing = false
                }
                totalRevenue = response.data.totalRevenue
                currentPage = response.data.orders.currentPage
                totalItem = response.data.orders.total
                setData()

                lastPage = response.data.orders.lastPage

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


                val oldSize = bannerPaymentList.size
                if (!pagination){
                    bannerPaymentList.clear()
                }
                updatePage()
                val newItems = response.data.orders.data ?: emptyList()

                bannerPaymentList.addAll(newItems)
                checkData()

                if (pagination){

                    response.data.orders?.data?.size?.let {
                        bannerPaymentAdapter?.notifyItemRangeInserted(oldSize,
                            it
                        )
                    }
                }
                else{
                    bannerPaymentAdapter?.notifyDataSetChanged()
                }

                pagination = false
                isFromSwipe = false
                isObserver = false
            }
        }

        viewModel.getGiftCardPaymentResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                binding?.mainProgressBar?.isVisible = false
                binding?.progressBar?.isVisible = false
                if (isFromSwipe){
                    binding?.swipeRefreshLayout?.isRefreshing = false
                }
                totalRevenue = response.data.totalRevenue
                currentPage = response.data.orders.currentPage
                totalItem = response.data.orders.total
                setData()

                lastPage = response.data.orders.lastPage

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


                val oldSize = giftCardPaymentList.size
                if (!pagination){
                    giftCardPaymentList.clear()
                }
                updatePage()
                val newItems = response.data.orders.data ?: emptyList()

                giftCardPaymentList.addAll(newItems)
                checkData()

                if (pagination){

                    response.data.orders?.data?.size?.let {
                        giftCardPaymentAdapter?.notifyItemRangeInserted(oldSize,
                            it
                        )
                    }
                }
                else{
                    giftCardPaymentAdapter?.notifyDataSetChanged()
                }

                pagination = false
                isFromSwipe = false
                isObserver = false
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
            if (startDate!=null){
                etMonth.text= CommonMethod.formatDate(startDate.toString(),"yyyy-MM-dd","MMMM")
                etYear.text= CommonMethod.formatDate(startDate.toString(),"yyyy-MM-dd","yyyy")
            }
            else{
                etMonth.text= ""
                etYear.text= ""
            }
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
            type = 2
            getPlanPayment()
            clOrderData.isVisible = false
            clDate.isVisible = true
            rvOrder.isVisible = false
            rvPlan.isVisible = true
            rvGift.isVisible = false
            rvBanner.isVisible = false
            if (startDatePlan!=null){
                etMonth.text= CommonMethod.formatDate(startDatePlan.toString(),"yyyy-MM-dd","MMMM")
                etYear.text= CommonMethod.formatDate(startDatePlan.toString(),"yyyy-MM-dd","yyyy")
            }
            else{
                etMonth.text= ""
                etYear.text= ""
            }
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
            type = 3
            getGiftCardPayment()
            clOrderData.isVisible = false
            clDate.isVisible = true
            rvOrder.isVisible = false
            rvPlan.isVisible = false
            rvGift.isVisible = true
            rvBanner.isVisible = false
            if (startDateGift!=null){
                etMonth.text= CommonMethod.formatDate(startDateGift.toString(),"yyyy-MM-dd","MMMM")
                etYear.text= CommonMethod.formatDate(startDateGift.toString(),"yyyy-MM-dd","yyyy")
            } else{
                etMonth.text= ""
                etYear.text= ""
            }
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
            type = 4
            getBannerPayment()
            clOrderData.isVisible = false
            clDate.isVisible = true
            rvOrder.isVisible = false
            rvPlan.isVisible = false
            rvGift.isVisible = false
            rvBanner.isVisible = true
            if (startDateBanner!=null){
                etMonth.text= CommonMethod.formatDate(startDateBanner.toString(),"yyyy-MM-dd","MMMM")

                etYear.text= CommonMethod.formatDate(startDateBanner.toString(),"yyyy-MM-dd","yyyy")
            }
            else{
                etMonth.text= ""
                etYear.text= ""
            }
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

    private fun updatePage() {

        when(type){
            1->{
                val start = (currentPage - 1) * itemsPerPage
                val end = minOf(start + itemsPerPage, orderPaymentList.size)
                if (start>=0&&end>=0) {
                    if (start < end) {
                        val pageData = orderPaymentList.subList(start, end)
                        orderPaymentAdapter?.setData(ArrayList(pageData))
                    }
                }
            }
            2->{
                val start = (currentPage - 1) * itemsPerPage
                val end = minOf(start + itemsPerPage, planPaymentList.size)
                if (start>=0&&end>=0) {
                    if (start < end) {
                        val pageData = planPaymentList.subList(start, end)
                        planPaymentAdapter?.setData(ArrayList(pageData))
                    }
                }
            }
            3->{
                val start = (currentPage - 1) * itemsPerPage
                val end = minOf(start + itemsPerPage, giftCardPaymentList.size)
                if (start>=0&&end>=0) {
                    if (start < end) {
                        val pageData = giftCardPaymentList.subList(start, end)
                        giftCardPaymentAdapter?.setData(ArrayList(pageData))
                    }
                }
            }
            4->{
                val start = (currentPage - 1) * itemsPerPage
                val end = minOf(start + itemsPerPage, bannerPaymentList.size)
                if (start>=0&&end>=0) {
                    if (start < end) {
                        val pageData = bannerPaymentList.subList(start, end)
                        bannerPaymentAdapter?.setData(ArrayList(pageData))
                    }
                }
            }
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
        val map = HashMap<String, Any>()
        map["limit"] = 10
        map["page"] = currentPage
        if (search!=null){
            map["search"] = search
        }

        if (startDate!=null){
            map["from_date"] = startDate.toString()
        }
        if (endDate!=null){
            map["to_date"] = endDate.toString()
        }
        if (countryId!=null){
            map["country_id"] = countryId.toString()
        }
        viewModel.getOrderPayment(map)
        if (loading) {
            binding?.mainProgressBar?.isVisible = true
        }
        isObserver = true
    }

    private fun getPlanPayment(loading: Boolean = true, search: String? = null) {
        val map = HashMap<String, Any>()
        map["limit"] = 10
        map["page"] = currentPage
        if (search!=null){
            map["search"] = search
        }

        if (startDatePlan!=null){
            map["from_date"] = startDatePlan.toString()
        }
        if (endDatePlan!=null){
            map["to_date"] = endDatePlan.toString()
        }
        if (countryIdPlan!=null){
            map["country_id"] = countryIdPlan.toString()
        }
        viewModel.getPlanPayment(map)
        if (loading) {
            binding?.mainProgressBar?.isVisible = true
        }
        isObserver = true
    }

    private fun getGiftCardPayment(loading: Boolean = true, search: String? = null) {
        val map = HashMap<String, Any>()
        map["limit"] = 10
        map["page"] = currentPage
        if (search!=null){
            map["search"] = search
        }
        if (startDateGift!=null){
            map["from_date"] = startDateGift.toString()
        }
        if (endDateGift!=null){
            map["to_date"] = endDateGift.toString()
        }
        if (countryIdGift!=null){
            map["country_id"] = countryIdGift.toString()
        }
        viewModel.getGiftCardPayment(map)
        if (loading) {
            binding?.mainProgressBar?.isVisible = true
        }
        isObserver = true
    }

    private fun getBannerPayment(loading: Boolean = true, search: String? = null) {
        val map = HashMap<String, Any>()
        map["limit"] = 10
        map["page"] = currentPage
        if (search!=null){
            map["search"] = search
        }
        if (startDateBanner!=null){
            map["from_date"] = startDateBanner.toString()
        }
        if (endDateBanner!=null){
            map["to_date"] = endDateBanner.toString()
        }

        if (countryIdBanner!=null){
            map["country_id"] = countryIdBanner.toString()
        }

        viewModel.getBannerPayment(map)
        if (loading) {
           binding?.mainProgressBar?.isVisible = true
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
        var selectedStartDate:String?=null
        var selectedEndDate:String?=null
        var selectedStartDateTimeStamp = 0L
        var selectedEndDateTimeStamp = 0L
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

            when(type){
                1->{
                    selectedCountryId = countryId
                    selectedStartDate = startDate
                    selectedStartDateTimeStamp = fromTimestamp
                    selectedEndDate = endDate
                    selectedEndDateTimeStamp = toTimestamp
                }
                2->{
                    selectedCountryId = countryIdPlan
                    selectedStartDate = startDatePlan
                    selectedStartDateTimeStamp = fromTimestampPlan
                    selectedEndDate = endDatePlan
                    selectedEndDateTimeStamp = toTimestampPlan
                }

                3->{
                    selectedCountryId = countryIdGift
                    selectedStartDate = startDateGift
                    selectedStartDateTimeStamp = fromTimestampGift
                    selectedEndDate = endDateGift
                    selectedEndDateTimeStamp = toTimestampGift
                }
                4->{
                    selectedCountryId = countryIdBanner
                    selectedStartDate = startDateBanner
                    selectedStartDateTimeStamp = fromTimestampBanner
                    selectedEndDate = endDateBanner
                    selectedEndDateTimeStamp = toTimestampBanner
                }
            }

            if(SharedPreference.get(requireActivity()).getCountryList() == null) {
                getCountryApi()
            }
            else{
                countryList.clear()
                SharedPreference.get(requireActivity()).getCountryList()?.let { countryList.addAll(it) }
            }

            if(selectedCountryId!=null){
                etCountry.text = countryList.firstOrNull { it.id.toString() == selectedCountryId }?.name
            }
            if (selectedStartDate!=null){
                etFrom.text= CommonMethod.formatDate(selectedStartDate.toString(),"yyyy-MM-dd","dd/MM/yyyy")
            }
            if (selectedEndDate!=null){
                etTo.text= CommonMethod.formatDate(selectedEndDate.toString(),"yyyy-MM-dd","dd/MM/yyyy")
            }
            tvTitle.setOnClickListener {
                dialog.dismiss()
            }

            etFrom.setOnClickListener {
                openCustomizeDatePicker(requireActivity(),maxDate = System.currentTimeMillis()){date,timestamp->
                    etFrom.text=date
                    selectedStartDateTimeStamp=timestamp
                }

            }
            etTo.setOnClickListener {
                openCustomizeDatePicker(requireActivity(), minDate = selectedStartDateTimeStamp,maxDate = System.currentTimeMillis()){date,timestamp->
                    etTo.text=date
                    selectedEndDateTimeStamp=timestamp
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


                currentPage = 1
                when(type) {
                    1 -> {
                        startDate = null
                        endDate = null
                        countryId = null
                        countryName = null
                        toTimestamp = 0L
                        fromTimestamp = 0L
                        getOrderPayment()
                    }
                    2 -> {
                        startDatePlan = null
                        endDatePlan = null
                        countryIdPlan = null
                        countryNamePlan = null
                        toTimestampPlan = 0L
                        fromTimestampPlan = 0L
                        getPlanPayment()
                    }
                    3 -> {
                        startDateGift = null
                        endDateGift = null
                        countryIdGift = null
                        countryNameGift = null
                        toTimestampGift = 0L
                        fromTimestampGift = 0L
                        getGiftCardPayment()
                    }
                    4 -> {
                        startDateBanner = null
                        endDateBanner = null
                        countryIdBanner = null
                        countryNameBanner = null
                        toTimestampBanner = 0L
                        fromTimestampBanner = 0L
                        getBannerPayment()
                    }
                }
                dialog.dismiss()
            }

            apply.setOnClickListener{
                if (selectedCountryId!=null) {
                    when(type) {
                        1 -> {
                            countryId = selectedCountryId
                            countryName = countryList.firstOrNull { it.id.toString() == countryId }?.name
                        }
                        2 -> {
                            countryIdPlan = selectedCountryId
                            countryNamePlan = countryList.firstOrNull { it.id.toString() == countryId }?.name
                        }
                        3 -> {
                            countryIdGift = selectedCountryId
                            countryNameGift = countryList.firstOrNull { it.id.toString() == countryId }?.name
                        }
                        4 -> {
                            countryIdBanner = selectedCountryId
                            countryNameBanner = countryList.firstOrNull { it.id.toString() == countryId }?.name
                        }
                    }

                }
                if (etFrom.text.isNotEmpty()){
                    val date = CommonMethod.formatDate(etFrom.text.toString(),"dd/MM/yyyy","yyyy-MM-dd")
                    when(type) {
                        1 -> {
                            startDate = date
                           fromTimestamp = selectedStartDateTimeStamp
                        }
                        2 -> {
                            startDatePlan = date
                            fromTimestampPlan = selectedStartDateTimeStamp
                        }

                        3 -> {
                            startDateGift = date
                            fromTimestampGift = selectedStartDateTimeStamp
                        }
                        4 -> {
                            startDateBanner = date
                            fromTimestampBanner = selectedStartDateTimeStamp
                        }
                    }
                }

                if (etTo.text.isNotEmpty()){
                    val date  = CommonMethod.formatDate(etTo.text.toString(),"dd/MM/yyyy","yyyy-MM-dd")
                    when(type) {
                        1 -> {
                            endDate = date
                            toTimestamp = selectedEndDateTimeStamp
                        }
                        2 -> {
                            endDatePlan = date
                            toTimestampPlan = selectedEndDateTimeStamp
                        }
                        3 -> {
                            endDateGift = date
                            toTimestampGift = selectedEndDateTimeStamp
                        }
                        4 -> {
                            endDateBanner = date
                            toTimestampBanner = selectedEndDateTimeStamp
                        }
                    }
                }
                currentPage = 1
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

    private fun setData(){
        binding?.apply {
            when(type){
                1->{
                    clOrderData.isVisible = true
                    tvSpecial.text = "${CommonMethod.formatDoubleValue(specialCommission)} KD"
                    tvCommission.text = "${CommonMethod.formatDoubleValue(orderCommission)} KD"
                }
                else ->{
                    clOrderData.isVisible = false
                }
            }
            tvTotalRevenue.text = "${CommonMethod.formatDoubleValue(totalRevenue)} KD"
        }
    }

    private fun openMonthPopup(anchor: View, onMonthSelected: (Int, String) -> Unit) {
        val popup = PopupMenu(requireContext(), anchor)

        months.forEachIndexed { index, month ->
            popup.menu.add(0, index, index, month)
        }

        popup.setOnMenuItemClickListener { item ->
            val monthIndex = item.itemId
            val monthName = months[monthIndex]
            onMonthSelected(monthIndex, monthName)
            true
        }

        popup.show()
    }

    private fun openYearPopup(anchor: View, onYearSelected: (Int) -> Unit) {
        val popup = PopupMenu(requireContext(), anchor)

        val currentYear = Calendar.getInstance().get(Calendar.YEAR)

        for (year in currentYear downTo 2000) {
            popup.menu.add(0, year, year, year.toString())
        }

        popup.setOnMenuItemClickListener { item ->
            val selectedYear = item.itemId
            onYearSelected(selectedYear)
            true
        }

        popup.show()
    }

    private fun applyMonthYearFilter() {
        if (selectedMonthIndex == -1 || selectedYear == -1) return

        binding?.btnReset?.isVisible = true
        val calendar = Calendar.getInstance()

        calendar.set(selectedYear, selectedMonthIndex, 1)
        val startDateStr = CommonMethod.formatDate(
            "${selectedYear}-${selectedMonthIndex + 1}-01",
            "yyyy-M-dd",
            "yyyy-MM-dd"
        )

        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
        val lastDay = calendar.get(Calendar.DAY_OF_MONTH)

        val endDateStr = CommonMethod.formatDate(
            "${selectedYear}-${selectedMonthIndex + 1}-$lastDay",
            "yyyy-M-dd",
            "yyyy-MM-dd"
        )

        when (type) {
            1 -> {
                startDate = startDateStr
                endDate = endDateStr
            }
            2 -> {
                startDatePlan = startDateStr
                endDatePlan = endDateStr
            }
            3 -> {
                startDateGift = startDateStr
                endDateGift = endDateStr
            }
            4 -> {
                startDateBanner = startDateStr
                endDateBanner = endDateStr
            }
        }

        currentPage = 1

        when (type) {
            1 -> getOrderPayment()
            2 -> getPlanPayment()
            3 -> getGiftCardPayment()
            4 -> getBannerPayment()
        }
    }

    private fun resetMonthYearFilter() {
        binding?.btnReset?.isVisible = false
        selectedMonthIndex = -1
        selectedYear = -1

        binding?.etMonth?.text = ""
        binding?.etYear?.text = ""

        binding?.etMonth?.hint = "Select Month"
        binding?.etYear?.hint = "Select Year"

        when (type) {
            1 -> {
                startDate = null
                endDate = null
            }
            2 -> {
                startDatePlan = null
                endDatePlan = null
            }
            3 -> {
                startDateGift = null
                endDateGift = null
            }
            4 -> {
                startDateBanner = null
                endDateBanner = null
            }
        }

        currentPage = 1

        when (type) {
            1 -> getOrderPayment()
            2 -> getPlanPayment()
            3 -> getGiftCardPayment()
            4 -> getBannerPayment()
        }
    }

}