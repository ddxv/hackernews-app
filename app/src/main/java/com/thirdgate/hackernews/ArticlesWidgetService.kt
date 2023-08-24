package com.thirdgate.hackernews

import android.content.Intent
import android.widget.RemoteViewsService

class ArticlesWidgetService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        return ArticlesRemoteViewsFactory(applicationContext, intent)
    }
}