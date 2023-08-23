package com.thirdgate.hackernews

import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.thirdgate.hackernews.databinding.FragmentNewsBinding
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */

class NewsFragment : Fragment() {

    private var _binding: FragmentNewsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val viewModel: SharedViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentNewsBinding.inflate(inflater, container, false)

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val articleType: String = arguments?.getString("articleType") ?: "top"

        // Get the correct LiveData based on articleType
        val articlesLiveData = when (articleType) {
            "top" -> viewModel.topArticles
            "new" -> viewModel.newArticles
            "best" -> viewModel.bestArticles
            else -> viewModel.topArticles // default to topArticles if no match found
        }

        articlesLiveData.observe(viewLifecycleOwner) { articles ->
            viewLifecycleOwner.lifecycleScope.launch {
                try {

                    for ((_, article) in articles) {
                        if (article !is Map<*, *>) {
                            Log.e("SecondFragment", "Article is not a map: $article")
                            continue
                        }

                        val typedValue = TypedValue()
                        val theme = requireContext().theme

                        theme.resolveAttribute(android.R.attr.textColorPrimary, typedValue, true)
                        val titleTextColor = typedValue.data

                        theme.resolveAttribute(android.R.attr.textColorSecondary, typedValue, true)
                        val otherTextColor = typedValue.data


                        theme.resolveAttribute(R.attr.backgroundSecondary, typedValue, true)
                        val articleBgColor = typedValue.data


                        theme.resolveAttribute(android.R.attr.colorAccent, typedValue, true)
                        val accentColor = typedValue.data


                        val title = article["title"] as? String ?: ""
                        val domain = article["domain"] as? String ?: ""
                        val score = article["score"]?.toString()?.replace(".0", "") ?: ""
                        val descendants =
                            article["descendants"]?.toString()?.replace(".0", "") ?: ""
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
                        val articleButton = Button(context)
                        articleButton.setBackgroundColor(articleBgColor)
                        articleButton.text = spannable

                        //articleButton.text = formattedArticle
                        articleButton.isAllCaps = false
                        articleButton.setTypeface(null, Typeface.NORMAL)
                        articleButton.gravity = Gravity.LEFT
                        articleButton.setOnClickListener {
                            val url = article["url"] as? String
                            url?.let {
                                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(it))
                                startActivity(browserIntent)
                            }
                        }

                        binding.articleContainer.addView(articleButton) // Assuming articleContainer is a LinearLayout or similar

                        // Add separator
                        val separator = View(context)
                        separator.layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            1
                        )
                        separator.setBackgroundColor(accentColor)
                        binding.articleContainer.addView(separator)
                    }

                } catch (e: Exception) {
                    Log.e("SecondFragment", "ApiService $e")
                    // Handle the error (e.g., show a Toast or a Snackbar)
                }


            }


        }


    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}