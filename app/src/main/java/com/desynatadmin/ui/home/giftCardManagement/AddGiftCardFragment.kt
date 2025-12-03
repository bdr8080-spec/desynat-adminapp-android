package com.desynatadmin.ui.home.giftCardManagement

import android.os.Bundle
import androidx.fragment.app.Fragment
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
import com.desynatadmin.databinding.FragmentAddGiftCardBinding
import com.desynatadmin.databinding.FragmentGiftCardManagementBinding
import com.desynatadmin.ui.home.HomeActivity
import com.desynatadmin.ui.home.brandManagement.response.GetBrandResponse
import com.desynatadmin.ui.home.giftCardManagement.response.GetAllGiftCardResponse
import com.desynatadmin.utils.CommonMethod
import com.desynatadmin.utils.ImagePicker
import com.desynatadmin.utils.ProgressDialogUtils
import com.desynatadmin.utils.showToast
import com.desynatadmin.utils.toPlainRequestBody
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.RequestBody
import java.io.File

@AndroidEntryPoint
class AddGiftCardFragment : BaseFragment() {
    private var binding: FragmentAddGiftCardBinding?=null
    private val viewModel: HomeViewModel by viewModels()
    private var isObserver = true
    private var giftData: GetAllGiftCardResponse.Data.Data?=null

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
        binding = FragmentAddGiftCardBinding.inflate(layoutInflater,container,false)
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
        val itemJson = arguments?.getString("giftData")
        binding?.apply {
            toolbar.tvToolbar.text = "Add New Card"
            binding?.apply {

                CommonMethod.applyArabicOnlyFilter(etArabicCardName)
                CommonMethod.removeSpace(etEnglishCardName)
                if (!itemJson.isNullOrEmpty()) {
                    giftData = Gson().fromJson(itemJson, GetAllGiftCardResponse.Data.Data::class.java)
                    toolbar.tvToolbar.text = "Edit Card"
                    btnSubmit.text = "Update"
                    setData()

                } else {
                    toolbar.tvToolbar.text = "Add New Card"

                }

                imagePicker = ImagePicker(
                    requireActivity(),
                    cameraLauncher,
                    galleryLauncher,
                    permissionLauncher
                ) { uri ->
                    uri?.let {
                        image = CommonMethod.uriToFile(requireActivity(), it)
                        ivGiftCard.setImageURI(it)
                    }
                }
            }
        }
    }

        override fun initControl() {
            binding?.apply {
                toolbar.tvToolbar.setOnClickListener {
                    findNavController().popBackStack()
                }

                btnSubmit.setOnClickListener {
                    if (validation()){
                        if (image!=null){
                            uploadFile()
                        }
                        else{
                            addGiftCard()
                        }

                    }
                }

                ivGiftCard.setOnClickListener {
                    imagePicker.showImageSourceDialog()
                }
            }
        }

        private fun validation(): Boolean {
            if (image==null&&giftData==null){
                showToast("Please upload gift card image")
                return false
            }
            else if (binding?.etEnglishCardName?.text?.toString()?.isBlank() == true ) {
                showToast("Please enter gift card type name in english")
                return false
            }
            else if (binding?.etArabicCardName?.text?.toString()?.isBlank() == true ) {
                showToast("Please enter gift card type name in arabic")
                return false
            }
            return true
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
                        addGiftCard()
                    }
                    isImageObserver = false
                }
            }

            viewModel.addGiftCardResult.observe(viewLifecycleOwner) { response ->
                if (isObserver) {
                    ProgressDialogUtils.getInstance().hideProgress()
                    CommonMethod.showSuccessDialog(requireContext(), "Gift card added successfully!"){
                        findNavController().popBackStack()
                    }
                    isObserver = false
                }
            }

            viewModel.editGiftCardResult.observe(viewLifecycleOwner) { response ->
                if (isObserver) {
                    ProgressDialogUtils.getInstance().hideProgress()
                    CommonMethod.showSuccessDialog(requireContext(), "Gift card updated successfully!"){
                        findNavController().popBackStack()
                    }
                    isObserver = false
                }
            }

        }

        private fun setData(){

            binding?.apply {

                etEnglishCardName.setText(giftData?.nameEnglish)
                etArabicCardName.setText(giftData?.nameArabic)
                imageUrl = giftData?.image.toString()
                ivGiftCard.load(giftData?.image) {
                    crossfade(true)
                    placeholder(R.drawable.ic_upload_brand)
                    error(R.drawable.ic_upload_brand)
                }
            }

        }

        fun addGiftCard(){
            val map = HashMap<String, Any>()

            if (giftData!=null){
                map["giftCard_id"] = giftData?.id.toString()
            }
            map["name_arabic"] = binding?.etArabicCardName?.text?.trim().toString()
            map["name_english"] = binding?.etEnglishCardName?.text?.trim().toString()
            map["image"] =imageUrl
            if (giftData!=null){
                viewModel.editGiftCard(map)
            }
            else{
                viewModel.addGiftCard(map)
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