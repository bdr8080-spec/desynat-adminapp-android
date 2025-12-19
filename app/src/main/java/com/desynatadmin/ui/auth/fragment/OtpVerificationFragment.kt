package com.desynatadmin.ui.auth.fragment

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.desynatadmin.R
import com.desynatadmin.base.BaseFragment
import com.desynatadmin.data.view_model.AuthViewModel
import com.desynatadmin.databinding.FragmentOtpVerificationBinding
import com.desynatadmin.utils.ProgressDialogUtils
import com.desynatadmin.utils.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OtpVerificationFragment : BaseFragment() {
    private var binding: FragmentOtpVerificationBinding? = null
    private val viewModel: AuthViewModel by viewModels()
    var isObserver = false
    var mobileNumber = ""
    var countryCode = ""
    var email = ""
    var from = ""
    var adminId = ""
    lateinit var timer: CountDownTimer
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOtpVerificationBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initControl()
        observe()
    }

    override fun initView() {
        startTimer()
        email = arguments?.getString("email").toString()
        adminId = arguments?.getString("adminId").toString()

        binding?.apply {
            toolbar.tvToolbar.text = "OTP Verification"
            tvEmail.text = email
        }
    }

    override fun initControl() {
        binding?.apply {
            toolbar.tvToolbar.setOnClickListener {
                findNavController().popBackStack()
            }

            tvResendOtp.setOnClickListener {
                resendOtp()
            }

            btnVerify.setOnClickListener {
                val otp = pinView.text.toString().trim()

                when {
                    otp.isEmpty() -> {
                        showToast("Please enter OTP")
                    }
                    otp.length < 6 -> {
                        showToast("Please enter a valid 6-digit OTP")
                    }
                    else -> {
                        verifyOtp()
                    }
                }
            }
        }

    }

    private fun verifyOtp(){
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
        val map = HashMap<String,Any>()
        map["admin_id"] = adminId
        map["otp"] = binding?.pinView?.text?.trim().toString()
        viewModel.otpVerification(map)
    }

    private fun resendOtp(){
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
        val map = HashMap<String,Any>()
        map["email"] = email
        viewModel.resendOtp(map)
    }

    override fun observe() {
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()

                isObserver = false
            }

        }
        viewModel.otpVerifyResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                response?.let {
                    showToast(response.message)
                    val bundle = Bundle().apply {
                        putString("adminId", adminId)
                    }
                    findNavController().navigate(R.id.action_otpVerificationFragment_to_resetPasswordFragment,bundle)

                }
                isObserver = false
            }
        }

        viewModel.resendOtpResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                showToast("OTP sent to your email address successfully")
                response?.let {
                    startTimer()

                }
                isObserver = false
            }
        }
    }

    private fun startTimer() {
        timer = object : CountDownTimer(90000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val seconds = millisUntilFinished / 1000
                binding?.tvResendOtp?.text = "${seconds} Sec"
                binding?.tvResendOtp?.isClickable = false
            }

            override fun onFinish() {
                binding?.tvResendOtp?.text = getString(R.string.resend_otp)
                binding?.tvResendOtp?.isClickable = true
            }
        }.start()
    }

    /*   private fun startTimer() {
           timer = object : CountDownTimer(60000, 1000) { // 2 minutes = 120000 milliseconds
               override fun onTick(millisUntilFinished: Long) {
                   val minutes = millisUntilFinished / 60000
                   val seconds = (millisUntilFinished % 60000) / 1000
                   binding?.tvResendOtp?.text = makeMeTwoDigits(minutes, seconds)
                   binding?.tvResendOtp?.isClickable = false
               }

               override fun onFinish() {
                   binding?.tvResendOtp?.text = getString(R.string.resend_otp)
                   binding?.tvResendOtp?.isClickable = true
               }
           }.start()
       }

       fun makeMeTwoDigits(minutes: Long, seconds: Long):String {
           val formattedMinutes = if (minutes < 10) "0$minutes" else minutes.toString()
           val formattedSeconds = if (seconds < 10) "0$seconds" else seconds.toString()

           return "${formattedMinutes}:${formattedSeconds}"

       }*/
}