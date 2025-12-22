package com.desynatadmin.ui.home.orderManagement

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.desynatadmin.R
import com.desynatadmin.adapter.ProductAdapter
import com.desynatadmin.base.BaseFragment
import com.desynatadmin.data.view_model.HomeViewModel
import com.desynatadmin.databinding.FragmentOrderDetailBinding
import com.desynatadmin.ui.home.orderManagement.response.GetActiveOrderResponse
import com.desynatadmin.utils.CommonMethod
import com.desynatadmin.utils.ProgressDialogUtils
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OrderDetailFragment : BaseFragment() {
    private var binding: FragmentOrderDetailBinding?=null
    private val viewModel: HomeViewModel by viewModels()
    private var isObserver = true

    private var productList = ArrayList<GetActiveOrderResponse.Data.Data.OrderDetail>()
    private var productAdapter : ProductAdapter?=null

    private var type = 0
    var orderDetailData: GetActiveOrderResponse.Data.Data?=null


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

            when(type){
                1->{
                    tvStatus.text ="New"
                    tvCancelReason.isVisible = false
                    tvCancellationReason.isVisible = false
                    llComplete.isVisible = false
                    clDriver.isVisible = false
                    btnAssignDriver.isVisible = true
                    btnInvoice.isVisible = true
                }

                2->{
                    tvStatus.text ="Confirmed"
                    tvCancelReason.isVisible = false
                    tvCancellationReason.isVisible = false
                    llComplete.isVisible = false
                }
                3->{
                    tvStatus.text ="Confirmed"
                    tvCancelReason.isVisible = false
                    tvCancellationReason.isVisible = false
                    llComplete.isVisible = true
                }

                4->{
                    tvStatus.text ="Cancelled"
                    tvCancelReason.isVisible = true
                    tvCancellationReason.isVisible = true
                    llComplete.isVisible = false
                    clDriver.isVisible = false
                    btnAssignDriver.isVisible = false
                    btnInvoice.isVisible = false
                }
            }

            if (!itemJson.isNullOrEmpty()) {
                orderDetailData = Gson().fromJson(itemJson, GetActiveOrderResponse.Data.Data::class.java)
                setData()
            }

        }
    }

    private fun setData() {
        productList.clear()
        orderDetailData?.orderDetails?.let { productList.addAll(it) }
        productAdapter?.notifyDataSetChanged()

        binding?.apply {
            tvOrderIdValue.text = "#${orderDetailData?.orderId}"
            tvDateTime.text = CommonMethod.customizeChangeDateFormat(orderDetailData?.createdAt.toString(), outputFormat = "dd/MM/yy 'at' hh:mm a")
            tvOrderTypeValue.text = "Normal Orders"
            tvCustomerName.text = orderDetailData?.deliveryDetails?.name
            tvSellerName.text = orderDetailData?.orderDetails?.get(0)?.productDetails?.sellerDetails?.firstName+" "+orderDetailData?.orderDetails?.get(0)?.productDetails?.sellerDetails?.lastName
            tvDeliveredBy.text = orderDetailData?.deliveryDetails?.name
            tvCountryValue.text = orderDetailData?.deliveryDetails?.countryName
            tvShippingLocationValue.text = orderDetailData?.deliveryDetails?.address

            tvTotalAmount.text = "${CommonMethod.formatPrice(orderDetailData?.totalAmount?:0.0)} KD"
            CommissionEarned.text = "${CommonMethod.formatPrice(orderDetailData?.totalAmount?:0.0)} KD"
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

            }

            }
        }

    override fun observe() {
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                isObserver = false
            }
        }
    }

}