package com.thirdgate.hackernews


import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView


class ArticleAdapter(
    private val articles: MutableList<ArticleData.ArticleInfo>,
    private val onClick: (ArticleData.ArticleInfo) -> Unit
) : RecyclerView.Adapter<ArticleAdapter.ViewHolder>() {

//    private val articles: MutableList<Map<String, Any>> = articles.toMutableList()

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val articleContainer: LinearLayout = view.findViewById(R.id.articleContainer)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.article_items, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = articles.size

    fun updateData(newArticles: List<ArticleData.ArticleInfo>) {
        this.articles.clear()
        this.articles.addAll(newArticles)
        notifyDataSetChanged()
    }

    private fun createArticlesView(holder: ViewHolder, article: ArticleData.ArticleInfo) {
        val context = holder.view.context

        val articleTextView = ArticleFormatter.makeArticleButton(article, context)

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

        holder.articleContainer.addView(articleTextView) // Add the formatted article button

        articleTextView.setOnClickListener {
            onClick(article)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val article = articles[position]

        createArticlesView(holder, article)
    }
}
