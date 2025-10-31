package com.desynatadmin.ui.auth.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.desynatadmin.R
import com.desynatadmin.base.BaseFragment
import com.desynatadmin.data.view_model.AuthViewModel
import com.desynatadmin.databinding.FragmentSignInBinding
import com.desynatadmin.ui.home.HomeActivity
import com.desynatadmin.utils.CommonMethod
import com.desynatadmin.utils.ProgressDialogUtils
import com.desynatadmin.utils.SharedPreference
import com.desynatadmin.utils.showToast
import com.desynatadmin.utils.togglePasswordVisibility
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignInFragment : BaseFragment() {
    private var binding: FragmentSignInBinding?=null
    private val viewModel: AuthViewModel by viewModels()
    var isObserver = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentSignInBinding.inflate(inflater,container,false)
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
            CommonMethod.removeAllTextSpaces(etEmail)
            CommonMethod.removeAllTextSpaces(etPassword)
            etPassword.togglePasswordVisibility(ivPassword)
        }
    }

    override fun initControl() {
        binding?.apply {
            tvForgetPassword.setOnClickListener {
                val bundle = Bundle().apply {
                    putString("email", binding?.etEmail?.text.toString().trim())
                }
                findNavController().navigate(R.id.action_signinFragment_to_forgetPasswordFragment,bundle)
            }

            btnLogin.setOnClickListener {
                if (validation()) {
                    loginWithPassword()
                }
            }
        }

    }

    private fun loginWithPassword(){
        val map = HashMap<String,Any>()

        map["email"] = binding?.etEmail?.text?.trim().toString()
        map["password"] = binding?.etPassword?.text?.trim().toString()
        viewModel.login(map)
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

    override fun observe() {
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                errorMessage?.let { showToast(it) }
                isObserver = false
            }

        }

        viewModel.loginResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                response?.let {
                    showToast(response.message)
                    SharedPreference.get(requireActivity()).accessToken = response.data.accessToken
                 //   SharedPreference.get(requireActivity()).saveUser(response.data)
                    requireActivity().startActivity(
                        Intent(requireActivity(),
                            HomeActivity::class.java)
                    )
                    requireActivity().finish()
                }
                isObserver = false
            }
        }
    }

    private fun validation():Boolean{
        if (binding?.etEmail?.text?.toString()?.isBlank() == true ) {
            showToast("Please enter email")
            return false
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(binding?.etEmail?.text.toString()).matches()) {
            showToast("Please enter valid email")
            return false
        }
        return true
    }

}