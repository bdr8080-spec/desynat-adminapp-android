package com.desynatadmin.ui.home.liveCoverage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.desynatadmin.R
import com.desynatadmin.base.BaseFragment
import com.desynatadmin.data.view_model.HomeViewModel
import com.desynatadmin.databinding.FragmentEventBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue
@AndroidEntryPoint
class EventFragment : BaseFragment() {
    private var binding: FragmentEventBinding?=null
    private val viewModel: HomeViewModel by viewModels()
    private var isObserver = true
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEventBinding.inflate(layoutInflater,container,false)
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

        }

    }

    override fun initControl() {
    }

    override fun observe() {
    }

}