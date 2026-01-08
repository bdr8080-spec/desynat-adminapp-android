package com.desynatadmin.ui.home.storyManagement

import android.app.Dialog
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.desynatadmin.R
import com.desynatadmin.base.BaseFragment
import com.desynatadmin.data.view_model.HomeViewModel
import com.desynatadmin.databinding.FragmentAddStoryBinding
import com.desynatadmin.databinding.UploadStoryPreviewDialogBinding
import com.desynatadmin.utils.CommonMethod
import com.desynatadmin.utils.ProgressDialogUtils
import com.desynatadmin.utils.mediaPicker.StoryMediaPicker
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class AddStoryFragment : BaseFragment() {
    private var binding: FragmentAddStoryBinding? = null
    private val viewModel: HomeViewModel by viewModels()
    private var isObserver = true

    private var isImageObserver = true

    private lateinit var storyMediaPicker: StoryMediaPicker
    private var mediaType = ""
    private var mediaFile: File? = null

    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            storyMediaPicker.onImageCameraResult(success)
        }

    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            storyMediaPicker.onImageGalleryResult(uri)
        }

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            storyMediaPicker.onPermissionResult(granted)
        }

    private val videoCameraLauncher =
        registerForActivityResult(ActivityResultContracts.CaptureVideo()) { success ->
            storyMediaPicker.onVideoCameraResult(success)
        }

    private val videoGalleryLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            storyMediaPicker.onVideoGalleryResult(uri)
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddStoryBinding.inflate(layoutInflater, container, false)
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
        binding?.apply {
            toolbar.tvToolbar.text = "Add Story"

            storyMediaPicker = StoryMediaPicker(
                requireActivity(),
                cameraLauncher,
                galleryLauncher,
                videoCameraLauncher,
                videoGalleryLauncher,
                permissionLauncher
            ) { uri, type ->
                uri?.let {
                    mediaType = type
                    mediaFile = CommonMethod.uriToFile(requireActivity(), it)

                    if (type == "image") {
                        binding?.ivAddStory?.setImageURI(it)
                    } else {
                        binding?.ivAddStory?.setImageResource(R.drawable.ic_placeholder)
                    }

                    previewDialog(it)
                }
            }

        }

    }

    override fun initControl() {
        binding?.apply {

            toolbar.ivBack.setOnClickListener {
                findNavController().popBackStack()
            }

            ivAddStory.setOnClickListener {
                storyMediaPicker.showCameraStoryPicker()
            }

        }
    }

    override fun observe() {
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (isObserver||isImageObserver) {
                ProgressDialogUtils.getInstance().hideProgress()

                isImageObserver = false
                isObserver = false
            }
        }

        viewModel.uploadImageResult.observe(viewLifecycleOwner) { response ->
            if (isImageObserver) {
                response?.let {
                    addStory(response.data.url)
                }
                isImageObserver = false
            }
        }

        viewModel.addStoryResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                CommonMethod.showSuccessDialog(requireContext(), "Story added successfully!"){
                    findNavController().popBackStack()
                }
                isObserver = false
            }
        }
    }

    fun addStory(url: String) {
        val map = HashMap<String, Any>()
        map["file"] = url
        viewModel.addStory(map)
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

    fun uploadFile(){
        viewModel.uploadImage(mediaFile)
        ProgressDialogUtils.getInstance().showProgress(requireActivity(), false)
        isImageObserver = true
    }

    private fun previewDialog(uri: Uri) {
        val dialog = Dialog(requireActivity(), R.style.CustomDialog)
        val dialogBinding = UploadStoryPreviewDialogBinding.inflate(LayoutInflater.from(context))
        dialogBinding.root.let { dialog.setContentView(it) }

        val window: Window = dialog.window!!
        window.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        window.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(false)

        val params: WindowManager.LayoutParams = window.attributes
        params.gravity = Gravity.CENTER
        window.attributes = params

        dialogBinding.apply {

            if (mediaType == "image") {
                ivImage.isVisible = true
                ivImage.setImageURI(uri)
            } else {
                videoView.isVisible = true
                videoView.setVideoURI(uri)
                videoView.start()
            }

            tvCancel.setOnClickListener {
                mediaType = ""
                mediaFile = null
                dialog.dismiss()
            }
             tvUpload.setOnClickListener{
                 uploadFile()
                 dialog.dismiss()
             }

        }

        dialog.show()
    }

}