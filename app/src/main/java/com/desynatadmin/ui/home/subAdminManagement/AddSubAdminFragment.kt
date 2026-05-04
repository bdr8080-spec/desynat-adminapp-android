package com.desynatadmin.ui.home.subAdminManagement

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.desynatadmin.R
import com.desynatadmin.adapter.AccessModuleAdapter
import com.desynatadmin.adapter.AddAccessModuleAdapter
import com.desynatadmin.base.BaseFragment
import com.desynatadmin.data.view_model.HomeViewModel
import com.desynatadmin.databinding.FragmentAddSubAdminBinding
import com.desynatadmin.ui.home.brandManagement.response.GetBrandResponse
import com.desynatadmin.ui.home.subAdminManagement.response.AddSubAdminRequest
import com.desynatadmin.ui.home.subAdminManagement.response.AddSubAdminRequest.Module
import com.desynatadmin.ui.home.subAdminManagement.response.GetSubAdminDetailResponse
import com.desynatadmin.ui.home.subAdminManagement.response.GetSubAdminPanelModuleResponse
import com.desynatadmin.ui.home.subscriptionPlan.response.GetCountryResponse
import com.desynatadmin.utils.CommonMethod
import com.desynatadmin.utils.ProgressDialogUtils
import com.desynatadmin.utils.SharedPreference
import com.desynatadmin.utils.mediaPicker.ImagePicker
import com.desynatadmin.utils.showToast
import com.desynatadmin.utils.togglePasswordVisibility
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.getValue

@AndroidEntryPoint
class AddSubAdminFragment : BaseFragment() {
    private var binding : FragmentAddSubAdminBinding?=null
    private val viewModel: HomeViewModel by activityViewModels()
    private var isObserver = true
    private var isModuleObserver = true

    private var isImageObserver = true

    private var accessModuleAdapter: AddAccessModuleAdapter?=null
    private var previousAccessModuleList = ArrayList<GetSubAdminDetailResponse.Data.PermissionDetail>()
    private var accessModuleList = ArrayList<AddSubAdminRequest.Module>()
    private var profileImage: File? = null
    private var profileImageUrl: String?=null
    private var subAdminData: GetSubAdminDetailResponse.Data?=null

    private var countryList = ArrayList<GetCountryResponse.Data>()
    private var selectedCountryCode: String? = null

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
        binding = FragmentAddSubAdminBinding.inflate(layoutInflater, container,false)
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
        SharedPreference.get(requireActivity()).getCountryList()?.let { countryList.addAll(it) }

        binding?.apply {
            val itemJson = arguments?.getString("subAdminData")
            if (!itemJson.isNullOrEmpty()) {
                subAdminData = Gson().fromJson(itemJson, GetSubAdminDetailResponse.Data::class.java)
                toolbar.tvToolbar.text = getString(R.string.edit)
                tvAdd.text = getString(R.string.save_changes)
                ivProfileImage.isVisible = true
                tvChangeImage.isVisible = true
                ivProfile.isVisible = false
                setData()

            }
            else{
                toolbar.tvToolbar.text = getString(R.string.add_sub_admin)
                tvAdd.text = getString(R.string.add)
                ivProfileImage.isVisible = false
                tvChangeImage.isVisible = false
                ivProfile.isVisible = true

            }
            CommonMethod.applyArabicOnlyFilter(etFullNameArabic)
            CommonMethod.removeSpace(etFullNameEnglish)
            CommonMethod.removeSpace(etEmailID)
            CommonMethod.removeAllTextSpaces(etPassword)
            CommonMethod.removeAllTextSpaces(etConfirmPassword)
            etPassword.togglePasswordVisibility(ivPasswordVisible)
            etConfirmPassword.togglePasswordVisibility(ivConfirmPasswordVisible)

            imagePicker = ImagePicker(
                requireActivity(),
                cameraLauncher,
                galleryLauncher,
                permissionLauncher
            ) { uri ->
                uri?.let { selectedUri ->

                    if (subAdminData != null) {
                        ivProfileImage.setImageURI(selectedUri)
                    } else {
                        ivProfile.setImageURI(selectedUri)
                    }

                    viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {

                        val file = CommonMethod.uriToFile(requireActivity(), selectedUri)

                        withContext(Dispatchers.Main) {
                            profileImage = file
                        }
                    }
                }
            }

            accessModuleAdapter = AddAccessModuleAdapter(
                requireActivity(),
                accessModuleList,
                object : AddAccessModuleAdapter.OnClick {

                    override fun ClickOnItem(position: Int, type: String) {
                        val item = accessModuleList[position]

                        when (type) {

                            "module" -> {
                                item.isSelected = !item.isSelected

                                if (item.isSelected) {
                                    item.canView = 1
                                    item.canAction = 0
                                } else {
                                    item.canView = 0
                                    item.canAction = 0
                                }
                            }

                            "view" -> {
                                if (item.isSelected) {
                                    if (item.canView == 1) {
                                        item.isSelected = false
                                        item.canView = 0
                                        item.canAction = 0
                                    } else {
                                        item.canView = 1
                                        item.canAction = 0
                                    }
                                }
                            }

                            "action" -> {
                                if (item.isSelected) {
                                    item.canAction = if (item.canAction == 1) 0 else 1

                                    if (item.canAction == 1) {
                                        item.canView = 1
                                    }
                                }
                            }
                        }

                        accessModuleAdapter?.notifyItemChanged(position)
                    }
                }
            )
            rvAccessModule.adapter = accessModuleAdapter

            getAdminPanelModule()
        }
    }

    override fun initControl() {
        binding?.apply {
            toolbar.tvToolbar.setOnClickListener {
                findNavController().popBackStack()
            }

            tvChangeImage.setOnClickListener {
                imagePicker.showImageSourceDialog()
            }

            ivProfile.setOnClickListener {
                imagePicker.showImageSourceDialog()
            }

            tvCancel.setOnClickListener {
                findNavController().popBackStack()
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

            etCountry.setOnClickListener {
                CommonMethod.showCountryPicker(requireActivity(), countryList = countryList, anchor = etCountry){ selectedCountry ->
                    etCountry.setText(selectedCountry.name)
                    selectedCountryCode = selectedCountry.id.toString()
                }
            }

            tvAdd.setOnClickListener {
                if (isValid()){
                    if (profileImage!=null){
                        uploadFile()
                    }
                    else{
                      addSubAdmin()
                    }

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
        viewModel.uploadImageResult.observe(viewLifecycleOwner) { response ->
            if (isImageObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                response?.let {
                    profileImageUrl = response.data.url
                    addSubAdmin()
                }
                isImageObserver = false
            }
        }
        viewModel.addSubAdminResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                CommonMethod.showSuccessDialog(requireActivity(),response.message){
                    findNavController().popBackStack()
                }
                isObserver = false
            }
        }
        viewModel.updateSubAdminResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                CommonMethod.showSuccessDialog(requireActivity(),response.message){
                    findNavController().popBackStack()
                }
                isObserver = false
            }
        }

        viewModel.getAdminPanelModuleResult.observe(viewLifecycleOwner) { response ->
            if (isModuleObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                addAccessModuleData(response.data)
                isModuleObserver = false
            }
        }
    }

    private fun addAccessModuleData(data: List<GetSubAdminPanelModuleResponse.Data>) {
        accessModuleList.clear()

        for (module in data) {

            val matched = previousAccessModuleList.find {
                it.moduleId == module.id
            }

            accessModuleList.add(
                AddSubAdminRequest.Module(
                    name = module.displayName,
                    canView = matched?.canView ?: 0,
                    canAction = matched?.canAction ?: 0,
                    moduleId = module.id,
                    isSelected = matched != null
                )
            )
        }

        accessModuleAdapter?.notifyDataSetChanged()
    }

    fun uploadFile(){
        viewModel.uploadImage(profileImage)
        ProgressDialogUtils.getInstance().showProgress(requireActivity(), false)
        isImageObserver = true
    }

    private fun addSubAdmin(){
        val selectedModules = accessModuleList.filter { it.isSelected }
        val request = AddSubAdminRequest(
            id = if(subAdminData!=null) subAdminData?.id.toString() else null,
            confirmPassword = binding?.etConfirmPassword?.text.toString(),
            countryCode = binding?.tvCountryPicker?.text.toString(),
            email = binding?.etEmailID?.text.toString(),
            fullName = binding?.etFullNameEnglish?.text.toString() ,
            fullNameAr = binding?.etFullNameArabic?.text.toString(),
            mobile = binding?.etMobile?.text.toString(),
            password = binding?.etPassword?.text.toString(),
            profilePic = profileImageUrl.toString(),
            countryId = selectedCountryCode.toString(),
            modules = selectedModules
        )
        if(subAdminData!=null){
            viewModel.updateSubAdmin(request)
        }
        else{
            viewModel.addSubAdmin(request)
        }
        ProgressDialogUtils.getInstance().showProgress(requireActivity(), false)
        isObserver = true
    }

    private fun setData() {
        binding?.apply {
            profileImageUrl = subAdminData?.profilePic.toString()
            etEmailID.setText(subAdminData?.email)
            etCountry.setText(subAdminData?.countryInfo?.name)
            selectedCountryCode = subAdminData?.countryInfo?.id.toString()
            etFullNameEnglish.setText(subAdminData?.fullName)
            etFullNameArabic.setText(subAdminData?.fullNameAr)
            etMobile.setText(subAdminData?.mobile)
            tvCountryPicker.text = subAdminData?.countryCode
            etPassword.setText(subAdminData?.unhashedPassword)
            etConfirmPassword.setText(subAdminData?.unhashedPassword)
            Glide.with(requireActivity()).load(subAdminData?.profilePic)
                .placeholder(R.drawable.ic_user).into(ivProfileImage)

            accessModuleList.clear()
            subAdminData?.permissionDetails?.let { previousAccessModuleList.addAll(it) }

        }
    }

    private fun isValid(): Boolean {

        binding?.apply {

            if (profileImage == null&&profileImageUrl==null) {
                showToast(getString(R.string.please_upload_profile_image))
                return false
            }
            if (etFullNameEnglish.text.toString().trim().isEmpty()) {
                showToast(getString(R.string.please_enter_full_name_in_english))
                return false
            }

            if (etFullNameArabic.text.toString().trim().isEmpty()) {
                showToast(getString(R.string.please_enter_full_name_in_arabic))
                return false
            }

            val email = etEmailID.text.toString().trim()
            if (email.isEmpty()) {
                showToast(getString(R.string.please_enter_email_address))
                return false
            }
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                showToast(getString(R.string.please_enter_valid_email_address))
                return false
            }
            val mobile = etMobile.text.toString().trim()
            if (mobile.isEmpty()) {
                showToast(getString(R.string.please_enter_mobile_number))
                return false
            }
            if (mobile.length < 7 ) {
                showToast(getString(R.string.mobile_number_in_between_7_15_digits))
                return false
            }
            if (etCountry.text.toString().trim().isEmpty()) {
                showToast(getString(R.string.please_select_country))
                return false
            }

            val pass = etPassword.text.toString()
            if (pass.isEmpty()) {
                showToast(getString(R.string.please_enter_password))
                return false
            }
         /*   if (pass.length<6) {
                showToast("Password must be 6 digits long")
                return false
            }*/
             else if (!CommonMethod.isValidPassword(pass)) {
           showToast(getString(R.string.password_must_be_at_least_8_characters_long_include_1_uppercase_1_lowercase_and_1_special_character))
           return false
       }

            val cPass = etConfirmPassword.text.toString()
            if (cPass.isEmpty()) {
                showToast(getString(R.string.please_enter_confirm_password))
                return false
            }

            if (pass != cPass) {
                showToast(getString(R.string.password_does_not_match))
                return false
            }
            val selectedModules = accessModuleList.filter { it.isSelected }
            if (selectedModules.isEmpty()) {
                showToast(getString(R.string.please_select_at_least_one_module))
                return false
            }

        }

        return true
    }

    private fun getAdminPanelModule(){
        viewModel.getAdminPanelModule()
        isModuleObserver = true
    }

}