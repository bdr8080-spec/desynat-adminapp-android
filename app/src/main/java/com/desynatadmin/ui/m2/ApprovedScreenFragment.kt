package com.desynatadmin.ui.m2

import android.os.BaseBundle
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.desynatadmin.R
import com.desynatadmin.adapter.ApprovedAdapter
import com.desynatadmin.base.BaseFragment
import com.desynatadmin.databinding.FragmentApprovedScreenBinding
import com.desynatadmin.databinding.FragmentNewScreenBinding


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
        binding?.approvedRecyclerView?.adapter = ApprovedAdapter()

    }

    override fun observe() {

    }

}