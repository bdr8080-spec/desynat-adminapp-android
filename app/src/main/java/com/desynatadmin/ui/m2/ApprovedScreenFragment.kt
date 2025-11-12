package com.desynatadmin.ui.m2

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.desynatadmin.adapter.ApprovedAdapter
import com.desynatadmin.adapter.NewAdapter
import com.desynatadmin.base.BaseFragment
import com.desynatadmin.data.view_model.HomeViewModel
import com.desynatadmin.databinding.FragmentApprovedScreenBinding
import com.desynatadmin.ui.home.brandManagement.response.ApprovedSellerResponse
import com.desynatadmin.utils.SharedPreference
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ApprovedScreenFragment : BaseFragment() {

    private var binding: FragmentApprovedScreenBinding? = null
    private var list=ArrayList<ApprovedSellerResponse.Data.Data>()
    private val viewModel: HomeViewModel by viewModels()

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
    }

    override fun onResume() {
        super.onResume()
        viewModel.hitApprovedSellerList(
            token = SharedPreference.get(requireContext()).accessToken,
            country_id = "",
            from_date = "",
            to_date = "",
            limit = "5",
            search = ""
        )
    }

    override fun initControl() {

    }

    override fun observe() {
        viewModel.getApprovedSellerList.observe(viewLifecycleOwner){
          if(!it.data.data.isNullOrEmpty()){
              binding?.noSeller?.isVisible = false
              binding?.approvedRecyclerView?.isVisible = true
              list=it.data.data
              binding?.approvedRecyclerView?.adapter = ApprovedAdapter(list, object : ApprovedAdapter.OnCLick{
                  override fun onClick(position: Int) {
                      startActivity(Intent(requireContext(), ApprovedDetailsActivity::class.java)
                          .putExtra("id", it.data?.data?.get(position)?.id)
                      )
                  }
              })
          }
            else{
                binding?.noSeller?.isVisible = true
                binding?.approvedRecyclerView?.isVisible = false
          }
        }
    }

    fun filterData(s:String){
        var arr=list.filter { it.first_name.contains(s,true) }
        (binding?.approvedRecyclerView?.adapter as ApprovedAdapter).list=arr
        (binding?.approvedRecyclerView?.adapter as ApprovedAdapter).notifyDataSetChanged()
    }

}