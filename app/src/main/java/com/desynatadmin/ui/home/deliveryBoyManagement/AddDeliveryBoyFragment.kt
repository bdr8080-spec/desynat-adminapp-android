package com.desynatadmin.ui.home.deliveryBoyManagement

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.InputFilter
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import coil.load
import com.desynatadmin.R
import com.desynatadmin.adapter.ZonePickerAdapter
import com.desynatadmin.base.BaseFragment
import com.desynatadmin.data.view_model.HomeViewModel
import com.desynatadmin.databinding.CustomZonePickerBinding
import com.desynatadmin.databinding.FragmentAddDeliveryBoyBinding
import com.desynatadmin.ui.UpdateLocationActivity
import com.desynatadmin.ui.home.deliveryBoyManagement.response.AddDeliveryBoyRequest
import com.desynatadmin.ui.home.deliveryBoyManagement.response.GetAllDeliveryBoyResponse
import com.desynatadmin.ui.home.deliveryBoyManagement.response.GetKuwaitZoneResponse
import com.desynatadmin.ui.home.subscriptionPlan.response.GetCountryResponse
import com.desynatadmin.utils.CommonMethod
import com.desynatadmin.utils.CommonMethod.loadDocument
import com.desynatadmin.utils.Constants
import com.desynatadmin.utils.mediaPicker.FilePickerPicker
import com.desynatadmin.utils.mediaPicker.ImagePicker
import com.desynatadmin.utils.ProgressDialogUtils
import com.desynatadmin.utils.SharedPreference
import com.desynatadmin.utils.showToast
import com.desynatadmin.utils.togglePasswordVisibility
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class AddDeliveryBoyFragment : BaseFragment() {
    private var binding: FragmentAddDeliveryBoyBinding?=null
    private val viewModel: HomeViewModel by viewModels()
    private var isObserver = true
    private var deliveryBoyData: GetAllDeliveryBoyResponse.Data.Data?=null
    var lat:Double=0.0
    var lng:Double=0.0
    var address:String=""
    var zoneId:String?=null
    private var isImageObserver = true
    private lateinit var imagePicker: ImagePicker
    private var kuwaitZoneList = ArrayList<GetKuwaitZoneResponse.Data>()

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

    private lateinit var filePicker: FilePickerPicker
    private val cameraLauncherFile =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            filePicker.onCameraResult(success)
        }

    private val galleryLauncherFile =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            filePicker.onGalleryResult(uri)
        }

    private val permissionLauncherFile =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            filePicker.onPermissionResult(granted)
        }

    private val pdfLauncherFile = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        filePicker.onPdfResult(uri)
    }

    var imageType = 0
    var profileImage:String? = null //imageType = 1
    var vehicleImage1:String? = null //imageType = 2
    var vehicleImage2:String? = null //imageType = 3
    var identityProof1:String? = null //imageType = 4
    var identityProof2:String? = null //imageType = 5
    var drivingLicense1:String? = null //imageType = 6
    var drivingLicense2:String? = null //imageType = 7
    var vehicleInsurance:String? = null //imageType = 8
    var pollutionPaper:String? = null //imageType = 9
    var registrationCertificate1:String? = null //imageType = 10
    var registrationCertificate2:String? = null //imageType = 11

    private var countryList = ArrayList<GetCountryResponse.Data>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddDeliveryBoyBinding.inflate(layoutInflater,container,false)
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
        countryList.clear()
        SharedPreference.get(requireActivity()).getCountryList()?.let { countryList.addAll(it) }

        getKuwaitZone()
            val itemJson = arguments?.getString("deliveryBoyData")
            binding?.apply {
                if (!itemJson.isNullOrEmpty()) {
                    deliveryBoyData = Gson().fromJson(itemJson, GetAllDeliveryBoyResponse.Data.Data::class.java)
                    toolbar.tvToolbar.text = getString(R.string.update_driver_detail)
                    btnAdd.text = getString(R.string.update)
                    setData()
                }
                else{
                    toolbar.tvToolbar.text = getString(R.string.add_new_driver)
                    btnAdd.text = getString(R.string.add)
                }

                etArabicFullName.filters = arrayOf(InputFilter { source, start, end, dest, dstart, dend ->
                    val regex = Regex("[a-zA-Z]")

                    if (source != null && source.contains(regex)) {
                        return@InputFilter ""
                    }

                    if (dstart == 0 && source.startsWith(" ")) {
                        return@InputFilter ""
                    }

                    return@InputFilter null
                })

                CommonMethod.removeSpace(etEnglishFullName)
                CommonMethod.removeAllTextSpaces(etEmail)
                CommonMethod.removeSpace(etAddressLine2)
                CommonMethod.removeSpace(etCity)
                CommonMethod.removeSpace(etState)
                CommonMethod.removeSpace(etVehicleName)
                CommonMethod.removeSpace(etVehichleRegistrationNumber)

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
                    uri?.let {
                        uploadFile(CommonMethod.uriToFile(requireActivity(), it))
                    }
                }

                filePicker = FilePickerPicker(
                    context = requireContext(),
                    cameraLauncher = cameraLauncherFile,
                    galleryLauncher = galleryLauncherFile,
                    filePickerLauncher = pdfLauncherFile,
                    permissionLauncher = permissionLauncherFile,
                ) { uri ->
                    uri?.let {
                        uploadFile(CommonMethod.fileFromUri(requireActivity(), it))
                    }
                }
        }
    }

    override fun initControl() {
        binding?.apply {

            toolbar.ivBack.setOnClickListener {
                findNavController().popBackStack()
            }

            tvCountryPicker.setOnClickListener {
               /* CommonMethod.showCountryPicker(requireActivity(),clMobile,countryList, isDialCode = true){selectedCountry->
                    tvCountryPicker.text = selectedCountry.dialCode
                }*/
            }

            relAddressLine1.setOnClickListener {
                val intent = Intent(requireActivity(), UpdateLocationActivity::class.java)
                intent.putExtra("savedLat", lat)
                intent.putExtra("savedLng", lng)
                intent.putExtra("savedAddress", address)
                Log.e("send", "initControl: $address $lat $lng" )
                locationResultLauncher.launch(intent)
            }

            etZone.setOnClickListener {
                showZoneDropdown(etZone)
            }

            btnAdd.setOnClickListener {
                if (isValid()){
                    if (deliveryBoyData!=null){
                        editDeliveryBoy()
                    }
                    else{
                        addDeliveryBoy()
                    }
                }
            }

            ivProfileImage.setOnClickListener {
                imageType = 1
                imagePicker.showImageSourceDialog()
            }

            ivVehicle1.setOnClickListener {
                imageType = 2
                filePicker.showFileSourceDialog()
            }

            ivVehicle2.setOnClickListener {
                imageType = 3
                filePicker.showFileSourceDialog()
            }

            ivIdentity1.setOnClickListener {
                imageType = 4
                filePicker.showFileSourceDialog()
            }

            ivIdentity2.setOnClickListener {
                imageType = 5
                filePicker.showFileSourceDialog()
            }

            ivDriving1.setOnClickListener {
                imageType = 6
                filePicker.showFileSourceDialog()
            }

            ivDriving2.setOnClickListener {
                imageType = 7
                filePicker.showFileSourceDialog()
            }

            ivVehicleInsurance.setOnClickListener {
                imageType = 8
                filePicker.showFileSourceDialog()
            }

            ivPollutionPaper.setOnClickListener {
                imageType = 9
                filePicker.showFileSourceDialog()
            }

            ivRegistration1.setOnClickListener {
                imageType = 10
                filePicker.showFileSourceDialog()
            }

            ivRegistration2.setOnClickListener {
                imageType =11
                filePicker.showFileSourceDialog()
            }
        }
    }

    override fun observe() {
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (isObserver||isImageObserver) {
                ProgressDialogUtils.getInstance().hideProgress()

                isObserver = false
                isImageObserver = false
            }
        }

        viewModel.kuwaitZoneResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                response?.let {
                    kuwaitZoneList.clear()
                    kuwaitZoneList.addAll(response.data)
                }
                isObserver = false
            }
        }

        viewModel.uploadImageResult.observe(viewLifecycleOwner) { response ->
            if (isImageObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                response?.let {
                    setImageData(response.data.url)
                }
                isImageObserver = false
            }
        }

        viewModel.addDeliveryBoyResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                response?.let {
                    CommonMethod.showSuccessDialog(requireContext(),
                        getString(R.string.delivery_boy_added_successfully)){
                        findNavController().popBackStack()
                    }
                }
                isObserver = false
            }
        }

        viewModel.updateDeliveryBoyResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                response?.let {
                    showToast(response.message)
                    findNavController().popBackStack()
                }
                isObserver = false
            }
        }
    }

    private fun setData(){
        binding?.apply {
            etEnglishFullName.setText(deliveryBoyData?.fullNameEnglish)
            etArabicFullName.setText(deliveryBoyData?.fullNameArabic)
            etEmail.setText(deliveryBoyData?.email)
            tvCountryPicker.text = deliveryBoyData?.countryCode
            etMobileNumber.setText(deliveryBoyData?.mobile)
            etAddressLine1.text = deliveryBoyData?.addressLine1
            etAddressLine2.setText(deliveryBoyData?.addressLine2)
            etCity.setText(deliveryBoyData?.city)
            etState.setText(deliveryBoyData?.state)
            etPassword.setText(deliveryBoyData?.unhashedPassword)
            etConfirmPassword.setText(deliveryBoyData?.unhashedPassword)
            etZone.setText(if (Constants.language=="ar") deliveryBoyData?.zoneInfo?.nameArabic else deliveryBoyData?.zoneInfo?.nameEnglish)

            etVehicleName.setText(deliveryBoyData?.vehicleName)
            etVehichleRegistrationNumber.setText(deliveryBoyData?.vehicleRegNo)

            zoneId = deliveryBoyData?.zoneId
            lat = deliveryBoyData?.let?:0.0
            lng = deliveryBoyData?.long?:0.0
            address = deliveryBoyData?.addressLine1.toString()

            profileImage = deliveryBoyData?.profilePic
            ivProfileImage.load(deliveryBoyData?.profilePic) {
                crossfade(true)
                placeholder(R.drawable.ic_upload_image)
                error(R.drawable.ic_upload_image)
            }

            clPlaceholder.isVisible = false

            drivingLicense1 = deliveryBoyData?.drivingLicense?.getOrNull(0)?.documentPath
            drivingLicense2 = deliveryBoyData?.drivingLicense?.getOrNull(1)?.documentPath

            vehicleImage1 = deliveryBoyData?.vehicleImage?.getOrNull(0)?.documentPath
            vehicleImage2 = deliveryBoyData?.vehicleImage?.getOrNull(1)?.documentPath

            identityProof1 = deliveryBoyData?.identityProof?.getOrNull(0)?.documentPath
            identityProof2 = deliveryBoyData?.identityProof?.getOrNull(1)?.documentPath

            registrationCertificate1 = deliveryBoyData?.registrationCertificate?.getOrNull(0)?.documentPath
            registrationCertificate2 = deliveryBoyData?.registrationCertificate?.getOrNull(1)?.documentPath

            pollutionPaper = deliveryBoyData?.pollutionPaper?.getOrNull(0)?.documentPath
            vehicleInsurance = deliveryBoyData?.vehicleInsurance?.getOrNull(0)?.documentPath



            if (deliveryBoyData?.drivingLicense?.getOrNull(0)?.documentPath!=null){
                loadDocument(ivDriving1, deliveryBoyData?.drivingLicense?.getOrNull(0)?.documentPath)
                clPlaceholderDriving1.isVisible = false
            }
            if (deliveryBoyData?.drivingLicense?.getOrNull(1)?.documentPath!=null){
                loadDocument(ivDriving2, deliveryBoyData?.drivingLicense?.getOrNull(1)?.documentPath)

                clPlaceholderDriving2.isVisible = false
            }

            if (deliveryBoyData?.registrationCertificate?.getOrNull(0)?.documentPath!=null){
                clPlaceholderRegistration1.isVisible = false
                loadDocument(ivRegistration1, deliveryBoyData?.registrationCertificate?.getOrNull(0)?.documentPath)
            }
            if (deliveryBoyData?.registrationCertificate?.getOrNull(1)?.documentPath!=null){
                clPlaceholderRegistration2.isVisible = false
                loadDocument(ivRegistration2, deliveryBoyData?.registrationCertificate?.getOrNull(1)?.documentPath)

            }

            if (deliveryBoyData?.vehicleImage?.getOrNull(0)?.documentPath!=null){
                loadDocument(ivVehicle1, deliveryBoyData?.vehicleImage?.getOrNull(0)?.documentPath)
                clPlaceholderVehicle1.isVisible = false
            }
            if (deliveryBoyData?.vehicleImage?.getOrNull(1)?.documentPath!=null){
                loadDocument(ivVehicle2, deliveryBoyData?.vehicleImage?.getOrNull(1)?.documentPath)
                clPlaceholderVehicle2.isVisible = false
            }

            if (deliveryBoyData?.identityProof?.getOrNull(0)?.documentPath!=null){
                loadDocument(ivIdentity1, deliveryBoyData?.identityProof?.getOrNull(0)?.documentPath)
                clPlaceholderIdentity1.isVisible = false
            }
            if (deliveryBoyData?.identityProof?.getOrNull(1)?.documentPath!=null){
                loadDocument(ivIdentity2, deliveryBoyData?.identityProof?.getOrNull(1)?.documentPath)
                clPlaceholderIdentity2.isVisible = false
            }

            if (deliveryBoyData?.pollutionPaper?.getOrNull(0)?.documentPath!=null){
                loadDocument(ivPollutionPaper, deliveryBoyData?.pollutionPaper?.getOrNull(0)?.documentPath)
                clPlaceholderPollutionPaper.isVisible = false
            }

            if (deliveryBoyData?.vehicleInsurance?.getOrNull(1)?.documentPath!=null){
                loadDocument(ivVehicleInsurance, deliveryBoyData?.vehicleInsurance?.getOrNull(0)?.documentPath)
                clPlaceholderVehicleInsuranc.isVisible = false
            }
        }
    }

    private fun addDeliveryBoy(){
        binding?.apply {

                val request = AddDeliveryBoyRequest(
                    fullNameEnglish = etEnglishFullName.text?.trim().toString(),
                    fullNameArabic = etArabicFullName.text?.trim().toString(),
                    vehicleName =  etVehicleName.text?.trim().toString(),
                    vehicleRegNo =  etVehichleRegistrationNumber.text?.trim().toString(),
                    countryCode = tvCountryPicker.text.toString(),
                    mobile = etMobileNumber.text?.trim().toString(),
                    email = etEmail.text?.trim().toString(),
                    addressLine1 = etAddressLine1.text?.trim().toString(),
                    addressLine2 = etAddressLine2.text?.trim().toString(),
                    city = etCity.text?.trim().toString(),
                    state = etState.text?.trim().toString(),
                    password = etPassword.text?.trim().toString(),
                    confirmPassword = etConfirmPassword.text?.trim().toString(),
                    let = lat,
                    long = lng,
                    zoneId = zoneId,
                    profilePic = profileImage,
                    vehicleImageFront = vehicleImage1,
                    vehicleImageBack = vehicleImage2,
                    identityProofFront = identityProof1,
                    identityProofBack = identityProof2,
                    drivingLicenseFront =  drivingLicense1,
                    drivingLicenseBack = drivingLicense2,
                    vehicleInsurance = vehicleInsurance,
                    pollutionPaper = pollutionPaper,
                    registrationCertificateFront = registrationCertificate1,
                    registrationCertificateBack = registrationCertificate2
                )
                viewModel.addDeliveryBoy(request)

            ProgressDialogUtils.getInstance().showProgress(requireActivity())
            isObserver = true

        }
    }

    private fun editDeliveryBoy(){
        binding?.apply {

            val request = AddDeliveryBoyRequest(
                fullNameEnglish = etEnglishFullName.text?.trim().toString(),
                fullNameArabic = etArabicFullName.text?.trim().toString(),
                vehicleName =  etVehicleName.text?.trim().toString(),
                vehicleRegNo =  etVehichleRegistrationNumber.text?.trim().toString(),
                countryCode = tvCountryPicker.text.toString(),
                mobile = etMobileNumber.text?.trim().toString(),
                email = etEmail.text?.trim().toString(),
                addressLine1 = etAddressLine1.text?.trim().toString(),
                addressLine2 = etAddressLine2.text?.trim().toString(),
                city = etCity.text?.trim().toString(),
                state = etState.text?.trim().toString(),
                password = etPassword.text?.trim().toString(),
                confirmPassword = etConfirmPassword.text?.trim().toString(),
                let = lat,
                long = lng,
                zoneId = zoneId,
                profilePic = profileImage,
                vehicleImageFront = vehicleImage1,
                vehicleImageBack = vehicleImage2,
                identityProofFront = identityProof1,
                identityProofBack = identityProof2,
                drivingLicenseFront =  drivingLicense1,
                drivingLicenseBack = drivingLicense2,
                vehicleInsurance = vehicleInsurance,
                pollutionPaper = pollutionPaper,
                registrationCertificateFront = registrationCertificate1,
                registrationCertificateBack = registrationCertificate2,
                deliveryBoyId = deliveryBoyData?.id.toString())
                viewModel.updateDeliveryBoy(request)
            ProgressDialogUtils.getInstance().showProgress(requireActivity())
            isObserver = true

        }
    }

    fun getKuwaitZone(){
        viewModel.getKuwaitZone()
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

    fun uploadFile(image:File){
        Log.e("imageupload", "uploadFile: $image" )
        viewModel.uploadImage(image)
        ProgressDialogUtils.getInstance().showProgress(requireActivity(), false)
        isImageObserver = true
    }

    fun setImageData(url:String){
        binding?.apply {
            when (imageType) {
                1 -> {
                    clPlaceholder.isVisible = false

                    profileImage = url
                    ivProfileImage.load(url) {
                        crossfade(true)
                        placeholder(R.drawable.ic_placeholder)
                        error(R.drawable.ic_placeholder)
                    }
                }
                2 -> {
                    clPlaceholderVehicle1.isVisible = false

                    vehicleImage1 = url
                    if(url.contains(".pdf")){
                        ivVehicle1.setImageResource(R.drawable.pdf)
                        return
                    }
                    ivVehicle1.load(url) {
                        crossfade(true)
                        placeholder(R.drawable.ic_placeholder)
                        error(R.drawable.ic_placeholder)
                    }
                }
                3 -> {
                    clPlaceholderVehicle2.isVisible = false

                    vehicleImage2 = url
                    if(url.contains(".pdf")){
                        ivVehicle2.setImageResource(R.drawable.pdf)
                        return
                    }
                    ivVehicle2.load(url) {
                        crossfade(true)
                        placeholder(R.drawable.ic_placeholder)
                        error(R.drawable.ic_placeholder)
                    }
                }
                4 -> {
                    clPlaceholderIdentity1.isVisible = false

                    identityProof1 = url
                    if(url.contains(".pdf")){
                        ivIdentity1.setImageResource(R.drawable.pdf)
                        return
                    }
                    ivIdentity1.load(url) {
                        crossfade(true)
                        placeholder(R.drawable.ic_placeholder)
                        error(R.drawable.ic_placeholder)
                    }
                }
                5 -> {
                    clPlaceholderIdentity2.isVisible = false

                    identityProof2 = url
                    if(url.contains(".pdf")){
                        ivIdentity2.setImageResource(R.drawable.pdf)
                        return
                    }
                    ivIdentity2.load(url) {
                        crossfade(true)
                        placeholder(R.drawable.ic_placeholder)
                        error(R.drawable.ic_placeholder)
                    }
                }
                6 -> {
                    clPlaceholderDriving1.isVisible = false

                    drivingLicense1 = url
                    if(url.contains(".pdf")){
                        ivDriving1.setImageResource(R.drawable.pdf)
                        return
                    }
                    ivDriving1.load(url) {
                        crossfade(true)
                        placeholder(R.drawable.ic_placeholder)
                        error(R.drawable.ic_placeholder)
                    }
                }
                7 -> {
                    clPlaceholderDriving2.isVisible = false

                    drivingLicense2 = url
                    if(url.contains(".pdf")){
                        ivDriving2.setImageResource(R.drawable.pdf)
                        return
                    }
                    ivDriving2.load(url) {
                        crossfade(true)
                        placeholder(R.drawable.ic_placeholder)
                        error(R.drawable.ic_placeholder)
                    }
                }
                8 -> {
                    clPlaceholderVehicle1.isVisible = false

                    vehicleInsurance = url
                    if(url.contains(".pdf")){
                        ivVehicleInsurance.setImageResource(R.drawable.pdf)
                        return
                    }
                    ivVehicleInsurance.load(url) {
                        crossfade(true)
                        placeholder(R.drawable.ic_placeholder)
                        error(R.drawable.ic_placeholder)
                    }
                }
                9 -> {
                    clPlaceholderVehicle2.isVisible = false

                    pollutionPaper = url
                    if(url.contains(".pdf")){
                        ivPollutionPaper.setImageResource(R.drawable.pdf)
                        return
                    }
                    ivPollutionPaper.load(url) {
                        crossfade(true)
                        placeholder(R.drawable.ic_placeholder)
                        error(R.drawable.ic_placeholder)
                    }
                }
                10 -> {
                    clPlaceholderRegistration1.isVisible = false

                    registrationCertificate1 = url
                    if(url.contains(".pdf")){
                        ivRegistration1.setImageResource(R.drawable.pdf)
                        return
                    }
                    ivRegistration1.load(url) {
                        crossfade(true)
                        placeholder(R.drawable.ic_placeholder)
                        error(R.drawable.ic_placeholder)
                    }
                }
                11 -> {
                    clPlaceholderRegistration2.isVisible = false
                    registrationCertificate2 = url
                    if(url.contains(".pdf")){
                        ivRegistration2.setImageResource(R.drawable.pdf)
                        return
                    }
                    ivRegistration2.load(url) {
                        crossfade(true)
                        placeholder(R.drawable.ic_placeholder)
                        error(R.drawable.ic_placeholder)
                    }

                }
            }
        }
    }

    private fun isValid(): Boolean {

        binding?.apply {

            if (profileImage == null) {
                showToast(getString(R.string.please_upload_profile_image))
                return false
            }
            if (etEnglishFullName.text.toString().trim().isEmpty()) {
                showToast(getString(R.string.please_enter_full_name_in_english))
                return false
            }

            if (etArabicFullName.text.toString().trim().isEmpty()) {
                showToast(getString(R.string.please_enter_full_name_in_arabic))
                return false
            }

            val mobile = etMobileNumber.text.toString().trim()
            if (mobile.isEmpty()) {
                showToast(getString(R.string.enter_mobile_number))
                return false
            }
            if (mobile.length < 7 ) {
                showToast(getString(R.string.mobile_number_in_between_7_15_digits))
                return false
            }
            val email = etEmail.text.toString().trim()
            if (email.isEmpty()) {
                showToast(getString(R.string.enter_email_address))
                return false
            }
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                showToast(getString(R.string.please_enter_valid_email_address))
                return false
            }
            if (etAddressLine1.text.toString().trim().isEmpty()) {
                showToast(getString(R.string.please_enter_address_line_1))
                return false
            }

            if (etAddressLine2.text.toString().trim().isEmpty()) {
                showToast(getString(R.string.please_enter_address_line_2))
                return false
            }

            if (etCity.text.toString().trim().isEmpty()) {
                showToast(getString(R.string.please_enter_city_district))
                return false
            }

            if (etState.text.toString().trim().isEmpty()) {
                showToast(getString(R.string.please_enter_state))
                return false
            }

            val pass = etPassword.text.toString()
            if (pass.isEmpty()) {
                showToast(getString(R.string.please_enter_password))
                return false
            }
            if (pass.length<6) {
                showToast(getString(R.string.password_must_be_6_digits_long))
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

            if (etZone.text.toString().trim().isEmpty()) {
                showToast(getString(R.string.please_select_zone))
                return false
            }

            if (etVehicleName.text.toString().trim().isEmpty()) {
                showToast(getString(R.string.please_enter_vehicle_name))
                return false
            }

            if (etVehichleRegistrationNumber.text.toString().trim().isEmpty()) {
                showToast(getString(R.string.please_enter_registration_number))
                return false
            }

            if (vehicleImage1 == null) {
                showToast(getString(R.string.please_upload_vehicle_front_image))
                return false
            }
            if (vehicleImage2 == null) {
                showToast(getString(R.string.please_upload_vehicle_back_image))
                return false
            }

            if (identityProof1 == null) {
                showToast(getString(R.string.please_upload_identity_front_image))
                return false
            }
            if (identityProof2 == null) {
                showToast(getString(R.string.please_upload_identity_back_image))
                return false
            }

            if (drivingLicense1 == null) {
                showToast(getString(R.string.please_upload_driving_license_front_image))
                return false
            }
            if (drivingLicense2 == null) {
                showToast(getString(R.string.please_upload_driving_license_back_image))
                return false
            }

            if (vehicleInsurance == null) {
                showToast(getString(R.string.please_upload_vehicle_insurance_document))
                return false
            }
           /* if (pollutionPaper == null) {
                showToast("Upload Pollution Paper Image")
                return false
            }

            if (registrationCertificate1 == null) {
                showToast("Upload Registration Certificate Front Image")
                return false
            }
            if (registrationCertificate2 == null) {
                showToast("Upload Registration Certificate Back Image")
                return false
            }*/

        }

        return true
    }

    private val locationResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            val city = data?.getStringExtra("city") ?: "N/A"
            val street = data?.getStringExtra("street") ?: "N/A"
            val state = data?.getStringExtra("state") ?: "N/A"
            val pincode = data?.getStringExtra("pincode") ?: "N/A"
            val houseno = data?.getStringExtra("houseno") ?: "N/A"
            val addressH = data?.getStringExtra("addressH") ?: "N/A"
            lat = data?.getDoubleExtra("lat", 0.0)?:0.0
            lng = data?.getDoubleExtra("lng", 0.0)?:0.0

            Log.e("send", "getnew: $lat $lng" )

            address = addressH

            val cleaned = cleanAddress(addressH, city, state)

            val (line1, line2) = splitIntoTwoLines(cleaned)

            binding?.etAddressLine1?.setText(line1)
            binding?.etAddressLine2?.setText(line2)

            binding?.etAddressLine1?.setText(line1)
            binding?.etAddressLine2?.setText(line2)

            binding?.etCity?.setText(city)
            binding?.etState?.setText(state)
        }
    }

    private fun showZoneDropdown(view: AppCompatEditText) {
        val list = kuwaitZoneList.map { it.nameEnglish }
        val popup = androidx.appcompat.widget.ListPopupWindow(requireActivity())
        popup.anchorView = view
        popup.setAdapter(ArrayAdapter(requireActivity(), android.R.layout.simple_list_item_1, list))
        popup.setOnItemClickListener { _, _, pos, _ ->
            zoneId = kuwaitZoneList[pos].id.toString()
            view.setText(kuwaitZoneList[pos].nameEnglish)
            popup.dismiss()
        }
        popup.show()
    }

    private fun showZoneDropdownOld(anchor: View) {

        val dropdownBinding = CustomZonePickerBinding.inflate(layoutInflater)

        val popupWindow = PopupWindow(
            dropdownBinding.root,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            true
        )

        popupWindow.isOutsideTouchable = true
        popupWindow.elevation = 20f

        popupWindow.showAsDropDown(anchor)

        dropdownBinding.apply {

            rvZone.adapter = ZonePickerAdapter(requireActivity(),kuwaitZoneList,object :
                ZonePickerAdapter.ClickOnItem{
                override fun onClickItem(position: Int) {
                    binding?.etZone?.setText(kuwaitZoneList[position].nameEnglish)
                    zoneId = kuwaitZoneList[position].id.toString()
                    popupWindow.dismiss()
                }

            })
        }
    }

    private fun cleanAddress(addressH: String, city: String, state: String): String {
        var cleaned = addressH

        // Remove city & state if contained
        if (city.isNotBlank()) cleaned = cleaned.replace(city, "", true)
        if (state.isNotBlank()) cleaned = cleaned.replace(state, "", true)

        // Remove country patterns commonly seen
        cleaned = cleaned
            .replace("United Arab Emirates", "", true)
            .replace("UAE", "", true)

        // Remove lat/lng numeric patterns
        cleaned = cleaned.replace(Regex("\\b\\d+\\.\\d+\\b"), "")

        // Remove double spaces, extra hyphens, commas
        cleaned = cleaned
            .replace(", ,", ",")
            .replace(",,", ",")
            .replace("--", "-")
            .replace(" - ", "-")
            .trim().trim(',', '-', ' ')

        return cleaned
    }

    private fun splitIntoTwoLines(cleaned: String): Pair<String, String> {

        // Split by comma OR hyphen OR space-group
        val parts = cleaned.split(Regex("[,-]"))
            .map { it.trim() }
            .filter { it.isNotEmpty() }

        if (parts.size < 2) {
            return Pair(cleaned, "")   // fallback
        }

        val half = parts.size / 2

        val line1 = parts.take(half).joinToString(", ")
        val line2 = parts.drop(half).joinToString(", ")

        return Pair(line1, line2)
    }


}