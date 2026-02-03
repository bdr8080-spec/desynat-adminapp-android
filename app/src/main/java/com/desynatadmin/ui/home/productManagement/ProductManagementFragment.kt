package com.desynatadmin.ui.home.productManagement

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.desynatadmin.adapter.ProductAdapter
import com.desynatadmin.base.BaseFragment
import com.desynatadmin.data.view_model.HomeViewModel
import com.desynatadmin.databinding.FragmentProductManagementBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class ProductManagementFragment : BaseFragment() {
    private var binding: FragmentProductManagementBinding?=null
    private val viewModel: HomeViewModel by viewModels()
    private var isObserver = true
    private var productList = ArrayList<String>()
    private var productAdapter: ProductAdapter?=null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProductManagementBinding.inflate(layoutInflater,container,false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initControl()
        observe()
    }

    override fun initView() {
        binding?.apply {

            productAdapter = ProductAdapter(requireActivity(),productList,object : ProductAdapter.ProductClick{
                override fun onProductClick(position: Int, type: Int) {

                }

            })
            rvProduct.adapter = productAdapter
        }

    }

    override fun initControl() {
    }

    override fun observe() {
    }

}