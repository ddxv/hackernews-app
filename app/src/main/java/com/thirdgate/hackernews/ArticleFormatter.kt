package com.thirdgate.hackernews

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.util.TypedValue
import android.view.Gravity
import android.widget.Button

object ArticleFormatter {
    fun formatArticle(context: Context, article: ArticleData.ArticleInfo): SpannableString {
        // ... (same logic as in createArticlesView to create the SpannableString) ...
        val typedValue = TypedValue()
        context.theme.resolveAttribute(android.R.attr.textColorPrimary, typedValue, true)
        val titleTextColor = typedValue.data

        context.theme.resolveAttribute(android.R.attr.textColorSecondary, typedValue, true)
        val otherTextColor = typedValue.data
        val rank = article.rank.toString().replace(".0", "") ?: ""
        val title = article.title as? String ?: ""
        val domain = article.domain as? String ?: ""
        val score = article.score.toString()?.replace(".0", "") ?: ""
        val descendants = article.descendants.toString().replace(".0", "") ?: ""
        val by = article.by as? String ?: ""

        val formattedArticle = """$rank. $title ($domain)
    |$score points by: $by | $descendants comments
    """.trimMargin()

        val spannable = SpannableString(formattedArticle)
        spannable.setSpan(
            ForegroundColorSpan(titleTextColor),
            0,
            title.length + rank.length + 2,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        // Set the rest of the text to another color (e.g., gray)
        spannable.setSpan(
            ForegroundColorSpan(otherTextColor),
            title.length + rank.length + 2,
            spannable.length,
            Spannable.SPAN_INCLUSIVE_INCLUSIVE
        )

        // Set the rest of the text to a smaller size
        spannable.setSpan(
            RelativeSizeSpan(0.8f),
            title.length + rank.length + 2,
            spannable.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        return spannable
    }

    fun makeArticleButton(article: ArticleData.ArticleInfo, context: Context): Button {
        val typedValue = TypedValue()

        context.theme.resolveAttribute(R.attr.backgroundSecondary, typedValue, true)
        val articleBgColor = typedValue.data


        val spannable = formatArticle(context, article)

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
            val url = article.url as? String
            url?.let {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(it))
                context.startActivity(browserIntent)
            }
        }
        return articleButton
    }

}