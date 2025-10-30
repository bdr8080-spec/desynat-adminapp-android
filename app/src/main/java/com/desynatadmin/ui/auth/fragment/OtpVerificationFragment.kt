package com.desynatadmin.ui.auth.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.desynatadmin.R
import com.desynatadmin.base.BaseFragment
import com.desynatadmin.databinding.FragmentOtpVerificationBinding
import com.desynatadmin.utils.showToast

class OtpVerificationFragment : BaseFragment() {
    private var binding: FragmentOtpVerificationBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOtpVerificationBinding.inflate(inflater, container, false)
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
        binding?.btnVerify?.setOnClickListener {
            val otp = binding?.pinView?.text.toString()

            when {
                otp.isEmpty() -> {
                        showToast("Please enter OTP")
                }
                otp.length < 6 -> {
                    showToast("Please enter valid OTP")
                }
                else -> {
                    findNavController().navigate(R.id.action_otpVerificationFragment_to_resetPasswordFragment)
                }
            }
//            if (otp.isEmpty()&&otp.length<6) {
//                showToast("Please enter OTP")
//            } else{
//                findNavController().navigate(R.id.action_otpVerificationFragment_to_resetPasswordFragment)
//            }

        }

    }

    override fun observe() {

    }

}