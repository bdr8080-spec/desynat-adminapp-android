package com.desynatadmin.ui.m2

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.desynatadmin.adapter.ApprovedAdapter
import com.desynatadmin.adapter.RejectedAdapter
import com.desynatadmin.base.BaseFragment
import com.desynatadmin.data.view_model.HomeViewModel
import com.desynatadmin.databinding.FragmentRejectedScreenBinding
import com.desynatadmin.ui.home.brandManagement.response.RejectedListResponse
import com.desynatadmin.utils.SharedPreference
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RejectedScreenFragment : BaseFragment() {

    var list=ArrayList<RejectedListResponse.Data.Data>()
    private var binding: FragmentRejectedScreenBinding? = null
    private val viewModel: HomeViewModel by viewModels()

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

    }

    override fun onResume() {
        super.onResume()
        viewModel.hitRejectedSellerList(
            SharedPreference.get(requireContext()).accessToken,
            country_id = "",
            from_date = "",
            to_date = "",
            search = "",
            limit = "5"
        )
    }

    override fun initControl() {


    }

    override fun observe() {
        viewModel.getRejectedSellerList.observe(viewLifecycleOwner) {
            if (!it.data.data.isNullOrEmpty()) {
                binding?.noSeller?.isVisible = false
                binding?.rejectedRecyclerView?.isVisible = true
                list=it.data.data
                binding?.rejectedRecyclerView?.adapter =
                    RejectedAdapter(list, object : RejectedAdapter.OnClick {
                        override fun onClick(position: Int) {
                            startActivity(
                                Intent(requireContext(), RejectDetailsActivity::class.java)
                                    .putExtra("id", it.data.data.get(position).id)
                            )
                        }
                    })
            } else {
                binding?.noSeller?.isVisible = true
                binding?.rejectedRecyclerView?.isVisible = false
            }
        }
    }

    fun filterData(s:String){
        var arr=list.filter { it.first_name.contains(s,true) }
        (binding?.rejectedRecyclerView?.adapter as RejectedAdapter).list=arr
        (binding?.rejectedRecyclerView?.adapter as RejectedAdapter).notifyDataSetChanged()
    }


}