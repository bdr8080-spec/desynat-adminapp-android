package com.desynatadmin.ui.auth.fragment

import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.desynatadmin.R
import com.desynatadmin.base.BaseFragment
import com.desynatadmin.data.view_model.AuthViewModel
import com.desynatadmin.databinding.FragmentForgetPasswordBinding
import com.desynatadmin.utils.CommonMethod
import com.desynatadmin.utils.ProgressDialogUtils
import com.desynatadmin.utils.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ForgetPasswordFragment : BaseFragment() {
    private var binding: FragmentForgetPasswordBinding? = null
    private val viewModel: AuthViewModel by viewModels()
    var isObserver = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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
        binding?.apply {
            CommonMethod.removeAllTextSpaces(etEmail)
            toolbar.tvToolbar.text = "Forgot Password"
        }
    }

    override fun initControl() {

        binding?.apply {

            toolbar.ivBack.setOnClickListener {
                findNavController().popBackStack()
            }

            btnContinue.setOnClickListener {
                if (validation()) {
                    ProgressDialogUtils.getInstance().showProgress(requireActivity())
                    isObserver = true
                    val map = HashMap<String, Any>()
                    map["email"] = binding?.etEmail?.text.toString().trim()

                    viewModel.forgotPassword(map)
                }

            }
        }
    }

    override fun observe() {
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()

                isObserver = false
            }

        }

        viewModel.forgotPasswordResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                response?.let {
                    CommonMethod.showSuccessDialog(requireActivity(),"OTP sent to your email address successfully"){
                        val bundle = Bundle().apply {
                            putString("adminId", response.data.id)
                            putString("email", binding?.etEmail?.text.toString().trim())
                        }
                        findNavController().navigate(R.id.action_forgetPasswordFragment_to_otpVerificationFragment,bundle)

                    }

                }
                isObserver = false
            }
        }

    }

    private fun validation():Boolean{
        val email = binding?.etEmail?.text.toString().trim()
if (email.isBlank()) {
            showToast("Please Enter Email Address")
            return false
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showToast("Please Enter Valid Email Address")
            return false
        }
        return true
    }


}