package com.desynatadmin.ui.auth.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.desynatadmin.R
import com.desynatadmin.base.BaseFragment
import com.desynatadmin.databinding.FragmentForgetPasswordBinding
import com.desynatadmin.utils.showToast

class ForgetPasswordFragment : BaseFragment() {
    private var binding: FragmentForgetPasswordBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = FragmentForgetPasswordBinding.inflate(inflater, container, false)
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
        binding?.btnContinue?.setOnClickListener {
            if (binding?.emailEt?.text.toString().isEmpty()) {
                showToast("Please enter email")
                return@setOnClickListener
            }else{
                findNavController().navigate(R.id.action_forgetPasswordFragment_to_otpVerificationFragment)
            }
        }
    }

    override fun observe() {

    }

}