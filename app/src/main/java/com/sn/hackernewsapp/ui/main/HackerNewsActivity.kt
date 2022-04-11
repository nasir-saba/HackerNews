package com.sn.hackernewsapp.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.sn.hackernewsapp.R
import com.sn.hackernewsapp.databinding.ActivityHackerNewsBinding
import com.sn.hackernewsapp.db.ArticleDatabase
import com.sn.hackernewsapp.repository.HackerNewsRepository

class HackerNewsActivity : AppCompatActivity() {
    lateinit var viewModel: HackerNewsViewModel
    private lateinit var binding: ActivityHackerNewsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHackerNewsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val hackerNewsRepository = HackerNewsRepository(ArticleDatabase(this))
        val viewModelProviderFactory = HackerNewsViewModelProviderFactory(hackerNewsRepository, application)
        viewModel =
            ViewModelProvider(this, viewModelProviderFactory)[HackerNewsViewModel::class.java]
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        binding.navView.setupWithNavController(navController)
    }
}