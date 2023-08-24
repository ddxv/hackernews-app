package com.thirdgate.hackernews


import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
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
        val typedValue = TypedValue()

        context.theme.resolveAttribute(R.attr.backgroundSecondary, typedValue, true)
        val articleBgColor = typedValue.data

        context.theme.resolveAttribute(android.R.attr.colorAccent, typedValue, true)
        val accentColor = typedValue.data


        val spannable = ArticleFormatter.formatArticle(context, article)

        val articleButton = Button(context)
        articleButton.text = spannable
        
        articleButton.setBackgroundColor(articleBgColor)
        articleButton.isAllCaps = false
        articleButton.setTypeface(null, Typeface.NORMAL)
        articleButton.gravity = Gravity.LEFT

        // Indent the text
        val paddingStart = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 16f,
            context.resources.displayMetrics
        ).toInt()
        articleButton.setPadding(
            paddingStart,
            0,
            0,
            0
        )  // Only adding padding to the start (left for LTR layouts)


        articleButton.setOnClickListener {
            val url = article["url"] as? String
            url?.let {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(it))
                context.startActivity(browserIntent)
            }
        }
        holder.articleContainer.removeAllViews() // Clean up previous views if any

        // Add separator
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
