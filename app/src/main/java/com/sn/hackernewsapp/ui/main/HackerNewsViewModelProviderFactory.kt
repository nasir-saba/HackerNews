package com.sn.hackernewsapp.ui.main

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sn.hackernewsapp.repository.HackerNewsRepository

class HackerNewsViewModelProviderFactory ( private val hackerNewsRepository: HackerNewsRepository,val app: Application): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return  HackerNewsViewModel(hackerNewsRepository, app ) as T
    }
}
