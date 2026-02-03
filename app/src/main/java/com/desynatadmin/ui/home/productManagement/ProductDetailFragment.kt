package com.desynatadmin.ui.home.productManagement

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.desynatadmin.adapter.ProductImageAdapter
import com.desynatadmin.adapter.ProductRectangleColorAdapter
import com.desynatadmin.adapter.SizeAdapter
import com.desynatadmin.adapter.SpecificationAdapter
import com.desynatadmin.base.BaseFragment
import com.desynatadmin.data.view_model.HomeViewModel
import com.desynatadmin.databinding.FragmentProductDetailBinding
import com.desynatadmin.utils.CommonMethod
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class ProductDetailFragment : BaseFragment() {
    private var binding: FragmentProductDetailBinding?=null
    private val viewModel: HomeViewModel by viewModels()
    private var isObserver = true

    private var colorList = ArrayList<String>()
    private var sizeList = ArrayList<String>()
    private var specificationList = ArrayList<String>()

    private var productImageList = ArrayList<String>()
    private var colorAdapter: ProductRectangleColorAdapter?=null
    private var sizeAdapter: SizeAdapter?=null
    private var specificationAdapter: SpecificationAdapter?=null
    private var productImageAdapter: ProductImageAdapter?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProductDetailBinding.inflate(layoutInflater,container,false)
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
            toolbar.tvToolbar.text = "Product Details"

            productImageAdapter = ProductImageAdapter(requireActivity(),productImageList,object :
                ProductImageAdapter.OnClick{
                override fun onClick(position: Int) {
                    CommonMethod.imagePreviewListDialog(requireActivity(),productImageList)
                }

            })
            viewPager.adapter = productImageAdapter
            TabLayoutMediator(dotsIndicator, viewPager) { _, _ -> }.attach()

            sizeAdapter = SizeAdapter(requireActivity(),sizeList,object : SizeAdapter.OnClick{
                override fun onClickItem(position: Int) {

                }

            })
            rvSize.adapter = sizeAdapter

            colorAdapter = ProductRectangleColorAdapter(requireActivity(),colorList,object :
                ProductRectangleColorAdapter.OnClick{
                override fun onClickItem(position: Int) {

                }

            })
            rvColor.adapter = colorAdapter

            specificationAdapter = SpecificationAdapter(requireActivity(),specificationList)
            rvSpecification.adapter = specificationAdapter

        }

    }

    override fun initControl() {
        binding?.apply {
            toolbar.ivBack.setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }

    override fun observe() {
    }

}