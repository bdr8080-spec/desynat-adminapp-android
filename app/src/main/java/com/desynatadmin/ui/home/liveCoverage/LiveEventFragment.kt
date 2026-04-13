package com.desynatadmin.ui.home.liveCoverage

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.desynatadmin.base.BaseFragment
import com.desynatadmin.data.view_model.HomeViewModel
import com.desynatadmin.databinding.FragmentLiveEventBinding
import com.desynatadmin.ui.home.liveCoverage.agora.AgoraManager
import com.desynatadmin.ui.home.liveCoverage.response.GetLiveStreamingRoomResponse
import com.desynatadmin.utils.CommonMethod
import com.desynatadmin.utils.ProgressDialogUtils
import com.desynatadmin.utils.SharedPreference
import com.desynatadmin.utils.showToast
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import io.agora.rtc2.ChannelMediaOptions
import io.agora.rtc2.Constants
import io.agora.rtc2.IRtcEngineEventHandler
import io.agora.rtc2.RtcEngine
import io.agora.rtc2.video.VideoCanvas
import kotlinx.coroutines.launch
import kotlin.getValue
import kotlin.text.toIntOrNull
import kotlin.toString

@AndroidEntryPoint
class LiveEventFragment : BaseFragment() {
    private var binding: FragmentLiveEventBinding?=null
    private val viewModel: HomeViewModel by viewModels()
    private var isObserver = true
    private var isTokenObserver = true
    private var eventData: GetLiveStreamingRoomResponse.Data?=null
    private var channelId = ""
    private var token = ""
    private var appId = ""
    private var isVideoSetup = false

    private var basicAuth = ""
    private var uidValue = 0
    private var isMuted = false

    private val userCountHandler = Handler(Looper.getMainLooper())
    private val userCountRunnable = object : Runnable {
        override fun run() {
            getLiveUserCount()
            userCountHandler.postDelayed(this, 5000)
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLiveEventBinding.inflate(layoutInflater,container,false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initControl()
        observe()
    }

    override fun initView() {
        convertBase64()
        val itemJson = arguments?.getString("eventData")
        if (!itemJson.isNullOrEmpty()) {
            eventData = Gson().fromJson(itemJson, GetLiveStreamingRoomResponse.Data::class.java)
            channelId = eventData?.channelId.toString()
        }
        binding?.apply {

            if (eventData?.liveToken==null){
                getAgoraToken(eventData?.id.toString())
            }
            else{

                token = eventData?.liveToken.toString()
                uidValue = eventData?.uid?.toIntOrNull() ?: 0

                startLiveStreaming()
            }
        }
    }

    override fun initControl() {
        binding?.apply {
            ivBack.setOnClickListener {
                AgoraManager.getEngine()?.apply {
                    leaveChannel()
                }

                findNavController().popBackStack()
            }

            tvLeave.setOnClickListener {
                AgoraManager.getEngine()?.apply {
                    leaveChannel()
                }

                findNavController().popBackStack()
            }

            tvMute.setOnClickListener {
                val engine = AgoraManager.getEngine()

                isMuted = !isMuted

                engine?.muteAllRemoteAudioStreams(isMuted)

                if (isMuted) {
                    binding?.tvMute?.text = "Unmute"
                } else {
                    binding?.tvMute?.text = "Mute"
                }
            }
        }
    }

    private fun getAgoraToken(id: String){
        val map = HashMap<String,Any>()
        map["id"] = id
        map["role"] = 1
        viewModel.getAgoraToken(SharedPreference.get(requireActivity()).accessToken,map)
        isTokenObserver = true
    }


    override fun observe() {
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                errorMessage?.let { showToast(it) }
                isObserver = false
            }
        }

        viewModel.getAgoraTokenResult.observe(viewLifecycleOwner) { response ->
            if (isTokenObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                if (response.data!=null){
                    token = response.data.token
                    uidValue = response.data.uid.toIntOrNull()?:0
                    channelId = response.data.channel
                    appId = response.data.appId
                    startLiveStreaming()
                    Log.d("LIVE_TEST", "token ${token}")

                }

                isTokenObserver = false
            }
        }
    }

    private val rtcEventHandler = object : IRtcEngineEventHandler() {

        override fun onJoinChannelSuccess(channel: String?, uid: Int, elapsed: Int) {

            Log.d("LIVE_TEST", "Audience joined channel")

            val engine = AgoraManager.getEngine()

            engine?.muteAllRemoteVideoStreams(false)
            engine?.muteAllRemoteAudioStreams(false)
            engine?.setDefaultMuteAllRemoteVideoStreams(false)
            engine?.setDefaultMuteAllRemoteAudioStreams(false)

            activity?.runOnUiThread {
                showNoDataUIWaiting(true)
            }
        }

        override fun onFirstRemoteVideoDecoded(uid: Int, width: Int, height: Int, elapsed: Int) {
            Log.d("LIVE_TEST", "First remote video decoded: $uid")

            activity?.runOnUiThread {
                setupRemoteVideo(uid)
                showNoDataUI(false)
            }
        }

        override fun onUserJoined(uid: Int, elapsed: Int) {
            Log.d("LIVE_TEST", "Host joined: $uid")

            if (!isVideoSetup) {
                isVideoSetup = true

                activity?.runOnUiThread {
                    setupRemoteVideo(uid)
                    showNoDataUI(false)
                    startUserCount()
                }
            }

        }

        override fun onConnectionStateChanged(state: Int, reason: Int) {
            super.onConnectionStateChanged(state, reason)
            Log.d("LIVE_TEST", "onConnectionStateChanged: $state")
            Log.d("LIVE_TEST", "onConnectionStateChanged: $reason")
        }

        override fun onRemoteVideoStateChanged(
            uid: Int,
            state: Int,
            reason: Int,
            elapsed: Int
        ) {
            Log.d("LIVE_TEST", "Remote video state: $state uid: $uid")
            if (state == 2 && !isVideoSetup) { // decoding
                isVideoSetup = true

                activity?.runOnUiThread {
                    setupRemoteVideo(uid)
                    showNoDataUI(false)
                    startUserCount()
                }
            }
        }


        override fun onUserOffline(uid: Int, reason: Int) {
            Log.d("LIVE_TEST", "Host left")
            activity?.runOnUiThread {
                stopUserCount()
                binding?.videoContainer?.removeAllViews()

                AgoraManager.getEngine()?.setupRemoteVideo(
                    VideoCanvas(null, VideoCanvas.RENDER_MODE_HIDDEN, uid)
                )
                CommonMethod.showSuccessDialog(requireActivity(),"Host left"){
                    findNavController().popBackStack()
                }

           //     showNoDataUI(true)
            }

        }

        override fun onError(err: Int) {
            Log.e("LIVE_TEST", "Agora Error: $err")
            activity?.runOnUiThread {
                showNoDataUI(true)
            }
        }
    }

    private fun showNoDataUI(isShow: Boolean=false) {
        binding?.apply {
            tvNoDataFound.isVisible = isShow
            tvToolbar.isVisible = isShow
            tvLive.isVisible = !isShow
            tvAudienceCount.isVisible = !isShow
            relMute.isVisible = !isShow
            tvNoDataFound.text = "No event found"
            if (isShow) stopUserCount()
        }
    }

    private fun showNoDataUIWaiting(isShow: Boolean=false) {
        binding?.apply {
            tvNoDataFound.isVisible = isShow
            tvToolbar.isVisible = isShow
            tvLive.isVisible = !isShow
            tvAudienceCount.isVisible = !isShow
            relMute.isVisible = !isShow
            tvNoDataFound.text = "Waiting for host to join..."
            if (isShow) stopUserCount()
        }
    }

    private fun joinChannel() {

        val engine = AgoraManager.getEngine()

        engine?.setChannelProfile(Constants.CHANNEL_PROFILE_LIVE_BROADCASTING)
        engine?.setClientRole(Constants.CLIENT_ROLE_AUDIENCE)
        engine?.enableVideo()
        engine?.enableAudio()

        val options = ChannelMediaOptions().apply {
            clientRoleType = Constants.CLIENT_ROLE_AUDIENCE
            channelProfile = Constants.CHANNEL_PROFILE_LIVE_BROADCASTING
        }

        engine?.joinChannel(
            token,
            channelId,
            uidValue,
            options
        )

    }

    private fun setupRemoteVideo(uid: Int) {
        binding?.videoContainer?.removeAllViews()
        val surfaceView = RtcEngine.CreateRendererView(requireContext())

        binding?.videoContainer?.addView(surfaceView)

        AgoraManager.getEngine()?.setupRemoteVideo(
            VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_HIDDEN, uid)
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        userCountHandler.removeCallbacks(userCountRunnable)

        AgoraManager.getEngine()?.apply {
            stopPreview()
            muteAllRemoteAudioStreams(true)
            leaveChannel()
        }

        AgoraManager.destroy()

        binding = null
    }

    private fun startLiveStreaming(){

        AgoraManager.initialize(
            requireContext(),
            appId,
            rtcEventHandler
        )

        joinChannel()
    }

    override fun onDestroy() {
        super.onDestroy()
        userCountHandler.removeCallbacks(userCountRunnable)

        AgoraManager.getEngine()?.leaveChannel()

        binding = null
    }

    private fun getLiveUserCount() {
        lifecycleScope.launch {
            try {
                val response = AgoraRetrofit.api.getLiveUserCount(
                    basicAuth,
                    appId,
                    channelId
                )

                val count = response.body()?.data?.audienceTotal ?: 0

                activity?.runOnUiThread {
                    setUserCountData(count)
                }

            } catch (e: Exception) {
                Log.e("LIVE_TEST", e.message ?: "Error")
            }
        }
    }

    private fun setUserCountData(audienceTotal: Int?) {
        binding?.apply {
            if ((audienceTotal ?: 0) > 0) {
             //   tvAudienceCount.isVisible = true
                tvAudienceCount.text = "Viewers : $audienceTotal"
            } else {
                //   tvAudienceCount.isVisible = false
              //  tvAudienceCount.isVisible = true
                tvAudienceCount.text = "Viewers : 0"
            }
        }
    }

    private fun convertBase64(){
        val customerID = "0ddf0359302c4e3b995282b44470e25e"
        val customerCertificate = "c258c2e0e578420fbdb0e43baf5b1ee3"
        val credentials = "$customerID:$customerCertificate"
        val base64Encoded = Base64.encodeToString(credentials.toByteArray(), Base64.NO_WRAP)
        println(base64Encoded)
        basicAuth = "Basic " + base64Encoded
        Log.d("LIVE_TEST", "base64Encoded: $base64Encoded")
    }

    private fun startUserCount() {
        userCountHandler.removeCallbacks(userCountRunnable)
        userCountHandler.post(userCountRunnable)
    }

    private fun stopUserCount() {
        userCountHandler.removeCallbacks(userCountRunnable)
    }

}