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
import com.desynatadmin.adapter.OrderProductAdapter
import com.desynatadmin.base.BaseFragment
import com.desynatadmin.data.view_model.HomeViewModel
import com.desynatadmin.databinding.AssignDriverDialogBinding
import com.desynatadmin.databinding.FragmentOrderDetailBinding
import com.desynatadmin.ui.home.orderManagement.response.GetActiveOrderResponse
import com.desynatadmin.ui.home.orderManagement.response.GetAvailableDeliveryBoyResponse
import com.desynatadmin.utils.CommonMethod
import com.desynatadmin.utils.Constants
import com.desynatadmin.utils.ProgressDialogUtils
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
    private var productAdapter : OrderProductAdapter?=null

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
            productAdapter = OrderProductAdapter(requireActivity(),productList)
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

            when(type){
                2->{
                    if(orderDetailData?.returnReason!=null){
                        tvStatus.text = getString(R.string.return_text)
                        btnAssignDriver.isVisible = false
                        tvCancelReason.isVisible = true
                        tvCancellationReason.isVisible = true
                        clDriver.isVisible = true
                        llComplete.isVisible = false
                        ivEdit.isVisible = false
                        ivDriver.load(orderDetailData?.driverDetails?.profilePic){
                            crossfade(true)
                            placeholder(R.drawable.ic_placeholder)
                            error(R.drawable.ic_placeholder)
                        }
                        tvDriverName.text = if (Constants.language=="ar") orderDetailData?.driverDetails?.fullNameArabic else orderDetailData?.driverDetails?.fullNameEnglish
                        ivPicked.setImageResource(R.drawable.ic_order_picked_selected)
                        ivOnTheWay.setImageResource(R.drawable.ic_order_on_way)
                        ivDelivered.setImageResource(R.drawable.ic_delivered_tab)
                        tvPicked.text = getString(R.string.picked)
                        tvDeliveredText.text = getString(R.string.return_text)
                        if (orderDetailData?.returnReason!=null){
                            tvCancellationReason.text = orderDetailData?.returnReason
                        }

                        if (orderDetailData?.assignStatus=="0"){
                            btnAssignDriver.isVisible = true
                        }
                        else{
                            btnAssignDriver.isVisible = false
                        }

                    }
                    else if(orderDetailData?.exchangeReason!=null){
                        tvStatus.text = getString(R.string.exchange)
                        btnAssignDriver.isVisible = false
                        tvCancelReason.isVisible = true
                        tvCancellationReason.isVisible = true
                        if (orderDetailData?.exchangeReason!=null){
                            tvCancellationReason.text = orderDetailData?.exchangeReason
                        }
                        ivEdit.isVisible = false
                        clDriver.isVisible = true
                        llComplete.isVisible = false
                        ivDriver.load(orderDetailData?.driverDetails?.profilePic){
                            crossfade(true)
                            placeholder(R.drawable.ic_placeholder)
                            error(R.drawable.ic_placeholder)
                        }
                        tvDriverName.text = if (Constants.language=="ar") orderDetailData?.driverDetails?.fullNameArabic else orderDetailData?.driverDetails?.fullNameEnglish
                        ivPicked.setImageResource(R.drawable.ic_order_picked_selected)
                        ivOnTheWay.setImageResource(R.drawable.ic_order_on_way)
                        ivDelivered.setImageResource(R.drawable.ic_delivered_tab)
                        tvPicked.text = getString(R.string.picked)
                        tvDeliveredText.text = getString(R.string.exchange)
                    }
                }
                3->{
                    tvStatus.text = getString(R.string.delivered)
                    btnAssignDriver.isVisible = false
                    tvCancelReason.isVisible = false
                    tvCancellationReason.isVisible = false
                    clDriver.isVisible = true

                    btnAssignDriver.isVisible = false
                    ivDriver.load(orderDetailData?.driverDetails?.profilePic){
                        crossfade(true)
                        placeholder(R.drawable.ic_placeholder)
                        error(R.drawable.ic_placeholder)
                    }
                    ivEdit.isVisible = false
                    tvDriverName.text = if (Constants.language=="ar") orderDetailData?.driverDetails?.fullNameArabic else orderDetailData?.driverDetails?.fullNameEnglish
                    ivPicked.setImageResource(R.drawable.ic_order_picked_selected)
                    ivOnTheWay.setImageResource(R.drawable.ic_order_on_way_selected)
                    ivDelivered.setImageResource(R.drawable.ic_delivered_tab_selected)

                    if (orderDetailData?.userRatingReviewProvide!=null){
                        llComplete.isVisible = true
                        rbProductRating.rating == orderDetailData?.userRatingReviewProvide?.productRating?:0f

                        if (orderDetailData?.userRatingReviewProvide?.storeReview!=null&&orderDetailData?.userRatingReviewProvide?.storeReview!=""&&orderDetailData?.userRatingReviewProvide?.storeReview!="null"){
                            tvSellerReview.text = orderDetailData?.userRatingReviewProvide?.storeReview
                        }
                        else{
                            tvStoreReview.isVisible = false
                            tvSellerReview.isVisible = false
                        }
                        if (orderDetailData?.userRatingReviewProvide?.productReview!=null&&orderDetailData?.userRatingReviewProvide?.productReview!=""&&orderDetailData?.userRatingReviewProvide?.productReview!="null"){
                            tvProductReview.text = orderDetailData?.userRatingReviewProvide?.productReview
                        }
                        else{
                            tvProductReview.isVisible = false
                            tvReview.isVisible = false
                        }

                        rbStoreRating.rating == orderDetailData?.userRatingReviewProvide?.storeRating?:0f
                    }
                    else{
                        llComplete.isVisible = false
                    }
                }
                4->{
                    tvStatus.text =getString(R.string.cancelled)
                    tvStatus.setTextColor(Color.parseColor("#EB2525"))
                    tvCancelReason.isVisible = true
                    tvCancellationReason.isVisible = true
                    llComplete.isVisible = false
                    btnInvoice.isVisible = false
                    btnAssignDriver.isVisible = false
                    ivOnTheWay.isVisible = false
                    tvOnTheWay.isVisible = false
                    clDriver.isVisible = false
                    clOrderStatus.isVisible = false
                    clBilling.isVisible = false
                    ivPicked.setImageResource(R.drawable.ic_order_picked_selected)
                    ivDelivered.setImageResource(R.drawable.ic_cancelled)
                    tvDeliveredText.text = getString(R.string.cancelled)
                    tvDeliveredText.setTextColor(Color.parseColor("#D72E04"))
                    ivDriver.load(orderDetailData?.driverDetails?.profilePic){
                        crossfade(true)
                        placeholder(R.drawable.ic_placeholder)
                        error(R.drawable.ic_placeholder)
                    }

                    tvDriverName.text = if (Constants.language=="ar") orderDetailData?.driverDetails?.fullNameArabic else orderDetailData?.driverDetails?.fullNameEnglish
                    if (orderDetailData?.cancelReason!=null){
                        tvCancellationReason.text = orderDetailData?.cancelReason
                        return
                    }
                    else if (orderDetailData?.notDeliveredReason!=null){
                        tvCancellationReason.text = orderDetailData?.notDeliveredReason
                        return
                    }
                    else if (orderDetailData?.returnReason!=null){
                        tvCancellationReason.text = orderDetailData?.returnReason
                        return
                    }
                    else if (orderDetailData?.exchangeReason!=null){
                        tvCancellationReason.text = orderDetailData?.exchangeReason
                        return
                    }

                }
                else ->{
                    when(orderDetailData?.deliveryStatus){
                        "1"->{
                            tvStatus.text =getString(R.string.active)
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
                                tvDriverName.text = if (Constants.language=="ar") orderDetailData?.driverDetails?.fullNameArabic else orderDetailData?.driverDetails?.fullNameEnglish
                                tvDeliveredBy.isVisible = true
                                tvDelivered.isVisible = true

                            }
                            else{
                                clDriver.isVisible = false
                                btnAssignDriver.isVisible = false
                            }
                        }

                        "2"->{
                            tvStatus.text =getString(R.string.active)
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
                                tvDriverName.text = if (Constants.language=="ar") orderDetailData?.driverDetails?.fullNameArabic else orderDetailData?.driverDetails?.fullNameEnglish

                            }
                            else{
                                btnAssignDriver.isVisible = true
                            }

                        }

                        "2.1"->{
                            tvStatus.text =getString(R.string.active)
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
                                tvDriverName.text = if (Constants.language=="ar") orderDetailData?.driverDetails?.fullNameArabic else orderDetailData?.driverDetails?.fullNameEnglish

                            }
                            else{
                                btnAssignDriver.isVisible = true
                            }

                        }

                        "6"->{
                            tvStatus.text = getString(R.string.active)
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
                            tvDriverName.text = if (Constants.language=="ar") orderDetailData?.driverDetails?.fullNameArabic else orderDetailData?.driverDetails?.fullNameEnglish
                        }

                        "4"->{
                            tvStatus.text = getString(R.string.cancelled)
                            tvStatus.setTextColor(Color.parseColor("#EB2525"))
                            tvCancelReason.isVisible = true
                            tvCancellationReason.isVisible = true
                            llComplete.isVisible = false
                            btnInvoice.isVisible = false
                            btnAssignDriver.isVisible = false
                            ivOnTheWay.isVisible = false
                            tvOnTheWay.isVisible = false
                            clDriver.isVisible = false
                            clOrderStatus.isVisible = false
                            clBilling.isVisible = false
                            ivDelivered.setImageResource(R.drawable.ic_cancelled)
                            tvDeliveredText.text = getString(R.string.cancelled)
                            tvDeliveredText.setTextColor(Color.parseColor("#D72E04"))
                            ivDriver.load(orderDetailData?.driverDetails?.profilePic){
                                crossfade(true)
                                placeholder(R.drawable.ic_placeholder)
                                error(R.drawable.ic_placeholder)
                            }
                            tvDriverName.text = if (Constants.language=="ar") orderDetailData?.driverDetails?.fullNameArabic else orderDetailData?.driverDetails?.fullNameEnglish
                            if (orderDetailData?.cancelReason!=null){
                                tvCancellationReason.text = orderDetailData?.cancelReason
                                return
                            }
                            else if (orderDetailData?.notDeliveredReason!=null){
                                tvCancellationReason.text = orderDetailData?.notDeliveredReason
                                return
                            }
                            else if (orderDetailData?.returnReason!=null){
                                tvCancellationReason.text = orderDetailData?.returnReason
                                return
                            }
                            else if (orderDetailData?.exchangeReason!=null){
                                tvCancellationReason.text = orderDetailData?.exchangeReason
                                return
                            }
                        }
                        "8"->{
                            tvStatus.text = getString(R.string.not_delivered)
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
                            tvDriverName.text = if (Constants.language=="ar") orderDetailData?.driverDetails?.fullNameArabic else orderDetailData?.driverDetails?.fullNameEnglish
                        }
                        "3"->{

                            if (orderDetailData?.rEApprove=="1"){
                                tvStatus.text =getString(R.string.delivered)
                                btnAssignDriver.isVisible = true
                            }
                            else if(orderDetailData?.returnType=="1"){
                                tvStatus.text = getString(R.string.return_req)
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
                                tvDriverName.text = if (Constants.language=="ar") orderDetailData?.driverDetails?.fullNameArabic else orderDetailData?.driverDetails?.fullNameEnglish
                                ivPicked.setImageResource(R.drawable.ic_order_picked_selected)
                                ivOnTheWay.setImageResource(R.drawable.ic_order_on_way)
                                ivDelivered.setImageResource(R.drawable.ic_delivered_tab)
                            }

                            else if(orderDetailData?.returnType=="2"){
                                tvStatus.text =getString(R.string.exchange)
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
                                tvDriverName.text = if (Constants.language=="ar") orderDetailData?.driverDetails?.fullNameArabic else orderDetailData?.driverDetails?.fullNameEnglish
                                ivPicked.setImageResource(R.drawable.ic_order_picked_selected)
                                ivOnTheWay.setImageResource(R.drawable.ic_order_on_way)
                                ivDelivered.setImageResource(R.drawable.ic_delivered_tab)
                                tvPicked.text = getString(R.string.picked)
                                tvDeliveredText.text = getString(R.string.exchange)
                            }
                            tvStatus.text =getString(R.string.delivered)
                            btnAssignDriver.isVisible = false
                            tvCancelReason.isVisible = false
                            tvCancellationReason.isVisible = false
                            clDriver.isVisible = true
                            btnAssignDriver.isVisible = false
                            ivDriver.load(orderDetailData?.driverDetails?.profilePic){
                                crossfade(true)
                                placeholder(R.drawable.ic_placeholder)
                                error(R.drawable.ic_placeholder)
                            }
                            tvDriverName.text = if (Constants.language=="ar") orderDetailData?.driverDetails?.fullNameArabic else orderDetailData?.driverDetails?.fullNameEnglish
                            ivPicked.setImageResource(R.drawable.ic_order_picked_selected)
                            ivOnTheWay.setImageResource(R.drawable.ic_order_on_way_selected)
                            ivDelivered.setImageResource(R.drawable.ic_delivered_tab_selected)
                            if (orderDetailData?.userRatingReviewProvide!=null){
                                llComplete.isVisible = true
                                rbProductRating.rating == orderDetailData?.userRatingReviewProvide?.productRating?:0f

                                if (orderDetailData?.userRatingReviewProvide?.storeReview!=null&&orderDetailData?.userRatingReviewProvide?.storeReview!=""&&orderDetailData?.userRatingReviewProvide?.storeReview!="null"){
                                    tvSellerReview.text = orderDetailData?.userRatingReviewProvide?.storeReview
                                }
                                else{
                                    tvStoreReview.isVisible = false
                                    tvSellerReview.isVisible = false
                                }
                                if (orderDetailData?.userRatingReviewProvide?.productReview!=null&&orderDetailData?.userRatingReviewProvide?.productReview!=""&&orderDetailData?.userRatingReviewProvide?.productReview!="null"){
                                    tvProductReview.text = orderDetailData?.userRatingReviewProvide?.productReview
                                }
                                else{
                                    tvProductReview.isVisible = false
                                    tvReview.isVisible = false
                                }

                                rbStoreRating.rating == orderDetailData?.userRatingReviewProvide?.storeRating?:0f
                            }
                            else{
                                llComplete.isVisible = false
                            }
                        }
                        "7"->{
                            tvStatus.text =getString(R.string.on_the_way)
                            btnAssignDriver.isVisible = false
                            clDriver.isVisible = true
                            ivDriver.load(orderDetailData?.driverDetails?.profilePic){
                                crossfade(true)
                                placeholder(R.drawable.ic_placeholder)
                                error(R.drawable.ic_placeholder)
                            }
                            ivEdit.isVisible = false
                            tvDriverName.text = if (Constants.language=="ar") orderDetailData?.driverDetails?.fullNameArabic else orderDetailData?.driverDetails?.fullNameEnglish
                            ivPicked.setImageResource(R.drawable.ic_order_picked_selected)
                            ivOnTheWay.setImageResource(R.drawable.ic_order_on_way_selected)
                            ivDelivered.setImageResource(R.drawable.ic_delivered_tab)

                            tvPicked.text = getString(R.string.picked)
                        }

                        "9"->{
                            tvStatus.text =getString(R.string.exchange)
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
                            tvDriverName.text = if (Constants.language=="ar") orderDetailData?.driverDetails?.fullNameArabic else orderDetailData?.driverDetails?.fullNameEnglish
                            ivPicked.setImageResource(R.drawable.ic_order_picked_selected)
                            ivOnTheWay.setImageResource(R.drawable.ic_order_on_way)
                            ivDelivered.setImageResource(R.drawable.ic_delivered_tab)
                            tvPicked.text = getString(R.string.picked)
                            tvDeliveredText.text = getString(R.string.exchange)
                        }

                        "5"->{
                            tvStatus.text =getString(R.string.return_text)
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
                            tvDriverName.text = if (Constants.language=="ar") orderDetailData?.driverDetails?.fullNameArabic else orderDetailData?.driverDetails?.fullNameEnglish
                            ivPicked.setImageResource(R.drawable.ic_order_picked_selected)
                            ivOnTheWay.setImageResource(R.drawable.ic_order_on_way)
                            ivDelivered.setImageResource(R.drawable.ic_delivered_tab)

                        }
                    }

                }
            }

            tvOrderIdValue.text = "#${orderDetailData?.id}"
            tvDateTime.text = CommonMethod.customizeChangeDateFormat(orderDetailData?.createdAt.toString(), outputFormat = "dd/MM/yy '${getString(R.string.at)}' hh:mm a")
            tvOrderTypeValue.text =  if (orderDetailData?.order?.orderType=="1") getString(R.string.normal_orders) else getString(R.string.custom_order)
            tvCustomerName.text = orderDetailData?.order?.deliveryDetails?.name
            tvSellerName.text = orderDetailData?.productDetails?.sellerDetails?.firstName+" "+orderDetailData?.productDetails?.sellerDetails?.lastName
            tvCountryValue.text = orderDetailData?.order?.deliveryDetails?.countryName
            tvShippingLocationValue.text = orderDetailData?.order?.deliveryDetails?.address

            tvTotalAmount.text = "${CommonMethod.formatPrice(orderDetailData?.amount?:0.0)} KD"
            CommissionEarned.text = "${CommonMethod.formatPrice(orderDetailData?.adminCommission?:0.0)} KD"
            if (orderDetailData?.driverDetails!=null){
                tvZoneValue.isVisible = true
                tvZone.isVisible = true
                tvDeliveredBy.text = if (Constants.language=="ar") orderDetailData?.driverDetails?.fullNameArabic else orderDetailData?.driverDetails?.fullNameEnglish
                tvZoneValue.text = orderDetailData?.driverDetails?.zoneInfo?.nameEnglish
            }
            else{
                tvDeliveredBy.text = "NA"
                tvZoneValue.isVisible = false
                tvZone.isVisible = false
            }
            when(orderDetailData?.order?.paymentMethod){
                "1"->{
                    tvPaymentMethod.isVisible = false
                    tvPaymentMethodValue.isVisible = false
                }
                "2"->{
                    tvPaymentMethod.isVisible = true
                    tvPaymentMethodValue.isVisible = true
                    tvPaymentMethodValue.text = getString(R.string.wallet)
                }
                "3"->{
                    tvPaymentMethod.isVisible = true
                    tvPaymentMethodValue.isVisible = true
                    tvPaymentMethodValue.text = getString(R.string.cod)
                }
            }
        }

    }

    override fun initControl() {
        binding?.apply {

            ivBack.setOnClickListener {
                findNavController().popBackStack()
            }

            btnInvoice.setOnClickListener {
                CommonMethod.openPdfInBrowser(requireActivity(),"${Constants.BASE_URL}/Invoice/${orderDetailData?.order?.orderId.toString()}")
               // getInvoice()
              //  CommonMethod.downloadPdf(requireActivity(),orderDetailData?.adminCommission)
            }

            btnAssignDriver.setOnClickListener {
                assignDriverDialog()
            }

            ivEdit.setOnClickListener {
                assignDriverDialog()
            }

            }
        }

    private fun getInvoice() {
        viewModel.getInvoice(orderDetailData?.order?.orderId.toString())
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
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

        viewModel.invoiceResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()

                isObserver = false
            }
        }

        viewModel.assignDriverResult.observe(viewLifecycleOwner) { response ->
            if (isAssignObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                dialogDriver?.dismiss()
                CommonMethod.showSuccessDialog(requireActivity(),getString(R.string.driver_assigned_successfully)){
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