package com.desynatadmin.ui.m2

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.desynatadmin.adapter.RejectedAdapter
import com.desynatadmin.base.BaseFragment
import com.desynatadmin.data.view_model.HomeViewModel
import com.desynatadmin.databinding.FragmentRejectedScreenBinding
import com.desynatadmin.ui.home.brandManagement.response.RejectedListResponse
import com.desynatadmin.utils.CommonMethod
import com.desynatadmin.utils.SharedPreference
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RejectedScreenFragment : BaseFragment() {

    private var binding: FragmentRejectedScreenBinding? = null
    private val viewModel: HomeViewModel by viewModels()

    private var list=ArrayList<RejectedListResponse.Data.Data>()
    private var filterList=ArrayList<RejectedListResponse.Data.Data>()
    private var rejectedAdapter:RejectedAdapter?=null

    private var page = 1
    private var isLoading = false
    private var isLastPage = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentRejectedScreenBinding.inflate(inflater, container, false)

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

            rejectedRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            rejectedAdapter =  RejectedAdapter(requireActivity(),list, object : RejectedAdapter.OnClick {
                override fun onClick(position: Int) {
                    startActivity(
                        Intent(requireContext(), RejectDetailsActivity::class.java)
                            .putExtra("id", filterList[position].id)
                    )
                }
            })

            rejectedRecyclerView?.adapter = rejectedAdapter

            rejectedRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {

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

    private fun loadData(page: Int) {

        isLoading = true

        viewModel.hitRejectedSellerList(
            token = SharedPreference.get(requireContext()).accessToken,
            country_id = "",
            from_date = "",
            to_date = "",
            limit = "5",
            search = "",
            page= page.toString()
        )

    }

    override fun onResume() {
        super.onResume()
//        viewModel.hitRejectedSellerList(
//            SharedPreference.get(requireContext()).accessToken,
//            country_id = "",
//            from_date = "",
//            to_date = "",
//            search = "",
//            limit = "5"
//        )
    }

    override fun initControl() {


    }

    override fun observe() {
        viewModel.getRejectedSellerList.observe(viewLifecycleOwner) {
            if (!it.data.data.isNullOrEmpty()) {
                binding?.noSeller?.isVisible = false
                binding?.rejectedRecyclerView?.isVisible = true

                if (page == 1) {
                    list.clear()
                    filterList.clear()
                }

                list.addAll(it.data.data)
                filterList.addAll(it.data.data)

                rejectedAdapter?.notifyDataSetChanged()

                rejectedAdapter?.notifyDataSetChanged()
            } else {
                binding?.noSeller?.isVisible = true
                binding?.rejectedRecyclerView?.isVisible = false
            }
        }
    }

    fun filterData(s:String){
        var arr=list.filter { it.first_name.contains(s,true) }
        (binding?.rejectedRecyclerView?.adapter as RejectedAdapter).list=arr
        if(arr.isNullOrEmpty()){
            binding?.noSeller?.isVisible = true
            binding?.rejectedRecyclerView?.isVisible = false
        }
        else{
            binding?.noSeller?.isVisible = false
            binding?.rejectedRecyclerView?.isVisible = true
        }
        (binding?.rejectedRecyclerView?.adapter as RejectedAdapter).notifyDataSetChanged()
    }

    fun exportData(){
        val csv = CommonMethod.listToCsv(list)
        CommonMethod.saveCsvFile(requireContext(), "rejected_seller_${System.currentTimeMillis()}", csv)
    }

}