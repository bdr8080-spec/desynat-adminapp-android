package com.desynatadmin.ui.home.storyManagement

import android.animation.ValueAnimator
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.activity.enableEdgeToEdge
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.desynatadmin.R
import com.desynatadmin.adapter.StoryPagerAdapter
import com.desynatadmin.base.BaseActivity
import com.desynatadmin.databinding.ActivityViewStoryBinding
import com.desynatadmin.ui.home.storyManagement.response.GetStoryResponse
import com.desynatadmin.utils.CommonMethod
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.AndroidEntryPoint
import kotlin.toString

@AndroidEntryPoint
class ViewStoryActivity : BaseActivity() {
    private var binding : ActivityViewStoryBinding?=null
    private val myStories = ArrayList<GetStoryResponse.Data.Data>()

    private val progressBars = ArrayList<ProgressBar>()
    private var storyPagerAdapter: StoryPagerAdapter?=null
    private var from = "admin"
    private var sellerId: String=""

    private var progressAnimator: ValueAnimator? = null

    private var isManualNavigation = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inflate *after* onCreate is called
        binding = ActivityViewStoryBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        enableEdgeToEdge()
        initView()
        initControl()
        observer()
    }

    override fun initView() {
        CommonMethod.detectGestureMode(binding?.root!!,binding?.viewBottom!!)

        from = intent?.getStringExtra("from").toString()
        val json = intent?.getStringExtra("storyData")

        if (!json.isNullOrEmpty()) {

            val type = object : TypeToken<ArrayList<GetStoryResponse.Data.Data>>() {}.type

            val storyList: ArrayList<GetStoryResponse.Data.Data> =
                Gson().fromJson(json, type)

            myStories.clear()
            myStories.addAll(storyList)
        }

        setData()

        setupAdapter()
        setupPagerCallback()


    }

    override fun initControl() {
        binding?.apply {
            ivCancel.setOnClickListener {
               finish()
            }

            ivDelete.setOnClickListener {

            }
        }
    }

    override fun observer() {


    }

    private fun setData() {
        if (myStories.isNotEmpty()) {
            setupProgressBars(myStories.size)
            startProgress(0)
        }
    }
    private fun setupAdapter() {

        storyPagerAdapter = StoryPagerAdapter(this,
            myStories,
            object : StoryPagerAdapter.OnClick {

                override fun clickOnItem(position: Int) {

                }

                override fun onNext(position: Int) {
                    isManualNavigation = true

                    stopProgress()

                    val current = binding?.viewPager?.currentItem ?: return
                    val next = current + 1

                    if (next < myStories.size) {
                        binding?.viewPager?.currentItem = next
                    } else {
                        finish()
                    }
                }

                override fun onPrevious(position: Int) {
                    isManualNavigation = true

                    stopProgress()

                    val current = binding?.viewPager?.currentItem ?: return
                    val prev = current - 1

                    if (prev >= 0) {
                        binding?.viewPager?.currentItem = prev
                    }
                }

                override fun onPause(position: Int) {
                    progressAnimator?.pause()
                }

                override fun onResume(position: Int) {
                    progressAnimator?.resume()
                }
            },

            onVideoReady = { position, duration ->
                startVideoProgress(position, duration)
            },

            onVideoFinished = { position ->

                if (isManualNavigation) {
                    isManualNavigation = false
                    return@StoryPagerAdapter
                }

                goToNext(position)
            }
        )

        binding?.viewPager?.adapter = storyPagerAdapter
    }

    private fun setupPagerCallback() {
        binding?.viewPager?.registerOnPageChangeCallback(
            object : ViewPager2.OnPageChangeCallback() {

                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)

                    stopProgress()

                    progressBars.forEachIndexed { index, progressBar ->
                        progressBar.progress = if (index < position) 100 else 0
                    }

                    startProgress(position)
                }
            }
        )
    }


    private fun stopProgress() {
        progressAnimator?.removeAllListeners()
        progressAnimator?.cancel()
    }

    private fun startProgress(index: Int) {

        if (progressBars.isEmpty() || index >= progressBars.size) return

        val url = myStories[index].file ?: ""

        // Skip video/gif
        if (url.endsWith(".mp4") || url.endsWith(".mov") || url.endsWith(".gif")) {
            return
        }

        progressAnimator = ValueAnimator.ofInt(0, 100).apply {
            duration = 10000L

            addUpdateListener {
                progressBars[index].progress = it.animatedValue as Int
            }

            doOnEnd {

                if (isManualNavigation) {
                    isManualNavigation = false
                    return@doOnEnd
                }

                goToNext(index)
            }

            start()
        }
    }

    private fun startVideoProgress(index: Int, duration: Long) {

        stopProgress()

        progressAnimator = ValueAnimator.ofInt(0, 100).apply {
         //   this.duration = duration

            val safeDuration = if (duration > 0) duration else 10000L

            this.duration = safeDuration

            addUpdateListener {
                progressBars[index].progress = it.animatedValue as Int
            }

            doOnEnd {

                if (isManualNavigation) {
                    isManualNavigation = false
                    return@doOnEnd
                }

                goToNext(index)
            }

            start()
        }
    }


    private fun goToNext(position: Int) {
        val next = position + 1

        if (next < myStories.size) {
            binding?.viewPager?.currentItem = next
        } else {
            finish()
        }
    }

    private fun setupProgressBars(count: Int) {

        binding?.layoutProgress?.removeAllViews()
        progressBars.clear()

        repeat(count) {

            val progressBar = ProgressBar(
                this,
                null,
                android.R.attr.progressBarStyleHorizontal
            )

            progressBar.progressDrawable =
                ContextCompat.getDrawable(this, R.drawable.story_progress)

            progressBar.layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.MATCH_PARENT,
                1f
            ).apply {
                marginEnd = 8
            }

            progressBar.max = 100

            binding?.layoutProgress?.addView(progressBar)
            progressBars.add(progressBar)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopProgress()
        binding = null
    }
}