package com.desynatadmin.ui.home.liveCoverage.agora

import android.content.Context
import io.agora.rtc2.IRtcEngineEventHandler
import io.agora.rtc2.RtcEngine

object AgoraManager {

    private var rtcEngine: RtcEngine? = null

    fun initialize(
        context: Context,
        appId: String,
        handler: IRtcEngineEventHandler
    ) {
        rtcEngine?.leaveChannel()
        RtcEngine.destroy()
        rtcEngine = null

        rtcEngine = RtcEngine.create(context.applicationContext, appId, handler)
    }

    fun getEngine(): RtcEngine? {
        return rtcEngine
    }

    fun destroy() {
        rtcEngine?.leaveChannel()
        RtcEngine.destroy()
        rtcEngine = null
    }

}