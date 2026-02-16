package com.desynatadmin.ui.home.fragment

import android.app.Dialog
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.desynatadmin.R
import com.desynatadmin.base.BaseFragment
import com.desynatadmin.data.view_model.HomeViewModel
import com.desynatadmin.databinding.ChangeLanguageDialogBinding
import com.desynatadmin.databinding.FragmentHomeBinding
import com.desynatadmin.ui.auth.LoginResponse
import com.desynatadmin.ui.home.HomeActivity
import com.desynatadmin.ui.home.subscriptionPlan.response.GetCountryResponse
import com.desynatadmin.utils.Constants
import com.desynatadmin.utils.ProgressDialogUtils
import com.desynatadmin.utils.SharedPreference
import com.desynatadmin.utils.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BaseFragment() {
    private var binding: FragmentHomeBinding?=null
    private val viewModel: HomeViewModel by viewModels()
    private var isObserver = true
    private var isCountryObserver = true
    var user : LoginResponse.Data?=null
    private var countryList = ArrayList<GetCountryResponse.Data>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater,container,false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initControl()
        observe()
    }

    override fun initView() {
        ProgressDialogUtils.getInstance().hideProgress()
        if(SharedPreference.get(requireActivity()).getCountryList() == null) {
            getCountryApi()
        }
        else{
            countryList.clear()
            SharedPreference.get(requireActivity()).getCountryList()?.let { countryList.addAll(it) }
        }
        user = SharedPreference.get(requireActivity()).getUser()
       binding?.apply {
           tvName.text = "Hi ${user?.fullName}"
       }
    }

    override fun initControl() {
        binding?.apply {
            ivProfileImage.setOnClickListener {
                (requireActivity() as? HomeActivity)?.openDrawer()
            }

            ivNotification.setOnClickListener {
              //  findNavController().navigate(R.id.action_home_to_notificationFragment)
            }

            ivLanguage.setOnClickListener {
               // changeLanguageDialog()
            }
        }
    }

    private fun getCountryApi(){
        viewModel.getCountry()
    }

    private fun setCountry() {
        if (!countryList.isNullOrEmpty()) {
            SharedPreference.get(requireActivity()).saveCountryList(countryList)
        }
    }

    override fun observe() {
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
                ProgressDialogUtils.getInstance().hideProgress()
                isObserver = false
        }

        viewModel.getCountryResult.observe(viewLifecycleOwner) { response ->
                ProgressDialogUtils.getInstance().hideProgress()
                countryList.clear()
                countryList.addAll(response.data)
                setCountry()
        }
    }

    private fun changeLanguageDialog() {
        val dialog = Dialog(requireActivity(), R.style.CustomDialog)
        val dialogBinding = ChangeLanguageDialogBinding.inflate(LayoutInflater.from(context))
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

            Constants.language = SharedPreference.get(requireActivity()).language
            if(Constants.language=="en"){
                cbEnglish.isChecked = true
                cbArabic.isChecked = false
            }
            else if(Constants.language=="ar"){
                cbEnglish.isChecked = false
                cbArabic.isChecked = true
            }

            tvToolbar.setOnClickListener {
                dialog.dismiss()
            }

            cbArabic.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    cbEnglish.isChecked = false
                }
                else {
                    if (!cbEnglish.isChecked) {
                        cbArabic.isChecked = true
                    }
                }
            }

            cbEnglish.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    cbArabic.isChecked = false
                }
                else {
                    if (!cbArabic.isChecked) {
                        cbEnglish.isChecked = true
                    }
                }
            }

            btnSave.setOnClickListener {
                if (cbArabic.isChecked) {
                    Constants.language = Constants.ARABIC
                }
                else if (cbEnglish.isChecked) {
                    Constants.language = Constants.ENGLISH
                }

                if (Constants.language==""){
                    showToast("Please select language")
                }
                else{
                    SharedPreference.get(requireActivity()).language = Constants.language
                    findNavController().navigate(R.id.action_language_to_signinFragment)
                }
            }
        }

        dialog.show()
    }

}