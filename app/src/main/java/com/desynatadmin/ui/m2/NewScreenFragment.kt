package com.desynatadmin.ui.m2

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import com.desynatadmin.R
import com.desynatadmin.adapter.NewAdapter
import com.desynatadmin.base.BaseFragment
import com.desynatadmin.databinding.FragmentNewScreenBinding

class NewScreenFragment : BaseFragment() {

    private var binding: FragmentNewScreenBinding? = null
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentNewScreenBinding.inflate(inflater, container, false)

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
        binding?.newRecycler?.adapter = NewAdapter(){
            startActivity(Intent(requireContext(), NewDetailActivity::class.java))

        }
    }

    override fun observe() {

    }

}