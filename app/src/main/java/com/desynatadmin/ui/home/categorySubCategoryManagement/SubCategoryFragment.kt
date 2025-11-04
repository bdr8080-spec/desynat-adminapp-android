package com.desynatadmin.ui.home.categorySubCategoryManagement

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.desynatadmin.R
import com.desynatadmin.adapter.SubCategoryAdapter
import com.desynatadmin.base.BaseFragment
import com.desynatadmin.data.view_model.HomeViewModel
import com.desynatadmin.databinding.FragmentSubCategoryBinding
import com.desynatadmin.ui.home.categorySubCategoryManagement.response.GetCategoryResponse
import com.desynatadmin.ui.home.categorySubCategoryManagement.response.GetSubCategoryResponse
import com.desynatadmin.utils.CommonMethod
import com.desynatadmin.utils.ProgressDialogUtils
import com.desynatadmin.utils.showToast
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SubCategoryFragment : BaseFragment() {
    private var binding: FragmentSubCategoryBinding?=null
    private val viewModel: HomeViewModel by viewModels()
    private var isObserver = true
    private var subCategoryList = ArrayList<GetSubCategoryResponse.Data.Data>()
    private var subCategoryAdapter : SubCategoryAdapter?=null
    private var currentPage = 1
    private var lastPage = 1
    private var totalItem=0
    private var pageLimit=10
    private var isFromSwipe=false
    private var pagination=false
    private var searchBrand: Job? = null
    private var lastQuery: String? = null
    private var categoryData: GetCategoryResponse.Data?=null
    private val itemsPerPage = 10

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSubCategoryBinding.inflate(layoutInflater,container,false)
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
        val itemJson = arguments?.getString("categoryData")
        if (!itemJson.isNullOrEmpty()) {
            categoryData = Gson().fromJson(itemJson, GetCategoryResponse.Data::class.java)
        }
        binding?.apply {
            CommonMethod.removeSpace(etSearch)
            toolbar.tvToolbar.text = "Sub-Category"

            swipeRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(requireActivity(), R.color.brown)
            )

            subCategoryList.clear()
            subCategoryAdapter = SubCategoryAdapter(requireActivity(),subCategoryList,object :SubCategoryAdapter.SubCategoryClick{
                override fun onSubCategoryClick(position: Int) {
                    val bundle = Bundle().apply {
                        putString("subCategoryData", Gson().toJson(subCategoryList[position]))
                        putString("categoryId", categoryData?.id.toString())
                    }
                    findNavController().navigate(R.id.action_subcategory_to_subCategoryDetailFragment,bundle)
                }

            })
            rvSubcategory.adapter = subCategoryAdapter

            etSearch.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    val query = s.toString().trim()
                    if (query == lastQuery) return
                    lastQuery = query
                    searchBrand?.cancel()
                    searchBrand = lifecycleScope.launch {
                        delay(600)
                        currentPage = 1
                        pagination = false
                        isFromSwipe = false
                        if (!isObserver)
                            getAllSubCategory(search =  query.ifEmpty { null })
                    }

                }
            })

            swipeRefreshLayout.setOnRefreshListener {
                isFromSwipe = true
                pagination = false
                currentPage = 1
                getAllSubCategory(false)
            }

           /* nestedScrollView.setOnScrollChangeListener { nestedScrollView: NestedScrollView, _, scrollY, _, oldScrollY ->
                val lastChild = nestedScrollView.getChildAt(nestedScrollView.childCount - 1)

                if (lastChild != null) {
                    val diff = lastChild.bottom - (nestedScrollView.height + scrollY)
                    if (diff == 0 && scrollY > oldScrollY) {
                        if(totalItem==0)
                            return@setOnScrollChangeListener
                        if (!isObserver && currentPage < lastPage) {
                            pagination = true
                            isFromSwipe = false
                            currentPage += 1
                            getAllSubCategory(false)
                            binding?.progressBar?.isVisible = true
                        }
                    }
                }
            }*/

        }
        getAllSubCategory()
    }

    private fun getAllSubCategory(loading:Boolean=true,search:String?=null){
        viewModel.getAllSUbCategory(categoryData?.id.toString(),search,currentPage)
        if (loading){
            ProgressDialogUtils.getInstance().showProgress(requireActivity())
        }
        isObserver = true
    }

    override fun initControl() {
        binding?.apply {
            toolbar.tvToolbar.setOnClickListener {
                findNavController().popBackStack()
            }
            ivAddSubCategory.setOnClickListener {
                val bundle = Bundle().apply {
                    putString("categoryId", categoryData?.id.toString())
                }
                findNavController().navigate(R.id.action_subcategory_to_addSubCategoryFragment,bundle)
            }

            ivNextArrow.setOnClickListener {
                    if (!isObserver && currentPage < lastPage&&totalItem!=0) {
                        currentPage += 1
                        getAllSubCategory(false)
                        binding?.progressBar?.isVisible = true
                    }


            }

            ivBackArrow.setOnClickListener {
                if (currentPage > 1) {
                    currentPage--
                    getAllSubCategory(false)
                    binding?.progressBar?.isVisible = true
                }
            }
        }
    }

    override fun observe() {
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                binding?.progressBar?.isVisible = false
                if (isFromSwipe){
                    binding?.swipeRefreshLayout?.isRefreshing = false
                }
                errorMessage?.let { showToast(it) }
                pagination = false
                isFromSwipe = false
                isObserver = false
            }
        }

        viewModel.getAllSubCategoryResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
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

                val oldSize = subCategoryList.size
                if (!pagination){
                    subCategoryList.clear()
                }
                subCategoryList.clear()
                updatePage()
                val newItems = response.data.data ?: emptyList()
                subCategoryList.addAll(newItems)
                checkData()

                if (pagination){

                    response.data.data?.size?.let {
                        subCategoryAdapter?.notifyItemRangeInserted(oldSize,
                            it
                        )
                    }
                }
                else{
                    subCategoryAdapter?.notifyDataSetChanged()
                }

                pagination = false
                isFromSwipe = false
                isObserver = false
            }
        }

    }

    private fun checkData(){
        binding?.tvNoDataFound?.isVisible = subCategoryList.isEmpty()
    }

    private fun updatePage() {
        val start = (currentPage - 1) * itemsPerPage
        val end = minOf(start + itemsPerPage, subCategoryList.size)

        if (start>=0&&end>=0) {
            if (start < end) {
                val pageData = subCategoryList.subList(start, end)
                subCategoryAdapter?.setData(ArrayList(pageData))
            }
        }


    }

}