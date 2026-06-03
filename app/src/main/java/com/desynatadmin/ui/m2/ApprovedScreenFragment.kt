package com.desynatadmin.ui.m2

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.desynatadmin.adapter.ApprovedAdapter
import com.desynatadmin.adapter.NewAdapter
import com.desynatadmin.base.BaseFragment
import com.desynatadmin.data.view_model.HomeViewModel
import com.desynatadmin.databinding.FragmentApprovedScreenBinding
import com.desynatadmin.ui.home.brandManagement.response.ApprovedSellerResponse
import com.desynatadmin.ui.home.storyManagement.ViewStoryActivity
import com.desynatadmin.ui.home.storyManagement.response.GetStoryResponse
import com.desynatadmin.utils.CommonMethod
import com.desynatadmin.utils.SharedPreference
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ApprovedScreenFragment : BaseFragment() {

    private var binding: FragmentApprovedScreenBinding? = null
    private val viewModel: HomeViewModel by viewModels()
    private var list=ArrayList<ApprovedSellerResponse.Data.Data>()
    private var filterList=ArrayList<ApprovedSellerResponse.Data.Data>()
    private var approvedAdapter:ApprovedAdapter?=null

    private var page = 1
    private var isLoading = false
    private var isLastPage = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentApprovedScreenBinding.inflate(inflater, container, false)

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

            approvedRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            approvedAdapter = ApprovedAdapter(requireActivity(),filterList, object : ApprovedAdapter.OnCLick {
                override fun onClick(position: Int,type:Int) {
                    when(type){
                        1->{
                            startActivity(
                                Intent(requireContext(), ApprovedDetailsActivity::class.java)
                                    .putExtra("id", filterList[position]?.id)
                            )
                        }
                        2->{
                            if (filterList[position].stories.isNotEmpty()){
                                val bundle = Bundle().apply {
                                    putString("from", "admin")
                                    putString("storyData", Gson().toJson(filterList[position].stories))
                                }
                                clickedPosition = position
                                val intent = Intent(requireContext(), ViewStoryActivity::class.java)
                                intent.putExtras(bundle)
                                storyLauncher.launch(intent)
                            }
                        }
                    }

                }
            })

            approvedRecyclerView?.adapter = approvedAdapter

            approvedRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {

                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager

                    val visibleItemCount = layoutManager.childCount
                    val totalItemCount = layoutManager.itemCount
                    val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                    if (!isLoading && !isLastPage) {

                        if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                            && firstVisibleItemPosition >= 0
                        ) {

                            page++
                            loadData(page)

                        }
                    }
                }
            })

            loadData(page)
        }
    }

    override fun onResume() {
        super.onResume()
//        viewModel.hitApprovedSellerList(
//            token = SharedPreference.get(requireContext()).accessToken,
//            country_id = "",
//            from_date = "",
//            to_date = "",
//            limit = "5",
//            search = ""
//        )
    }

    private fun loadData(page: Int) {

        isLoading = true

        viewModel.hitApprovedSellerList(
            token = SharedPreference.get(requireContext()).accessToken,
            country_id = "",
            from_date = "",
            to_date = "",
            limit = "5",
            search = "",
            page= page.toString()
        )

    }

    override fun initControl() {

    }

    override fun observe() {
        viewModel.getApprovedSellerList.observe(viewLifecycleOwner) {

            isLoading = false

            if (!it.data.data.isNullOrEmpty()) {

                binding?.noSeller?.isVisible = false
                binding?.approvedRecyclerView?.isVisible = true

                if (page == 1) {
                    list.clear()
                    filterList.clear()
                }

                list.addAll(it.data.data)
                filterList.addAll(it.data.data)

                approvedAdapter?.notifyDataSetChanged()

            } else {

                if (page == 1) {
                    binding?.noSeller?.isVisible = true
                    binding?.approvedRecyclerView?.isVisible = false
                }

                isLastPage = true
            }
        }
    }

//    fun filterData(s:String){
//        var arr=list.filter { it.first_name.contains(s,true) }
//        (binding?.approvedRecyclerView?.adapter as ApprovedAdapter).list=arr
//        if(arr.isNullOrEmpty()){
//            binding?.noSeller?.isVisible = true
//            binding?.approvedRecyclerView?.isVisible = false
//        }
//        else{
//            binding?.noSeller?.isVisible = false
//            binding?.approvedRecyclerView?.isVisible = true
//        }
//        (binding?.approvedRecyclerView?.adapter as ApprovedAdapter).notifyDataSetChanged()
//    }

    fun filterData(s: String) {

        filterList.clear()

        if (s.isEmpty()) {
            filterList.addAll(list)
        } else {
            filterList.addAll(
                list.filter {
                    it.first_name.contains(s, true)
                }
            )
        }

        if (filterList.isEmpty()) {
            binding?.noSeller?.isVisible = true
            binding?.approvedRecyclerView?.isVisible = false
        } else {
            binding?.noSeller?.isVisible = false
            binding?.approvedRecyclerView?.isVisible = true
        }

        approvedAdapter?.notifyDataSetChanged()
    }

    fun exportData(){
        val csv = CommonMethod.listToCsv(list)
        CommonMethod.saveCsvFile(requireContext(), "approved_seller_${System.currentTimeMillis()}", csv)
    }


    private var clickedPosition = -1

    private val storyLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

            if (result.resultCode == AppCompatActivity.RESULT_OK) {

                val json = result.data?.getStringExtra("updatedStories")

                if (!json.isNullOrEmpty() && clickedPosition != -1) {

                    val type = object :
                        TypeToken<ArrayList<GetStoryResponse.Data.Data>>() {}.type

                    val updatedStories =
                        Gson().fromJson<ArrayList<GetStoryResponse.Data.Data>>(
                            json,
                            type
                        )

                    filterList[clickedPosition].stories.clear()
                    filterList[clickedPosition].stories.addAll(updatedStories)

                    approvedAdapter?.notifyItemChanged(clickedPosition)
                }
            }
        }
}