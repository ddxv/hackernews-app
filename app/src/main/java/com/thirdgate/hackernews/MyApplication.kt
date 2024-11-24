package com.thirdgate.hackernews

import android.app.Application
import kotlinx.coroutines.*

class MyApplication : Application() {

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private lateinit var openAttribution: OpenAttribution

    override fun onCreate() {
        super.onCreate()
        // Initialize the OpenAttribution SDK
        openAttribution = OpenAttribution(this)

        appScope.launch {
            openAttribution.trackAppOpen()
        }
    }

    override fun onTerminate() {
        super.onTerminate()
        appScope.cancel() // Cancel the appScope when the application is terminated
    }
}