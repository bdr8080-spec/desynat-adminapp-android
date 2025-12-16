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
import com.desynatadmin.utils.ProgressDialogUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OrderDetailFragment : BaseFragment() {
    private var binding: FragmentOrderDetailBinding?=null
    private val viewModel: HomeViewModel by viewModels()
    private var isObserver = true

    private var productList = ArrayList<String>()
    private var productAdapter : ProductAdapter?=null

    private var type = 0


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