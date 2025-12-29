package com.desynatadmin.ui.home.orderManagement

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import coil.load
import com.desynatadmin.R
import com.desynatadmin.adapter.AssignDriverAdapter
import com.desynatadmin.adapter.ProductAdapter
import com.desynatadmin.base.BaseFragment
import com.desynatadmin.data.view_model.HomeViewModel
import com.desynatadmin.databinding.AssignDriverDialogBinding
import com.desynatadmin.databinding.FragmentOrderDetailBinding
import com.desynatadmin.databinding.UserFilterDialogBinding
import com.desynatadmin.ui.home.orderManagement.response.GetActiveOrderResponse
import com.desynatadmin.ui.home.orderManagement.response.GetAvailableDeliveryBoyResponse
import com.desynatadmin.utils.CommonMethod
import com.desynatadmin.utils.ProgressDialogUtils
import com.desynatadmin.utils.openCustomDatePicker
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OrderDetailFragment : BaseFragment() {
    private var binding: FragmentOrderDetailBinding?=null
    private val viewModel: HomeViewModel by viewModels()
    private var isObserver = true
    private var isDriverObserver = true
    private var isAssignObserver = true

    private var productList = ArrayList<GetActiveOrderResponse.Data.Data>()
    private var productAdapter : ProductAdapter?=null

    private var type = 0
    private var orderDetailData: GetActiveOrderResponse.Data.Data?=null
    private var assignDriverAdapter : AssignDriverAdapter?=null
    private var selectedDriver: GetAvailableDeliveryBoyResponse.Data?=null
    private var assignDriverList = ArrayList<GetAvailableDeliveryBoyResponse.Data>()
    private var dialogDriver: Dialog?=null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOrderDetailBinding.inflate(layoutInflater,container,false)
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

        val itemJson = arguments?.getString("orderData")
        type = arguments?.getInt("type")?:1
        binding?.apply {

            productList.clear()
            productAdapter = ProductAdapter(requireActivity(),productList)
            rvProduct.adapter = productAdapter

            if (!itemJson.isNullOrEmpty()) {
                orderDetailData = Gson().fromJson(itemJson, GetActiveOrderResponse.Data.Data::class.java)
                setData()
                getOrderDetail(orderDetailData?.id.toString())
            }


        }
    }

    private fun getOrderDetail(orderId:String) {
        val map = HashMap<String,Any>()
        map["id"]=orderId
        viewModel.getOrderDetail(map)
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }


    private fun setData() {


        productList.clear()
        orderDetailData?.let { productList.add(it) }
        productAdapter?.notifyDataSetChanged()

        binding?.apply {

            when(orderDetailData?.deliveryStatus){
                "1"->{
                    tvStatus.text ="New"
                    tvCancelReason.isVisible = false
                    tvCancellationReason.isVisible = false
                    llComplete.isVisible = false
                    btnInvoice.isVisible = true
                    ivPicked.setImageResource(R.drawable.ic_order_picked)
                    ivOnTheWay.setImageResource(R.drawable.ic_order_on_way)
                    ivDelivered.setImageResource(R.drawable.ic_delivered_tab)

                    if (orderDetailData?.assignStatus=="1"){
                        btnAssignDriver.isVisible = false
                        clDriver.isVisible = true
                        ivDriver.load(orderDetailData?.driverDetails?.profilePic){
                            crossfade(true)
                            placeholder(R.drawable.ic_placeholder)
                            error(R.drawable.ic_placeholder)
                        }
                        tvDeliveredBy.text = orderDetailData?.driverDetails?.fullNameEnglish
                        tvZoneValue.text = orderDetailData?.driverDetails?.zoneInfo?.nameEnglish
                        tvDriverName.text = orderDetailData?.driverDetails?.fullNameEnglish
                        tvDeliveredBy.isVisible = true
                        tvDelivered.isVisible = true
                        tvZoneValue.isVisible = true
                        tvZone.isVisible = true
                    }
                    else{
                        clDriver.isVisible = false
                        btnAssignDriver.isVisible = true
                        tvZoneValue.isVisible = false
                        tvZone.isVisible = false
                    }
                }

                "2"->{
                    tvStatus.text ="Confirmed"
                    tvCancelReason.isVisible = false
                    tvCancellationReason.isVisible = false
                    llComplete.isVisible = false
                    btnInvoice.isVisible = true
                    ivPicked.setImageResource(R.drawable.ic_order_picked_selected)
                    ivOnTheWay.setImageResource(R.drawable.ic_order_on_way)
                    ivDelivered.setImageResource(R.drawable.ic_delivered_tab)

                    if (orderDetailData?.assignStatus=="1"){
                        btnAssignDriver.isVisible = false
                        clDriver.isVisible = true
                        ivDriver.load(orderDetailData?.driverDetails?.profilePic){
                            crossfade(true)
                            placeholder(R.drawable.ic_placeholder)
                            error(R.drawable.ic_placeholder)
                        }
                        tvDeliveredBy.text = orderDetailData?.driverDetails?.fullNameEnglish
                        tvZoneValue.text = orderDetailData?.driverDetails?.zoneInfo?.nameEnglish
                        tvDriverName.text = orderDetailData?.driverDetails?.fullNameEnglish
                        tvDeliveredBy.isVisible = true
                        tvDelivered.isVisible = true
                        tvZoneValue.isVisible = true
                        tvZone.isVisible = true
                    }
                    else{
                        btnAssignDriver.isVisible = true
                        tvDeliveredBy.isVisible = false
                        tvDelivered.isVisible = false
                        tvZoneValue.isVisible = false
                        tvZone.isVisible = false
                    }

                }

                "6"->{
                    tvStatus.text ="Confirmed"
                    tvCancelReason.isVisible = false
                    tvCancellationReason.isVisible = false
                    llComplete.isVisible = false
                    btnAssignDriver.isVisible = false
                    ivPicked.setImageResource(R.drawable.ic_order_picked_selected)
                    ivOnTheWay.setImageResource(R.drawable.ic_order_on_way)
                    ivDelivered.setImageResource(R.drawable.ic_delivered_tab)
                    clDriver.isVisible = true
                    ivDriver.load(orderDetailData?.driverDetails?.profilePic){
                        crossfade(true)
                        placeholder(R.drawable.ic_placeholder)
                        error(R.drawable.ic_placeholder)
                    }
                    tvDeliveredBy.text = orderDetailData?.driverDetails?.fullNameEnglish
                    tvZoneValue.text = orderDetailData?.driverDetails?.zoneInfo?.nameEnglish
                    tvDriverName.text = orderDetailData?.driverDetails?.fullNameEnglish
                }

                "4"->{
                    tvStatus.text ="Cancelled"
                    tvStatus.setTextColor(Color.parseColor("#EB2525"))
                    tvCancelReason.isVisible = true
                    tvCancellationReason.isVisible = true
                    llComplete.isVisible = false
                    btnInvoice.isVisible = false
                    btnAssignDriver.isVisible = false
                    ivOnTheWay.isVisible = false
                    tvOnTheWay.isVisible = false
                    clDriver.isVisible = true
                    ivDelivered.setImageResource(R.drawable.ic_cancelled)
                    tvDeliveredText.text = "Cancelled"
                    tvDeliveredText.setTextColor(Color.parseColor("#D72E04"))
                    ivDriver.load(orderDetailData?.driverDetails?.profilePic){
                        crossfade(true)
                        placeholder(R.drawable.ic_placeholder)
                        error(R.drawable.ic_placeholder)
                    }
                    tvDeliveredBy.text = orderDetailData?.driverDetails?.fullNameEnglish
                    tvZoneValue.text = orderDetailData?.driverDetails?.zoneInfo?.nameEnglish
                    tvDriverName.text = orderDetailData?.driverDetails?.fullNameEnglish
                }
                "8"->{
                    tvStatus.text ="Not Delivered"
                    tvStatus.setTextColor(Color.parseColor("#EB2525"))
                    tvCancelReason.isVisible = true
                    tvCancellationReason.isVisible = true
                    llComplete.isVisible = false
                    btnInvoice.isVisible = false
                    btnAssignDriver.isVisible = false
                    clDriver.isVisible = true
                    ivDriver.load(orderDetailData?.driverDetails?.profilePic){
                        crossfade(true)
                        placeholder(R.drawable.ic_placeholder)
                        error(R.drawable.ic_placeholder)
                    }
                    tvDeliveredBy.text = orderDetailData?.driverDetails?.fullNameEnglish
                    tvZoneValue.text = orderDetailData?.driverDetails?.zoneInfo?.nameEnglish
                    tvDriverName.text = orderDetailData?.driverDetails?.fullNameEnglish
                }
                "3"->{
                    tvStatus.text ="Delivered"
                    btnAssignDriver.isVisible = false
                    tvCancelReason.isVisible = false
                    tvCancellationReason.isVisible = false
                    clDriver.isVisible = true
                    llComplete.isVisible = true
                    btnAssignDriver.isVisible = false
                    ivDriver.load(orderDetailData?.driverDetails?.profilePic){
                        crossfade(true)
                        placeholder(R.drawable.ic_placeholder)
                        error(R.drawable.ic_placeholder)
                    }
                    tvDeliveredBy.text = orderDetailData?.driverDetails?.fullNameEnglish
                    tvZoneValue.text = orderDetailData?.driverDetails?.zoneInfo?.nameEnglish
                    tvDriverName.text = orderDetailData?.driverDetails?.fullNameEnglish
                    ivPicked.setImageResource(R.drawable.ic_order_picked_selected)
                    ivOnTheWay.setImageResource(R.drawable.ic_order_on_way_selected)
                    ivDelivered.setImageResource(R.drawable.ic_delivered_tab_selected)

                }
                "7"->{
                    tvStatus.text ="On the way"
                    btnAssignDriver.isVisible = false
                    clDriver.isVisible = true
                    ivDriver.load(orderDetailData?.driverDetails?.profilePic){
                        crossfade(true)
                        placeholder(R.drawable.ic_placeholder)
                        error(R.drawable.ic_placeholder)
                    }
                    tvDeliveredBy.text = orderDetailData?.driverDetails?.fullNameEnglish
                    tvZoneValue.text = orderDetailData?.driverDetails?.zoneInfo?.nameEnglish
                    tvDriverName.text = orderDetailData?.driverDetails?.fullNameEnglish
                    ivPicked.setImageResource(R.drawable.ic_order_picked_selected)
                    ivOnTheWay.setImageResource(R.drawable.ic_order_on_way_selected)
                    ivDelivered.setImageResource(R.drawable.ic_delivered_tab)

                    tvPicked.text = "Picked"
                }

                "9"->{
                    tvStatus.text ="Exchange"
                    btnAssignDriver.isVisible = false
                    tvCancelReason.isVisible = false
                    tvCancellationReason.isVisible = false
                    clDriver.isVisible = true
                    llComplete.isVisible = true
                    ivDriver.load(orderDetailData?.driverDetails?.profilePic){
                        crossfade(true)
                        placeholder(R.drawable.ic_placeholder)
                        error(R.drawable.ic_placeholder)
                    }
                    tvDeliveredBy.text = orderDetailData?.driverDetails?.fullNameEnglish
                    tvZoneValue.text = orderDetailData?.driverDetails?.zoneInfo?.nameEnglish
                    tvDriverName.text = orderDetailData?.driverDetails?.fullNameEnglish
                    ivPicked.setImageResource(R.drawable.ic_order_picked_selected)
                    ivOnTheWay.setImageResource(R.drawable.ic_order_on_way)
                    ivDelivered.setImageResource(R.drawable.ic_delivered_tab)
                    tvPicked.text = "Picked"
                    tvDeliveredText.text = "Exchange"
                }

                "5"->{
                    tvStatus.text ="Return"
                    btnAssignDriver.isVisible = false
                    tvCancelReason.isVisible = false
                    tvCancellationReason.isVisible = false
                    clDriver.isVisible = true
                    llComplete.isVisible = true
                    ivDriver.load(orderDetailData?.driverDetails?.profilePic){
                        crossfade(true)
                        placeholder(R.drawable.ic_placeholder)
                        error(R.drawable.ic_placeholder)
                    }
                    tvDeliveredBy.text = orderDetailData?.driverDetails?.fullNameEnglish
                    tvZoneValue.text = orderDetailData?.driverDetails?.zoneInfo?.nameEnglish
                    tvDriverName.text = orderDetailData?.driverDetails?.fullNameEnglish
                    ivPicked.setImageResource(R.drawable.ic_order_picked_selected)
                    ivOnTheWay.setImageResource(R.drawable.ic_order_on_way)
                    ivDelivered.setImageResource(R.drawable.ic_delivered_tab)

                }
            }

            tvOrderIdValue.text = "#${orderDetailData?.id}"
            tvDateTime.text = CommonMethod.customizeChangeDateFormat(orderDetailData?.createdAt.toString(), outputFormat = "dd/MM/yy 'at' hh:mm a")
            tvOrderTypeValue.text = "Normal Orders"
            tvCustomerName.text = orderDetailData?.order?.deliveryDetails?.name
            tvSellerName.text = orderDetailData?.productDetails?.sellerDetails?.firstName+" "+orderDetailData?.productDetails?.sellerDetails?.lastName
            tvCountryValue.text = orderDetailData?.order?.deliveryDetails?.countryName
            tvShippingLocationValue.text = orderDetailData?.order?.deliveryDetails?.address

            tvTotalAmount.text = "${CommonMethod.formatPrice(orderDetailData?.order?.totalAmount?:0.0)} KD"
            CommissionEarned.text = "${CommonMethod.formatPrice(orderDetailData?.productDetails?.affiliatedCommission?:0.0)} KD"
        }

    }

    override fun initControl() {
        binding?.apply {

            ivBack.setOnClickListener {
                findNavController().popBackStack()
            }

            btnInvoice.setOnClickListener {

            }

            btnAssignDriver.setOnClickListener {
                assignDriverDialog()
            }

            ivEdit.setOnClickListener {
                assignDriverDialog()
            }

            }
        }

    override fun observe() {
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (isObserver||isDriverObserver||isAssignObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                isObserver = false
                isDriverObserver = false
                isAssignObserver = false
            }
        }

        viewModel.assignDriverResult.observe(viewLifecycleOwner) { response ->
            if (isAssignObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                dialogDriver?.dismiss()
                CommonMethod.showSuccessDialog(requireActivity(),"Driver assigned successfully"){
                    getOrderDetail(orderDetailData?.id.toString())
                }
                isAssignObserver = false
            }
        }

        viewModel.getOrderDetailResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                orderDetailData = response.data
                setData()
                isObserver = false
            }
        }

        viewModel.getAssignDriverListResult.observe(viewLifecycleOwner) { response ->
            if (isDriverObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                assignDriverList.clear()
                assignDriverList.addAll(response.data)
                assignDriverAdapter?.notifyDataSetChanged()
                isDriverObserver = false
            }
        }
    }

    private fun getAssignDriver(orderId:String) {
        viewModel.getDriverToAssign(orderId)
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isDriverObserver = true
    }

    private fun assignDriver() {
        val map = HashMap<String,Any>()
        map["driver_id"] = selectedDriver?.id.toString()
        map["order_id"] = orderDetailData?.id.toString()
        viewModel.assignDriver(map)
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isAssignObserver = true
    }

    private fun assignDriverDialog() {
        var newSelectedDriver:GetAvailableDeliveryBoyResponse.Data? = null
        dialogDriver = Dialog(requireActivity(), R.style.CustomDialog)
        val dialogBinding = AssignDriverDialogBinding.inflate(LayoutInflater.from(context))
        dialogBinding.root.let { dialogDriver?.setContentView(it) }

        val window: Window = dialogDriver?.window!!
        window.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        window.setBackgroundDrawableResource(android.R.color.transparent)
        dialogDriver?.setCancelable(false)

        val params: WindowManager.LayoutParams = window.attributes
        params.gravity = Gravity.BOTTOM
        window.attributes = params

        dialogBinding.apply {

            assignDriverAdapter = AssignDriverAdapter(requireActivity(),assignDriverList,object :AssignDriverAdapter.DriverClick{
                override fun onDriverClick(position: Int) {
                    newSelectedDriver = assignDriverList[position]
                    tvSelectedDriver.text = assignDriverList[position].fullNameEnglish
                }

            })
            rvDriver.adapter = assignDriverAdapter

            getAssignDriver(orderDetailData?.id.toString())

            tvToolbar.setOnClickListener {
                dialogDriver?.dismiss()
            }

            btnSubmit.setOnClickListener {
                if (newSelectedDriver!=null){
                    selectedDriver = newSelectedDriver
                    assignDriver()
                }

            }

        }

        dialogDriver?.show()
    }

}