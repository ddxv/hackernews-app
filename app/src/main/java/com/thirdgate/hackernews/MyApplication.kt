package com.thirdgate.hackernews

import android.app.Application
import android.util.Log

import dev.openattribution.sdk.OpenAttribution

class MyApplication : Application() {

    private lateinit var openAttribution: OpenAttribution

    override fun onCreate() {
        Log.d("MyOA", "onCreateBefore")
        super.onCreate()
        Log.d("MyOA", "onCreateAfter")
        // Initialize the OpenAttribution SDK
        openAttribution = OpenAttribution.initialize(this, "https://oa.thirdgate.dev")

    }

}