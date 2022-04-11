package com.sn.hackernewsapp.ui.fragments

import android.annotation.SuppressLint
import android.graphics.text.LineBreaker
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.Html.fromHtml
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.sn.hackernewsapp.R
import com.sn.hackernewsapp.adapters.CommentsAdapter
import com.sn.hackernewsapp.models.ArticleInternal
import com.sn.hackernewsapp.models.Comment
import com.sn.hackernewsapp.ui.main.HackerNewsActivity
import com.sn.hackernewsapp.ui.main.HackerNewsViewModel
import com.sn.hackernewsapp.util.Resource
import kotlinx.android.synthetic.main.article_layout.*

@Suppress("DEPRECATION")
class ArticleFragment : Fragment(R.layout.article_layout) {
    val args: ArticleFragmentArgs by navArgs()
    lateinit var viewModel: HackerNewsViewModel
    val TAG = "ArticleFragment"
    private var itemsData = ArrayList<Comment>()
    private var expandedSize = ArrayList<Int>()
    private lateinit var adapter: CommentsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as HackerNewsActivity).viewModel
        setUpToolbar()
        setUpRecyclerView()
        setupObserverForArticle()
        load()
    }

    private fun load() {
        val url = args.articleUrl
        val articleUrl = url.replace("?id=", "/")
        viewModel.getArticleInternal(articleUrl)
    }

    private fun setUpToolbar() {
        toolbar.setNavigationIcon(R.drawable.outline_arrow_back_24)
        toolbar.title = getString(R.string.detail)
        toolbar.setOnClickListener {
            activity?.onBackPressed()
        }
        setHasOptionsMenu(true)
    }

    private fun showUI() {
        imageView.visibility = View.VISIBLE
        nestedScrollView.visibility = View.VISIBLE
    }

    private fun setUpRecyclerView() {
        adapter = CommentsAdapter(itemsData, expandedSize)
        val llm = LinearLayoutManager(activity)
        rvComments.layoutManager = llm
        rvComments.isNestedScrollingEnabled = false
    }

    private fun setupObserverForArticle() {
        viewModel.articleInternal
            .observe(viewLifecycleOwner) { response ->
                Log.e(TAG, response.toString())
                when (response) {
                    is Resource.Success -> {
                        hideProgressBar()
                        response.data?.let { newsResponse ->
                            renderUI(newsResponse)
                        }
                    }
                    is Resource.Error -> {
                        hideProgressBar()
                        response.message?.let { message ->
                            Log.e(TAG, "An error occured: $message")
                        }

                    }
                    is Resource.Loading -> {
                        showProgressBar()
                    }
                }
            }
    }

    private fun hideProgressBar() {
        progressBar.visibility = View.GONE
        showUI()
    }

    private fun showProgressBar() {
        progressBar.visibility = View.VISIBLE
    }

    private fun renderUI(article: ArticleInternal) {
        tvArticlePublishTime.text = article.time_ago
        tvArticleTitle.text = article.title
        (getString(R.string.by_string) + article.user).also { tvUserName.text = it }
        if (article.comments_count != 0) {
            setAdapterData(article)
            rvComments.adapter = adapter
        } else {
            commentsHeader.visibility = View.GONE
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            tvArticleContent.text = fromHtml(article.content, Html.FROM_HTML_MODE_LEGACY)
        } else {
            tvArticleContent.text = fromHtml(article.content)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setAdapterData(article: ArticleInternal) {
        setCellSize(article)
        adapter = CommentsAdapter(article.comments, expandedSize)
        adapter.notifyDataSetChanged()
    }

    // Set the expanded view size to 0, because all expanded views are collapsed at the beginning
    private fun setCellSize(article: ArticleInternal) {
        expandedSize = ArrayList()
        for (i in 0 until article.comments.count()) {
            expandedSize.add(0)
        }
    }
}
