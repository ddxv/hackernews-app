package com.thirdgate.hackernews

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.provider.Settings
import android.util.Log
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.UUID
import java.util.concurrent.TimeUnit


//val myBaseUrl: String = "https://demo.openattribution.dev"
val myBaseUrl: String = "http://localhost:8000"



class UserIdManager(context: Context) {
    private val preferences: SharedPreferences = context.getSharedPreferences(
        PREF_NAME,
        Context.MODE_PRIVATE
    )

    fun getUserId(): String {
        // Try to get existing ID
        var userId = preferences.getString(KEY_USER_ID, null)

        // If no ID exists, create one and save it
        if (userId == null) {
            userId = UUID.randomUUID().toString()
            preferences.edit()
                .putString(KEY_USER_ID, userId)
                .apply()
        }

        return userId
    }

    companion object {
        private const val PREF_NAME = "user_prefs"
        private const val KEY_USER_ID = "user_id"

        @Volatile
        private var instance: UserIdManager? = null

        fun getInstance(context: Context): UserIdManager {
            return instance ?: synchronized(this) {
                instance ?: UserIdManager(context.applicationContext).also { instance = it }
            }
        }
    }
}



object EmulatorDetector {
    fun isEmulator(): Boolean {
        return (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                || Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.HARDWARE.contains("goldfish")
                || Build.HARDWARE.contains("ranchu")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || Build.PRODUCT.contains("sdk_gphone")
                || Build.PRODUCT.contains("google_sdk")
                || Build.PRODUCT.contains("sdk")
                || Build.PRODUCT.contains("sdk_x86")
                || Build.PRODUCT.contains("sdk_google")
                || Build.PRODUCT.contains("vbox86p")
                || Build.HOST.startsWith("Build") // Android Studio
                || (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
    }
}

class MyOpenAttribution : Application() {

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    // Lazy initialize userIdManager
    private val userIdManager by lazy { UserIdManager.getInstance(this) }

    override fun onCreate() {
        super.onCreate()
        appScope.launch {
            trackAppOpen()
        }
        val userId = userIdManager.getUserId()
        Log.d("OpenAttribution", "Persistent User ID: $userId")
    }

    private suspend fun trackAppOpen() {
        val androidId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        val myTimestamp = System.currentTimeMillis()
        val eventId = "app_open"
        val myUid = UUID.randomUUID().toString()
        val myOaUid = userIdManager.getUserId()

        val basePackageName = applicationContext.packageName
        val appendedPackageName = if (EmulatorDetector.isEmulator()) {
            "${basePackageName}_test"
        } else {
            basePackageName
        }


        Log.d("OpenAttribution", "Base Package Name: $basePackageName")
        Log.d("OpenAttribution", "Appended Package Name: $appendedPackageName")

        val gaid = withContext(Dispatchers.IO) {
            try {
                val adInfo = AdvertisingIdClient.getAdvertisingIdInfo(applicationContext)
                adInfo.id
            } catch (e: Exception) {
                Log.e("OpenAttribution", "Error retrieving GAID: ${e.message}")
                null
            }
        }

        val url = constructTrackingUrl(
            appendedPackageName,
            myOaUid,
            gaid,
            androidId ?: "unknown",
            eventId,
            myUid,
            myTimestamp
        )

        try {
            val client = OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .build()

            withContext(Dispatchers.IO) {
                client.newCall(Request.Builder().url(url).build()).execute()
            }.use { response ->
                if (response.isSuccessful) {
                    Log.i("OpenAttribution", "Tracking request successful: ${response.code}")
                } else {
                    val responseBody = response.body?.string()
                    Log.w("OpenAttribution", "Tracking request failed: ${response.code} $responseBody")
                }
            }
        } catch (e: Exception) {
            Log.e("OpenAttribution", "Error sending tracking request: ${e.message}")
        }
    }

    private fun constructTrackingUrl(
        packageName: String,
        oauid: String,
        gaid: String?,
        androidId: String,
        eventId: String,
        eventUid: String,
        eventTime: Long
    ): String {
        val url= "$myBaseUrl/collect/events/$packageName?oa_uid=$oauid&ifa=$gaid&android_id=$androidId&event_id=$eventId&event_uid=$eventUid&event_time=$eventTime"

        Log.i("OpenAttribution", "Constructing tracking URL $url")
        return url
    }


    override fun onTerminate() {
        super.onTerminate()
        appScope.cancel()
    }
}