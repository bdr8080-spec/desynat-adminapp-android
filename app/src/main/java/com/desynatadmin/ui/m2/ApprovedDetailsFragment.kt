package com.desynatadmin.ui.m2

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.desynatadmin.R
import com.desynatadmin.base.BaseFragment
import com.desynatadmin.databinding.FragmentApprovedDetailsBinding
import com.desynatadmin.databinding.FragmentHomeBinding


class ApprovedDetailsFragment : BaseFragment() {
    private var binding: FragmentApprovedDetailsBinding? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentApprovedDetailsBinding.inflate(layoutInflater, container, false)
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
        binding?.btnSellerInfo?.setOnClickListener {
            binding?.btnSellerInfo?.setBackgroundResource(R.drawable.btn_seller_selected)
            binding?.btnSellerInfo?.setTextColor(Color.WHITE)

            binding?.btnOtherInfo?.setBackgroundResource(R.drawable.btn_other_unselected)
            binding?.btnOtherInfo?.setTextColor(Color.BLACK)
        }

        binding?.btnOtherInfo?.setOnClickListener {
            binding?.btnOtherInfo?.setBackgroundResource(R.drawable.btn_seller_selected)
            binding?.btnOtherInfo?.setTextColor(Color.WHITE)

            binding?.btnSellerInfo?.setBackgroundResource(R.drawable.btn_other_unselected)
            binding?.btnSellerInfo?.setTextColor(Color.BLACK)
        }

    }

    override fun observe() {
    }


}
