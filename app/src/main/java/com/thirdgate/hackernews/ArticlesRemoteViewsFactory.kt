package com.thirdgate.hackernews

import android.content.Context
import android.content.Intent
import android.text.SpannableString
import android.util.Log
import android.widget.RemoteViews
import android.widget.RemoteViewsService

class ArticlesRemoteViewsFactory(private val context: Context, intent: Intent) :
    RemoteViewsService.RemoteViewsFactory {

    private var articles: List<SpannableString> = listOf()

    override fun onCreate() {
        Log.i("RemoteViewFactoryWidget", "GetViewAt: Make remote views with single_article_widget")
//        val rv = RemoteViews(
//            context.packageName,
//            R.layout.widget_single_article_layout
//        )  // This layout will represent a single article in the widget.
//        Log.i("RemoteViewFactoryWidget", "GetViewAt: Set ${articles[position]}")
//        rv.setTextViewText(
//            R.id.articleTextView,
//            articles[position]
//        )  // Assuming you have a TextView with this id in single_article_layout.xml
    }

    override fun onDataSetChanged() {
        // Here you'd fetch the formatted articles from somewhere (e.g., SharedPreferences) and populate the articles list
        val preferences = context.getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE)
        val articlesSet = preferences.getStringSet("FormattedArticles", setOf()) ?: setOf()

        // Convert the Set<String> back to List<SpannableString>
        articles = articlesSet.map { SpannableString(it) }
        Log.i("RemoteViewFactoryWidget", "OnDataSetChanged: Got Formatted ARticles: $articlesSet")
    }

    override fun onDestroy() {}

    override fun getCount(): Int = articles.size

    override fun getViewAt(position: Int): RemoteViews {
        Log.i("RemoteViewFactoryWidget", "GetViewAt: Make remote views with single_article_widget")
        val rv = RemoteViews(
            context.packageName,
            R.layout.widget_single_article_layout
        )  // This layout will represent a single article in the widget.
        Log.i("RemoteViewFactoryWidget", "GetViewAt: Set ${articles[position]}")
        rv.setTextViewText(
            R.id.articleTextView,
            articles[position]
        )  // Assuming you have a TextView with this id in single_article_layout.xml
        return rv
    }

    override fun getLoadingView(): RemoteViews? = null

    override fun getViewTypeCount(): Int = 1

    override fun getItemId(position: Int): Long = position.toLong()

    override fun hasStableIds(): Boolean = true

}