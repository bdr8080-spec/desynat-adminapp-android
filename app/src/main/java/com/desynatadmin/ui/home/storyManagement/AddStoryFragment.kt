package com.desynatadmin.ui.home.storyManagement

import android.Manifest
import android.app.Dialog
import android.content.ContentUris
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.desynatadmin.R
import com.desynatadmin.adapter.GalleryAdapter
import com.desynatadmin.base.BaseFragment
import com.desynatadmin.data.view_model.HomeViewModel
import com.desynatadmin.databinding.FragmentAddStoryBinding
import com.desynatadmin.databinding.UploadStoryPreviewDialogBinding
import com.desynatadmin.ui.home.storyManagement.response.GalleryMedia
import com.desynatadmin.utils.CommonMethod
import com.desynatadmin.utils.ProgressDialogUtils
import com.desynatadmin.utils.mediaPicker.StoryMediaPicker
import com.desynatadmin.utils.showToast
import com.otaliastudios.transcoder.Transcoder
import com.otaliastudios.transcoder.TranscoderListener
import com.otaliastudios.transcoder.resize.AtMostResizer
import com.otaliastudios.transcoder.strategy.DefaultAudioStrategy
import com.otaliastudios.transcoder.strategy.DefaultVideoStrategy
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
    private var galleryAdapter: GalleryAdapter?=null
    private val imageList = mutableListOf<Uri>()
    private var offset = 0
    private var isLoading = false
    private val PAGE_SIZE = 40

    private var compressedVideoFile: File? = null

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
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            storyMediaPicker.onVideoCameraResult(
                result.resultCode == AppCompatActivity.RESULT_OK
            )
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
        checkPermission()
    }

    override fun initView() {
        ProgressDialogUtils.getInstance().hideProgress()
        binding?.apply {
            toolbar.tvToolbar.text = "Add Story"

            galleryAdapter = GalleryAdapter(requireActivity(),mutableListOf()) { media ->
                when (media) {
                    is GalleryMedia.Image -> {
                        mediaType = "image"
                        previewDialog(media.uri)
                    }
                    is GalleryMedia.Video -> {
                        if(isVideoWithinLimit(media.uri,30)) {
                            mediaType = "video"
                            previewDialog(media.uri)
                        }
                        else{
                            showToast("Please select a video of 30 seconds or less.")
                        }
                    }
                }
            }

            rvGallery.apply {
                layoutManager = GridLayoutManager(requireContext(), 3)
                adapter = galleryAdapter
                addOnScrollListener(scrollListener)
            }

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
//                    if (type == "image") {
//                        binding?.ivAddStory?.setImageURI(it)
//                    } else {
//                        binding?.ivAddStory?.setImageResource(R.drawable.ic_placeholder)
//                    }

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

        viewModel.uploadVideoImageResult.observe(viewLifecycleOwner) { response ->
            if (isImageObserver) {
                response?.let {
                    addStory(response.data.url)
                    compressedVideoFile?.delete()
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

    fun uploadFile(uri: Uri) {
        val file = CommonMethod.uriToFileForStory(requireActivity(), uri)

        if (file == null || !file.exists()) {
            showToast("File not found")
            return
        }

        viewModel.uploadVideoImage(file)
        ProgressDialogUtils.getInstance().showProgress(requireActivity(), false)
        isImageObserver = true
    }

    private fun uploadCompressedVideo(file: File) {
        if (!file.exists()) {
            showToast("Compressed file not found")
            return
        }

        viewModel.uploadVideoImage(file)
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
                dialog.dismiss()
            }
             tvUpload.setOnClickListener{
                 if (mediaType == "video") {
                     compressAndUploadVideo(uri)
                 } else {
                     uploadFile(uri)
                 }
                 dialog.dismiss()
             }

        }

        dialog.setOnDismissListener {
            if (mediaType == "video") {
                dialogBinding.videoView.stopPlayback()
            }
        }
        dialog.show()
    }

    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val lm = recyclerView.layoutManager as GridLayoutManager
            val visibleCount = lm.childCount
            val totalCount = lm.itemCount
            val firstVisible = lm.findFirstVisibleItemPosition()

            if (!isLoading && visibleCount + firstVisible >= totalCount) {
                loadGalleryMedia()
            }
        }
    }

    private fun loadGalleryMedia() {
        if (isLoading) return
        isLoading = true

        val newItems = ArrayList<GalleryMedia>()

        val projection = arrayOf(
            MediaStore.Files.FileColumns._ID,
            MediaStore.Files.FileColumns.MEDIA_TYPE
        )

        val selection =
            "${MediaStore.Files.FileColumns.MEDIA_TYPE}=? OR " +
                    "${MediaStore.Files.FileColumns.MEDIA_TYPE}=?"

        val selectionArgs = arrayOf(
            MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE.toString(),
            MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO.toString()
        )

        val sortOrder = "${MediaStore.Files.FileColumns.DATE_ADDED} DESC"
        val cursor = requireContext().contentResolver.query(
            MediaStore.Files.getContentUri("external"),
            projection,
            selection,
            selectionArgs,
            sortOrder
        )

        cursor?.use {
            val allItems = mutableListOf<GalleryMedia>()
            val idCol = it.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)
            val typeCol = it.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MEDIA_TYPE)

            while (it.moveToNext()) {
                val id = it.getLong(idCol)
                val type = it.getInt(typeCol)
                val uri = when (type) {
                    MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE ->
                        ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                    MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO ->
                        ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id)
                    else -> null
                }
                uri?.let {
                    if (type == MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE)
                        allItems.add(GalleryMedia.Image(it))
                    else
                        allItems.add(GalleryMedia.Video(it))
                }
            }

            // Apply pagination manually
            val pagedItems = allItems.drop(offset).take(PAGE_SIZE)
            galleryAdapter?.addItems(pagedItems)
            offset += PAGE_SIZE
            isLoading = false

        }
    }

    private fun checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            galleryPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
        } else {
            galleryPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

    private val galleryPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                loadGalleryMedia()
            }
        }

    fun isVideoWithinLimit( uri: Uri, maxDurationSec: Int = 30): Boolean {
        val retriever = android.media.MediaMetadataRetriever()
        retriever.setDataSource(requireActivity(), uri)
        val durationMs = retriever.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLongOrNull()
        retriever.release()
        return durationMs != null && durationMs <= maxDurationSec * 1000
    }

    private fun compressAndUploadVideo(inputUri: Uri) {
        ProgressDialogUtils.getInstance().showProgress(requireActivity())

        val outputFile = File(
            requireContext().cacheDir,
            "story_${System.currentTimeMillis()}.mp4"
        )

        Transcoder.into(outputFile.absolutePath)
            .addDataSource(requireContext(), inputUri)
            .setVideoTrackStrategy(
                DefaultVideoStrategy.Builder()
                    .addResizer(AtMostResizer(1280)) // 720p
                    .bitRate(1_500_000)              // 1.5 Mbps
                    .frameRate(30)
                    .build()
            )
            .setAudioTrackStrategy(
                DefaultAudioStrategy.Builder()
                    .bitRate(96_000)
                    .build()
            )
            .setListener(object : TranscoderListener {

                override fun onTranscodeProgress(progress: Double) {
                }

                override fun onTranscodeCompleted(successCode: Int) {
                    requireActivity().runOnUiThread {
                        ProgressDialogUtils.getInstance().hideProgress()

                        compressedVideoFile = outputFile
                        uploadCompressedVideo(outputFile)
                    }
                }

                override fun onTranscodeCanceled() {
                    requireActivity().runOnUiThread {
                        ProgressDialogUtils.getInstance().hideProgress()
                        showToast("Video compression cancelled")
                    }
                }

                override fun onTranscodeFailed(exception: Throwable) {
                    requireActivity().runOnUiThread {
                        ProgressDialogUtils.getInstance().hideProgress()
                        showToast("Video compression failed")
                    }
                    exception.printStackTrace()
                }
            })
            .transcode()
    }


}