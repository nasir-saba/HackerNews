package com.sn.hackernewsapp.ui.fragments.home

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.sn.hackernewsapp.ui.main.HackerNewsActivity
import com.sn.hackernewsapp.R
import com.sn.hackernewsapp.adapters.NewsAdapter
import com.sn.hackernewsapp.databinding.FragmentHomeBinding
import com.sn.hackernewsapp.ui.main.HackerNewsViewModel
import com.sn.hackernewsapp.util.Constants.Companion.PAGE_LIMIT
import com.sn.hackernewsapp.util.Constants.Companion.QUERY_PAGE_SIZE
import com.sn.hackernewsapp.util.Resource
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    lateinit var viewModel: HackerNewsViewModel
    var newsAdapter: NewsAdapter = NewsAdapter()
    val TAG = "HomeFragment"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        viewModel = (activity as HackerNewsActivity).viewModel
        viewModel.getNewArticles()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupObserverForArticles()
        swipeRefreshLayout.setOnRefreshListener{
            viewModel.getNewArticles()
        }
        newsAdapter.setOnItemClickListener {
            if (it.url?.contains("item?id") == true) {
                val bundle = Bundle()
                bundle.putSerializable("articleUrl", it.url)
                findNavController().navigate(
                    R.id.action_homeFragment_to_articleFragment,
                    bundle
                )
            } else {
                it.url?.let { it1 -> openExternalArticle(it1) }

            }
        }

        val itemTouchHelperCallback =
            object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val position = viewHolder.adapterPosition
                    val article = newsAdapter.differ.currentList[position]
                    viewModel.saveArticle(article)
                    Snackbar.make(view, "Successfully saved article!", Snackbar.LENGTH_LONG).apply {
                        setAction("UNDO") {
                            viewModel.deleteArticle(article)
                        }
                        show()
                    }
                }

                override fun onChildDraw(
                    c: Canvas,
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    dX: Float,
                    dY: Float,
                    actionState: Int,
                    isCurrentlyActive: Boolean
                ) {

                    RecyclerViewSwipeDecorator.Builder(
                        c,
                        recyclerView,
                        viewHolder,
                        dX,
                        dY,
                        actionState,
                        isCurrentlyActive
                    )
                        .addBackgroundColor(
                            ContextCompat.getColor(
                                requireActivity(),
                                android.R.color.holo_green_light
                            )
                        )
                        .addActionIcon(R.drawable.outline_archive_white_24)
                        .addSwipeRightLabel("Archiving the Item")
                        .setSwipeRightLabelColor(R.color.black)
                        .create()
                        .decorate()

                    super.onChildDraw(
                        c,
                        recyclerView,
                        viewHolder,
                        dX,
                        dY,
                        actionState,
                        isCurrentlyActive
                    )
                }
            }
        ItemTouchHelper(itemTouchHelperCallback).apply {
            attachToRecyclerView(rvTopStories)
        }
    }

    private fun openExternalArticle(url: String) {
        val openURL = Intent(Intent.ACTION_VIEW)
        openURL.data = Uri.parse(url)
        startActivity(openURL)
    }

    @SuppressLint("LongLogTag")
    private fun setupObserverForArticles() {
        viewModel.articles
            .observe(viewLifecycleOwner) { response ->
                Log.e(TAG, response.toString())
                when (response) {
                    is Resource.Success -> {
                        swipeRefreshLayout.isRefreshing = false
                        hideProgressBar()
                        response.data?.let { newsResponse ->
                            newsAdapter.differ.submitList(newsResponse.toList())
                            isLastPage = viewModel.hackerNewsPage == PAGE_LIMIT
                            if (isLastPage) {
                                rvTopStories.setPadding(0, 0, 0, 0)
                            }
                        }
                    }
                    is Resource.Error -> {
                        hideProgressBar()
                        swipeRefreshLayout.isRefreshing = false
                        response.message?.let { message ->
                            displayErrorMessage(message)
                        }

                    }
                    is Resource.Loading -> {
                        swipeRefreshLayout.isRefreshing = false
                        showProgressBar()
                    }
                }
            }
    }

    private fun displayErrorMessage(message: String) {
        val snack = Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)
        snack.view.setBackgroundColor(Color.BLACK)
        val textView =
            snack.view.findViewById(com.google.android.material.R.id.snackbar_text) as TextView
        textView.setTextColor(Color.WHITE)
        snack.show()
    }

    private fun hideProgressBar() {
        pbLoading.visibility = View.GONE
        isLoading = false
    }

    private fun showProgressBar() {
        pbLoading.visibility = View.VISIBLE
        isLoading = true
    }

    var isError = false
    var isLoading = false
    var isLastPage = false
    var isScrolling = false

    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNoErrors = !isError
            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= QUERY_PAGE_SIZE
            val shouldPaginate =
                isNoErrors && isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning &&
                        isTotalMoreThanVisible && isScrolling
            if (shouldPaginate) {
                viewModel.getNewArticles()
                isScrolling = false
            }
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true
            }
        }
    }

    private fun setupRecyclerView() {
        binding.rvTopStories.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(this@HomeFragment.scrollListener)
        }
    }


    override fun onDestroyView() {
        if (binding.rvTopStories.adapter != null)
            binding.rvTopStories.adapter = null
        super.onDestroyView()
    }

}

