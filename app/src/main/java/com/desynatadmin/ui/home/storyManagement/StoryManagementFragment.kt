package com.desynatadmin.ui.home.storyManagement

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.desynatadmin.R
import com.desynatadmin.adapter.StoryAdapter
import com.desynatadmin.base.BaseFragment
import com.desynatadmin.data.view_model.HomeViewModel
import com.desynatadmin.databinding.FragmentStoryManagementBinding
import com.desynatadmin.ui.home.HomeActivity
import com.desynatadmin.ui.home.storyManagement.response.GetStoryResponse
import com.desynatadmin.utils.CommonMethod
import com.desynatadmin.utils.ProgressDialogUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StoryManagementFragment : BaseFragment() {
    private var binding: FragmentStoryManagementBinding? = null
    private val viewModel: HomeViewModel by viewModels()
    private var isObserver = true

    private var currentPage = 1
    private var lastPage = 1
    private var totalPage = 0
    private var totalItem=0
    private var pageLimit=10
    private var isFromSwipe=false
    private var pagination=false
    private var selectedPos = -1

    private var storyList = ArrayList<GetStoryResponse.Data.Data>()
    private var storyAdapter: StoryAdapter?=null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStoryManagementBinding.inflate(layoutInflater, container, false)
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
            toolbar.tvToolbar.text = "Story Management"

            storyAdapter = StoryAdapter(requireActivity(),storyList,object :StoryAdapter.OnClick{
                override fun onClick(position: Int, type: Int) {
                    when(type){
                        1->{
                            findNavController().navigate(R.id.action_storyManagement_to_addStoryFragment)
                        }
                        2->{
                            selectedPos = position
                            deleteStory()
                        }
                    }
                }

            })

            swipeRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(requireActivity(), R.color.brown)
            )

            nestedScrollView.setOnScrollChangeListener { nestedScrollView: NestedScrollView, _, scrollY, _, oldScrollY ->
                val lastChild = nestedScrollView.getChildAt(nestedScrollView.childCount - 1)

                if (lastChild != null) {
                    val diff = lastChild.bottom - (nestedScrollView.height + scrollY)
                    if (diff == 0 && scrollY > oldScrollY) {
                        if(totalPage==0)
                            return@setOnScrollChangeListener
                        if (!isObserver && currentPage < lastPage) {
                            pagination = true
                            isFromSwipe = false
                            currentPage += 1
                            getAdminStory(false)
                            binding?.progressBar?.isVisible = true
                        }
                    }
                }
            }
        }

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
                binding?.progressBar?.isVisible = false
                if (isFromSwipe){
                    binding?.swipeRefreshLayout?.isRefreshing = false
                }

                pagination = false
                isFromSwipe = false
                isObserver = false
            }
        }

        viewModel.getAdminStoryResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                binding?.progressBar?.isVisible = false
                if (isFromSwipe){
                    binding?.swipeRefreshLayout?.isRefreshing = false
                }

                currentPage = response.data.currentPage
                totalItem = response.data.total

                lastPage = response.data.lastPage

                val oldSize = storyList.size
                if (!pagination){
                    storyList.clear()
                }
                val newItems = response.data.data ?: emptyList()

                storyList.addAll(newItems)
                checkData()

                if (pagination){
                    response.data.data?.size?.let {
                        storyAdapter?.notifyItemRangeInserted(oldSize,
                            it
                        )
                    }
                }
                else{
                    storyAdapter?.notifyDataSetChanged()
                }

                pagination = false
                isFromSwipe = false
                isObserver = false
            }
        }

        viewModel.deleteStoryResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                CommonMethod.showSuccessDialog(requireContext(), "Story deleted successfully!"){
                    storyList.removeAt(selectedPos)
                    storyAdapter?.notifyItemRemoved(selectedPos)
                    storyAdapter?.notifyItemRangeChanged(selectedPos, storyList.size)
                    checkData()
                    selectedPos = -1
                }
                isObserver = false
            }
        }

    }

    private fun getAdminStory(loading:Boolean=true){
        viewModel.getAdminStory(page = currentPage)
        if (loading){
            ProgressDialogUtils.getInstance().showProgress(requireActivity())
        }
        isObserver = true
    }


    fun deleteStory(){
        val map = HashMap<String, Any>()
        map["story_id"] = storyList[selectedPos].id
        viewModel.deleteStory(map)
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }


    private fun checkData(){
        binding?.tvNoDataFound?.isVisible = storyList.isEmpty()
    }

}