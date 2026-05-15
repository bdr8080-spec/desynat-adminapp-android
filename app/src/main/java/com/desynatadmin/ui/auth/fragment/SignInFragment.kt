package com.desynatadmin.ui.auth.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.desynatadmin.R
import com.desynatadmin.base.BaseFragment
import com.desynatadmin.data.view_model.AuthViewModel
import com.desynatadmin.data.view_model.HomeViewModel
import com.desynatadmin.databinding.FragmentSignInBinding
import com.desynatadmin.ui.home.HomeActivity
import com.desynatadmin.ui.home.SubAdminHomeActivity
import com.desynatadmin.utils.CommonMethod
import com.desynatadmin.utils.Constants
import com.desynatadmin.utils.ProgressDialogUtils
import com.desynatadmin.utils.SharedPreference
import com.desynatadmin.utils.showToast
import com.desynatadmin.utils.togglePasswordVisibility
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import kotlin.text.get

@AndroidEntryPoint
class SignInFragment : BaseFragment() {
    private var binding: FragmentSignInBinding?=null
    private val viewModel: AuthViewModel by viewModels()
    private val homeViewModel: HomeViewModel by viewModels()
    var isObserver = false
    var isSubAdminObserver = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentSignInBinding.inflate(inflater,container,false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.e("FCM_ERROR", "Token failed", task.exception)
                return@addOnCompleteListener
            }

            val token = task.result
            Log.d("FCM_TOKEN", token)
            SharedPreference.get(requireActivity()).deviceToken = token
        }
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
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
        val map = HashMap<String,Any>()
        map["email"] = binding?.etEmail?.text?.trim().toString()
        map["password"] = binding?.etPassword?.text?.trim().toString()
        map["device_type"] = Constants.DEVICE_TYPE
        map["fcm_token"] = SharedPreference.get(requireActivity()).deviceToken
        map["lang"] = SharedPreference.get(requireActivity()).language
        viewModel.login(map)
    }

    override fun observe() {
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()

                isObserver = false
            }
        }

        viewModel.loginResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {

                response?.let {
                    showToast(response.message)
                    SharedPreference.get(requireActivity()).accessToken = response.data.accessToken
                    SharedPreference.get(requireActivity()).saveUser(response.data)
                    if (response.data.type=="2"){
                        requireActivity().startActivity(
                            Intent(requireActivity(),
                                SubAdminHomeActivity::class.java)
                        )
                        requireActivity().finish()
                    }
                    else{
                        ProgressDialogUtils.getInstance().hideProgress()
                        requireActivity().startActivity(
                            Intent(requireActivity(),
                                HomeActivity::class.java)
                        )
                        requireActivity().finish()
                    }

                }
                isObserver = false
            }
        }

        homeViewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (isSubAdminObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                isSubAdminObserver = false
            }
        }

        homeViewModel.getSubAdminDetailResult.observe(viewLifecycleOwner) { response ->
            if (isSubAdminObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                SharedPreference.get(requireActivity()).saveSubAdmin(response.data)
                requireActivity().startActivity(
                    Intent(requireActivity(),
                        SubAdminHomeActivity::class.java)
                )
                requireActivity().finish()
                isSubAdminObserver = false
            }
        }
    }

    private fun validation():Boolean{
        if (binding?.etEmail?.text?.toString()?.isBlank() == true ) {
            showToast(getString(R.string.please_enter_email_address))
            return false
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(binding?.etEmail?.text.toString()).matches()) {
            showToast(getString(R.string.please_enter_valid_email_address))
            return false
        }
        else if (binding?.etPassword?.text?.toString()?.isBlank() == true ) {
            showToast(getString(R.string.please_enter_password))
            return false
        }
        return true
    }

    private fun getSubAdminDetail(id: String){
        val map = HashMap<String,Any>()
        map["id"] = id
        homeViewModel.getSubAdminDetail(map)
        isSubAdminObserver = true

    }

}