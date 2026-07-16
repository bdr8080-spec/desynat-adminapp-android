package com.desynatadmin.ui.m2

import android.animation.ValueAnimator
import android.media.MediaPlayer
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat
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

@AndroidEntryPoint
class ViewReelActivity : BaseActivity() {
    private var binding : ActivityViewReelBinding?=null
    private val viewModel : HomeViewModel by viewModels()
    private var reelData: GetStoryResponse.Data.Data?=null
    private val myStories = ArrayList<GetStoryResponse.Data.Data>()
    private val progressBars = ArrayList<ProgressBar>()
    private var progressAnimator: ValueAnimator? = null
    private var currentStoryPosition = 0
    var from = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
            
            myStories.clear()
            if (from == "highlight" && !reelData?.highlights.isNullOrEmpty()) {
                reelData?.highlights?.forEach { highlight ->
                    myStories.add(
                        GetStoryResponse.Data.Data(
                            createdAt = highlight.createdAt,
                            file = highlight.file,
                            id = highlight.id,
                            isSeen = highlight.isSeen,
                            sellerId = highlight.sellerId,
                            status = highlight.status,
                            type = highlight.type,
                            updatedAt = highlight.updatedAt,
                            title = highlight.title,
                            caption = highlight.caption
                        )
                    )
                }
            } else {
                reelData?.let { myStories.add(it) }
            }
        }

        if (from == "highlight") {
            binding?.tvToolbar?.text = getString(R.string.highlights)
            binding?.layoutProgress?.isVisible = true
            setupProgressBars(myStories.size)
        } else {
            binding?.tvToolbar?.text = getString(R.string.reel)
            binding?.layoutProgress?.isVisible = false
        }

        showStory(0)
    }

    private fun showStory(position: Int) {
        if (myStories.isEmpty() || position >= myStories.size) {
            finish()
            return
        }
        
        currentStoryPosition = position
        stopProgress()
        
        val story = myStories[position]
        binding?.apply {
            val url = story.file
            val isVideo = url.endsWith(".mp4") || url.endsWith(".mov") || url.endsWith(".avi") || url.endsWith(".mkv")
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
                        layoutParams.height = viewHeight
                        layoutParams.width = (viewHeight * videoRatio).toInt()
                    } else {
                        layoutParams.width = viewWidth
                        layoutParams.height = (viewWidth / videoRatio).toInt()
                    }

                    videoStory.layoutParams = layoutParams
                    mp.isLooping = false
                    videoLoader.isVisible = false
                    videoStory.start()
                    
                    if (from == "highlight") {
                        startVideoProgress(position, mp.duration.toLong())
                    }
                }

                videoStory.setOnInfoListener { _, what, _ ->
                    when (what) {
                        MediaPlayer.MEDIA_INFO_BUFFERING_START -> videoLoader.isVisible = true
                        MediaPlayer.MEDIA_INFO_BUFFERING_END -> videoLoader.isVisible = false
                    }
                    false
                }
                
                videoStory.setOnCompletionListener {
                    if (from == "highlight") {
                        // Progress animator will handle moving to next
                    } else {
                        ivPlay.setImageResource(R.drawable.play_ic)
                    }
                }

            } else {
                ivPlay.isVisible = false
                videoLoader.isVisible = false
                ivStory.isVisible = true
                videoStory.isVisible = false

                if (isGif) {
                    Glide.with(this@ViewReelActivity).asGif().load(url).into(ivStory)
                } else {
                    Glide.with(this@ViewReelActivity).load(url).into(ivStory)
                }
                
                if (from == "highlight") {
                    startProgress(position)
                }
            }

            if (from == "reel" && !story.caption.isNullOrEmpty() && story.caption != "null") {
                clInfo.isVisible = true
                tvHighlight.isVisible = false
                tvTitle.text = story.caption
            } else if (from == "highlight") {
                clInfo.isVisible = false
                tvHighlight.isVisible = true
                tvHighlight.text = story.title ?: reelData?.title
            } else {
                tvTitle.isVisible = false
            }

            tvDate.text = "${getString(R.string.added_on)} ${CommonMethod.customizeChangeDateFormat(story.createdAt, outputFormat = "dd/MM/yyyy")}"
            
            // Update progress bars state
            progressBars.forEachIndexed { index, progressBar ->
                progressBar.progress = if (index < position) 100 else 0
            }
        }
    }

    private fun setupProgressBars(count: Int) {
        binding?.layoutProgress?.removeAllViews()
        progressBars.clear()
        repeat(count) {
            val progressBar = ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal)
            progressBar.progressDrawable = ContextCompat.getDrawable(this, R.drawable.story_progress)
            progressBar.layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f).apply {
                marginEnd = 8
            }
            progressBar.max = 100
            binding?.layoutProgress?.addView(progressBar)
            progressBars.add(progressBar)
        }
    }

    private fun startProgress(index: Int) {
        if (progressBars.isEmpty() || index >= progressBars.size) return
        progressAnimator = ValueAnimator.ofInt(0, 100).apply {
            duration = 5000L // 5 seconds as requested
            addUpdateListener {
                progressBars[index].progress = it.animatedValue as Int
            }
            doOnEnd {
                goToNext()
            }
            start()
        }
    }

    private fun startVideoProgress(index: Int, duration: Long) {
        if (progressBars.isEmpty() || index >= progressBars.size) return
        stopProgress()
        progressAnimator = ValueAnimator.ofInt(0, 100).apply {
            this.duration = if (duration > 0) duration else 5000L
            addUpdateListener {
                progressBars[index].progress = it.animatedValue as Int
            }
            doOnEnd {
                goToNext()
            }
            start()
        }
    }

    private fun stopProgress() {
        progressAnimator?.removeAllListeners()
        progressAnimator?.cancel()
    }

    private fun goToNext() {
        val next = currentStoryPosition + 1
        if (next < myStories.size) {
            showStory(next)
        } else {
            finish()
        }
    }

    private fun goToPrevious() {
        val prev = currentStoryPosition - 1
        if (prev >= 0) {
            showStory(prev)
        }
    }

    override fun initControl() {
        binding?.apply {
            ivBack.setOnClickListener {
                finish()
            }

            leftClick.setOnClickListener {
                if (from == "highlight") {
                    goToPrevious()
                }
            }

            rightClick.setOnClickListener {
                if (from == "highlight") {
                    goToNext()
                }
            }

            ivDelete.setOnClickListener {
                if (from == "reel") {
                    deleteReel(myStories[currentStoryPosition].id.toString())
                } else {
                    deleteHighlight(myStories[currentStoryPosition].id.toString())
                }
            }
        }
    }

    override fun observer() {
        viewModel.error.observe(this) { errorMessage ->
            ProgressDialogUtils.getInstance().hideProgress()
        }
        viewModel.deleteSellerReelData.observe(this) {
            ProgressDialogUtils.getInstance().hideProgress()
            CommonMethod.showSuccessDialog(this, getString(R.string.reel_deleted_successfully)) {
                finish()
            }
        }
        viewModel.deleteSellerHighlightData.observe(this) {
            ProgressDialogUtils.getInstance().hideProgress()
            CommonMethod.showSuccessDialog(this, getString(R.string.highlights_deleted_successfully)) {
                finish()
            }
        }
    }

    private fun deleteReel(id: String) {
        viewModel.deleteSellerReel(id)
        ProgressDialogUtils.getInstance().showProgress(this)
    }

    private fun deleteHighlight(id: String) {
        viewModel.deleteSellerHighlight(id)
        ProgressDialogUtils.getInstance().showProgress(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopProgress()
        binding = null
    }
}
