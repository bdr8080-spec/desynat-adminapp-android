package com.desynatadmin.ui.auth.fragment

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import com.desynatadmin.base.BaseFragment
import com.desynatadmin.databinding.FragmentLanguageBinding
import com.desynatadmin.ui.auth.AuthActivity
import com.desynatadmin.utils.CommonMethod
import com.desynatadmin.utils.Constants
import com.desynatadmin.utils.SharedPreference

class LanguageFragment : BaseFragment() {
    private var binding: FragmentLanguageBinding? = null
    private var selectedLanguage = Constants.ARABIC

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLanguageBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initControl()
        observe()
    }

    override fun initView() {
        requestPermissions()

        val savedLanguage = SharedPreference.get(requireActivity()).language

        selectedLanguage = if (savedLanguage.isNullOrEmpty()) {
            Constants.ARABIC
        } else {
            savedLanguage
        }

        updateLanguageUI(selectedLanguage)
    }


    override fun initControl() {
        binding?.apply {

            cbArabic.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked && selectedLanguage != Constants.ARABIC) {
                    selectedLanguage = Constants.ARABIC
                    updateLanguageUI(selectedLanguage)
                }
            }

            cbEnglish.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked && selectedLanguage != Constants.ENGLISH) {
                    selectedLanguage = Constants.ENGLISH
                    updateLanguageUI(selectedLanguage)
                }
            }

            btnContinue.setOnClickListener {
                Constants.language = selectedLanguage

                SharedPreference.get(requireActivity()).language =
                    selectedLanguage

                CommonMethod.setLocale(
                    requireActivity(),
                    selectedLanguage
                )

                val intent = Intent(
                    requireActivity(),
                    AuthActivity::class.java
                ).apply {
                    flags =
                        Intent.FLAG_ACTIVITY_NEW_TASK or
                                Intent.FLAG_ACTIVITY_CLEAR_TASK
                    putExtra("from", "language")
                }

                startActivity(intent)
                requireActivity().finish()
            }
        }
    }


    override fun observe() {

    }

    private val requestAllPermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val notificationGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                permissions[Manifest.permission.POST_NOTIFICATIONS] ?: false
            } else {
                true
            }
            val fineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
            val coarseLocationGranted =
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false

            if (notificationGranted && (fineLocationGranted || coarseLocationGranted)) {

            } else {

            }
        }

    private fun requestPermissions() {
        val permissionsList = mutableListOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionsList.add(Manifest.permission.POST_NOTIFICATIONS)
        }

        requestAllPermissions.launch(permissionsList.toTypedArray())
    }

    //updateLanguageUI
    private fun updateLanguageUI(language: String) {
        binding?.apply {

            // Remove listeners temporarily
            cbArabic.setOnCheckedChangeListener(null)
            cbEnglish.setOnCheckedChangeListener(null)

            if (language == Constants.ARABIC) {
                root.layoutDirection = View.LAYOUT_DIRECTION_RTL
                cbArabic.isChecked = true
                cbEnglish.isChecked = false

                cbArabic.text = "العربية"
                cbEnglish.text = "الإنجليزية"

                tvInfo.text =
                    "اختر اللغة واستمر للحصول على تجربة التطبيق بلغتك المفضلة."

                tvSelectLanguage.text = "اختر اللغة"
                btnContinue.text = "متابعة"

            } else {
                root.layoutDirection = View.LAYOUT_DIRECTION_LTR
                cbArabic.isChecked = false
                cbEnglish.isChecked = true

                cbArabic.text = "Arabic"
                cbEnglish.text = "English"

                tvInfo.text =
                    "Choose the language and continue to get the experience the app in your preferred language."

                tvSelectLanguage.text = "Select language"
                btnContinue.text = "Continue"
            }

            // Add listeners again
            cbArabic.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked && selectedLanguage != Constants.ARABIC) {
                    selectedLanguage = Constants.ARABIC
                    updateLanguageUI(selectedLanguage)
                }
            }

            cbEnglish.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked && selectedLanguage != Constants.ENGLISH) {
                    selectedLanguage = Constants.ENGLISH
                    updateLanguageUI(selectedLanguage)
                }
            }
        }
    }


}