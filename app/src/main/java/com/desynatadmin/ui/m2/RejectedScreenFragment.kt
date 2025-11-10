package com.desynatadmin.ui.m2

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.desynatadmin.R
import com.desynatadmin.adapter.RejectedAdapter
import com.desynatadmin.base.BaseFragment
import com.desynatadmin.databinding.FragmentApprovedScreenBinding
import com.desynatadmin.databinding.FragmentRejectedScreenBinding

class RejectedScreenFragment : BaseFragment() {

    private var binding: FragmentRejectedScreenBinding? = null

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

    override fun initControl() {

        binding?.rejectedRecyclerView?.adapter = RejectedAdapter()
    }

    override fun observe() {

    }

}