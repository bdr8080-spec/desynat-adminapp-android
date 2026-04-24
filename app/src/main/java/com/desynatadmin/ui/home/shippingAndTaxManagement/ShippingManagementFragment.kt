package com.desynatadmin.ui.home.shippingAndTaxManagement

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.desynatadmin.R
import com.desynatadmin.adapter.ShippingCountryTaxAdapter
import com.desynatadmin.base.BaseFragment
import com.desynatadmin.data.view_model.HomeViewModel
import com.desynatadmin.databinding.FragmentShippingManagementBinding
import com.desynatadmin.ui.home.HomeActivity
import com.desynatadmin.ui.home.SubAdminHomeActivity
import com.desynatadmin.ui.home.shippingAndTaxManagement.response.GetShippingTaxResponse
import com.desynatadmin.utils.ProgressDialogUtils
import com.desynatadmin.utils.showToast
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ShippingManagementFragment : BaseFragment() {
    private var binding: FragmentShippingManagementBinding?=null
    private val viewModel: HomeViewModel by viewModels()
    private var isObserver = true
    private var shippingList = ArrayList<GetShippingTaxResponse.Data>()
    private var shippingCountryTaxAdapter : ShippingCountryTaxAdapter?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentShippingManagementBinding.inflate(layoutInflater,container,false)
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
            toolbar.tvToolbar.text = "Delivery Charges & Other Taxs"

            shippingList.clear()
            shippingCountryTaxAdapter = ShippingCountryTaxAdapter(requireActivity(),shippingList,object : ShippingCountryTaxAdapter.ClickOnItem{
                override fun onClickItem(position: Int) {
                    val bundle = Bundle().apply {
                        putString("shippingData", Gson().toJson(shippingList[position]))
                    }
                    findNavController().navigate(R.id.action_shippingTax_to_shippingChargeFragment,bundle)
                }

            })

            rvShipping.adapter = shippingCountryTaxAdapter
        }

        getShippingTax()
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

        }
    }

    override fun observe() {
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()

                isObserver = false
            }
        }

        viewModel.getShippingTaxResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()

                shippingList.clear()
                 shippingList.addAll(response.data)
                checkData()
                    shippingCountryTaxAdapter?.notifyDataSetChanged()
                isObserver = false
            }
        }
    }


    private fun getShippingTax(){
        viewModel.getShippingTax()
            ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

    private fun checkData(){
        binding?.tvNoDataFound?.isVisible = shippingList.isEmpty()
    }

}