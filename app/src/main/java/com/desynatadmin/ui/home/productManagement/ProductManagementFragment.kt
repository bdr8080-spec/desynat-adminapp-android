package com.desynatadmin.ui.home.productManagement

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
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.desynatadmin.R
import com.desynatadmin.adapter.FilterBrandAdapter
import com.desynatadmin.adapter.FilterCategoryAdapter
import com.desynatadmin.adapter.FilterCountryAdapter
import com.desynatadmin.adapter.FilterSubCategoryAdapter
import com.desynatadmin.adapter.ProductAdapter
import com.desynatadmin.base.BaseFragment
import com.desynatadmin.data.view_model.HomeViewModel
import com.desynatadmin.databinding.FragmentProductManagementBinding
import com.desynatadmin.databinding.ProductFilterBinding
import com.desynatadmin.ui.home.brandManagement.response.GetBrandResponse
import com.desynatadmin.ui.home.categorySubCategoryManagement.response.GetCategoryResponse
import com.desynatadmin.ui.home.categorySubCategoryManagement.response.GetSubCategoryResponse
import com.desynatadmin.ui.home.productManagement.response.GetAllProductListResponse
import com.desynatadmin.ui.home.subscriptionPlan.response.GetCountryResponse
import com.desynatadmin.utils.ProgressDialogUtils
import com.desynatadmin.utils.SharedPreference
import com.desynatadmin.utils.showToast
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.getValue

@AndroidEntryPoint
class ProductManagementFragment : BaseFragment() {
    private var binding: FragmentProductManagementBinding?=null
    private val viewModel: HomeViewModel by viewModels()
    private var isObserver = true
    private var isCountryObserver = true
    private var isBrandObserver = true
    private var isCategoryObserver = true
    private var isSubCategoryObserver = true
    private var productList = ArrayList<GetAllProductListResponse.Data.Data>()
    private var productAdapter: ProductAdapter?=null
    private var search: Job? = null
    private var lastQuery: String? = null
    private var filterBrandAdapter: FilterBrandAdapter? = null
    private var filterCategoryAdapter: FilterCategoryAdapter? = null
    private var filterSubCategoryAdapter: FilterSubCategoryAdapter? = null
    private var filterCountryAdapter: FilterCountryAdapter? = null

    private var categoryList = ArrayList<GetCategoryResponse.Data>()
    private var subCategoryList = ArrayList<GetSubCategoryResponse.Data.Data>()
    private var countryList = ArrayList<GetCountryResponse.Data>()
    private var brandList = ArrayList<GetBrandResponse.Data.Data>()
    private var selectedBrandList = ArrayList<GetBrandResponse.Data.Data>()
    private var selectedSubCategory: GetSubCategoryResponse.Data.Data?=null
    private var selectedBrand: GetBrandResponse.Data.Data?=null
    private var selectedCountry: GetCountryResponse.Data?=null
    private var selectedCountryList = ArrayList<GetCountryResponse.Data>()
    private var selectedSubCategoryList = ArrayList<GetSubCategoryResponse.Data.Data>()
    private var selectedCategory: GetCategoryResponse.Data?=null

    private var currentPage = 1
    private var lastPage = 1
    private var totalPage = 0
    private var totalItem=0
    private var pageLimit=10
    private val itemsPerPage = 10
    private var isFromSwipe=false
    private var pagination=false
    private var selectedPos = -1

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
            ProgressDialogUtils.getInstance().hideProgress()
            if (SharedPreference.get(requireActivity()).getCountryList()!=null){
                countryList.clear()
                SharedPreference.get(requireActivity()).getCountryList()?.let { countryList.addAll(it) }
            }
            else{
                getCountryApi()
            }

            getAllBrand()
            getAllCategory()

            productAdapter = ProductAdapter(requireActivity(),productList,object : ProductAdapter.ProductClick{
                override fun onProductClick(position: Int, type: Int) {
                    when(type){
                        1->{
                            val bundle = Bundle().apply {
                                putString("productData", Gson().toJson(productList[position]))
                            }
                            findNavController().navigate(R.id.action_productManagementManagement_to_productDetailFragment,bundle)
                        }
                        2->{
                            selectedPos = position
                            updateProductStatus()
                        }
                    }
                }

            })
            rvProduct.adapter = productAdapter

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
                        currentPage = 1
                        pagination = false
                        isFromSwipe = false
                        if (!isObserver)
                            getProduct(isLoading = false, search =  query.ifEmpty { null })
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
                getProduct(isLoading = false)
            }
        }

        getProduct()

    }

    override fun initControl() {
        binding?.apply {
            ivFilter.setOnClickListener {
                productFilterDialog()
            }

            ivNextArrow.setOnClickListener {
                if (!isObserver && currentPage < lastPage&&totalItem!=0) {
                    currentPage += 1
                    getProduct(isLoading=  false)
                    binding?.progressBar?.isVisible = true
                }
            }

            ivBackArrow.setOnClickListener {
                if (currentPage > 1) {
                    currentPage--
                    getProduct(isLoading = false)
                    binding?.progressBar?.isVisible = true
                }
            }
        }
    }

    override fun observe() {

        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (isObserver||isCountryObserver||isBrandObserver||isSubCategoryObserver||isCategoryObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                binding?.progressBar?.isVisible = false
                if (isFromSwipe){
                    binding?.swipeRefreshLayout?.isRefreshing = false
                }

                pagination = false
                isFromSwipe = false
                isObserver = false
                isCountryObserver = false
                isSubCategoryObserver = false
                isCategoryObserver = false
                isBrandObserver = false
            }
        }

        viewModel.getAllProductResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                binding?.mainProgressBar?.isVisible = false
                ProgressDialogUtils.getInstance().hideProgress()
                binding?.progressBar?.isVisible = false
                if (isFromSwipe){
                    binding?.swipeRefreshLayout?.isRefreshing = false
                }

                currentPage = response.data.currentPage
                totalItem = response.data.total

                lastPage = response.data.lastPage

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


                val oldSize = productList.size
                if (!pagination){
                    productList.clear()
                }
                updatePage()
                val newItems = response.data.data ?: emptyList()

                productList.addAll(newItems)
                checkData()

                if (pagination){

                    response.data.data?.size?.let {
                        productAdapter?.notifyItemRangeInserted(oldSize,
                            it
                        )
                    }
                }
                else{
                    productAdapter?.notifyDataSetChanged()
                }

                pagination = false
                isFromSwipe = false
                isObserver = false
            }
        }

        viewModel.getAllBrandResult.observe(viewLifecycleOwner) { response ->
            if (isBrandObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                brandList.clear()
                response.data.data?.let { brandList.addAll(it) }
                filterBrandAdapter?.notifyDataSetChanged()
                isBrandObserver = false
            }
        }

        viewModel.getAllCategoryResult.observe(viewLifecycleOwner) { response ->
            if (isCategoryObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                categoryList.clear()
                categoryList.addAll(response.data)
                if (categoryList.size>0){
                    getAllSubCategory(categoryList[0].id.toString())
                }
                filterCategoryAdapter?.notifyDataSetChanged()
                isCategoryObserver = false
            }
        }

        viewModel.getAllSubCategoryResult.observe(viewLifecycleOwner) { response ->
            if (isSubCategoryObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                subCategoryList.clear()
                response.data.data?.let { subCategoryList.addAll(it) }
                filterSubCategoryAdapter?.notifyDataSetChanged()
                isSubCategoryObserver = false
            }
        }

        viewModel.getCountryResult.observe(viewLifecycleOwner) { response ->
            if (isCountryObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                countryList.clear()
                countryList.addAll(response.data)
                filterCountryAdapter?.notifyDataSetChanged()
                isCountryObserver = false
            }
        }

        viewModel.updateProductStatusResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                showToast(response.message)
                productList[selectedPos].productActiveStatus = response.data.productActiveStatus
                productAdapter?.notifyItemChanged(selectedPos)
                selectedPos = -1
                isObserver = false
            }
        }
    }
    private fun checkData(){
        binding?.tvNoDataFound?.isVisible = productList.isEmpty()
    }

    private fun updatePage() {
        val start = (currentPage - 1) * itemsPerPage
        val end = minOf(start + itemsPerPage, productList.size)
        if (start>=0&&end>=0) {
            if (start < end) {
                val pageData = productList.subList(start, end)
                productAdapter?.setData(ArrayList(pageData))
            }
        }
    }

    private fun getProduct(isLoading: Boolean=true,search:String?=lastQuery){
        val map = HashMap<String,Any>()

        if (search!=null){
            map["search"] = search
        }
        map["page"] = currentPage
        map["limit"] = pageLimit

//        if (selectedBrandList.isNotEmpty()) {
//            map["brand"] = selectedBrandList.map { it.id }
//                .joinToString(prefix = "[", postfix = "]", separator = ",")
//        }
//
//        if (selectedSubCategoryList.isNotEmpty()) {
//            map["subcategory"] = selectedSubCategoryList.map { it.id }
//                .joinToString(prefix = "[", postfix = "]", separator = ",")
//        }

        if (selectedBrand!=null) {
            map["brand"] = selectedBrand?.id.toString()
        }
        if (selectedSubCategory!=null) {
            map["subcategory"] = selectedSubCategory?.id.toString()
        }
        if (selectedCountry!=null) {
            map["country"] = selectedCountry?.id.toString()
        }

//        if (selectedCountryList.isNotEmpty()) {
//            map["country"] = selectedCountryList.map { it.id }
//                .joinToString(prefix = "[", postfix = "]", separator = ",")
//        }

//        if (selectedBrandList.isNotEmpty()) {
//            map["\"brand\""] = selectedBrandList.map { it.id }
//                .joinToString(prefix = "[", postfix = "]", separator = ",")
//        }
//
//        if (selectedSubCategoryList.isNotEmpty()) {
//            map["\"subcategory\""] = selectedSubCategoryList.map { it.id }
//                .joinToString(prefix = "[", postfix = "]", separator = ",")
//        }
//
//        if (selectedCountryList.isNotEmpty()) {
//            map["\"country\""] = selectedCountryList.map { it.id }
//                .joinToString(prefix = "[", postfix = "]", separator = ",")
//        }

        if (selectedCategory != null) {
            map["category"] = selectedCategory?.id.toString()
        }

        viewModel.getAllProduct(map)
        if (isLoading){
            binding?.mainProgressBar?.isVisible = true
        }
        isObserver = true
    }
    private fun getAllSubCategory(id: String){
        viewModel.getAllSUbCategory(categoryId = id,search=null, page = 1)
        isSubCategoryObserver = true
    }

    private fun getAllCategory(){
        viewModel.getAllCategory()
        isCategoryObserver = true
    }

    private fun getAllBrand(){
        val map = HashMap<String,Any>()
        viewModel.getAllBrand(map)
        isBrandObserver = true
    }

    private fun updateProductStatus(){
        val map = HashMap<String,Any>()
        map["id"] = productList[selectedPos].id
        viewModel.updateProductStatus(map)
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

    private fun getCountryApi(){
        viewModel.getCountry()
        isCountryObserver = true
    }

    private fun productFilterDialog() {
        var category : GetCategoryResponse.Data? = null

        val dialog = Dialog(requireActivity(), R.style.CustomDialog)
        val dialogBinding = ProductFilterBinding.inflate(LayoutInflater.from(context))
        dialogBinding.root.let { dialog.setContentView(it) }

        val window: Window = dialog.window!!
        window.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        window.setBackgroundDrawableResource(android.R.color.white)
        dialog.setCancelable(false)

        val params: WindowManager.LayoutParams = window.attributes
        params.gravity = Gravity.BOTTOM
        window.attributes = params

        dialogBinding.apply {

            brandList.forEach { it.isSelected = false }
            countryList.forEach { it.isSelected = false }
            categoryList.forEach { it.isSelected = false }
            subCategoryList.forEach { it.isSelected = false }

//            if (selectedBrandList.isNotEmpty()) {
//                brandList.forEach { brand ->
//                    brand.isSelected = selectedBrandList.any { it.id == brand.id }
//                }
//            }

            if (selectedBrand!=null){
                if (brandList.isNotEmpty()) {
                    brandList.forEach { brand ->
                        brand.isSelected = selectedBrand?.id.toString()==brand.id.toString()
                    }
                }
            }


//            if (selectedCountryList.isNotEmpty()) {
//                countryList.forEach { country ->
//                    country.isSelected = selectedCountryList.any { it.id == country.id }
//                }
//            }

            if (selectedCountry!=null){
                if (countryList.isNotEmpty()) {
                    countryList.forEach { country ->
                        country.isSelected = selectedCountry?.id.toString()==country.id.toString()
                    }
                }

            }

            if (selectedCategory!=null){
                if (selectedSubCategoryList.isNotEmpty()) {
                    subCategoryList.forEach { subCategory ->
                        subCategory.isSelected = selectedSubCategoryList.any { it.id == subCategory.id }
                    }
                }
                tvSubCategory.isVisible = true
                rvSubCategory.isVisible = true
            }
            else{
                tvSubCategory.isVisible = false
                rvSubCategory.isVisible = false
            }

            filterSubCategoryAdapter = FilterSubCategoryAdapter(requireActivity(),subCategoryList,object :FilterSubCategoryAdapter.OnClick{
                override fun onClickItem(position: Int) {
                }

            })

            rvSubCategory.adapter = filterSubCategoryAdapter

            filterBrandAdapter = FilterBrandAdapter(requireActivity(), brandList,object :FilterBrandAdapter.OnClick{
                override fun onClickItem(position: Int) {

                }

            })
            rvBrand.adapter = filterBrandAdapter

            filterCountryAdapter = FilterCountryAdapter(requireActivity(), countryList,object :FilterCountryAdapter.OnClick{
                override fun onClickItem(position: Int) {

                }

            })
            rvCountry.adapter = filterCountryAdapter

            filterCategoryAdapter = FilterCategoryAdapter(requireActivity(), categoryList,object :FilterCategoryAdapter.OnClick{
                override fun onClickItem(position: Int) {
                    if (position==-1){
                        category = null
                        subCategoryList.clear()
                        filterSubCategoryAdapter?.notifyDataSetChanged()
                        tvSubCategory.isVisible = false
                        rvSubCategory.isVisible = false
                    }
                    else{
                        category = categoryList[position]
                        subCategoryList.clear()
                        getAllSubCategory(category?.id.toString())
                        tvSubCategory.isVisible = true
                        rvSubCategory.isVisible = true
                    }

                }

            })
            filterCategoryAdapter?.setSelectedCategoryId(selectedCategory?.id)
            rvCategory.adapter = filterCategoryAdapter

            tvToolbar.setOnClickListener {
                dialog.dismiss()
            }

            tvClear.setOnClickListener {
                selectedCategory = null
                selectedSubCategory = null
                selectedBrand = null
                selectedCategory = null
                selectedCountry = null
                selectedBrandList.clear()
                selectedCountryList.clear()
                selectedSubCategoryList.clear()
                getProduct()
                dialog.dismiss()
            }

            tvApply.setOnClickListener{
                selectedCountry = null
                selectedCategory = category
                selectedBrandList.clear()
                selectedSubCategoryList.clear()
                selectedCountryList.clear()
                for(i in 0 until brandList.size){
                    if (brandList[i].isSelected) {
                        selectedBrandList.add(brandList[i])
                        selectedBrand = brandList[i]
                    }
                }
                for(i in 0 until subCategoryList.size){
                    if (subCategoryList[i].isSelected) {
                        selectedSubCategoryList.add(subCategoryList[i])
                        selectedSubCategory = subCategoryList[i]
                    }
                }
                for(i in 0 until countryList.size){
                    if (countryList[i].isSelected) {
                        selectedCountryList.add(countryList[i])
                        selectedCountry = countryList[i]
                    }
                }
                getProduct()
                dialog.dismiss()
            }
        }

        dialog.show()
    }

}