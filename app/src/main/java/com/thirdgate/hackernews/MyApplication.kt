package com.thirdgate.hackernews

import android.app.Application

import dev.openattribution.sdk.OpenAttribution

class MyApplication : Application() {

    private lateinit var openAttribution: OpenAttribution

    override fun onCreate() {
        super.onCreate()
        // Initialize the OpenAttribution SDK
        openAttribution = OpenAttribution.initialize(this, "https://oa.thirdgate.dev")

    }


}