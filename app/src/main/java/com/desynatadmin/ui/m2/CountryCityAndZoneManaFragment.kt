package com.desynatadmin.ui.m2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import com.desynatadmin.adapter.CityListAdapter
import com.desynatadmin.base.BaseFragment
import com.desynatadmin.databinding.FragmentCountryCityAndZoneManaBinding

class CountryCityAndZoneManaFragment : BaseFragment() {
private var binding: FragmentCountryCityAndZoneManaBinding ?=null
    private var navController: NavController?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCountryCityAndZoneManaBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initControl()
        observe()
    }
    override fun initView() {
        binding?.recyclerView?.adapter = CityListAdapter(){

        }

    }

    override fun initControl() {

    }

    override fun observe() {

    }

}