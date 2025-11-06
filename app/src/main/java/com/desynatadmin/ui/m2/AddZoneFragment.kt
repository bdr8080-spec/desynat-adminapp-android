package com.desynatadmin.ui.m2

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import com.desynatadmin.R
import com.desynatadmin.base.BaseFragment
import com.desynatadmin.databinding.FragmentAddZoneBinding
import com.desynatadmin.databinding.FragmentCountryCityAndZoneManaBinding

class AddZoneFragment : BaseFragment() {
    private var binding: FragmentAddZoneBinding? = null
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAddZoneBinding.inflate(inflater, container, false)

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
        binding?.tvToolbar?.setOnClickListener {
            navController.popBackStack()
        }
    }

    override fun observe() {
    }

}