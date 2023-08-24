package com.thirdgate.hackernews

import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.widget.RemoteViewsService

class WidgetService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        return NewsRemoteViewsFactory(this.applicationContext)
    }
}

class NewsRemoteViewsFactory(private val context: Context) : RemoteViewsService.RemoteViewsFactory {

    private val articles: MutableList<Map<String, Any>> = mutableListOf()

    override fun onCreate() {}

    override fun onDataSetChanged() {
        // This is where you'd fetch new data if needed.
        // For simplicity, we'll rely on the JobIntentService to fetch and update data.
    }

    override fun onDestroy() {}

    override fun getCount(): Int = articles.size

    override fun getViewAt(position: Int): RemoteViews {
        val views = RemoteViews(context.packageName, R.layout.widget_article_item)
        val article = articles[position]
        views.setTextViewText(R.id.articleButton, article["title"] as? String ?: "No Title")
        return views
    }

    override fun getLoadingView(): RemoteViews? {
        return null
    }

    override fun getViewTypeCount(): Int {
        TODO("Not yet implemented")
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun hasStableIds(): Boolean {
        return true
    }

    // Implement other required methods...

}