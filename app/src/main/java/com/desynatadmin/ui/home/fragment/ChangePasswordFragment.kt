package com.desynatadmin.ui.home.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.desynatadmin.R
import com.desynatadmin.base.BaseFragment
import com.desynatadmin.data.view_model.HomeViewModel
import com.desynatadmin.databinding.FragmentChangePasswordBinding
import com.desynatadmin.utils.CommonMethod
import com.desynatadmin.utils.ProgressDialogUtils
import com.desynatadmin.utils.showToast
import com.desynatadmin.utils.togglePasswordVisibility
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChangePasswordFragment : BaseFragment() {
    private var binding: FragmentChangePasswordBinding?=null
    private val viewModel: HomeViewModel by viewModels()
    var isObserver = false
    var from = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChangePasswordBinding.inflate(layoutInflater,container,false)
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

            from = arguments?.getString("from","").toString()

            toolbar.tvToolbar.text = getString(R.string.change_password)
            CommonMethod.removeAllTextSpaces(etPassword)
            CommonMethod.removeAllTextSpaces(etConfirmPassword)
            CommonMethod.removeAllTextSpaces(etOldPassword)

            etOldPassword.togglePasswordVisibility(ivOldPasswordVisible)
            etPassword.togglePasswordVisibility(ivPasswordVisible)
            etConfirmPassword.togglePasswordVisibility(ivConfirmPasswordVisible)
        }
    }

    override fun initControl() {

        binding?.apply {

                toolbar.tvToolbar.setOnClickListener{
                    findNavController().popBackStack()
                }

            btnChange.setOnClickListener {
                    if (validation()) {
                        val map = HashMap<String,Any>()
                            map["old_password"] = binding?.etOldPassword?.text.toString().trim()
                            map["new_password"] = binding?.etPassword?.text.toString().trim()
                            map["confirm_password"] = binding?.etConfirmPassword?.text.toString().trim()
                            viewModel.updatePassword(map)

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
                errorMessage?.let { showToast(it) }
                isObserver = false
            }
        }

        viewModel.updatePasswordResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                response?.let {
                    CommonMethod.showSuccessDialog(requireActivity(),
                        getString(R.string.password_changed_successfully)){
                        findNavController().popBackStack()
                    }
                }
                isObserver = false
            }
        }

    }

    private fun validation():Boolean{
        val oldPassword = binding?.etOldPassword?.text.toString().trim()
        val password = binding?.etPassword?.text.toString().trim()
        val confirmPassword = binding?.etConfirmPassword?.text.toString().trim()

        if (oldPassword.isEmpty()) {
            showToast(getString(R.string.please_enter_old_password))
            return false
        }
        else if (password.isEmpty()) {
            showToast(getString(R.string.please_enter_new_password))
            return false
        }
        else if (confirmPassword.isEmpty() ) {
            showToast(getString(R.string.please_re_enter_confirm_password))
            return false
        }
        else if (password!=confirmPassword) {
            showToast(getString(R.string.new_password_and_confirm_password_didn_t_match))
            return false
        }
        return true
    }
}