package com.desynatadmin.ui.home.brandManagement

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import coil.load
import com.desynatadmin.R
import com.desynatadmin.base.BaseFragment
import com.desynatadmin.data.view_model.HomeViewModel
import com.desynatadmin.databinding.FragmentAddBrandBinding
import com.desynatadmin.ui.home.brandManagement.response.GetBrandResponse
import com.desynatadmin.utils.CommonMethod
import com.desynatadmin.utils.mediaPicker.ImagePicker
import com.desynatadmin.utils.ProgressDialogUtils
import com.desynatadmin.utils.showToast
import com.desynatadmin.utils.toPlainRequestBody
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.RequestBody
import java.io.File

@AndroidEntryPoint
class AddBrandFragment : BaseFragment() {
    private var binding: FragmentAddBrandBinding?=null
    private val viewModel: HomeViewModel by viewModels()
    private var brandData: GetBrandResponse.Data.Data?=null
    private var isObserver = true
    private var isImageObserver = true

    private lateinit var imagePicker: ImagePicker
    private var image: File? = null
    private var imageUrl = ""
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
        binding = FragmentAddBrandBinding.inflate(layoutInflater,container,false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initControl()
        observe()
    }

    override fun initView() {
        val itemJson = arguments?.getString("brandData")
        binding?.apply {

            CommonMethod.applyArabicOnlyFilter(etArabicBrandName)
            CommonMethod.removeSpace(etEnglishBrandName)
            if (!itemJson.isNullOrEmpty()) {
                brandData = Gson().fromJson(itemJson, GetBrandResponse.Data.Data::class.java)
                toolbar.tvToolbar.text = "Edit Brand"
                btnSubmit.text = "Update"
                setData()

            }
            else{
                toolbar.tvToolbar.text = "Add New Brand"

            }

            imagePicker = ImagePicker(
                requireActivity(),
                cameraLauncher,
                galleryLauncher,
                permissionLauncher
            ) { uri ->
                uri?.let {
                    image = CommonMethod.uriToFile(requireActivity(), it)
                    ivBrand.setImageURI(it)
                }
            }
        }
    }

    override fun initControl() {
        binding?.apply {
            toolbar.ivBack.setOnClickListener {
                findNavController().popBackStack()
            }

            btnSubmit.setOnClickListener {
                if (validation()){
                    if (image!=null){
                        uploadFile()
                    }
                    else{
                        addBrand()
                    }

                }
            }

            ivBrand.setOnClickListener {
                imagePicker.showImageSourceDialog()
            }
        }
    }

    private fun validation(): Boolean {
        if (image==null&&brandData==null){
            showToast("Please upload brand image")
            return false
        }
        else if (binding?.etEnglishBrandName?.text?.toString()?.isBlank() == true ) {
            showToast("Please enter brand name in english")
            return false
        }
        else if (binding?.etArabicBrandName?.text?.toString()?.isBlank() == true ) {
            showToast("Please enter brand name in arabic")
            return false
        }
        return true
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
                    imageUrl = response.data.url
                    addBrand()
                }
                isImageObserver = false
            }
        }

        viewModel.addBrandResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                CommonMethod.showSuccessDialog(requireContext(), "Brand added successfully!"){
                    findNavController().popBackStack()
                }
                isObserver = false
            }
        }

        viewModel.updateBrandResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                CommonMethod.showSuccessDialog(requireContext(), "Brand updated successfully!"){
                    findNavController().popBackStack()
                }
                isObserver = false
            }
        }

    }

    private fun setData(){

        binding?.apply {

            etEnglishBrandName.setText(brandData?.nameEnglish)
            etArabicBrandName.setText(brandData?.nameArabic)
            imageUrl = brandData?.image.toString()
            ivBrand.load(brandData?.image) {
                crossfade(true)
                placeholder(R.drawable.ic_placeholder)
                error(R.drawable.ic_placeholder)
            }
        }

    }

    fun addBrand(){
        val map = HashMap<String, RequestBody>()

        if (brandData!=null){
            map["brand_id"] = brandData?.id.toString().toPlainRequestBody()
        }
        map["name_arabic"] = binding?.etArabicBrandName?.text?.trim().toString().toPlainRequestBody()
        map["name_english"] = binding?.etEnglishBrandName?.text?.trim().toString().toPlainRequestBody()
        map["image"] =imageUrl.toPlainRequestBody()
        if (brandData!=null){
            viewModel.updateBrand(map)
        }
        else{
            viewModel.addBrand(map)
        }

        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

    fun uploadFile(){
        viewModel.uploadImage(image)
        ProgressDialogUtils.getInstance().showProgress(requireActivity(), false)
        isImageObserver = true
    }

}