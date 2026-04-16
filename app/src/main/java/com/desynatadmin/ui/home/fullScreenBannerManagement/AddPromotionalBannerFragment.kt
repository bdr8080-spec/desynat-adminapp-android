package com.desynatadmin.ui.home.fullScreenBannerManagement

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.PopupMenu
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import coil.load
import com.desynatadmin.R
import com.desynatadmin.base.BaseFragment
import com.desynatadmin.data.view_model.HomeViewModel
import com.desynatadmin.databinding.FragmentAddPromotionalBannerBinding
import com.desynatadmin.ui.home.categorySubCategoryManagement.response.GetCategoryResponse
import com.desynatadmin.ui.home.categorySubCategoryManagement.response.GetSubCategoryResponse
import com.desynatadmin.ui.home.fullScreenBannerManagement.response.AddPromotionalBannerRequest
import com.desynatadmin.ui.home.fullScreenBannerManagement.response.GetPromotionalBannerResponse
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
        private var bannerData: GetPromotionalBannerResponse.Data.Data?=null

    private var countryList = ArrayList<GetCountryResponse.Data>()
    private var selectedCountryCode: String? = null

        var startDate:String?=null
        var endDate:String?=null
        var fromTimestamp=0L
        var toTimestamp=0L
    var days = 0

        private lateinit var imagePicker: ImagePicker
        private var image: File? = null
        private var imageUrl = ""
    private var bannerPosition = ""
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

                if (!itemJson.isNullOrEmpty()) {
                    bannerData = Gson().fromJson(itemJson, GetPromotionalBannerResponse.Data.Data::class.java)
                    toolbar.tvToolbar.text = "Edit Promotional Banner"
                    btnSubmit.text = "Update"
                    setData()

                }
                else{
                    toolbar.tvToolbar.text = "Add Promotional Banner"
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

                etCountry.setOnClickListener {
                    CommonMethod.showAllCountryPicker(requireActivity(), countryList = countryList, anchor = etCountry){ selectedCountry ->
                        etCountry.setText(selectedCountry.name)
                        selectedCountryCode = selectedCountry.id.toString()
                    }
                }

                etPosition.setOnClickListener {
                    showTypeDropdown(it) { value ->
                        when(value) {
                            "Category" -> {
                                bannerPosition = "3"
                                getCategory()
                                tvCategory.isVisible = true
                                rgCategory.isVisible = true

                            }

                            "Home Top" -> {
                                bannerPosition = "1"
                                selectedCategory = null
                                selectedSubCategory = null
                                tvCategory.isVisible = false
                                rgCategory.isVisible = false
                                tvSubCategory.isVisible = false
                                etSubCategory.isVisible = false
                            }

                            "Home Bottom" -> {
                                bannerPosition = "2"
                                selectedCategory = null
                                selectedSubCategory = null
                                tvCategory.isVisible = false
                                rgCategory.isVisible = false
                                tvSubCategory.isVisible = false
                                etSubCategory.isVisible = false
                            }
                        }
                        etPosition.setText(value)
                    }
                }

                etSubCategory.setOnClickListener{
                    showSubCategoryDropdown(etSubCategory)
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

                etStartDate.setOnClickListener {
                    openCustomDatePicker(requireActivity(),
                        Calendar.getInstance().timeInMillis){ date, timestamp->
                        etStartDate.text=date
                        fromTimestamp=timestamp
                        startDate = CommonMethod.formatDate(date,"dd/MM/yyyy","yyyy-MM-dd")
                        binding?.clDays?.isVisible = false
                        days = 0
                    }

                }

                etEndDate.setOnClickListener {
                    openCustomDatePicker(requireActivity(), minDate = if (fromTimestamp!=0L) fromTimestamp else Calendar.getInstance().timeInMillis){date,timestamp->
                        etEndDate.text=date
                        toTimestamp=timestamp
                        endDate = CommonMethod.formatDate(date,"dd/MM/yyyy","yyyy-MM-dd")
                        calculateDays()
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

            viewModel.uploadImageResult.observe(viewLifecycleOwner) { response ->
                if (isImageObserver) {
                    ProgressDialogUtils.getInstance().hideProgress()
                    response?.let {
                        imageUrl = response.data.url
                        addBanner()
                    }
                    isImageObserver = false
                }
            }

            viewModel.addPromotionalBannerResult.observe(viewLifecycleOwner) { response ->
                if (isObserver) {
                    ProgressDialogUtils.getInstance().hideProgress()
                    CommonMethod.showSuccessDialog(requireContext(), "Banner added successfully!"){
                        findNavController().popBackStack()
                    }
                    isObserver = false
                }
            }

            viewModel.updatePromotionalBannerResult.observe(viewLifecycleOwner) { response ->
                if (isObserver) {
                    ProgressDialogUtils.getInstance().hideProgress()
                //    bannerData?.countryId = selectedCountryCode?.toIntOrNull()?:0
                    bannerData?.uploadBanner = response.data.uploadBanner
                    bannerData?.bannerPosition = response.data.bannerPosition
                    bannerData?.startDate = response.data.startDate
                    bannerData?.endDate = response.data.endDate
                    bannerData?.totalDays = response.data.totalDays
                    bannerData?.categoryDetails = selectedCategory?:null
                    bannerData?.subcategoryDetails = selectedSubCategory?:null
                    findNavController().previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("updatedPromotionalBannerData", Gson().toJson(bannerData))
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

                    if (bannerData==null){
                        getCategoryId("Men")
                    }

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

    private fun showSubCategoryDropdown(view: AppCompatEditText) {
        val list = subCategoryList.map { it.nameEnglish }
        val popup = androidx.appcompat.widget.ListPopupWindow(requireActivity())
        popup.anchorView = view
        popup.setAdapter(ArrayAdapter(requireActivity(), android.R.layout.simple_list_item_1, list))
        popup.setOnItemClickListener { _, _, pos, _ ->
            selectedSubCategory = subCategoryList[pos]
            view.setText(subCategoryList[pos].nameEnglish)
            popup.dismiss()
        }
        popup.show()
    }

        private fun setData(){

            binding?.apply {
                clDays.isVisible = true
                tvTotalDays.text = bannerData?.totalDays
                bannerPosition = bannerData?.bannerPosition.toString()
                    when(bannerData?.bannerPosition) {
                        "1" ->  {
                            etPosition.setText("Home Top")

                        }
                        "2" -> etPosition.setText("Home Bottom")
                        "3" -> {
                            selectedCategory = bannerData?.categoryDetails
                            selectedSubCategory = bannerData?.subcategoryDetails
                            getCategory()
                            getSubCategory()
                            etPosition.setText("Category")
                                tvCategory.isVisible = true
                                rgCategory.isVisible = true
                                etSubCategory.isVisible = true
                                tvSubCategory.isVisible = true
                                etSubCategory.setText(bannerData?.subcategoryDetails?.nameEnglish)
                            when (bannerData?.categoryDetails?.name) {
                                "Women" -> {
                                    rbWomen.isChecked = true
                                    rbMen.isChecked = false
                                    rbKids.isChecked = false
                                }
                                "Men" -> {
                                    rbWomen.isChecked = false
                                    rbMen.isChecked = true
                                    rbKids.isChecked = false
                                }
                                "Kids"-> {
                                    rbWomen.isChecked = false
                                    rbMen.isChecked = false
                                    rbKids.isChecked = true
                                }
                            }

                        }
                    }

                selectedCountryCode = bannerData?.countryId.toString()
                if(selectedCountryCode!=null){
                    etCountry.setText(countryList.firstOrNull { it.id.toString() == selectedCountryCode }?.name)
                }
                if (image==null) {
                    imageUrl = bannerData?.uploadBanner.toString()
                }
                ivBanner.load(bannerData?.uploadBanner) {
                    crossfade(true)
                    placeholder(R.drawable.ic_placeholder)
                    error(R.drawable.ic_placeholder)
                }
                startDate = bannerData?.startDate.toString()
                endDate = bannerData?.endDate.toString()
                if (startDate!=null){
                    etStartDate.text=CommonMethod.formatDate(startDate.toString(),"yyyy-MM-dd","dd/MM/yyyy")
                }
                if (endDate!=null){
                    etEndDate.text=CommonMethod.formatDate(endDate.toString(),"yyyy-MM-dd","dd/MM/yyyy")
                }
            }

        }

        private fun addBanner(){
            val request = AddPromotionalBannerRequest(
              //  countryId = selectedCountryCode.toString(),
                bannerPosition = bannerPosition,
                startDate = startDate.toString(),
                endDate = endDate.toString(),
                uploadBanner = imageUrl,
                totalDays = days.toString(),
                bannerId = if (bannerData != null) bannerData?.id.toString() else null,
                category = if (bannerPosition=="3") selectedCategory?.id.toString() else null,
                subcategory = if (bannerPosition=="3") selectedSubCategory?.id else null
            )
            if (bannerData!=null){
                viewModel.updatePromotionalBanner(request)
            }
            else{
                viewModel.addPromotionalBanner(request)
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
            Log.e("image", "validation: ${image}")
            binding?.apply {
                if(image==null&&bannerData==null){
                    showToast("Please upload banner image")
                    return false
                }
              /*  else if(etCountry.text.toString().isBlank()) {
                    showToast("Please select country")
                    return false
                }*/
                else if(etPosition.text.toString().trim().isEmpty()) {
                    showToast("Please select position")
                    return false
                }
                else if(etStartDate.text.toString().trim().isEmpty()) {
                    showToast("Please select start date")
                    return false
                }

                else if(etEndDate.text.toString().trim().isEmpty()) {
                    showToast("Please select end date")
                    return false
                }

                else if(!isEndDateValid()) {
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

    private fun calculateDays() {
        if (fromTimestamp > 0 && toTimestamp > 0 && toTimestamp >= fromTimestamp) {
            val diff = toTimestamp - fromTimestamp
            days = ((diff / (1000 * 60 * 60 * 24)) + 1).toInt()
            if (days<10){
                binding?.tvTotalDays?.text = "0"+days.toString()
            }
            else{
                binding?.tvTotalDays?.text = days.toString()
            }

            binding?.clDays?.isVisible = true
        }
    }

    private fun setCountry() {
        countryList.clear()
        SharedPreference.get(requireActivity()).getUserCountryList()?.let { countryList.addAll(it) }
    }

    }

