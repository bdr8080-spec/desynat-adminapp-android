package com.desynatadmin.ui.home.categorySubCategoryManagement

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.desynatadmin.R
import com.desynatadmin.adapter.CategoryAdapter
import com.desynatadmin.base.BaseFragment
import com.desynatadmin.data.view_model.HomeViewModel
import com.desynatadmin.databinding.FragmentCategoryBinding
import com.desynatadmin.ui.home.HomeActivity
import com.desynatadmin.ui.home.categorySubCategoryManagement.response.GetCategoryResponse
import com.desynatadmin.utils.ProgressDialogUtils
import com.desynatadmin.utils.showToast
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CategoryFragment : BaseFragment() {
    private var binding: FragmentCategoryBinding?=null
    private val viewModel: HomeViewModel by viewModels()
    private var isObserver = true
    private var categoryList = ArrayList<GetCategoryResponse.Data>()
    private var categoryAdapter : CategoryAdapter?=null
    private var selectedPos = -1

    private var isFromSwipe=false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCategoryBinding.inflate(layoutInflater,container,false)
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
            toolbar.tvToolbar.text = "Category"

           swipeRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(requireActivity(), R.color.brown)
            )

            categoryList.clear()
            categoryAdapter = CategoryAdapter(requireActivity(),categoryList,object :CategoryAdapter.CategoryClick{
                override fun onCategoryClick(position: Int,type:Int) {
                    if (type==1){
                        selectedPos = position
                        updateSubCategoryStatus()
                    }
                    else {
                        val bundle = Bundle().apply {
                            putString("categoryData", Gson().toJson(categoryList[position]))
                        }
                        findNavController().navigate(
                            R.id.action_category_to_subcategoryFragment,
                            bundle
                        )
                    }
                }

            })

            rvCategory.adapter = categoryAdapter

            swipeRefreshLayout.setOnRefreshListener {
                isFromSwipe = true
                getAllCategory(false)
            }

        }
        getAllCategory()
    }

    private fun updateSubCategoryStatus(){
        val map = HashMap<String,Any>()
        map["category_id"]=categoryList[selectedPos].id
        viewModel.updateCategoryStatus(map)
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

    private fun getAllCategory(loading:Boolean=true){
        viewModel.getAllCategory()
        if (loading){
            ProgressDialogUtils.getInstance().showProgress(requireActivity())
        }
        isObserver = true
    }

    override fun initControl() {
        binding?.apply {
            toolbar.tvToolbar.setOnClickListener {
                (requireActivity() as? HomeActivity)?.onBackPressed()
            }
        }
    }

    override fun observe() {
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                if (isFromSwipe){
                    binding?.swipeRefreshLayout?.isRefreshing = false
                }
                errorMessage?.let { showToast(it) }
                isFromSwipe = false
                isObserver = false
            }
        }

        viewModel.updateCategoryStatusResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                categoryList[selectedPos] = response.data
                if (response.data.status==1){
                    showToast("Category Activated Successfully")
                }
                else{
                    showToast("Category Deactivated Successfully")

                }
                categoryAdapter?.notifyItemChanged(selectedPos)
                selectedPos = -1
                isObserver = false

            }
        }

        viewModel.getAllCategoryResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                if (isFromSwipe){
                    binding?.swipeRefreshLayout?.isRefreshing = false
                }

                    categoryList.clear()
                categoryList.addAll(response.data)
                checkData()
                categoryAdapter?.notifyDataSetChanged()

                isFromSwipe = false
                isObserver = false
            }
        }

    }

    private fun checkData(){
        binding?.tvNoDataFound?.isVisible = categoryList.isEmpty()
    }

}