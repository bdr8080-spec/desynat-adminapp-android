package com.desynatadmin.ui.home.categorySubCategoryManagement

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
import com.desynatadmin.databinding.FragmentAddSubCategoryBinding
import com.desynatadmin.ui.home.categorySubCategoryManagement.response.GetSubCategoryResponse
import com.desynatadmin.utils.CommonMethod
import com.desynatadmin.utils.ImagePicker
import com.desynatadmin.utils.ProgressDialogUtils
import com.desynatadmin.utils.showToast
import com.desynatadmin.utils.toMultipartPart
import com.desynatadmin.utils.toPlainRequestBody
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.RequestBody
import retrofit2.http.Part
import java.io.File

@AndroidEntryPoint
class AddSubCategoryFragment : BaseFragment() {
    private var binding: FragmentAddSubCategoryBinding?=null
    private val viewModel: HomeViewModel by viewModels()
    private var isObserver = true
    private var subCategoryData: GetSubCategoryResponse.Data.Data?=null
    private var categoryId = ""
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
        binding = FragmentAddSubCategoryBinding.inflate(layoutInflater,container,false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initControl()
        observe()
    }

    override fun initView() {
        val itemJson = arguments?.getString("subCategoryData")
        categoryId = arguments?.getString("categoryId").toString()
        binding?.apply {

            CommonMethod.removeSpace(etArabicSubCategoryName)
            CommonMethod.removeSpace(etEnglishSubCategoryName)
            if (!itemJson.isNullOrEmpty()) {
                subCategoryData = Gson().fromJson(itemJson, GetSubCategoryResponse.Data.Data::class.java)
                toolbar.tvToolbar.text = "Edit Sub-Category"
                btnSubmit.text = "Update"
                setData()

            }
            else{
                toolbar.tvToolbar.text = "Add Sub-Category"

            }

            imagePicker = ImagePicker(
                requireActivity(),
                cameraLauncher,
                galleryLauncher,
                permissionLauncher
            ) { uri ->
                uri?.let {
                    image = CommonMethod.uriToFile(requireActivity(), it)
                    ivSubCategory.setImageURI(it)
                }
            }
        }
    }

    override fun initControl() {
        binding?.apply {
            toolbar.tvToolbar.setOnClickListener {
                findNavController().popBackStack()
            }

            ivSubCategory.setOnClickListener {
                imagePicker.showImageSourceDialog()
            }

            btnSubmit.setOnClickListener {
                if (validation()){
                    if (image!=null){
                        uploadFile()
                    }
                    else{
                        addSubCategory()
                    }

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

        viewModel.uploadImageResult.observe(viewLifecycleOwner) { response ->
            if (isImageObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                response?.let {
                    imageUrl = response.data.url
                    addSubCategory()
                }
                isImageObserver = false
            }
        }

        viewModel.addSubCategoryResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                CommonMethod.showSuccessDialog(requireContext(), "Sub-Category added successfully!"){
                    findNavController().popBackStack()
                }
                isObserver = false
            }
        }

        viewModel.updateSubCategoryResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                CommonMethod.showSuccessDialog(requireContext(), "Sub-Category updated successfully!"){
                    findNavController().popBackStack()
                }
                isObserver = false
            }
        }

    }

    private fun setData(){

        binding?.apply {

            etEnglishSubCategoryName.setText(subCategoryData?.nameEnglish)
            etArabicSubCategoryName.setText(subCategoryData?.nameArabic)
            imageUrl = subCategoryData?.image.toString()
            ivSubCategory.load(subCategoryData?.image) {
                crossfade(true)
                placeholder(R.drawable.ic_placeholder)
                error(R.drawable.ic_placeholder)
            }
        }

    }

    fun addSubCategory(){
        val map = HashMap<String,RequestBody>()
        if (subCategoryData!=null){
            map["subcategory_id"] = subCategoryData?.id.toString().toPlainRequestBody()
        }
        map["category_id"] = categoryId.toPlainRequestBody()
        map["name_arabic"] = binding?.etArabicSubCategoryName?.text?.trim().toString().toPlainRequestBody()
        map["name_english"] = binding?.etEnglishSubCategoryName?.text?.trim().toString().toPlainRequestBody()
        map["image"] = imageUrl.toPlainRequestBody()

        if (subCategoryData!=null){
            viewModel.updateSubCategory(map)
        }
        else{
            viewModel.addSubCategory(map)
        }
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

    fun uploadFile(){
        viewModel.uploadImage(image)
        ProgressDialogUtils.getInstance().showProgress(requireActivity(), false)
        isImageObserver = true
    }

    private fun validation(): Boolean {
        if (image==null&&subCategoryData==null){
            showToast("Please upload sub-category image")
            return false
        }
        else if (binding?.etEnglishSubCategoryName?.text?.toString()?.isBlank() == true ) {
            showToast("Please enter sub-category name in english")
            return false
        }
        else if (binding?.etArabicSubCategoryName?.text?.toString()?.isBlank() == true ) {
            showToast("Please enter sub-category name in arabic")
            return false
        }
        return true
    }

}