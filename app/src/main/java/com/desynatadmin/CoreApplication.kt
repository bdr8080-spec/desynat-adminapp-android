package com.desynatadmin

import android.app.Application
import android.app.NotificationManager
import android.content.Context
import android.util.Log
import com.desynatadmin.utils.SharedPreference
import com.google.android.libraries.places.api.Places
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.HiltAndroidApp
import kotlin.collections.get

@HiltAndroidApp
class CoreApplication : Application(){
    companion object {
        var notificationManager: NotificationManager? = null
    }
override fun onCreate() {
    super.onCreate()

    if (!Places.isInitialized()) {
        Places.initialize(applicationContext, getString(R.string.map_key))
    }
    notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
        if (!task.isSuccessful) {
            Log.e("FCM_ERROR", "Token failed", task.exception)
            return@addOnCompleteListener
        }

        val token = task.result
        Log.d("FCM_TOKEN", token)
        SharedPreference.get(this).deviceToken = token
    }
}
}