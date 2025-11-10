package com.desynatadmin.ui.m2

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.desynatadmin.R
import com.desynatadmin.adapter.HighlightsAdapter
import com.desynatadmin.base.BaseFragment
import com.desynatadmin.databinding.FragmentRejectedDetailsBinding

class RejectedDetailsFragment : BaseFragment() {

    private var binding: FragmentRejectedDetailsBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentRejectedDetailsBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initControl()
        observe()
    }

    override fun initView() {
        // initialize your UI views if needed
    }

    override fun initControl() {

    }

    override fun observe() {
        // observe LiveData or ViewModel if needed
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}
