package com.desynatadmin.ui.home.fullScreenBannerManagement

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
import com.desynatadmin.databinding.FragmentAddFullScreenBannerBinding
import com.desynatadmin.ui.home.fullScreenBannerManagement.response.AddFullScreenBannerRequest
import com.desynatadmin.ui.home.fullScreenBannerManagement.response.GetFullScreenBannerResponse
import com.desynatadmin.ui.home.subscriptionPlan.response.GetCountryResponse
import com.desynatadmin.utils.CommonMethod
import com.desynatadmin.utils.ImagePicker
import com.desynatadmin.utils.ProgressDialogUtils
import com.desynatadmin.utils.SharedPreference
import com.desynatadmin.utils.openCustomDatePicker
import com.desynatadmin.utils.showToast
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

@AndroidEntryPoint
class AddFullScreenBannerFragment : BaseFragment() {
    private var binding: FragmentAddFullScreenBannerBinding?=null
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddFullScreenBannerBinding.inflate(layoutInflater,container,false)
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
                toolbar.tvToolbar.text = "Edit Advertisement"
                btnSubmit.text = "Update"
                setData()

            }
            else{
                toolbar.tvToolbar.text = "Add Advertisement"

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
            toolbar.tvToolbar.setOnClickListener {
                findNavController().popBackStack()
            }

                    etCountry.setOnClickListener {
                        CommonMethod.showCountryPicker(requireActivity(), countryList = countryList, anchor = etCountry){ selectedCountry ->
                            etCountry.setText(selectedCountry.name)
                            selectedCountryCode = selectedCountry.id.toString()
                        }
                    }

                    etStartDate.setOnClickListener {
                        openCustomDatePicker(requireActivity()){date,timestamp->
                            etStartDate.text=date
                            fromTimestamp=timestamp
                            startDate = CommonMethod.formatDate(date,"dd/MM/yyyy","yyyy-MM-dd")
                        }

                    }

                    etEndDate.setOnClickListener {
                        openCustomDatePicker(requireActivity(), minDate = fromTimestamp){date,timestamp->
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
                imagePicker.showImageSourceDialog()
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
        viewModel.uploadImage(image)
        ProgressDialogUtils.getInstance().showProgress(requireActivity(), false)
        isImageObserver = true
    }

    private fun validation(): Boolean {
        binding?.apply {
            if (image==null&&bannerData==null){
                showToast("Please upload banner image")
                return false
            }
            else if (binding?.etTitle?.text?.toString()?.isBlank() == true ) {
                showToast("Please enter title")
                return false
            }
            else if (etCountry.text.toString().isBlank()) {
                showToast("Please select country")
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

            if (!isEndDateValid()) {
                showToast("End date must be after start date")
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


}