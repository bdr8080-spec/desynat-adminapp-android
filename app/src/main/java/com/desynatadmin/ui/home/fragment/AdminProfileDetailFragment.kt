package com.desynatadmin.ui.home.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.desynatadmin.R
import com.desynatadmin.base.BaseFragment
import com.desynatadmin.data.view_model.HomeViewModel
import com.desynatadmin.databinding.FragmentAdminProfileDetailBinding
import com.desynatadmin.ui.auth.LoginResponse
import com.desynatadmin.ui.home.HomeActivity
import com.desynatadmin.ui.home.SubAdminHomeActivity
import com.desynatadmin.ui.home.subscriptionPlan.response.GetCountryResponse
import com.desynatadmin.utils.CommonMethod
import com.desynatadmin.utils.ProgressDialogUtils
import com.desynatadmin.utils.SharedPreference
import com.desynatadmin.utils.mediaPicker.ImagePicker
import com.desynatadmin.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class AdminProfileDetailFragment : BaseFragment() {
    private var binding: FragmentAdminProfileDetailBinding? = null
    private val viewModel: HomeViewModel by viewModels()
    private var isObserver = true

    private var profileInfo: LoginResponse.Data? = null

    private var isImageObserver = true

    var profileImage: String? = null
    var profileImageFile: File? = null
    private var countryList = ArrayList<GetCountryResponse.Data>()

    private lateinit var imagePicker: ImagePicker

    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            imagePicker.onCameraResult(success)
        }

    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            imagePicker.onGalleryResult(uri)
        }

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            imagePicker.onPermissionResult(granted)
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAdminProfileDetailBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initControl()
        observe()
    }

    override fun initView() {
        profileInfo = SharedPreference.get(requireActivity()).getUser()
        setData()
        SharedPreference.get(requireActivity()).getCountryList()?.let { countryList.addAll(it) }
        ProgressDialogUtils.getInstance().hideProgress()
        binding?.apply {
            toolbar.tvToolbar.text = "Profile Details"

            CommonMethod.removeSpace(etFullName)
            CommonMethod.removeAllTextSpaces(etEmail)

            val userInfo = SharedPreference.get(requireActivity()).getUser()
            if (userInfo?.type=="2"){
                val subAdminInfo = SharedPreference.get(requireActivity()).getSubAdmin()
                val module = subAdminInfo?.permissionDetails
                    ?.firstOrNull {
                        it.moduleDetails.name.equals("account_setting", true)
                    }

                val actionAccess = module?.canAction == 1

                btnSaveChanges.isVisible = actionAccess
                btnChangePassword.isVisible = actionAccess
                tvChangeImage.isVisible = actionAccess
            }

            imagePicker = ImagePicker(
                requireActivity(),
                cameraLauncher,
                galleryLauncher,
                permissionLauncher
            ) { uri ->
                uri?.let {
                    ivProfileImage.setImageURI(it)
                    profileImageFile = CommonMethod.uriToFile(requireActivity(), it)
                }
            }

        }
    }

    private fun setData() {
        binding?.apply {
            profileImage = profileInfo?.profilePic
            etEmail.setText(profileInfo?.email)
            etFullName.setText(profileInfo?.fullName)
            etMobileNumber.setText(profileInfo?.mobile)
            tvCountryPicker.setText(profileInfo?.countryCode)
            Glide.with(requireActivity()).load(profileInfo?.profilePic)
                .placeholder(R.drawable.ic_user).into(ivProfileImage)
        }
    }

    override fun initControl() {
        binding?.apply {

    toolbar.ivBack.setOnClickListener {
    when (val activity = requireActivity()) {
        is HomeActivity -> activity.onBackPressed()
        is SubAdminHomeActivity -> activity.onBackPressed()
        else -> activity.onBackPressed()
    }
}


            tvCountryPicker.setOnClickListener {
                CommonMethod.showCountryPicker(
                    requireActivity(),
                    clMobile,
                    countryList,
                    isDialCode = true
                ) { selectedCountry ->
                    tvCountryPicker.text = selectedCountry.dialCode
                }
            }

            ivProfileImage.setOnClickListener {
                if (profileInfo?.profilePic != null) {
                    CommonMethod.imagePreviewDialog(
                        requireActivity(),
                        profileInfo?.profilePic.toString()
                    )
                }
            }

            tvChangeImage.setOnClickListener {
                imagePicker.showImageSourceDialog()
            }

            btnSaveChanges.setOnClickListener {
                if (isValid()) {
                    if (profileImageFile != null) {
                        uploadFile(profileImageFile!!)
                    } else {
                        updateProfile()
                    }
                }
            }

            btnChangePassword.setOnClickListener {
                findNavController().navigate(R.id.action_adminProfile_to_subcategoryFragment)
            }
        }
    }

    override fun observe() {
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (isObserver || isImageObserver) {
                ProgressDialogUtils.getInstance().hideProgress()

                isObserver = false
                isImageObserver = false
            }
        }
        viewModel.uploadImageResult.observe(viewLifecycleOwner) { response ->
            if (isImageObserver) {
                response?.let {
                    profileImage = response.data.url
                    updateProfile()
                }
                isImageObserver = false
            }
        }
        viewModel.accountSettingResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                response?.let {
                    SharedPreference.get(requireActivity()).saveUser(response.data)
                    when (val activity = requireActivity()) {
                        is HomeActivity -> activity.setDrawerData(SharedPreference.get(requireActivity()).getUser())
                        is SubAdminHomeActivity -> activity.setDrawerData(SharedPreference.get(requireActivity()).getUser())
                    }
                    CommonMethod.showSuccessDialog(
                        requireContext(),
                        "Profile updated successfully!"
                    ) {
                         when (val activity = requireActivity()) {
        is HomeActivity -> activity.onBackPressed()
        is SubAdminHomeActivity -> activity.onBackPressed()
        else -> activity.onBackPressed()
    }
                    }
                }
                isObserver = false
            }
        }
    }

    private fun updateProfile() {
        val map = HashMap<String, Any>()
        map["profile_pic"] = profileImage.toString()
        map["email"] = binding?.etEmail?.text.toString().trim()
        map["country_code"] = binding?.tvCountryPicker?.text.toString().trim()
        map["mobile"] = binding?.etMobileNumber?.text.toString().trim()
        map["full_name"] = binding?.etFullName?.text.toString().trim()
        viewModel.accountSetting(map)

        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

    fun uploadFile(image: File) {
        Log.e("imageupload", "uploadFile: $image")
        viewModel.uploadImage(image)
        ProgressDialogUtils.getInstance().showProgress(requireActivity(), false)
        isImageObserver = true
    }

    private fun isValid(): Boolean {

        binding?.apply {
            if (etFullName.text.toString().trim().isEmpty()) {
                showToast("Please Enter Full Name")
                return false
            }

            val mobile = etMobileNumber.text.toString().trim()
            if (mobile.isEmpty()) {
                showToast("Please Enter Mobile Number")
                return false
            }
            if (mobile.length < 7) {
                showToast("Mobile Number in Between 7-15 digits ")
                return false
            }
            val email = etEmail.text.toString().trim()
            if (email.isEmpty()) {
                showToast("Please Enter Email Address")
                return false
            }
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                etEmail.error = "Please Enter Valid Email Address"
                return false
            }
        }
        return true
    }

}