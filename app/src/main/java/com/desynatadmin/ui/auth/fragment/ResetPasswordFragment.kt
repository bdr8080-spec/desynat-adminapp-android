package com.desynatadmin.ui.auth.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.desynatadmin.R
import com.desynatadmin.base.BaseFragment
import com.desynatadmin.data.view_model.AuthViewModel
import com.desynatadmin.databinding.FragmentResetPasswordBinding
import com.desynatadmin.utils.CommonMethod
import com.desynatadmin.utils.ProgressDialogUtils
import com.desynatadmin.utils.showToast
import com.desynatadmin.utils.togglePasswordVisibility
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ResetPasswordFragment : BaseFragment() {
    private var binding: FragmentResetPasswordBinding? = null
    private val viewModel: AuthViewModel by viewModels()
    var isObserver = false
    var adminId = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentResetPasswordBinding.inflate(layoutInflater, container, false)
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
            toolbar.tvToolbar.text = "Reset Password"
            CommonMethod.removeAllTextSpaces(etPassword)
            CommonMethod.removeAllTextSpaces(etConfirmPassword)

            etPassword.togglePasswordVisibility(ivPasswordVisible)
            etConfirmPassword.togglePasswordVisibility(ivConfirmPasswordVisible)
        }
    }

    override fun initControl() {
        adminId = arguments?.getString("adminId").toString()

        binding?.apply {

            toolbar.tvToolbar.setOnClickListener{
                findNavController().popBackStack()
            }

            btnDone.setOnClickListener {
                if (validation()) {
                    val map = HashMap<String,Any>()
                    map["id"] = adminId
                    map["password"] = etPassword.text.toString().trim()
                    map["confirm_password"] = etConfirmPassword.text.toString().trim()
                    viewModel.createPassword(map)
                    ProgressDialogUtils.getInstance().showProgress(requireActivity())
                    isObserver = true

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

        viewModel.createPasswordResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                response?.let {
                    CommonMethod.showSuccessDialog(
                        requireActivity(),
                        getString(R.string.password_reset_successfully), false
                    ) {
                        findNavController().popBackStack(R.id.signInFragment, false)
                    }
                }
                isObserver = false
            }
        }

    }

    private fun validation():Boolean{
        val password = binding?.etPassword?.text.toString().trim()
        val confirmPassword = binding?.etConfirmPassword?.text.toString().trim()

        if (password.isEmpty()) {
            showToast("Please Enter New Password")
            return false
        }
       /* else if (!CommonMethod.isValidPassword(password)) {
            showToast("Password must be at least 8 characters long and include one uppercase letter, one lowercase letter, one number, and one special character.")
            return false
        }*/
        else if (confirmPassword.isEmpty() ) {
            showToast("Please Re-Enter Confirm Password")
            return false
        }
        else if (password!=confirmPassword) {
            showToast("Password and Confirm Password didn't match")
            return false
        }
        return true
    }

}