package com.desynatadmin.ui.home.cmsManagement

import android.app.Dialog
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.desynatadmin.R
import com.desynatadmin.base.BaseFragment
import com.desynatadmin.data.view_model.HomeViewModel
import com.desynatadmin.databinding.AddContactDialogBinding
import com.desynatadmin.databinding.FragmentContactUsBinding
import com.desynatadmin.ui.home.cmsManagement.response.GetContactResponse
import com.desynatadmin.utils.ProgressDialogUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class ContactUsFragment : BaseFragment() {
    private var binding: FragmentContactUsBinding? = null
    private val viewModel: HomeViewModel by viewModels()
    private var isObserver = false
    private var userType = 1
    private var contactData: GetContactResponse.Data.ContentEnglish?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentContactUsBinding.inflate(layoutInflater, container, false)
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
            toolbar.tvToolbar.text = "Contact Us"
        }
    }

    override fun initControl() {
        binding?.apply {
            toolbar.tvToolbar.setOnClickListener {
                findNavController().popBackStack()
            }

            ivEditEmail.setOnClickListener {
                updateContactDialog("email")
            }

            ivEditPhone.setOnClickListener {
                updateContactDialog("phone")
            }

            ivEditWhatsAppPhone.setOnClickListener {
                updateContactDialog("whatsapp")
            }

        }

        getContactUs()

    }

    override fun observe() {
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()

                isObserver = false
            }
        }

        viewModel.getContactUsResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                contactData = response.data.contentEnglish
                setContactData()
                isObserver = false
            }
        }

        viewModel.createUpdateContactUsResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                contactData = response.data.contentEnglish
                setContactData()
                isObserver = false
            }
        }
    }

    private fun updateContactDialog(from: String) {
        val dialog = Dialog(requireActivity(), R.style.CustomDialog)
        val dialogBinding = AddContactDialogBinding.inflate(LayoutInflater.from(context))
        dialogBinding.root.let { dialog.setContentView(it) }

        val window: Window = dialog.window!!
        window.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        window.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(false)

        val params: WindowManager.LayoutParams = window.attributes
        params.gravity = Gravity.BOTTOM
        window.attributes = params

        dialogBinding.apply {
            when (from){
                "email"->{
                    tvTitle.text = "Support Email Address"
                    tvToolbar.text = "Edit Support Email Address"
                    clMobile.isVisible = false
                    etEmail.isVisible = true
                    etEmail.setText(binding?.tvSupportEmailAddress?.text?.trim().toString())
                }
                "phone"->{
                    tvTitle.text = "Support Contact Number"
                    tvToolbar.text = "Edit Support Contact Number"
                    clMobile.isVisible = true
                    etEmail.isVisible = false
                    etMobileNumber.setText(binding?.tvSupportMobileNumber?.text?.trim().toString())
                }
                "whatsapp"->{
                    tvTitle.text = "Support Whatsapp Number"
                    tvToolbar.text = "Edit Support Whatsapp Number"
                    clMobile.isVisible = true
                    etEmail.isVisible = false
                    etMobileNumber.setText(binding?.tvSupportWhatsAppMobileNumber?.text?.trim().toString())
                }

            }

            tvToolbar.setOnClickListener {
                dialog.dismiss()
            }

            btnSubmit.setOnClickListener {
                val map = HashMap<String,Any>()
                map["type"] = userType
                when (from) {
                    "email" -> {
                        map["email"] = etEmail.text?.trim().toString()
                        map["mobile"] = contactData?.mobile.toString()
                        map["whatsapp"] = contactData?.whatsapp.toString()
                    }
                    "phone" -> {
                        map["email"] = contactData?.email.toString()
                        map["mobile"] = etMobileNumber.text?.trim().toString()
                        map["whatsapp"] = contactData?.whatsapp.toString()
                    }
                    "whatsapp" -> {
                        map["email"] = contactData?.email.toString()
                        map["mobile"] = contactData?.mobile.toString()
                        map["whatsapp"] = etMobileNumber.text?.trim().toString()
                    }
                }

                createUpdateContactUs(map)

            }

        }

        dialog.show()
    }

    private fun getContactUs(loading:Boolean=true){
        val map = HashMap<String,Any>()
        map["type"] = userType
        viewModel.getContactUs(map)
        if (loading){
            ProgressDialogUtils.getInstance().showProgress(requireActivity())
        }
        isObserver = true
    }

    private fun createUpdateContactUs(map: HashMap<String, Any>) {
        viewModel.createUpdateContactUs(map)
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

    private fun setContactData(){
        binding?.apply {
            tvSupportEmailAddress.text = contactData?.email
            tvSupportMobileNumber.text = contactData?.mobile
            tvSupportWhatsAppMobileNumber.text = contactData?.whatsapp
        }
    }
}
