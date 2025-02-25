package com.thirdgate.hackernews

import android.app.Application
import dev.openattribution.sdk.OpenAttribution

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        // Initialize the OpenAttribution SDK
        OpenAttribution.initialize(this, "https://oa.thirdgate.dev")

    }

}