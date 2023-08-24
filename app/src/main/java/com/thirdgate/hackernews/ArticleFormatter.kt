package com.thirdgate.hackernews

import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.util.TypedValue

object ArticleFormatter {
    fun formatArticle(context: Context, article: Map<String, Any>): SpannableString {
        // ... (same logic as in createArticlesView to create the SpannableString) ...
        val typedValue = TypedValue()
        context.theme.resolveAttribute(android.R.attr.textColorPrimary, typedValue, true)
        val titleTextColor = typedValue.data

        context.theme.resolveAttribute(android.R.attr.textColorSecondary, typedValue, true)
        val otherTextColor = typedValue.data
        val title = article["title"] as? String ?: ""
        val domain = article["domain"] as? String ?: ""
        val score = article["score"]?.toString()?.replace(".0", "") ?: ""
        val descendants = article["descendants"]?.toString()?.replace(".0", "") ?: ""
        val by = article["by"] as? String ?: ""

        val formattedArticle = """$title ($domain)
    |score: $score comments: $descendants  by: $by
    """.trimMargin()

        val spannable = SpannableString(formattedArticle)
        spannable.setSpan(
            ForegroundColorSpan(titleTextColor),
            0,
            title.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        // Set the rest of the text to another color (e.g., gray)
        spannable.setSpan(
            ForegroundColorSpan(otherTextColor),
            title.length,
            spannable.length,
            Spannable.SPAN_INCLUSIVE_INCLUSIVE
        )

        // Set the rest of the text to a smaller size
        spannable.setSpan(
            RelativeSizeSpan(0.8f),
            title.length,
            spannable.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        return spannable
    }
}