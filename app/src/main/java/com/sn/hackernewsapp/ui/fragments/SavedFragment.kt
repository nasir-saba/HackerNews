package com.sn.hackernewsapp.ui.fragments

import android.content.Intent
import android.graphics.Canvas
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.sn.hackernewsapp.ui.main.HackerNewsActivity
import com.sn.hackernewsapp.R
import com.sn.hackernewsapp.adapters.NewsAdapter
import com.sn.hackernewsapp.ui.main.HackerNewsViewModel
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import kotlinx.android.synthetic.main.fragment_article.*
import kotlinx.android.synthetic.main.fragment_saved.*
import kotlinx.android.synthetic.main.fragment_saved.pbLoading

class SavedFragment : Fragment(R.layout.fragment_saved) {
    lateinit var viewModel: HackerNewsViewModel
    var newsAdapter: NewsAdapter = NewsAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as HackerNewsActivity).viewModel
        setupRecyclerView()
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


        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT)
        {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val article = newsAdapter.differ.currentList[position]
                viewModel.deleteArticle(article)
                Snackbar.make(view, "Successfully deleted saved article", Snackbar.LENGTH_LONG).apply {
                    setAction("Undo") {
                        viewModel.saveArticle(article)
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
                            android.R.color.holo_red_light
                        )
                    )
                    .addActionIcon(R.drawable.outline_delete_24)
                    .addSwipeLeftLabel("Deleting the article")
                    .setSwipeLeftLabelColor(R.color.white)
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
            attachToRecyclerView(rvSavedArticles)
        }

        setupObserverForSavedArticles()
    }

    private fun setupObserverForSavedArticles() {
        viewModel.getSavedArticles().observe(viewLifecycleOwner, Observer { articles ->
            newsAdapter.differ.submitList(articles)
            pbLoading.visibility = View.GONE
        })
    }

    private fun openExternalArticle(url: String) {
        val openURL = Intent(Intent.ACTION_VIEW)
        openURL.data = Uri.parse(url)
        startActivity(openURL)
    }


    private fun setupRecyclerView() {
        rvSavedArticles.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }
}
