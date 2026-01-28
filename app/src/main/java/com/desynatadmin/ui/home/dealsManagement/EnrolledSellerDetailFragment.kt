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
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.desynatadmin.R
import com.desynatadmin.adapter.EnrolledSellerProductAdapter
import com.desynatadmin.base.BaseFragment
import com.desynatadmin.data.view_model.HomeViewModel
import com.desynatadmin.databinding.DealProductFilterDialogBinding
import com.desynatadmin.databinding.FragmentEnrolledSellerDetailBinding
import com.desynatadmin.ui.home.dealsManagement.response.GetDealDetailResponse
import com.desynatadmin.ui.home.dealsManagement.response.GetDealProductResponse
import com.desynatadmin.utils.CommonMethod
import com.desynatadmin.utils.ProgressDialogUtils
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
    private var sellerDealRecord: GetDealDetailResponse.Data.SellerDealrecord?=null
    private var enrolledProductList = ArrayList<GetDealProductResponse.Data.SellerDealrecord.Product>()
    private var enrolledSellerProductAdapter: EnrolledSellerProductAdapter?=null

    private var search: Job? = null
    private var lastQuery: String? = null
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

        if (!itemJson.isNullOrEmpty()) {
            sellerDealRecord = Gson().fromJson(itemJson, GetDealDetailResponse.Data.SellerDealrecord::class.java)
            setData()
        }
        binding?.apply {
            toolbar.tvToolbar.text = "Details"

            enrolledSellerProductAdapter = EnrolledSellerProductAdapter(requireActivity(),enrolledProductList,object :
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
            tvCountry.text =  sellerDealRecord?.sellerDetails?.countryDetails?.name
            tvTotalProductAdded.text = "${sellerDealRecord?.productCount} Products"
        }
    }

    private fun getDealProduct(search:String?=null){
        val map = HashMap<String,Any>()
        if (search!=null){
            map["search"]=search.toString()
        }
        map["deal_id"]=sellerDealRecord?.dealId.toString()
        map["seller_id"]=sellerDealRecord?.sellerId.toString()
        viewModel.getDealProduct(map)
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

    private fun checkData(){
        binding?.tvNotData?.isVisible = enrolledProductList.isEmpty()
    }

    private fun productFilterDialog() {
        val dialog = Dialog(requireActivity(), R.style.CustomDialog)
        val dialogBinding = DealProductFilterDialogBinding.inflate(LayoutInflater.from(context))
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

            tvToolbar.setOnClickListener {
                dialog.dismiss()
            }

            etBrand.setOnClickListener {

            }

            tvClear.setOnClickListener {

                dialog.dismiss()
            }

            tvApply.setOnClickListener{
                dialog.dismiss()
            }

        }

        dialog.show()
    }


}