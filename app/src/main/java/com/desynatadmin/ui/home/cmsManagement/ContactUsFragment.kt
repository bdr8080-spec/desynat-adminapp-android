package com.desynatadmin.ui.home.cmsManagement

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.desynatadmin.R
import com.desynatadmin.adapter.CountryPickerAdapter
import com.desynatadmin.base.BaseFragment
import com.desynatadmin.data.view_model.HomeViewModel
import com.desynatadmin.databinding.AddContactDialogBinding
import com.desynatadmin.databinding.CustomCountryPickerBinding
import com.desynatadmin.databinding.FragmentContactUsBinding
import com.desynatadmin.ui.home.cmsManagement.response.GetContactResponse
import com.desynatadmin.ui.home.subscriptionPlan.response.GetCountryResponse
import com.desynatadmin.utils.CommonMethod
import com.desynatadmin.utils.ProgressDialogUtils
import com.desynatadmin.utils.SharedPreference
import com.desynatadmin.utils.showToast
import com.google.i18n.phonenumbers.PhoneNumberUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class ContactUsFragment : BaseFragment() {
    private var binding: FragmentContactUsBinding? = null
    private val viewModel: HomeViewModel by viewModels()
    private var isObserver = false
    private var userType = 1
    private var contactData: GetContactResponse.Data.ContentEnglish?=null
    private var tempContactData: GetContactResponse.Data.ContentEnglish?=null
    private var countryList = ArrayList<GetCountryResponse.Data>()
    private var contactDialog: Dialog?=null

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

            userType = arguments?.getInt("userType")?:1
            countryList.clear()
            SharedPreference.get(requireActivity()).getCountryList()?.let { countryList.addAll(it) }

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
                contactDialog?.dismiss()
                contactData = tempContactData
                tempContactData = null
                setContactData()
                isObserver = false
            }
        }
    }

    private fun updateContactDialog(from: String) {
        contactDialog = Dialog(requireActivity(), R.style.CustomDialog)
        val dialogBinding = AddContactDialogBinding.inflate(LayoutInflater.from(context))
        dialogBinding.root.let { contactDialog?.setContentView(it) }

        val window: Window = contactDialog?.window!!
        window.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        window.setBackgroundDrawableResource(android.R.color.transparent)
        contactDialog?.setCancelable(false)

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
                    tvCountryPicker.setText(getCountryCode(binding?.tvSupportMobileNumber?.text?.trim().toString()))
                    etMobileNumber.setText(getNationalNumber(binding?.tvSupportMobileNumber?.text?.trim().toString()))
                }
                "whatsapp"->{
                    tvTitle.text = "Support Whatsapp Number"
                    tvToolbar.text = "Edit Support Whatsapp Number"
                    clMobile.isVisible = true
                    etEmail.isVisible = false
                    tvCountryPicker.setText(getCountryCode(binding?.tvSupportWhatsAppMobileNumber?.text?.trim().toString()))
                    etMobileNumber.setText(getNationalNumber(binding?.tvSupportWhatsAppMobileNumber?.text?.trim().toString()))
                }

            }

            tvToolbar.setOnClickListener {
                contactDialog?.dismiss()
            }

            tvCountryPicker.setOnClickListener {
                countryPicker(
                    requireContext(),
                    clMobile,
                    countryList,
                    isDialCode = true
                ) { selectedCountry ->
                    tvCountryPicker.text = selectedCountry.dialCode
                }
            }

            btnSubmit.setOnClickListener {

                  val map = HashMap<String,Any>()
                  map["type"] = userType
                  when (from) {
                      "email" -> {
                          val email = etEmail.text.toString().trim()
                          if (email.isEmpty()) {
                              showToast("Please Enter Email Address")
                          }
                          else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                              etEmail.error = "Please Enter Valid Email Address"
                          }
                          else {
                              map["email"] = email
                              map["mobile"] = contactData?.mobile.toString()
                              map["whatsapp"] = contactData?.whatsapp.toString()
                              tempContactData = GetContactResponse.Data.ContentEnglish(
                                  email = etEmail.text?.trim().toString(),
                                  contactData?.mobile.toString(),
                                  contactData?.whatsapp.toString()
                              )
                          }
                      }
                      "phone" -> {
                          val mobile = etMobileNumber.text.toString().trim()
                          if (mobile.isEmpty()) {
                              showToast("Please Enter Mobile Number")
                          }
                          else if (mobile.length < 7 ) {
                              showToast("Mobile Number in Between 7-15 digits ")
                          }
                          else{
                              map["email"] = contactData?.email.toString()
                              map["mobile"] = tvCountryPicker.text.trim().toString()+etMobileNumber.text?.trim().toString()
                              map["whatsapp"] = contactData?.whatsapp.toString()
                              tempContactData = GetContactResponse.Data.ContentEnglish(email = contactData?.email.toString(),tvCountryPicker.text.trim().toString()+etMobileNumber.text?.trim().toString(),contactData?.whatsapp.toString())
                          }
                          }
                      "whatsapp" -> {
                          val mobile = etMobileNumber.text.toString().trim()
                          if (mobile.isEmpty()) {
                              showToast("Please Enter Mobile Number")
                          }
                          else if (mobile.length < 7 ) {
                              showToast("Mobile Number in Between 7-15 digits ")
                          }
                          else {
                              map["email"] = contactData?.email.toString()
                              map["mobile"] = contactData?.mobile.toString()
                              map["whatsapp"] = tvCountryPicker.text.trim()
                                  .toString() + etMobileNumber.text?.trim().toString()
                              tempContactData = GetContactResponse.Data.ContentEnglish(
                                  email = contactData?.email.toString(),
                                  contactData?.mobile.toString(),
                                  tvCountryPicker.text.trim()
                                      .toString() + etMobileNumber.text?.trim().toString()
                              )
                          }
                      }
                  }
                  createUpdateContactUs(map)

            }
        }
        contactDialog?.show()
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

    fun getCountryCode(phone: String): String {
        val phoneUtil = PhoneNumberUtil.getInstance()
        return try {
            val number = phoneUtil.parse(phone, null)
            "+${number.countryCode}"
        } catch (e: Exception) {
            "+965"
        }
    }
    fun getNationalNumber(phone: String): String {
        val phoneUtil = PhoneNumberUtil.getInstance()
        return try {
            val number = phoneUtil.parse(phone, null)
            number.nationalNumber.toString()
        } catch (e: Exception) {
            phone
        }
    }

    fun countryPicker(
        context: Context,
        anchor: View,
        countryList: ArrayList<GetCountryResponse.Data>,
        isDialCode: Boolean = false,
        onCountrySelected: (GetCountryResponse.Data) -> Unit
    ) {
        val binding = CustomCountryPickerBinding.inflate(LayoutInflater.from(context))

        val popupWindow = PopupWindow(
            binding.root,
            anchor.width,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            true
        )

        popupWindow.isOutsideTouchable = true
        popupWindow.elevation = 20f

        val location = IntArray(2)
        anchor.getLocationOnScreen(location)

        binding.root.measure(
            View.MeasureSpec.UNSPECIFIED,
            View.MeasureSpec.UNSPECIFIED
        )
        val popupHeight = binding.root.measuredHeight

        val y = location[1] - popupHeight

        popupWindow.showAtLocation(anchor, Gravity.NO_GRAVITY, location[0], y)

        binding.rvCountry.adapter = CountryPickerAdapter(
            context,
            countryList,
            isDialCode,
            object : CountryPickerAdapter.ClickOnItem {
                override fun onClickItem(position: Int) {
                    onCountrySelected(countryList[position])
                    popupWindow.dismiss()
                }
            }
        )
    }

}
