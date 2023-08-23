package com.thirdgate.hackernews

import ApiService
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.thirdgate.hackernews.databinding.FragmentNewsBinding

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */

class NewsFragment : Fragment() {

    private var _binding: FragmentNewsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val viewModel: SharedViewModel by activityViewModels()
    private lateinit var apiService: ApiService


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
        apiService = ApiService()  // Initialize ApiService here

        // Set activity title based on article type
        activity?.title = when (articleType) {
            "top" -> "HackerNews: Top Articles"
            "new" -> "HackerNews: New Articles"
            "best" -> "HackerNews: Best Articles"
            else -> "Articles"
        }

        val recyclerView = binding.recyclerView
        val adapter = ArticleAdapter(mutableListOf()) { article ->
            val url = article["url"] as? String
            url?.let {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(it))
                startActivity(browserIntent)
            }
        }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        val articlesLiveData = when (articleType) {
            "top" -> viewModel.topArticles
            "new" -> viewModel.newArticles
            "best" -> viewModel.bestArticles
            else -> viewModel.topArticles
        }

        articlesLiveData.observe(viewLifecycleOwner) { articles ->
            for (article in articles.values) {
                if (article is Map<*, *>) {
                    @Suppress("UNCHECKED_CAST")
                    adapter.articles.add(article as Map<String, Any>)
                }
            }
            adapter.notifyDataSetChanged()
        }

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (!recyclerView.canScrollVertically(1) && dy > 0) {
                    // Logic for infinite scrolling
                    when (articleType) {
                        "top" -> {
                            val nextPage = viewModel.topArticlePage.value?.plus(1)
                            viewModel.topArticlePage.value = nextPage
                            viewModel.fetchArticles(apiService, articleType, nextPage!!)
                        }

                        "new" -> {
                            val nextPage = viewModel.newArticlePage.value?.plus(1)
                            viewModel.newArticlePage.value = nextPage
                            viewModel.fetchArticles(apiService, articleType, nextPage!!)
                        }

                        "best" -> {
                            val nextPage = viewModel.bestArticlePage.value?.plus(1)
                            viewModel.bestArticlePage.value = nextPage
                            viewModel.fetchArticles(apiService, articleType, nextPage!!)
                        }
                    }
                }
            }
        })
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}