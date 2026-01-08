package com.desynatadmin.ui.home.dealsManagement

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatEditText
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import coil.load
import com.desynatadmin.R
import com.desynatadmin.base.BaseFragment
import com.desynatadmin.data.view_model.HomeViewModel
import com.desynatadmin.databinding.FragmentAddDealBinding
import com.desynatadmin.ui.home.categorySubCategoryManagement.response.GetCategoryResponse
import com.desynatadmin.ui.home.categorySubCategoryManagement.response.GetSubCategoryResponse
import com.desynatadmin.ui.home.dealsManagement.response.AddDealRequest
import com.desynatadmin.ui.home.dealsManagement.response.GetActiveDealResponse
import com.desynatadmin.utils.CommonMethod
import com.desynatadmin.utils.mediaPicker.ImagePicker
import com.desynatadmin.utils.ProgressDialogUtils
import com.desynatadmin.utils.openCustomDatePicker
import com.desynatadmin.utils.showToast
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class AddDealFragment : BaseFragment() {
    private var binding: FragmentAddDealBinding? = null
    private val viewModel: HomeViewModel by viewModels()
    private var isObserver = true
    private var isSubCategoryObserver = true

    private var dealData: GetActiveDealResponse.Data.Data?=null

    private var categoryList = ArrayList<GetCategoryResponse.Data>()
    private var selectedCategory: GetCategoryResponse.Data? = null

    private var subCategoryList = ArrayList<GetSubCategoryResponse.Data.Data>()
    private var selectedSubCategory: GetSubCategoryResponse.Data.Data? = null
    var startDate:String?=null
    var endDate:String?=null
    var fromTimestamp=0L
    var toTimestamp=0L

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
        binding = FragmentAddDealBinding.inflate(layoutInflater, container, false)
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
        val itemJson = arguments?.getString("dealsData")
        binding?.apply {

            CommonMethod.applyArabicOnlyFilter(etArabicFullName)
            CommonMethod.removeSpace(etEnglishFullName)

            if (!itemJson.isNullOrEmpty()) {
                dealData = Gson().fromJson(itemJson, GetActiveDealResponse.Data.Data::class.java)
                toolbar.tvToolbar.text = "Edit"
                btnSubmit.text = "Save Changes"
                setData()

            } else {
                toolbar.tvToolbar.text = "Add Deal"
                btnSubmit.text = "Submit"
            }

            val percentageWatcher = object : TextWatcher {

                private var isEditing = false

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    if (isEditing) return
                    isEditing = true

                    val value = s.toString()

                    if (value.isNotEmpty()) {
                        val number = value.toDoubleOrNull() ?: 0.0

                        if (number > 100) {
                            val corrected = value.take(2)
                            s?.replace(0, s.length, corrected)
                        }
                    }

                    isEditing = false
                }
            }

            etDiscount.addTextChangedListener(percentageWatcher)

            imagePicker = ImagePicker(
                requireActivity(),
                cameraLauncher,
                galleryLauncher,
                permissionLauncher
            ) { uri ->
                uri?.let {
                    image = CommonMethod.uriToFile(requireActivity(), it)
                    ivDeal.setImageURI(it)
                }
            }
        }
        getCategory()
    }

    private fun setData() {
        binding?.apply {
            etEnglishFullName.setText(dealData?.nameEnglish)
            etArabicFullName.setText(dealData?.nameArabic)
            etSubCategory.setText(dealData?.subCategoryDetails?.nameEnglish)
            etDiscount.setText(dealData?.discount)
            etFromDate.setText(CommonMethod.customizeChangeDateFormat(dealData?.from.toString(), inputFormat = "yyyy-MM-dd", outputFormat = "dd/MM/yyyy"))
            etToDate.setText(CommonMethod.customizeChangeDateFormat(dealData?.to.toString(), inputFormat = "yyyy-MM-dd", outputFormat = "dd/MM/yyyy"))

            if (image==null){
                imageUrl = dealData?.image.toString()
                ivDeal.load(dealData?.image) {
                    crossfade(true)
                    placeholder(R.drawable.ic_upload_brand)
                    error(R.drawable.ic_upload_brand)
                }
                selectedCategory = dealData?.categoryDetails
                selectedSubCategory = dealData?.subCategoryDetails
                getSubCategory()
                when (dealData?.categoryDetails?.name) {
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
    }

    override fun initControl() {
        binding?.apply {
            toolbar.ivBack.setOnClickListener {
                findNavController().popBackStack()
            }

            ivDeal.setOnClickListener{
                imagePicker.showImageSourceDialog()
            }

            etSubCategory.setOnClickListener{
                showSubCategoryDropdown(etSubCategory)
            }

            etFromDate.setOnClickListener{
                openCustomDatePicker(requireActivity(), Calendar.getInstance().timeInMillis){ date, timestamp->
                    etFromDate.setText(date)
                    fromTimestamp=timestamp
                    startDate = CommonMethod.formatDate(date,"dd/MM/yyyy","yyyy-MM-dd")
                }
            }

            etToDate.setOnClickListener{
                openCustomDatePicker(requireActivity(), minDate = fromTimestamp){date,timestamp->
                    etToDate.setText(date)
                    toTimestamp=timestamp
                    endDate = CommonMethod.formatDate(date,"dd/MM/yyyy","yyyy-MM-dd")
                }
            }

            rgCategory.setOnCheckedChangeListener { group, checkedId ->
                when (checkedId) {
                    R.id.rbWomen -> {
                        if (selectedCategory?.name!="Women") {
                            getCategoryId("Women")
                            clearAllField()
                        }
                    }
                    R.id.rbMen -> {
                        if (selectedCategory?.name!="Men") {
                            getCategoryId("Men")
                            clearAllField()
                        }
                    }
                    R.id.rbKids -> {
                        if (selectedCategory?.name!="Kids") {
                            getCategoryId("Kids")
                            clearAllField()
                        }
                    }
                }
            }

            btnSubmit.setOnClickListener {
                if (validation()){
                    if (image!=null){
                        uploadFile()
                    }
                    else{
                        addDeal()
                    }

                }
            }

        }
    }

    override fun observe() {
        viewModel.getAllCategoryResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                 categoryList.clear()
                categoryList.addAll(response.data)
                if (dealData==null){
                    getCategoryId("Men")
                }

                isObserver = false
            }
        }


        viewModel.uploadImageResult.observe(viewLifecycleOwner) { response ->
            if (isImageObserver) {
                response?.let {
                    imageUrl = response.data.url
                    addDeal()
                }
                isImageObserver = false
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

        viewModel.addDealResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                CommonMethod.showSuccessDialog(requireContext(), "Deal added successfully!"){
                    findNavController().popBackStack()
                }
                isObserver = false
            }
        }

        viewModel.updateDealResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                dealData = response.data
                dealData?.categoryDetails = selectedCategory!!
                dealData?.subCategoryDetails = selectedSubCategory!!
                findNavController().previousBackStackEntry
                    ?.savedStateHandle
                    ?.set("updatedDealData", Gson().toJson(dealData))
                CommonMethod.showSuccessDialog(requireContext(), response.message){
                    findNavController().popBackStack()
                }
                isObserver = false
            }
        }
    }


    private fun addDeal(){
        val request = AddDealRequest(
            categoryId = selectedCategory?.id.toString(),
            subCategoryId = selectedSubCategory?.id.toString(),
            discount = binding?.etDiscount?.text.toString(),
            from = startDate.toString(),
            to = endDate.toString(),
            image = imageUrl,
            nameArabic = binding?.etArabicFullName?.text.toString(),
            nameEnglish = binding?.etEnglishFullName?.text.toString(),
            dealId = if(dealData!=null) dealData?.id.toString() else null
        )
        if (dealData!=null){
            viewModel.updateDeal(request)
        }
        else{
            viewModel.addDeal(request)
        }

        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

    fun uploadFile(){
        viewModel.uploadImage(image)
        ProgressDialogUtils.getInstance().showProgress(requireActivity(), false)
        isImageObserver = true
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
                }
            }
        }
    }

    private fun clearAllField(){
        binding?.apply {
            etSubCategory.setText("")
            etEnglishFullName.setText("")
            etArabicFullName.setText("")
            etFromDate.setText("")
            etToDate.setText("")
            etDiscount.setText("")
            imageUrl = ""
            image = null
            startDate = null
            endDate = null
            toTimestamp = 0L
            fromTimestamp = 0L
            ivDeal.setImageResource(R.drawable.ic_upload_brand)
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

    private fun validation(): Boolean {
        binding?.apply {

            if (imageUrl == null) {
                showToast("Please upload image")
                return false
            }

            if (etEnglishFullName.text.toString().trim().isEmpty()) {
                showToast("Please enter full name in English")
                return false
            }

            if (etArabicFullName.text.toString().trim().isEmpty()) {
                showToast("Please enter full name in Arabic")
                return false
            }

            if (rgCategory.checkedRadioButtonId == -1) {
                showToast("Please select category")
                return false
            }

            if (etSubCategory.text.toString().trim().isEmpty()) {
                showToast("Please select sub-category")
                return false
            }

            if (etDiscount.text.toString().trim().isEmpty()) {
                showToast("Please enter discount percentage")
                return false
            }

            val discount = etDiscount.text.toString().toDoubleOrNull()
            if (discount == null || discount <= 0 || discount > 100) {
                showToast("Please enter a valid discount (1–100)")
                return false
            }

            if (etFromDate.text.toString().trim().isEmpty()) {
                showToast("Please select start date")
                return false
            }

            if (etToDate.text.toString().trim().isEmpty()) {
                showToast("Please select end date")
                return false
            }

            if (!isEndDateValid(etFromDate.text.toString(), etToDate.text.toString())) {
                showToast("End date must be after start date")
                return false
            }
        }
        return true
    }

    private fun isEndDateValid(fromDate: String, toDate: String): Boolean {
        return try {
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val start = sdf.parse(fromDate)
            val end = sdf.parse(toDate)
            end?.after(start) == true
        } catch (e: Exception) {
            false
        }
    }

}