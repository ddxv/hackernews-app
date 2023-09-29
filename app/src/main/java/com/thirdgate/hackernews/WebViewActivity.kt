package com.thirdgate.hackernews

import android.os.Bundle
import android.view.KeyEvent
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity

class WebViewActivity : ComponentActivity() {

    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webview)

        webView = findViewById(R.id.webView)
        setupWebView()

        val url = intent.getStringExtra(EXTRA_URL) ?: return
        webView.loadUrl(url)
    }

    private fun setupWebView() {
        webView.settings.apply {
            javaScriptEnabled = true
            // Add other settings as needed
        }

        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                // This ensures that all redirects and new links open inside the WebView instead of in an external browser
                view?.loadUrl(request?.url.toString())
                return true
            }
        }

    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
            webView.goBack()
            return true
        }
        // If it wasn't the Back key or there's no webpage history, bubble up to the default
        // system behavior (probably exit the activity)
        return super.onKeyDown(keyCode, event)
    }

    companion object {
        const val EXTRA_URL = "extra_url"
    }
}
