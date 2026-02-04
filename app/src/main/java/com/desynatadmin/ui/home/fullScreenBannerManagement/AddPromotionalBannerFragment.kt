package com.desynatadmin.ui.home.fullScreenBannerManagement

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import coil.load
import com.desynatadmin.R
import com.desynatadmin.base.BaseFragment
import com.desynatadmin.data.view_model.HomeViewModel
import com.desynatadmin.databinding.FragmentAddFullScreenBannerBinding
import com.desynatadmin.databinding.FragmentAddPromotionalBannerBinding
import com.desynatadmin.ui.home.categorySubCategoryManagement.response.GetCategoryResponse
import com.desynatadmin.ui.home.categorySubCategoryManagement.response.GetSubCategoryResponse
import com.desynatadmin.ui.home.fullScreenBannerManagement.response.AddFullScreenBannerRequest
import com.desynatadmin.ui.home.fullScreenBannerManagement.response.GetFullScreenBannerResponse
import com.desynatadmin.ui.home.subscriptionPlan.response.GetCountryResponse
import com.desynatadmin.utils.CommonMethod
import com.desynatadmin.utils.ProgressDialogUtils
import com.desynatadmin.utils.SharedPreference
import com.desynatadmin.utils.mediaPicker.ImagePicker
import com.desynatadmin.utils.openCustomDatePicker
import com.desynatadmin.utils.showToast
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.getValue

@AndroidEntryPoint
class AddPromotionalBannerFragment : BaseFragment() {
        private var binding: FragmentAddPromotionalBannerBinding?=null
        private val viewModel: HomeViewModel by viewModels()
        private var isObserver = true

        private var isImageObserver = true

        private var bannerData: GetFullScreenBannerResponse.Data.Data?=null
        private var countryList = ArrayList<GetCountryResponse.Data>()
        private var selectedCountryCode: String? = null
        var startDate:String?=null
        var endDate:String?=null
        var fromTimestamp=0L
        var toTimestamp=0L

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

    private var isSubCategoryObserver = true

    private var categoryList = ArrayList<GetCategoryResponse.Data>()
    private var selectedCategory: GetCategoryResponse.Data? = null

    private var subCategoryList = ArrayList<GetSubCategoryResponse.Data.Data>()
    private var selectedSubCategory: GetSubCategoryResponse.Data.Data? = null

    private var originalCategoryName: String? = null
    private var originalSubCategory: GetSubCategoryResponse.Data.Data? = null

        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            binding = FragmentAddPromotionalBannerBinding.inflate(layoutInflater,container,false)
            return binding?.root
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            initView()
            initControl()
            observe()
        }

        override fun initView() {
            setCountry()
            val itemJson = arguments?.getString("bannerData")
            binding?.apply {

                CommonMethod.removeSpace(etTitle)
                if (!itemJson.isNullOrEmpty()) {
                    bannerData = Gson().fromJson(itemJson, GetFullScreenBannerResponse.Data.Data::class.java)
                    toolbar.tvToolbar.text = "Edit Banner"
                    btnSubmit.text = "Update"
                    setData()

                }
                else{
                    toolbar.tvToolbar.text = "Add Banner"
                }

                imagePicker = ImagePicker(
                    requireActivity(),
                    cameraLauncher,
                    galleryLauncher,
                    permissionLauncher
                ) { uri ->
                    uri?.let {
                        image = CommonMethod.uriToFile(requireActivity(), it)
                        ivBanner.setImageURI(it)
                    }
                }
            }
        }

        override fun initControl() {
            binding?.apply {
                toolbar.ivBack.setOnClickListener {
                    findNavController().popBackStack()
                }

                etPosition.setOnClickListener {
                    showTypeDropdown(it) { value ->
                        if (value=="Category"){
                            getCategory()
                            tvCategory.isVisible = true
                            rgCategory.isVisible = true
                        }
                        else{
                            selectedCategory = null
                            selectedSubCategory = null
                            tvCategory.isVisible = false
                            rgCategory.isVisible = false
                            tvSubCategory.isVisible = false
                            etSubCategory.isVisible = false
                        }
                        etPosition.setText(value)
                    }
                }

                etSubCategory.setOnClickListener{
                   // showMultipleSubCategoryDropdown(etSubCategory)
                }

                rgCategory.setOnCheckedChangeListener { group, checkedId ->
                    when (checkedId) {
                        R.id.rbWomen -> {
                            if (selectedCategory?.name!="Women") {
                                getCategoryId("Women")
                            }
                        }
                        R.id.rbMen -> {
                            if (selectedCategory?.name!="Men") {
                                getCategoryId("Men")
                            }
                        }
                        R.id.rbKids -> {
                            if (selectedCategory?.name!="Kids") {
                                getCategoryId("Kids")
                            }
                        }
                    }
                }

                etCountry.setOnClickListener {
                    CommonMethod.showAllCountryPicker(requireActivity(), countryList = countryList, anchor = etCountry){ selectedCountry ->
                        etCountry.setText(selectedCountry.name)
                        selectedCountryCode = selectedCountry.id.toString()
                    }
                }

                etStartDate.setOnClickListener {
                    openCustomDatePicker(requireActivity(),
                        Calendar.getInstance().timeInMillis){ date, timestamp->
                        etStartDate.text=date
                        fromTimestamp=timestamp
                        startDate = CommonMethod.formatDate(date,"dd/MM/yyyy","yyyy-MM-dd")
                    }

                }

                etEndDate.setOnClickListener {
                    openCustomDatePicker(requireActivity(), minDate = if (fromTimestamp!=0L) fromTimestamp else Calendar.getInstance().timeInMillis){date,timestamp->
                        etEndDate.text=date
                        toTimestamp=timestamp
                        endDate = CommonMethod.formatDate(date,"dd/MM/yyyy","yyyy-MM-dd")
                    }
                }

                btnSubmit.setOnClickListener {
                    if (validation()){
                        if (image!=null){
                            uploadFile()
                        }
                        else{
                            addBanner()
                        }

                    }
                }

                ivBanner.setOnClickListener {
                    imagePicker.openGallery()
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

            viewModel.uploadWithResolutionResult.observe(viewLifecycleOwner) { response ->
                if (isImageObserver) {
                    ProgressDialogUtils.getInstance().hideProgress()
                    response?.let {
                        imageUrl = response.data.url
                        addBanner()
                    }
                    isImageObserver = false
                }
            }

            viewModel.addFullScreenBannerResult.observe(viewLifecycleOwner) { response ->
                if (isObserver) {
                    ProgressDialogUtils.getInstance().hideProgress()
                    CommonMethod.showSuccessDialog(requireContext(), "Banner added successfully!"){
                        findNavController().popBackStack()
                    }
                    isObserver = false
                }
            }

            viewModel.updateFullScreenBannerResult.observe(viewLifecycleOwner) { response ->
                if (isObserver) {
                    ProgressDialogUtils.getInstance().hideProgress()
                    findNavController().previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("updatedBannerData", Gson().toJson(response.data))
                    CommonMethod.showSuccessDialog(requireContext(), response.message){
                        findNavController().popBackStack()
                    }
                    isObserver = false
                }
            }

            viewModel.getAllCategoryResult.observe(viewLifecycleOwner) { response ->
                if (isObserver) {
                    ProgressDialogUtils.getInstance().hideProgress()
                    categoryList.clear()
                    categoryList.addAll(response.data)
                    binding?.tvSubCategory?.isVisible = true
                    binding?.etSubCategory?.isVisible = true

//                    if (dealData==null){
//                        getCategoryId("Men")
//                    }

                    isObserver = false
                }
            }

            viewModel.getAllSubCategoryResult.observe(viewLifecycleOwner) { response ->
                if (isSubCategoryObserver) {
                    ProgressDialogUtils.getInstance().hideProgress()

                    subCategoryList.clear()
                    response.data.data?.let { subCategoryList.addAll(it) }
                    isSubCategoryObserver = false
                }
            }

        }

        private fun setCountry() {
            countryList.clear()
            SharedPreference.get(requireActivity()).getUserCountryList()?.let { countryList.addAll(it) }
        }

        private fun setData(){

            binding?.apply {

                etTitle.setText(bannerData?.title)
                if (image==null) {
                    imageUrl = bannerData?.image.toString()
                }
                ivBanner.load(bannerData?.image) {
                    crossfade(true)
                    placeholder(R.drawable.ic_placeholder)
                    error(R.drawable.ic_placeholder)
                }
                selectedCountryCode = bannerData?.countryId.toString()
                startDate = bannerData?.startDate.toString()
                endDate = bannerData?.endDate.toString()
                if(selectedCountryCode!=null){
                    etCountry.setText(countryList.firstOrNull { it.id.toString() == selectedCountryCode }?.name)
                }
                if (startDate!=null){
                    etStartDate.text=CommonMethod.formatDate(startDate.toString(),"yyyy-MM-dd","dd/MM/yyyy")
                }
                if (endDate!=null){
                    etEndDate.text=CommonMethod.formatDate(endDate.toString(),"yyyy-MM-dd","dd/MM/yyyy")
                }
            }

        }

        private fun addBanner(){
            val request = AddFullScreenBannerRequest(
                countryId = selectedCountryCode.toString(),
                type = if (binding?.etPosition?.text?.trim().toString()=="Web") "2" else "1",
                startDate = startDate.toString(),
                endDate = endDate.toString(),
                image = imageUrl,
                title = binding?.etTitle?.text.toString(),
                bannerId = if(bannerData!=null) bannerData?.id.toString() else null
            )
            if (bannerData!=null){
                viewModel.updateFullScreenBanner(request)
            }
            else{
                viewModel.addFullScreenBanner(request)
            }

            ProgressDialogUtils.getInstance().showProgress(requireActivity())
            isObserver = true
        }

        fun uploadFile(){
            viewModel.uploadWithResolution(image,if(binding?.etPosition?.text?.trim().toString()=="Web") "2" else "1")
            ProgressDialogUtils.getInstance().showProgress(requireActivity(), false)
            isImageObserver = true
        }

        private fun validation(): Boolean {
            Log.e("image", "validation: ${image}")
            binding?.apply {
                if (image==null&&bannerData==null){
                    showToast("Please upload banner image")
                    return false
                }
                else if (image != null) {
                    val type = binding?.etPosition?.text.toString()
                    if (!isGif(image!!)) {
                        if (!validateBannerImage(image!!, type)) {
                            showToast(
                                if (type == "Mobile")
                                //  "Please upload 9:16 image (720x1280 to 1440x2560)"
                                    "Please upload 9:16 image"
                                else
                                // "Please upload 16:9 image (1280x720 to 2560x1440)"
                                    "Please upload 16:9 image"
                            )
                            return false
                        }
                    }
                }


                else if (binding?.etTitle?.text?.toString()?.isBlank() == true ) {
                    showToast("Please enter title")
                    return false
                }
                else if (etCountry.text.toString().isBlank()) {
                    showToast("Please select country")
                    return false
                }
                else if (etPosition.text.toString().trim().isEmpty()) {
                    showToast("Please select position")
                    return false
                }
                else if (etStartDate.text.toString().trim().isEmpty()) {
                    showToast("Please select start date")
                    return false
                }

                else if (etEndDate.text.toString().trim().isEmpty()) {
                    showToast("Please select end date")
                    return false
                }

                else if (!isEndDateValid()) {
                    showToast("From Date should be smaller then to date")
                    return false
                }

            }
            return true
        }


        private fun isEndDateValid(): Boolean {
            return try {
                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val start = sdf.parse(binding?.etStartDate?.text.toString())
                val end = sdf.parse(binding?.etEndDate?.text.toString())
                end != null && start != null && end >= start
            } catch (e: Exception) {
                false
            }
        }

        private fun showTypeDropdown(view: View, onSelect: (String) -> Unit) {
            val popup = PopupMenu(requireContext(), view)
                popup.menu.add("Home Top")
                popup.menu.add("Home Bottom")
                popup.menu.add("Category")

            popup.setOnMenuItemClickListener {
                onSelect(it.title.toString())
                true
            }
            popup.show()
        }

        fun isGif(file: File): Boolean {
            return file.extension.equals("gif", ignoreCase = true)
        }

        data class ImageMeta(
            val width: Int,
            val height: Int,
            val ratio: Float
        )

        fun getImageMeta(file: File): ImageMeta {
            val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            BitmapFactory.decodeFile(file.absolutePath, options)

            val width = options.outWidth
            val height = options.outHeight
            val ratio = width.toFloat() / height.toFloat()

            return ImageMeta(width, height, ratio)
        }

        fun validateBannerImage(
            file: File,
            type: String
        ): Boolean {

            val meta = getImageMeta(file)

            return if (type == "Mobile") {
                val minW = 720
                val minH = 1280
                val maxW = 1440
                val maxH = 2560
                val expectedRatio = 9f / 16f

                meta.width in minW..maxW &&
                        meta.height in minH..maxH &&
                        kotlin.math.abs(meta.ratio - expectedRatio) < 0.02f

            } else {
                val minW = 1280
                val minH = 720
                val maxW = 2560
                val maxH = 1440
                val expectedRatio = 16f / 9f

                meta.width in minW..maxW &&
                        meta.height in minH..maxH &&
                        kotlin.math.abs(meta.ratio - expectedRatio) < 0.02f
            }
        }

    private fun getCategory(){
        viewModel.getAllCategory()
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

    private fun getSubCategory(){
        viewModel.getAllSUbCategory(selectedCategory?.id.toString(), page = 1)
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isSubCategoryObserver = true
    }

    private fun getCategoryId(match:String){
        if (categoryList.isNotEmpty()){
            for(i in 0 until categoryList.size){
                if (categoryList[i].name==match){
                    selectedCategory = categoryList[i]
                    getSubCategory()
                    Log.e("category", "match: $match" )
                    Log.e("category", "originalCategoryName: $originalCategoryName" )

                    if (match == originalCategoryName) {
                        selectedSubCategory = originalSubCategory
                        binding?.etSubCategory
                            ?.setText(originalSubCategory?.nameEnglish ?: "")
                        Log.e("category", "set: ${originalSubCategory?.nameEnglish}" )
                    } else {
                        clearAllField()
                        selectedSubCategory = null
                        Log.e("category", "clear: $originalCategoryName" )

                    }

                }
            }
        }
    }

    private fun clearAllField(){
        binding?.apply {
            etSubCategory.setText("")
        }
    }


    }

