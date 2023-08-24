package com.thirdgate.hackernews


import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView

class ArticleAdapter(
    val articles: MutableList<Map<String, Any>>,
    val onClick: (Map<String, Any>) -> Unit
) : RecyclerView.Adapter<ArticleAdapter.ViewHolder>() {


    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val articleContainer: LinearLayout = view.findViewById(R.id.articleContainer)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.article_items, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = articles.size


    fun createArticlesView(holder: ViewHolder, article: Map<String, Any>) {
        val context = holder.view.context

        val articleButton = ArticleFormatter.makeArticleButton(article, context)

        holder.articleContainer.removeAllViews() // Clean up previous views if any

        // Add separator
        val typedValue = TypedValue()
        context.theme.resolveAttribute(android.R.attr.colorAccent, typedValue, true)
        val accentColor = typedValue.data

        val separator = View(context)
        separator.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            1
        )
        separator.setBackgroundColor(accentColor)
        holder.articleContainer.addView(separator)

        holder.articleContainer.addView(articleButton) // Add the formatted article button

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val article = articles[position]

        createArticlesView(holder, article)


    }

}
