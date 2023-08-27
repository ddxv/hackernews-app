package com.thirdgate.hackernews

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.text.Spannable
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.TextView

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

        // Set the click listener for the rank and title
        val url = article.url as? String ?: ""
        val titleClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                context.startActivity(browserIntent)
            }
        }
        spannable.setSpan(
            titleClickableSpan,
            0,
            title.length + rank.length + 2,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        // Set the click listener for the score, by, and comments
        val commentUrl = article.commentUrl as? String ?: ""
        val commentClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(commentUrl))
                context.startActivity(browserIntent)
            }
        }
        spannable.setSpan(
            commentClickableSpan,
            title.length + rank.length + 2,
            spannable.length,
            Spannable.SPAN_INCLUSIVE_INCLUSIVE
        )

        return spannable
    }

    fun makeArticleButton(article: ArticleData.ArticleInfo, context: Context): TextView {
        val typedValue = TypedValue()

        context.theme.resolveAttribute(R.attr.backgroundSecondary, typedValue, true)
        val articleBgColor = typedValue.data


        val spannable = formatArticle(context, article)

        val articleTextView = TextView(context)
        articleTextView.text = spannable

        articleTextView.setBackgroundColor(articleBgColor)
        articleTextView.isAllCaps = false
        articleTextView.setTypeface(null, Typeface.NORMAL)
        articleTextView.gravity = Gravity.LEFT

        // Indent the text
        val paddingStart = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 16f,
            context.resources.displayMetrics
        ).toInt()
        articleTextView.setPadding(
            paddingStart,
            0,
            0,
            0
        )  // Only adding padding to the start (left for LTR layouts)


        articleTextView.setOnClickListener {
            val url = article.url as? String
            url?.let {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(it))
                context.startActivity(browserIntent)
            }
        }
        // IMPORTANT: You need to set the movement method to make the clickable spans work
        articleTextView.movementMethod = LinkMovementMethod.getInstance()

        return articleTextView
    }

}