package com.desynatadmin.ui.home.dealsManagement

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
import android.widget.ArrayAdapter
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.desynatadmin.R
import com.desynatadmin.adapter.DealBrandFilterAdapter
import com.desynatadmin.adapter.EnrolledSellerProductAdapter
import com.desynatadmin.base.BaseFragment
import com.desynatadmin.data.view_model.HomeViewModel
import com.desynatadmin.databinding.DealProductFilterDialogBinding
import com.desynatadmin.databinding.FragmentEnrolledSellerDetailBinding
import com.desynatadmin.ui.home.brandManagement.response.GetBrandResponse
import com.desynatadmin.ui.home.dealsManagement.response.GetDealDetailResponse
import com.desynatadmin.ui.home.dealsManagement.response.GetDealProductResponse
import com.desynatadmin.utils.CommonMethod
import com.desynatadmin.utils.Constants
import com.desynatadmin.utils.ProgressDialogUtils
import com.desynatadmin.utils.showToast
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EnrolledSellerDetailFragment : BaseFragment() {
    private var binding: FragmentEnrolledSellerDetailBinding? = null
    private val viewModel: HomeViewModel by viewModels()
    private var isObserver = true
    private var isBrandObserver = true
    private var brandList = ArrayList<GetBrandResponse.Data.Data>()
    private var dealBrandFilterAdapter: DealBrandFilterAdapter?=null
    private var selectedBrand: GetBrandResponse.Data.Data?=null
    private var filterBrand: GetBrandResponse.Data.Data?=null

    private var sellerDealRecord: GetDealDetailResponse.Data.SellerDealrecord?=null
    private var enrolledProductList = ArrayList<GetDealProductResponse.Data.SellerDealrecord.Product>()
    private var enrolledSellerProductAdapter: EnrolledSellerProductAdapter?=null
    private var dealType = 1
    private var search: Job? = null
    private var lastQuery: String? = null

    private var dialogBinding: DealProductFilterDialogBinding?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEnrolledSellerDetailBinding.inflate(layoutInflater, container, false)
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

        val itemJson = arguments?.getString("dealsData")
        dealType = arguments?.getInt("dealType")?:1
        if (!itemJson.isNullOrEmpty()) {
            sellerDealRecord = Gson().fromJson(itemJson, GetDealDetailResponse.Data.SellerDealrecord::class.java)
            setData()
        }
        binding?.apply {
            toolbar.tvToolbar.text = getString(R.string.details)

            if (dealType==2){
                llRevenue.isVisible = true
            }
            else{
                llRevenue.isVisible = false
            }

            enrolledSellerProductAdapter = EnrolledSellerProductAdapter(requireActivity(),dealType,enrolledProductList,object :
                EnrolledSellerProductAdapter.ClickOnItem{
                override fun onClickItem(position: Int) {
                    enrolledProductList[position].productDetails?.productVariants?.get(0)?.variantImage?.let {
                        CommonMethod.imagePreviewListDialog(requireActivity(),
                            it
                        )
                    }
                }
            })
            rvEnrolledProduct.adapter = enrolledSellerProductAdapter

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
                        if (!isObserver)
                            getDealProduct(search =  query.ifEmpty { null })
                    }

                }
            })

            getDealProduct()

        }

    }

    override fun initControl() {
        binding?.apply {
            toolbar.ivBack.setOnClickListener {
                findNavController().popBackStack()
            }

            ivFilter.setOnClickListener {
                productFilterDialog()
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

        viewModel.getDealProductResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                setProductData(response.data)
                isObserver = false
            }
        }

        viewModel.getAllBrandResult.observe(viewLifecycleOwner) { response ->
            if (isBrandObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                brandList.clear()
                response.data.data?.let { brandList.addAll(it) }
                dealBrandFilterAdapter?.notifyDataSetChanged()
                isBrandObserver = false
            }
        }
    }

    private fun setProductData(data: GetDealProductResponse.Data) {
        enrolledProductList.clear()
        data?.sellerDealrecords?.get(0)?.products?.let { enrolledProductList.addAll(it) }
        enrolledSellerProductAdapter?.updateCategorySubCategory(data?.categoryDetails,data?.subCategoryDetails)
        checkData()
    }

    private fun setData(){
        binding?.apply {
            tvSellerName.text = sellerDealRecord?.sellerDetails?.firstName+" "+sellerDealRecord?.sellerDetails?.lastName
            tvCountry.text =   if (Constants.language=="ar") sellerDealRecord?.sellerDetails?.countryDetails?.nameAr else sellerDealRecord?.sellerDetails?.countryDetails?.name
            val count = sellerDealRecord?.productCount ?: 0
            tvTotalProductAdded.text = if (count == 1) {
                "$count ${getString(R.string.product)}"
            } else {
                "$count ${getString(R.string.products)}"
            }
            tvTotalSoldAdded.text = "$count ${getString(R.string.products)}"
        }
    }

    private fun getDealProduct(search:String?=null){
        val map = HashMap<String,Any>()
        if (search!=null){
            map["search"]=search.toString()
        }
        map["deal_id"]=sellerDealRecord?.dealId.toString()
        map["seller_id"]=sellerDealRecord?.sellerId.toString()
        if (filterBrand!=null){
            map["filter"]= filterBrand?.nameEnglish.toString()
        }
        viewModel.getDealProduct(map)
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

    private fun checkData(){
        binding?.tvNotData?.isVisible = enrolledProductList.isEmpty()
    }

    private fun productFilterDialog() {
        val dialog = Dialog(requireActivity(), R.style.CustomDialog)
        dialogBinding = DealProductFilterDialogBinding.inflate(LayoutInflater.from(context))
        dialogBinding?.root.let { it?.let { it1 -> dialog.setContentView(it1) } }

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

        dialogBinding?.apply {

            if (filterBrand!=null){
                etBrand.setText(if (Constants.language=="ar") filterBrand?.nameArabic else filterBrand?.nameEnglish)
            }

            dealBrandFilterAdapter=DealBrandFilterAdapter(requireActivity(),brandList,object :
                DealBrandFilterAdapter.OnClick{
                override fun onClick(position: Int) {
                    etBrand.setText(if (Constants.language=="ar") brandList[position].nameArabic else brandList[position].nameEnglish)
                    selectedBrand = brandList[position]
                    rvBrand.isVisible = false
                }

            })
            rvBrand.adapter = dealBrandFilterAdapter

            tvToolbar.setOnClickListener {
                dialog.dismiss()
            }

            etBrand.setOnClickListener {
                if (brandList.size==0){
                    rvBrand.isVisible = true
                    getAllBrand()
                }
                else{
                    rvBrand.isVisible = true
                }
            }

            tvClear.setOnClickListener {
                filterBrand = null
                getDealProduct()
                dialog.dismiss()
            }

            tvApply.setOnClickListener{
                if (selectedBrand!=null){
                    filterBrand = selectedBrand
                    getDealProduct()
                    dialog.dismiss()
                }
                else{
                    showToast(getString(R.string.please_select_brand))
                }

            }
        }

        dialog.show()
    }

    private fun getAllBrand(){
        val map = HashMap<String,Any>()
        viewModel.getAllBrand(map)
            ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isBrandObserver = true
    }

}