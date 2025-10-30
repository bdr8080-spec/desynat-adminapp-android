package com.desynatadmin.ui.auth.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.desynatadmin.R
import com.desynatadmin.base.BaseFragment
import com.desynatadmin.databinding.FragmentLanguageBinding
import com.desynatadmin.databinding.FragmentSigninBinding
import com.desynatadmin.utils.togglePasswordVisibility


class SigninFragment : BaseFragment() {
    private var binding: FragmentSigninBinding?=null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentSigninBinding.inflate(inflater,container,false)
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
        binding?.passwordVisivility?.setOnClickListener {
//            binding!!.passwordEt.togglePasswordVisibility(binding.passwordVisivility)

        }
        binding?.forgetPassword?.setOnClickListener {
            findNavController().navigate(R.id.action_signinFragment_to_forgetPasswordFragment)
        }

    }

    override fun observe() {

    }

}