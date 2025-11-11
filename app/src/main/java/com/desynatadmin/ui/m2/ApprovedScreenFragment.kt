package com.desynatadmin.ui.m2

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.desynatadmin.adapter.ApprovedAdapter
import com.desynatadmin.base.BaseFragment
import com.desynatadmin.databinding.FragmentApprovedScreenBinding


class ApprovedScreenFragment : BaseFragment() {

    private var binding: FragmentApprovedScreenBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentApprovedScreenBinding.inflate(inflater, container, false)

        return binding?.root    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initControl()
        observe()

    }

    override fun initView() {

    }

    override fun initControl() {
        binding?.approvedRecyclerView?.adapter = ApprovedAdapter(){
            startActivity(Intent(requireActivity(), ApprovedDetailsActivity::class.java))
        }

    }

    override fun observe() {

    }

}