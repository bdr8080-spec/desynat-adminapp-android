package com.desynatadmin.ui.m2

import android.media.MediaPlayer
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.desynatadmin.base.BaseActivity
import com.desynatadmin.ui.home.storyManagement.response.GetStoryResponse
import com.desynatadmin.R
import com.desynatadmin.data.view_model.HomeViewModel
import com.desynatadmin.databinding.ActivityViewReelBinding
import com.desynatadmin.utils.CommonMethod
import com.desynatadmin.utils.ProgressDialogUtils
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class ViewReelActivity : BaseActivity() {
    private var binding : ActivityViewReelBinding?=null
    private val viewModel : HomeViewModel by viewModels()
    private var reelData: GetStoryResponse.Data.Data?=null
    var from = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inflate *after* onCreate is called
        binding = ActivityViewReelBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        enableEdgeToEdge()
        initView()
        initControl()
        observer()
    }


    override fun initView() {
        ProgressDialogUtils.getInstance().hideProgress()
        val itemJson = intent?.getStringExtra("storyData")
         from = intent?.getStringExtra("from").toString()

        if (!itemJson.isNullOrEmpty()) {
            reelData = Gson().fromJson(itemJson, GetStoryResponse.Data.Data::class.java)
        }
        binding?.apply {

            if (from=="highlight"){
                tvToolbar.text = getString(R.string.highlights)
            }
            else{
                tvToolbar.text = getString(R.string.reel)
            }


            val url = reelData?.file ?: ""

            val isVideo = url.endsWith(".mp4") || url.endsWith(".mov") ||url.endsWith(".avi") || url.endsWith(".mkv")
            val isGif = url.endsWith(".gif")

            if (isVideo) {
                videoLoader.isVisible = true
                ivStory.isVisible = false
                videoStory.isVisible = true

                videoStory.setVideoPath(url)

                videoStory.setOnPreparedListener { mp ->

                    val videoWidth = mp.videoWidth
                    val videoHeight = mp.videoHeight

                    val viewWidth = videoStory.width
                    val viewHeight = videoStory.height

                    val videoRatio = videoWidth.toFloat() / videoHeight
                    val viewRatio = viewWidth.toFloat() / viewHeight

                    val layoutParams = videoStory.layoutParams

                    if (videoRatio > viewRatio) {
                        // 👉 video is wider → scale by HEIGHT (crop left/right)
                        layoutParams.height = viewHeight
                        layoutParams.width = (viewHeight * videoRatio).toInt()
                    } else {
                        // 👉 video is taller → scale by WIDTH (crop top/bottom)
                        layoutParams.width = viewWidth
                        layoutParams.height = (viewWidth / videoRatio).toInt()
                    }

                   videoStory.layoutParams = layoutParams
                    mp.isLooping = false
                    videoLoader.isVisible = false
                    videoStory.start()
                }

                videoStory.setOnInfoListener { _, what, _ ->

                    when (what) {

                        MediaPlayer.MEDIA_INFO_BUFFERING_START -> {
                            videoLoader.isVisible = true
                            ivPlay.setImageResource(R.drawable.ic_pause)
                        }

                        MediaPlayer.MEDIA_INFO_BUFFERING_END -> {
                            videoLoader.isVisible = false
                            ivPlay.setImageResource(R.drawable.ic_pause)
                        }
                    }

                    false
                }

            } else {
                ivPlay.isVisible = false
                videoLoader.isVisible = false
                ivStory.isVisible = true
                videoStory.isVisible = false

                if (isGif) {
                    Glide.with(this@ViewReelActivity)
                        .asGif()
                        .load(url)
                        .into(ivStory)
                } else {
                    Glide.with(this@ViewReelActivity)
                        .load(url)
                        .into(ivStory)
                }
            }


            if (from=="reel"&&!reelData?.caption.isNullOrEmpty() && reelData?.caption != "null"){
                clInfo.isVisible = true
                tvHighlight.isVisible = false
                tvTitle.text = reelData?.caption
            }
            else if (from=="highlight"){
                clInfo.isVisible = false
                tvHighlight.isVisible = true
                tvHighlight.text = reelData?.title
            }
                else{
                tvTitle.isVisible = false
            }


            tvDate.text = "${getString(R.string.added_on)} ${CommonMethod.customizeChangeDateFormat(reelData?.createdAt.toString(), outputFormat = "dd/MM/yyyy")}"

            videoStory.setOnCompletionListener {
                ivPlay.setImageResource(R.drawable.play_ic)

            }
        }
    }

    override fun initControl() {
        binding?.apply {
            ivBack.setOnClickListener {
                finish()
            }

            videoStory.setOnClickListener {

            }

            ivDelete.setOnClickListener {
                if (from=="reel"){
                    deleteReel(reelData?.id.toString())
                }
                else{
                    deleteHighlight(reelData?.id.toString())
                }

            }

        }
    }

    override fun observer() {
        viewModel.error.observe(this) { errorMessage ->
            ProgressDialogUtils.getInstance().hideProgress()
        }
        viewModel.deleteSellerReelData.observe(this){
            ProgressDialogUtils.getInstance().hideProgress()
            CommonMethod.showSuccessDialog(this,getString(R.string.reel_deleted_successfully)){
                finish()
            }

        }

        viewModel.deleteSellerHighlightData.observe(this){
            ProgressDialogUtils.getInstance().hideProgress()
            CommonMethod.showSuccessDialog(this,getString(R.string.highlights_deleted_successfully)){
                finish()
            }

        }


    }

    private fun deleteReel(id: String){
        viewModel.deleteSellerReel(id)
        ProgressDialogUtils.getInstance().showProgress(this)

    }

    private fun deleteHighlight(id: String){
        viewModel.deleteSellerHighlight(id)
        ProgressDialogUtils.getInstance().showProgress(this)

    }


}